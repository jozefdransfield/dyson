package dyson

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockito_kotlin.*
import okhttp3.*
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import kotlin.test.assertFailsWith

private val mediaType = MediaType.parse("application/json")

class DysonApiSpek : Spek({
    describe("the dyson api") {
        val client = mock<OkHttpClient>()
        describe("the constructor") {
            it("should authenticate successfully") {
                respondWith(
                        client,
                        ResponseBody.create(mediaType, """{"Account": "MyAccount", "Password": "My Password"}"""),
                        200
                )
                DysonApi("email", "password", "GB", client, ObjectMapper())
            }
            it("should throw an exception if not authenticated") {
                respondWith(
                        client,
                        ResponseBody.create(mediaType, """{"Message": "Something went wrong"}"""),
                        401
                )
                assertFailsWith<IllegalStateException> {
                    DysonApi("email", "password", "GB", client, ObjectMapper())
                }
            }
        }
        describe("the call to devices") {
            respondWith(
                    client,
                    ResponseBody.create(mediaType, """{"Account": "MyAccount", "Password": "My Password"}"""),
                    200
            )
            val dysonApi = DysonApi("email", "password", "GB", client, ObjectMapper())
            it("should return the device list") {
                // TODO Make this return some real json!
                respondWith(client, ResponseBody.create(mediaType, "[]"), 200)
                dysonApi.devices()

            }
        }
    }
})

private fun respondWith(client: OkHttpClient, responseBody: ResponseBody?, i: Int) {
    val call = mock<Call>()
    var request: Request? = null
    whenever(client.newCall(any())).thenAnswer {
        request = it.arguments[0] as Request
        call
    }
    whenever(call.execute()).thenAnswer {
        Response.Builder()
                .protocol(Protocol.HTTP_1_1)
                .request(request!!)
                .message("Success!")
                .body(responseBody)
                .code(i).build()
    }
}