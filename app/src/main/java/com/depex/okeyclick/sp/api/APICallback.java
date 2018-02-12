package com.depex.okeyclick.sp.api;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public  class APICallback implements Callback<JsonObject>  {

    ApiListener<JsonObject> apiListener;

    Object[]arr;
    public APICallback(ApiListener<JsonObject> apiListener, Object... objects){
        this.apiListener=apiListener;
        this.arr=objects;
    }

    @Override
    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
        apiListener.success(call, response.body(),arr);
    }

    @Override
    public void onFailure(Call<JsonObject> call, Throwable t) {
        Log.e("retrofit2Error", t.toString());
    }

}
