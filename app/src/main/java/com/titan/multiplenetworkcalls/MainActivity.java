package com.titan.multiplenetworkcalls;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.titan.multiplenetworkcalls.api.CryptoCurrencyApi;
import com.titan.multiplenetworkcalls.models.Crypto;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    Retrofit retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initCommunications();
        callSingleEndpoint();
    }


    private void callSingleEndpoint(){

        CryptoCurrencyApi cryptocurrencyService = retrofit.create(CryptoCurrencyApi.class);

        Observable<Crypto> cryptoObservable = cryptocurrencyService.getCoinData("btc");

        cryptoObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Crypto>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                        Timber.d("onSubscribe");
                    }

                    @Override
                    public void onNext(Crypto crypto) {
                        Timber.d("onNext: " + crypto.toString());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.d("onError " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Timber.d("onComplete");
                    }
                });
    }

    private void initCommunications(){

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Gson gson = new GsonBuilder().setLenient().create();

        retrofit = new Retrofit.Builder()
                .baseUrl(CryptoCurrencyApi.BASE_URL)
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }
}
