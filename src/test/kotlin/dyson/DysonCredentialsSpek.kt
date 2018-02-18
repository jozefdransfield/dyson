package dyson

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class DysonCredentialsSpek : Spek({

    describe("the instance of dyson credentials") {
        it("should decode and encoded string") {
            val encrypt = DysonCredentials.encrypt("flibble")
            val decrypt = DysonCredentials.decrypt(encrypt)
            assert(encrypt == decrypt)
        }
    }
})