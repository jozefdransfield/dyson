package dyson

import dyson.model.DeviceMetaData
import java.io.Closeable
import java.net.InetAddress
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer
import javax.jmdns.JmDNS
import javax.jmdns.ServiceEvent
import javax.jmdns.ServiceListener

class DeviceDiscovery(val jmdns: JmDNS) : Closeable {

    constructor() : this(JmDNS.create(InetAddress.getLocalHost()))

    private val futures: MutableMap<String, CompletableFuture<Pair<String, Int>>> = mutableMapOf()

    init {
        jmdns.addServiceListener("_dyson_mqtt._tcp.local.", object : ServiceListener {
            override fun serviceAdded(event: ServiceEvent?) {

            }

            override fun serviceRemoved(event: ServiceEvent?) {
            }

            override fun serviceResolved(event: ServiceEvent?) {
                val serviceName = event!!.info.name

                futures.entries.find { serviceName.endsWith(it.key) }?.let {
                    val hostAddresses = event.info.hostAddresses[0]
                    val port = event.info.port
                    it.value.complete(Pair(hostAddresses, port))
                }
            }
        })
    }

    fun watchFor(deviceMetaData: DeviceMetaData): CompletableFuture<Pair<String, Int>> {
        val future = CompletableFuture<Pair<String, Int>>()
        futures.put(deviceMetaData.serial, future)
        return future
    }

    override fun close() {
        jmdns.close()
    }
}