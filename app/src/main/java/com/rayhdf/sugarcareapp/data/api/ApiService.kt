package com.rayhdf.sugarcareapp.data.api

import com.rayhdf.sugarcareapp.data.model.LoginResponse
import com.rayhdf.sugarcareapp.data.model.PredictRequest
import com.rayhdf.sugarcareapp.data.model.PredictResponse
import com.rayhdf.sugarcareapp.data.model.RegisterResponse
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {

    @FormUrlEncoded
    @POST("register")
    suspend fun registerUser(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @POST("predictions")
    suspend fun predict(@Body request: PredictRequest): PredictResponse
}