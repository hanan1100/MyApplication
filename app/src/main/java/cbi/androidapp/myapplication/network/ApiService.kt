package cbi.androidapp.myapplication.network

import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("login")
    fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @GET("list-items")
    fun getListItems(
        @Header("Authorization") token: String
    ): Call<ListItemsResponse>

}
