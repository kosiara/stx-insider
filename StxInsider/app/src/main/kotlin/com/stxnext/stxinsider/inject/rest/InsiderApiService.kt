package com.stxnext.stxinsider.inject.rest

import android.util.Log
import com.stxnext.stxinsider.model.SliderItem
import dagger.Module
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import javax.inject.Inject

/**
 * Created by bkosarzycki on 01.03.16.
 */

@Module
class InsiderApiService {

    @Inject lateinit var service: InsiderApiResource

    @Inject constructor() {}


    fun getTeamsAsync(callback: Callback<List<SliderItem>>) {
        val call = service.getTeams()
        call.enqueue(callback)
    }

    fun getTeamsAsync(onResponse: (List<SliderItem>) -> Unit, onError: () -> Unit)  {
        val callback = object : Callback<List<SliderItem>> {
            override fun onResponse(p0: Call<List<SliderItem>>?, response: Response<List<SliderItem>>?) {
                val body = response?.body()
                body?.let {
                    onResponse.invoke(body);
                } ?: onFailure(null, Throwable("Null BODY in RESPONSE"))
            }

            override fun onFailure(p0: Call<List<SliderItem>>?, throwable: Throwable?) {
                Log.e(InsiderApiService::class.java.simpleName, "Error in REST call: " + throwable.toString())
                onError.invoke()
            }
        }

        val call = service.getTeams()
        call.enqueue(callback)
    }
}