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

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    Retrofit retrofit, retrofit_erro;

    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initCommunications();
        initRecyclerView();
        //callSingleEndpoint();
        //callEndpoints();
        //callForResponseCode();
        callEndpoints_v2();
    }

    private void callEndpoints_v2(){

        CryptoCurrencyApi cryptoCurrencyApi = retrofit.create(CryptoCurrencyApi.class);

        CryptoCurrencyApi cryptoCurrencyApi_erro = retrofit_erro.create(CryptoCurrencyApi.class);

        List<Observable<?>> requests = new ArrayList<>();

        // Make a collection of all requests you need to call at once, there can be any number of requests, not only 3. You can have 2 or 5, or 100.
        //requests.add(cryptoCurrencyApi.getCoinData("btc"));
        //requests.add(cryptoCurrencyApi.getCoinData("eth"));
        requests.add(cryptoCurrencyApi.getCoinData("eth"));
        requests.add(cryptoCurrencyApi_erro.getCoinData("eth"));
        //requests.add(cryptoCurrencyApi_erro.getCoinData("eth"));

        // Zip all requests with the Function, which will receive the results.
        Observable.zip(
                requests,
                new Function<Object[], Object>() {
                    @Override
                    public Object apply(Object[] objects) throws Exception {
                        // Objects[] is an array of combined results of completed requests

                        // do something with those results and emit new event

                        Timber.d("apply: " + objects);
                        return new Object();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                // After all requests had been performed the next observer will receive the Object, returned from Function
                .subscribe(
                        // Will be triggered if all requests will end successfully (4xx and 5xx also are successful requests too)
                        new Consumer<Object>() {
                            @Override
                            public void accept(Object o) throws Exception {
                                //Do something on successful completion of all requests

                                Timber.d("accept: " + o);
                            }
                        },

                        // Will be triggered if any error during requests will happen
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable e) throws Exception {
                                //Do something on error completion of requests

                                Timber.d("accept-error: " + e);
                            }
                        }
                )
                /*
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Timber.d("onSubscribe: " + d);
                    }

                    @Override
                    public void onNext(Object o) {
                        Timber.d("onNext: " + o);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.d("onError: " + e);
                    }

                    @Override
                    public void onComplete() {
                        Timber.d("onComplete: ");
                    }
                })
*/

        ;
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


        List<Observable<?>> requests = new ArrayList<>();

        // Make a collection of all requests you need to call at once, there can be any number of requests, not only 3. You can have 2 or 5, or 100.

        Observable<List<Object>> responseOneObservable_1 = cryptoCurrencyApi.getCoinData("btc")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<Crypto, Observable<Crypto.Market>>() {
                    @Override
                    public Observable<Crypto.Market> apply(Crypto crypto) throws Exception {
                        return Observable.fromIterable(crypto.ticker.markets);
                    }
                })
                .flatMap(new Function<Observable<Crypto.Market>, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Observable<Crypto.Market> marketObservable) throws Exception {
                        return marketObservable;
                    }
                })
                .filter(new Predicate<Object>() {
                    @Override
                    public boolean test(Object o) throws Exception {
                        ((Crypto.Market) o).coinName = "btc";
                        return true;
                    }
                })
                .toList().toObservable();

        Observable<List<Object>> responseOneObservable_2 = cryptoCurrencyApi.getCoinData("eth")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<Crypto, Observable<Crypto.Market>>() {
                    @Override
                    public Observable<Crypto.Market> apply(Crypto crypto) throws Exception {
                        return Observable.fromIterable(crypto.ticker.markets);
                    }
                })
                .flatMap(new Function<Observable<Crypto.Market>, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Observable<Crypto.Market> marketObservable) throws Exception {
                        return marketObservable;
                    }
                })
                .filter(new Predicate<Object>() {
                    @Override
                    public boolean test(Object o) throws Exception {
                        ((Crypto.Market) o).coinName = "eth";
                        return true;
                    }
                })
                .toList().toObservable();


        requests.add(responseOneObservable_1);
        requests.add(responseOneObservable_2);



         Observable.zip(requests,
                 new Function<Object[],  List<Crypto.Market>>() {
                    @Override
                    public  List<Crypto.Market> apply(Object[] objects) throws Exception {

                        Timber.d("apply: " + objects);

                        ArrayList<Crypto.Market> mm = new ArrayList<>();
                        for(int i = 0; i < objects.length; ++i){
                            mm.addAll((ArrayList<Crypto.Market>) objects[i]);
                        }

                        return mm;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Crypto.Market>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Timber.d("onSubscribe: ");
                    }

                    @Override
                    public void onNext(List<Crypto.Market> cryptos) {
                        Timber.d("onNext: " + cryptos);

                        recyclerViewAdapter.setData(cryptos);

                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.d("onError: " + e);
                    }

                    @Override
                    public void onComplete() {
                        Timber.d("onComplete: ");
                    }
                });
    }


    private void callForResponseCode(){

        CryptoCurrencyApi cryptoCurrencyApi = retrofit.create(CryptoCurrencyApi.class);
/*
        cryptoCurrencyApi.getCoinDataDiff("btc")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Response<Crypto>>() {
                    @Override
                    public void onSubscribe(Subscription s) {

                    }

                    @Override
                    public void onNext(Response<Crypto> cryptoResponse) {

                    }

                    @Override
                    public void onError(Throwable t) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
        */


        //cryptoCurrencyApi.getCoinDataDiff("btc")

        List<Observable<?>> requests = new ArrayList<>();

        requests.add(cryptoCurrencyApi.getCoinDataDiff("btc"));
        requests.add(cryptoCurrencyApi.getCoinDataDiff("eth"));
        //Observable<Response<Crypto>> observable_1 = cryptoCurrencyApi.getCoinDataDiff("btc");
        //Observable<Response<Crypto>> observable_2 = cryptoCurrencyApi.getCoinDataDiff("btc");

        Observable.zip(requests,
                new Function<Object[],  List<Crypto.Market>>() {
                    @Override
                    public  List<Crypto.Market> apply(Object[] objects) throws Exception {

                        Timber.d("apply: " + objects);

                        ArrayList<Crypto.Market> mm = new ArrayList<>();
                        for(int i = 0; i < objects.length; ++i){
                            mm.addAll((ArrayList<Crypto.Market>) ((Response) objects[i]).body());
                        }

                        return mm;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Crypto.Market>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Timber.d("onSubscribe: ");
                    }

                    @Override
                    public void onNext(List<Crypto.Market> cryptos) {
                        Timber.d("onNext: " + cryptos);

                        recyclerViewAdapter.setData(cryptos);

                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.d("onError: " + e);
                    }

                    @Override
                    public void onComplete() {
                        Timber.d("onComplete: ");
                    }
                });


        /* funciona
        observable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableObserver<Response<Crypto>>() {
                        @Override
                        public void onNext(Response<Crypto> cryptoResponse) {
                            Timber.d("onNext: %s", cryptoResponse.code());
                        }

                        @Override
                        public void onError(Throwable e) {
                            Timber.e("onError: %" + e.toString());
                        }

                        @Override
                        public void onComplete() {
                            Timber.d("onComplete");
                        }
                    });
   */


        /*
        new Subscriber<Response<StartupResponse>>() {
            @Override
            public void onCompleted() {}

            @Override
            public void onError(Throwable e) {
                Timber.e(e, "onError: %", e.toString());

                // network errors, e. g. UnknownHostException, will end up here
            }

            @Override
            public void onNext(Response<StartupResponse> startupResponseResponse) {
                Timber.d("onNext: %s", startupResponseResponse.code());

                // HTTP errors, e. g. 404, will end up here!
            }
        }
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

        retrofit_erro = new Retrofit.Builder()
                .baseUrl("https://api.cryptonator.com/sdfapi/full/" + "fake/")
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
