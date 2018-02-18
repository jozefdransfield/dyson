package dyson

import com.nhaarman.mockito_kotlin.*
import dyson.model.DeviceMetaData
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import javax.jmdns.JmDNS
import javax.jmdns.ServiceEvent
import javax.jmdns.ServiceInfo
import javax.jmdns.ServiceListener

class DeviceDiscoverySpek : Spek({
    describe("device discovery") {
        describe("the constructor") {
            val jmDNS = mock<JmDNS>()
            it("should listen to connections on jmdns") {
                DeviceDiscovery(jmDNS)
                verify(jmDNS).addServiceListener(
                        argThat { equals("_dyson_mqtt._tcp.local.") },
                        argWhere { it is DysonServiceListener }
                )

            }
        }
        describe("an instance") {
            val dmd= DeviceMetaData(serial = "seria")
            var serviceListener: ServiceListener? = null
            val jmDNS = mock<JmDNS> {
                on { addServiceListener(any(), any()) } doAnswer {
                    serviceListener = it.getArgument(1) as ServiceListener
                    null
                }
            }
            val deviceDiscovery = DeviceDiscovery(jmDNS)
            val serviceInfo = mock<ServiceInfo> {
                on {name} doReturn dmd.serial
                on {hostAddresses} doReturn arrayOf("1.1.1.1")
                on {port} doReturn 80

            }
            val serviceEvent = mock<ServiceEvent> {
                on {info} doReturn serviceInfo
            }

            it("should watch for a service") {
                val watchFor = deviceDiscovery.watchFor(dmd)
                serviceListener?.serviceResolved(serviceEvent)

                assert(watchFor.isDone)
                assert(watchFor.get().first == "1.1.1.1")
                assert(watchFor.get().second == 80)

            }
        }
    }
})