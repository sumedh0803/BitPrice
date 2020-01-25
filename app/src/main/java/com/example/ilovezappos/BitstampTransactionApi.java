package com.example.ilovezappos;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface BitstampTransactionApi {

    @GET("api/v2/transactions/btcusd/")
    Call<List<Transaction>> getTransactions();
}
