package com.example.smartdtr_remade.Api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

object RetrofitInstance {

    //change to https://127.0.0.1:8000/api/ if that api is not working
    // change to the laptop ip address that has the db on if connected to hotspot
    private const val BASE_URL = "http://10.0.2.2:8000/api/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    val dutyApi: ApiService.DutyApi = retrofit.create(ApiService.DutyApi::class.java)
    val studentlistApi: ApiService.StudentListApi = retrofit.create(ApiService.StudentListApi::class.java)
    val teacherApi: ApiService.TeacherApi = retrofit.create(ApiService.TeacherApi::class.java)
    val studentApi: ApiService.StudentApi = retrofit.create(ApiService.StudentApi::class.java)

}
