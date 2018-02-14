package dyson

import com.fasterxml.jackson.databind.JsonNode

data class DeviceMetaData(
        val active: Boolean = false,
        val serial: String = "",
        val name: String = "",
        val scaleUnit: String = "",
        val version: String = "",
        val localCredentials: String = "",
        val autoUpdate: Boolean = false,
        val newVersionAvailable: Boolean = false,
        val productType: String = "") {
    companion object {
        fun fromJson(data: JsonNode): DeviceMetaData {
            return DeviceMetaData(
                    data["Active"].asBoolean(),
                    data["Serial"].asText(),
                    data["Name"].asText(),
                    data["ScaleUnit"].asText(),
                    data["Version"].asText(),
                    data["LocalCredentials"].asText(),
                    data["AutoUpdate"].asBoolean(),
                    data["NewVersionAvailable"].asBoolean(),
                    data["ProductType"].asText()
            )
        }
    }
}
