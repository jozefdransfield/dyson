package dyson

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import dyson.model.DeviceMetaData
import org.fusesource.mqtt.client.BlockingConnection
import org.fusesource.mqtt.client.MQTT
import org.fusesource.mqtt.client.QoS
import org.fusesource.mqtt.client.Topic
import java.io.Closeable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.function.Consumer

class Device(hostAddress: String, port: Int, password: String, metaData: DeviceMetaData, mqtt: MQTT) : Closeable {

    private val connection: BlockingConnection
    private val executor: ExecutorService = Executors.newFixedThreadPool(1)
    private val mapper = ObjectMapper().registerModule(KotlinModule())
    private val receivers: MutableList<Consumer<JsonNode>> = mutableListOf()

    private val baseTopic = "${metaData.productType}/${metaData.serial}"

    constructor(hostAddress: String, port: Int, password: String, metaData: DeviceMetaData) : this(hostAddress, port, password, metaData, MQTT())

    init {
        mqtt.setHost(hostAddress, port)
        mqtt.setUserName(metaData.serial)
        mqtt.setPassword(password)

        connection = mqtt.blockingConnection()
        connection.connect()

        val topics = arrayOf(Topic("$baseTopic/status/current", QoS.AT_LEAST_ONCE))

        connection.subscribe(topics)

        executor.submit({
            while (true) {
                val message = connection.receive()
                val payload = message.payload
                message.ack()
                receivers.forEach({
                    it.accept(mapper.readTree(String(payload)))
                })

            }
        })
    }

    fun sendCommand(payload: String) {
        connection.publish("$baseTopic/command", payload.toByteArray(), QoS.AT_LEAST_ONCE, false)
    }

    fun receive(consumer: Consumer<JsonNode>) {
        receivers.add(consumer)
    }

    override fun close() {
        executor.shutdownNow()
        connection.disconnect()
    }
}