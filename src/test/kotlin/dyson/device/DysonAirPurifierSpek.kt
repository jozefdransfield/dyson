package dyson.device

import com.nhaarman.mockito_kotlin.*
import dyson.Device
import org.hamcrest.Matchers
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class DysonAirPurifierSpek : Spek({
    describe("a dyson air purifier") {
        val device = mock<Device> {
//            on { receive(any()) } doAnswer {
//
//            }
        }
        describe("the constructor") {
            it("should request the state and sensor data") {
                DysonAirPurifier(device)
                verify(device).sendCommand(argThat { contains("REQUEST-PRODUCT-ENVIRONMENT-CURRENT-SENSOR-DATA") })
                verify(device).sendCommand(argThat { contains("REQUEST-CURRENT-STATE") })
            }
        }


    }
})