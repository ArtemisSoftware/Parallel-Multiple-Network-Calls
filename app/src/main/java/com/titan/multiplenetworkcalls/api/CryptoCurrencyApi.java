package com.titan.multiplenetworkcalls.api;

import com.titan.multiplenetworkcalls.models.Crypto;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface CryptoCurrencyApi {

    String BASE_URL = "https://api.cryptonator.com/api/full/";

    @GET("{coin}-usd")
    Observable<Crypto> getCoinData(@Path("coin") String coin);

    @GET("{coin}-usd")
    Observable<List<Crypto>> getCoinData_(@Path("coin") String coin);
}
