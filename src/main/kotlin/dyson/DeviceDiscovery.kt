package dyson

import java.net.InetAddress
import java.util.function.Consumer
import javax.jmdns.JmDNS
import javax.jmdns.ServiceEvent
import javax.jmdns.ServiceListener

class DeviceDiscovery(deviceMetaData: DeviceMetaData, consumer: Consumer<Pair<String, Int>>) {
    init {
        val jmdns = JmDNS.create(InetAddress.getLocalHost())
        jmdns.addServiceListener("_dyson_mqtt._tcp.local.", object : ServiceListener {
            override fun serviceAdded(event: ServiceEvent?) {

            }

            override fun serviceRemoved(event: ServiceEvent?) {
            }

            override fun serviceResolved(event: ServiceEvent?) {
                val serviceName = event!!.info.name
                if (serviceName.endsWith(deviceMetaData.serial)) {
                    val hostAddresses = event.info.hostAddresses[0]
                    val port = event.info.port

                    consumer.accept(Pair(hostAddresses, port))
                }
            }
        })
    }
}