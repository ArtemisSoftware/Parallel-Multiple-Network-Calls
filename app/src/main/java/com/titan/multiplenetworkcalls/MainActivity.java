package com.titan.multiplenetworkcalls;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.titan.multiplenetworkcalls.adapter.RecyclerViewAdapter;
import com.titan.multiplenetworkcalls.api.CryptoCurrencyApi;
import com.titan.multiplenetworkcalls.models.Crypto;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.HttpException;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    Retrofit retrofit;

    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initCommunications();
        initRecyclerView();
        //callSingleEndpoint();
        callEndpoints();
    }


    private void callSingleEndpoint(){

        CryptoCurrencyApi cryptoCurrencyApi = retrofit.create(CryptoCurrencyApi.class);


        cryptoCurrencyApi.getCoinData("btc")
                .subscribeOn(Schedulers.io())
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
                        Timber.e("onError " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Timber.d("onComplete");
                    }
                });

    }


    private void callEndpoints() {

        CryptoCurrencyApi cryptoCurrencyApi = retrofit.create(CryptoCurrencyApi.class);

        Observable<Crypto> btcObservable = cryptoCurrencyApi.getCoinData("btc");

        Observable<Crypto> ethObservable = cryptoCurrencyApi.getCoinData("eth");

        Observable.merge(btcObservable, ethObservable)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Crypto>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Timber.d("onSubscribe");
                    }

                    @Override
                    public void onNext(Crypto markets) {
                        Timber.d("onNext: " + markets.toString());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e("onError " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Timber.d("onComplete");
                    }
                });


/*
        List<Observable<Crypto>> tasks = new ArrayList<>();
        tasks.add(cryptoCurrencyApi.getCoinData("btc"));
        tasks.add(cryptoCurrencyApi.getCoinData("eth"));


        Observable<List<Crypto.Market>> btcObservable = cryptoCurrencyApi.getCoinData("btc")
                .toList().toObservable();


        Observable<Observable<Crypto>> taskObservable = Observable // create a new Observable object
                .fromIterable(tasks) // apply 'fromIterable' operator
                .subscribeOn(Schedulers.io()) // designate worker thread (background)
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<Observable<Crypto>, Observable<Crypto>>() {
                    @Override
                    public Observable<Crypto> apply(Observable<Crypto> cryptoObservable) throws Exception {
                        return  Observable.fromIterable(cryptoObservable.);
                    }
                }); // designate observer thread (main thread)

        taskObservable.subscribe(new Observer<Observable<Crypto>>() {
            @Override
            public void onSubscribe(Disposable d) {
                Timber.d("onSubscribe");
            }

            @Override
            public void onNext(Observable<Crypto> cryptoObservable) {


                Timber.d("onNext: " + cryptoObservable.toString());
            }

            @Override
            public void onError(Throwable e) {
                Timber.e("onError " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Timber.d("onComplete");
            }
        });
        */
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

    private void initRecyclerView(){
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAdapter = new RecyclerViewAdapter();
        recyclerView.setAdapter(recyclerViewAdapter);
    }
}
