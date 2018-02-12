package com.depex.okeyclick.sp.api;

import retrofit2.Call;
public interface ApiListener<T>  {
    void success(Call<T> call,  T response , Object... objects);
}
