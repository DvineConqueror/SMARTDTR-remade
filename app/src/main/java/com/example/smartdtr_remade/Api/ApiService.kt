package com.example.smartdtr_remade.Api

import LoginRequest
import LoginResponse
import com.example.smartdtr_remade.models.Duty
import com.example.smartdtr_remade.models.GetDuty
import com.example.smartdtr_remade.models.ResetPasswordRequest
import com.example.smartdtr_remade.models.ResetPasswordResponse
import com.example.smartdtr_remade.models.SignUpRequest
import com.example.smartdtr_remade.models.SignupResponse
import com.example.smartdtr_remade.models.Student
import com.example.smartdtr_remade.models.Teacher
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ApiService {

    interface DutyApi {
        @GET("duties/upcoming-students")
        fun getUpcomingDutiesStudent(@Query("student_id") studentId: String): Call<List<Duty>>

        @GET("duties/upcoming-teachers")
        fun getUpcomingDutiesTeacher(@Query("teacher_id") teacherId: String): Call<List<Duty>>

        @GET("duties/completed-students")
        fun getCompletedDutiesStudent(@Query("student_id") studentId: String): Call<List<Duty>>

        @GET("duties/completed-teachers")
        fun getCompletedDutiesTeacher(@Query("teacher_id") teacherId: String): Call<List<Duty>>

        @POST("duties")
        fun createDuty(@Body duty: Duty): Call<Duty>

        @PUT("duties/{id}")
        fun updateDuty(@Path("id") id: Int, @Body duty: Duty): Call<Duty>

        @DELETE("duties/{id}")
        fun deleteDuty(@Path("id") id: Int): Call<Void>

        @GET("duties/{id}")
        fun getDuty(@Path("id") dutyId: Int): Call<GetDuty>
    }

    interface StudentListApi {
        @GET("students/get") // Ensure this matches your API route
        fun getAllStudents(): Call<List<Student>>
    }

    @POST("/api/login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @PUT("/students/{id}") // Use the correct endpoint for password updates
    fun updateStudent(
        @Path("id") id: String,
        @Body request: ResetPasswordRequest
    ): Call<ResetPasswordResponse>

    @PUT("/teachers/{id}") // Use the correct endpoint for password updates
    fun updateTeacher(
        @Path("id") id: String,
        @Body request: ResetPasswordRequest
    ): Call<ResetPasswordResponse>

    @POST("changepassword")
    fun changePassword(@Body request: ResetPasswordRequest): Call<ResetPasswordResponse>

    @POST("signup")
    fun signup(@Body request: SignUpRequest): Call<SignupResponse>

    @POST("logout")
    fun logout(@Header("Authorization") token: String): Call<Void>

    // Companion object to create the Retrofit instance
    companion object {
        private const val BASE_URL =
            "http://10.0.2.2:8000/api/" // Ensure you have the correct base URL

        fun create(): ApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
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

        @GET("duties/{id}/students")
        fun getStudentsFromDuty(@Path("id") dutyId: Int): Call<List<Student>>
    }

}
