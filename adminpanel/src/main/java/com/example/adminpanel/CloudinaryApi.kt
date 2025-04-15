package com.example.adminpanel

import retrofit2.Call
import retrofit2.http.*

interface CloudinaryApi {
    @FormUrlEncoded
    @POST("v1_1/dybfgpc5i/image/destroy")
    fun deleteImage(
        @Field("public_id") publicId: String,
        @Field("api_key") apiKey: String,
        @Field("timestamp") timestamp: Long,
        @Field("signature") signature: String
    ): Call<CloudinaryResponse>
}
