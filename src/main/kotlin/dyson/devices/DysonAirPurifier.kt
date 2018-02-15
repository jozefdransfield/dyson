package dyson.devices

import com.fasterxml.jackson.databind.JsonNode
import dyson.Device
import dyson.model.ToggleSwitch
import java.util.function.Consumer

class DysonAirPurifier(private val device: Device) {

    var sensorData: SensorData? = null
    var state: DeviceState = DeviceState(FanMode.AUTO, FanSpeed.AUTO, ToggleSwitch.OFF, "0", ToggleSwitch.OFF)

    init {
        device.receive(Consumer {
            val msgType = it["msg"].asText()
            println("Message of type [$msgType] received")

            when (msgType) {
                "ENVIRONMENTAL-CURRENT-SENSOR-DATA" -> updateSensorData(it["data"])
                "CURRENT-STATE" -> updateState(it["product-state"])
                "STATE-CHANGE" -> currentState()
                else -> println("unrecognised event ${it["msg"]}")
            }
        })
        currentSensorData()
        currentState()
    }

    private fun updateState(data: JsonNode) {
        state = DeviceState.fromStateJson(data)
    }

    private fun updateSensorData(data: JsonNode) {
        sensorData = SensorData(data["tact"].asText(), data["hact"].asText(), data["pact"].asText(), data["vact"].asText(), ToggleSwitch.valueOf(data["sltm"].asText()))
    }

    private fun currentSensorData() {
        device.sendCommand("""{
            "msg": "REQUEST-PRODUCT-ENVIRONMENT-CURRENT-SENSOR-DATA"
        }""")
    }

    private fun currentState() {
        device.sendCommand("""{
            "msg": "REQUEST-CURRENT-STATE"
        }""")
    }

    private fun setDeviceState(newState: DeviceState) {
        // TODO fix the time!
        val payload = """{
            "msg": "STATE-SET",
            "mode-reason": "LAPP",
            "time": "2018-02-13T22:31:50Z",
            "data": ${DeviceState.toStateJson(newState)}
        }"""
        device.sendCommand(payload)
    }

    fun close() {
        device.close()
    }

    fun oscillate(value: ToggleSwitch) {
        setDeviceState(state.copy(oscilation = value))
    }

    fun fanSpeedAndMode(speed: FanSpeed, mode: FanMode) {
        setDeviceState(state.copy(fanSpeed = speed, fanMode = mode))
    }

}

data class SensorData(val temperature: String, val humidity: String, val dust: String, val volatileCompounds: String, val sleepTimer: ToggleSwitch)
data class DeviceState(
        val fanMode: FanMode,
        val fanSpeed: FanSpeed,
        val oscilation: ToggleSwitch,
        val qualityTarget: String,
        val nightMode: ToggleSwitch) {
    companion object {
        fun fromStateJson(data: JsonNode): DeviceState {
            return DeviceState(
                    FanMode.valueOf(data["fmod"].asText()),
                    FanSpeed.fromString(data["fnsp"].asText()),
                    ToggleSwitch.valueOf(data["oson"].asText()),
                    data["qtar"].asText(),
                    ToggleSwitch.valueOf(data["nmod"].asText())
            )
        }

        fun toStateJson(state: DeviceState): String {
            return """{
                "fmod":"${state.fanMode}",
                "fnst":"FAN",
                "fnsp":"${state.fanSpeed}",
                "qtar":"${state.qualityTarget}",
                "oson":"${state.oscilation}",
                "nmod":"${state.nightMode}"
                }"""
        }
    }
}

enum class FanMode {
    AUTO, FAN
}

enum class FanSpeed(val strValue: String) {
    AUTO("AUTO"),
    ONE("0001"),
    TWO("0002"),
    THREE("0003"),
    FOUR("0004"),
    FIVE("0005"),
    SIX("0006"),
    SEVEN("0007"),
    EIGHT("0008"),
    NINE("0009"),
    TEN("0010");

    companion object {
        fun fromString(strValue: String): FanSpeed {
            return FanSpeed.values().first { it.strValue == strValue }
        }
    }

}