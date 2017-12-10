package meng.nothd.api

import com.squareup.moshi.Json

/**
 * Created by meng on 2017/12/4.
 */

class DemoResponse(val data: String)

data class ClarifaiImage(val base4: String? = "")

data class ClarifaiData(val image: ClarifaiImage? = null)

data class ClarifaiInput(val data: ClarifaiData? = null)

data class ClarifaiResponse(val inputs: List<ClarifaiInput>? = ArrayList())

data class AuthToken(
        @Json(name = "access_token") val accessToken: String? = "",
        @Json(name = "expires_in") val expiresIn: Int? = 0
)