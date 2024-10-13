package com.example.smartdtr_remade.Api

import LoginRequest
import LoginResponse
import com.example.smartdtr_remade.models.Duty
import com.example.smartdtr_remade.models.Student
import com.example.smartdtr_remade.models.Teacher
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
interface ApiService {

    interface DutyApi {
        @GET("duties/upcoming")
        fun getUpcomingDuties(): Call<List<Duty>>

        @GET("duties/completed")
        fun getCompletedDuties(): Call<List<Duty>>

        @POST("duties")
        fun createDuty(@Body duty: Duty): Call<Duty>

        @PUT("duties/{id}")
        fun updateDuty(@Path("id") id: Int, @Body duty: Duty): Call<Duty>

        @DELETE("duties/{id}")
        fun deleteDuty(@Path("id") id: Int): Call<Void>
    }

    interface StudentListApi {
        @GET("students/get") // Ensure this matches your API route
        fun getAllStudents(): Call<List<Student>>
    }

    @POST("/api/login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    //@POST("signup")
    //fun signup(@Body request: SignupRequest): Call<LoginResponse>

    @POST("logout")
    fun logout(@Header("Authorization") token: String): Call<Void>

    // Companion object to create the Retrofit instance
    companion object {
        private const val BASE_URL = "http://10.0.2.2:8000/api/"

        fun create(): ApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL) // Ensure you have the correct base URL
                .addConverterFactory(GsonConverterFactory.create()) // For JSON conversion
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }

    interface TeacherApi {
        @GET("teachers/{userId}")
        fun getTeacherAccountDetails(
            @Path("userId") userId: String
        ): Call<Teacher>
    }

    interface StudentApi {
        @GET("students/{userId}")
        fun getStudentAccountDetails(
            @Path("userId") userId: String
        ): Call<Student>
    }
}