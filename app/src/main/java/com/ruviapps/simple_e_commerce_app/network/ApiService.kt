package com.ruviapps.simple_e_commerce_app.network

import com.ruviapps.simple_e_commerce_app.model.Product
import com.ruviapps.simple_e_commerce_app.model.SelectedProduct
import com.ruviapps.simple_e_commerce_app.model.StoreInfo
import com.ruviapps.simple_e_commerce_app.ui.home.NetworkStatus
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import java.util.concurrent.TimeUnit


private const val BASE_URL = "https://apimocha.com/examplestore/"
    //"https://mocki.io"
    //for real device "http://192.168.29.10:3000"
   //for Emulator "http://10.0.2.2:3000"
    //"http://localhost:3000"

/*
Building moshi object
 */
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()


/*
adding client (mainly for post method)
 */
val client = OkHttpClient.Builder()
    .connectTimeout(30, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
    .writeTimeout(30, TimeUnit.SECONDS)
    .build()
/**
 * Building retrofit object
 */
private val retrofit = Retrofit.Builder()
    .client(client)
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

/**
 * public interface to expose [getProducts()] method
 */
interface ProductApiService{

    @GET("products")
   // @GET("v1/dd52b3cd-408c-4bbe-bfdf-31644f543c3c")
    suspend fun getProducts() : List<Product>

    @POST("orderDone")
    suspend fun checkOutOrder(@Body products: List<SelectedProduct>)

    @GET("storeInfo")
    suspend fun getStoreInfo() : List<StoreInfo>
}

/**
 * A public Api object that exposes  retrofit service (lazily initialized - to initialize object just once)
 * also, a singleton to have a single instance through out the app
 */
object ProductApi{
    val retrofitService : ProductApiService by lazy{
        retrofit.create(ProductApiService::class.java)
    }
}