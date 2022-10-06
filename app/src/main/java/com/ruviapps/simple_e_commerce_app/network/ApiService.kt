package com.ruviapps.simple_e_commerce_app.network

import com.ruviapps.simple_e_commerce_app.model.Product
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET


private const val BASE_URL = "http://localhost:3000"

/*
Building moshi object
 */
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

/**
 * Building retrofit object
 */
private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

/**
 * public interface to expose [getProducts()] method
 */
interface ProductApiService{

    @GET("products")
    suspend fun getProducts() : List<Product>
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