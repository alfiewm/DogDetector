package meng.nothd.rest

import android.content.Context
import com.squareup.moshi.Moshi
import meng.nothd.App
import meng.nothd.api.AuthToken
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Created by meng on 2017/12/5.
 */
class AuthorizationInterceptor(private val apiId: String,
                               private val apiSecret: String,
                               private val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain?): Response {
        val uri = chain?.request()?.url()?.uri()
        val path = uri?.path
        val authValue = if (path == "/v2/token") {
            Credentials.basic(apiId, apiSecret)
        } else {
            val prefs = context.getSharedPreferences(App.PREFS_NAME, Context.MODE_PRIVATE)
            val authString = prefs.getString(App.AUTH_TOKEN_KEY, "")
            val authResponse = Moshi.Builder().build().adapter(AuthToken::class.java).fromJson(authString)
            "Bearer ${authResponse?.accessToken}"
        }
        val request = chain?.request()?.newBuilder()?.addHeader("Authorization", authValue)?.build()
        return chain?.proceed(request)!!
    }
}