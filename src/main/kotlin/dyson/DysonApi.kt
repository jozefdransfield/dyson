package dyson

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import okhttp3.*


class DysonApi(email: String, password: String, country: String) {

    private val client: OkHttpClient = OkHttpClient()
    private val mapper = ObjectMapper().registerModule(KotlinModule())
    private val credentials: String

    init {
        val mediaType = MediaType.parse("application/json")
        val body = RequestBody.create(mediaType, "{\"Email\":\"${email}\",\"Password\":\"${password}\"}")
        val request = Request.Builder().url("https://api.cp.dyson.com/v1/userregistration/authenticate?country=$country").post(body).build()
        val response = client.newCall(request).execute()

        if (response.code() == 200) {
            val json = mapper.readTree(response.body()?.string())
            credentials = Credentials.basic(json["Account"].asText(), json["Password"].asText())
        } else {
            throw IllegalStateException("Failed to authenticate with Dyson")
        }
    }

    fun devices(): List<DeviceMetaData> {
        val request = Request.Builder()
                .get()
                .url("https://api.cp.dyson.com/v1/provisioningservice/manifest")
                .addHeader("Authorization", credentials)
                .build()

        val response = client.newCall(request).execute()

        return mapper.readTree(response.body()?.string()).map { DeviceMetaData.fromJson(it) }
    }
}