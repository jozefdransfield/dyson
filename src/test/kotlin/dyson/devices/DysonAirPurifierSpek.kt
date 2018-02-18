package dyson.devices

import com.nhaarman.mockito_kotlin.argThat
import com.nhaarman.mockito_kotlin.atLeastOnce
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import dyson.Device
import dyson.model.ToggleSwitch
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class DysonAirPurifierSpek : Spek({
    describe("a dyson air purifier") {
        val device = mock<Device>()
        describe("the constructor") {
            val dysonAirPurifier = DysonAirPurifier(device)
            it("should request the state and sensor data") {
                verify(device).sendCommand(argThat { contains("REQUEST-PRODUCT-ENVIRONMENT-CURRENT-SENSOR-DATA") })
                verify(device).sendCommand(argThat { contains("REQUEST-CURRENT-STATE") })
            }
            describe("an instance") {
                it("should set the fan to oscillate") {
                    dysonAirPurifier.oscillate(ToggleSwitch.ON)
                    verify(device, atLeastOnce()).sendCommand(argThat { contains("\"oson\":\"ON\"") })
                }
                it("should set the fan to a speed and mode") {
                    dysonAirPurifier.fanSpeedAndMode(FanSpeed.AUTO, FanMode.AUTO)
                    verify(device, atLeastOnce()).sendCommand(argThat { contains("\"fmod\":\"AUTO\"") })
                }
            }
        }



    }
})