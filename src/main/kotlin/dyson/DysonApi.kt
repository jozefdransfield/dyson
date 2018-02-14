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

        val json = mapper.readTree(response.body()?.string())

        credentials = Credentials.basic(json["Account"].asText(), json["Password"].asText())
    }

    fun devices(): List<DeviceMetaData> {
        val request = Request.Builder()
                .get()
                .url("https://api.cp.dyson.com/v1/provisioningservice/manifest")
                .addHeader("Authorization", credentials)
                .build()

        val response = client.newCall(request).execute()

        //TODO handle failure responses
        val message = response.body()?.string()

        return mapper.readTree(message!!).map { DeviceMetaData.fromJson(it) }
    }
}