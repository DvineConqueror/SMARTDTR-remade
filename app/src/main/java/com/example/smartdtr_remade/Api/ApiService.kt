package com.example.smartdtr_remade.Api

import com.example.smartdtr_remade.models.Duty
import retrofit2.Call
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

}