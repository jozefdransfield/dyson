package dyson

import com.fasterxml.jackson.databind.JsonNode
import com.nhaarman.mockito_kotlin.*
import dyson.model.DeviceMetaData
import org.awaitility.Awaitility.await
import org.fusesource.mqtt.client.BlockingConnection
import org.fusesource.mqtt.client.MQTT
import org.fusesource.mqtt.client.Message
import org.fusesource.mqtt.client.QoS
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.nullValue
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.util.concurrent.TimeUnit
import java.util.function.Consumer

class DeviceSpek : Spek({
    describe("a device") {
        val message = mock<Message>{
            on {payload} doReturn """{}""".toByteArray()
        }
        val connection = mock<BlockingConnection>{
            on{receive()} doAnswer {
                Thread.sleep(100)
                message
            }
        }
        val mqtt = mock<MQTT> {
            on {blockingConnection()} doReturn connection
        }
        describe("the constructor") {
            it("connects to mqtt") {
                MQTTDevice("host", 1, "password", DeviceMetaData(), mqtt).use {}
            }
        }
        describe("an instance") {
            it("should send a command") {
                MQTTDevice("host", 1, "password", DeviceMetaData(productType = "productType", serial = "serial"), mqtt).use {
                    it.sendCommand("payload")
                }
                verify(connection).publish("productType/serial/command", "payload".toByteArray(), QoS.AT_LEAST_ONCE, false)
            }

            it("should receive a message") {
                MQTTDevice("host", 1, "password", DeviceMetaData(productType = "productType", serial = "serial"), mqtt).use {
                    var newMessage : JsonNode? = null
                    it.receive(Consumer {
                        newMessage = it
                    })
                    await().atMost(1, TimeUnit.SECONDS).until({newMessage}, not(nullValue()))
                }
            }
        }

    }
})