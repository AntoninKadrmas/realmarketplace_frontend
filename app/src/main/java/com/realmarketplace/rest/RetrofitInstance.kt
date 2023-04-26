package com.realmarketplace.rest

import com.google.gson.GsonBuilder
import com.realmarketplace.model.text.TextModelGlobal
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


/**
 * A group of *api_class*.
 *
 * Class used to communicate with backend.
 *
 * Used **RetroFit2** module.
 */
class RetrofitInstance {
    companion object{
        private val interceptor = HttpLoggingInterceptor().apply {
            this.level = HttpLoggingInterceptor.Level.BODY
        }
        private val interceptorHeader = object:Interceptor{
            override fun intercept(chain: Interceptor.Chain): Response {
                val request:Request= chain.request().newBuilder().addHeader("Admin","b326b5062b2f0e69046810717534cb09").build()
                return chain.proceed(request)
            }
        }
        private val client = OkHttpClient.Builder().apply {
            this.addInterceptor(interceptorHeader)
            this.addInterceptor(interceptor)
                .connectTimeout(30,TimeUnit.SECONDS)
                .readTimeout(30,TimeUnit.SECONDS)
                .writeTimeout(25,TimeUnit.SECONDS)
        }.build()

        private const val BASE_URL = TextModelGlobal.REAL_MARKET_URL
        /**
         * A group of *api_function*.
         *
         * Function is used to get retrofit instance to communicate with backend.
         */
        fun getRetroFitInstance():Retrofit{
            return Retrofit
                .Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(
                    GsonConverterFactory
                        .create(
                            GsonBuilder()
                                .create()
                        )
                ).build()
        }
    }
}