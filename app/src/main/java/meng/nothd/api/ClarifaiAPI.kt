package meng.nothd.api

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Created by meng on 2017/12/5.
 */
interface ClarifaiAPI {

    @POST("/v2/token")
    fun authorize(@Body requestBody: RequestBody): Call<AuthToken>
}