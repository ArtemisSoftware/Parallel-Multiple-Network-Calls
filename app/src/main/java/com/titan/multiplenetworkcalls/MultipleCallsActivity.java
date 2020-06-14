package com.titan.multiplenetworkcalls;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.titan.multiplenetworkcalls.adapter.CryptoRecyclerViewAdapter;
import com.titan.multiplenetworkcalls.api.CryptoCurrencyApi;
import com.titan.multiplenetworkcalls.api.JsonplaceholderApi;
import com.titan.multiplenetworkcalls.models.Crypto;
import com.titan.multiplenetworkcalls.models.Post;
import com.titan.multiplenetworkcalls.util.NetworkService;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Function3;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class MultipleCallsActivity extends AppCompatActivity {

    CryptoRecyclerViewAdapter cryptoRecyclerViewAdapter;

    private Observable<List<Crypto.Market>> btcObservable, ethObservable, errorObservable;
    private Observable<List<List<Post>>> jsonObservable;
    private List<Observable<List<Crypto.Market>>> requests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_calls);

        initRecyclerView();
        initObservables();
        //initErrorObservables();

        //callMultipleEndpoint();
        //zipMultipleCallsEndpoint();

        zipMultipleDifferentApiCallsEndpoints();
    }

    private void initObservables(){

        CryptoCurrencyApi cryptoCurrencyApi = NetworkService.getCurrencyApi();
        JsonplaceholderApi jsonplaceholderApi = NetworkService.getJsonplaceholderApi();


        btcObservable = cryptoCurrencyApi.getCoinData("btc")
                .map(new Function<Crypto, Observable<Crypto.Market>>() {

                    @Override
                    public Observable<Crypto.Market> apply(Crypto crypto) throws Exception {

                        //Transformar a lista de markets numa observable source
                        Timber.d("onMap");
                        return Observable.fromIterable(crypto.ticker.markets);
                    }
                })

                .flatMap(new Function<Observable<Crypto.Market>, ObservableSource<Crypto.Market>>() {
                    @Override
                    public ObservableSource<Crypto.Market> apply(Observable<Crypto.Market> marketObservable) throws Exception {

                        Timber.d("onflatMap");
                        return marketObservable;
                    }
                })
                .filter(new Predicate<Crypto.Market>() {
                    @Override
                    public boolean test(Crypto.Market market) throws Exception {

                        Timber.d("onfilter: " + market.toString());

                        //filtrar cada market emitido pelo flatmap
                        market.coinName = "btc";
                        return true;
                    }
                })
                .toList()
                .toObservable();

        ethObservable = cryptoCurrencyApi.getCoinData("eth")
                .map(new Function<Crypto, Observable<Crypto.Market>>() {

                    @Override
                    public Observable<Crypto.Market> apply(Crypto crypto) throws Exception {

                        //Transformar a lista de markets numa observable source
                        Timber.d("onMap");
                        return Observable.fromIterable(crypto.ticker.markets);
                    }
                })

                .flatMap(new Function<Observable<Crypto.Market>, ObservableSource<Crypto.Market>>() {
                    @Override
                    public ObservableSource<Crypto.Market> apply(Observable<Crypto.Market> marketObservable) throws Exception {

                        Timber.d("onflatMap");
                        return marketObservable;
                    }
                })
                .filter(new Predicate<Crypto.Market>() {
                    @Override
                    public boolean test(Crypto.Market market) throws Exception {

                        Timber.d("onfilter: " + market.toString());

                        //filtrar cada market emitido pelo flatmap
                        market.coinName = "eth";
                        return true;
                    }
                })
                .toList()
                .toObservable();


        jsonObservable = jsonplaceholderApi.getPosts()
                /*
                .map(new Function<List<Post>, List<Post>>() {
                    @Override
                    public List<Post> apply(List<Post> posts) throws Exception {
                        return posts;
                    }
                })
                .flatMap(new Function<List<Post>, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(List<Post> posts) throws Exception {
                        return null;
                    }
                })
                .filter(new Predicate<Object>() {
                    @Override
                    public boolean test(Object o) throws Exception {
                        return false;
                    }
                })
                */
                .toList()
                .toObservable();

        requests = new ArrayList<>();
        requests.add(btcObservable);
        requests.add(ethObservable);

    }



    private void initErrorObservables(){

        CryptoCurrencyApi cryptoCurrencyApi = NetworkService.getCurrencyApi();

        errorObservable = cryptoCurrencyApi.getError()
                .map(new Function<Crypto, Observable<Crypto.Market>>() {

                    @Override
                    public Observable<Crypto.Market> apply(Crypto crypto) throws Exception {

                        //Transformar a lista de markets numa observable source
                        Timber.d("onMap");
                        return Observable.fromIterable(crypto.ticker.markets);
                    }
                })

                .flatMap(new Function<Observable<Crypto.Market>, ObservableSource<Crypto.Market>>() {
                    @Override
                    public ObservableSource<Crypto.Market> apply(Observable<Crypto.Market> marketObservable) throws Exception {

                        Timber.d("onflatMap");
                        return marketObservable;
                    }
                })
                .filter(new Predicate<Crypto.Market>() {
                    @Override
                    public boolean test(Crypto.Market market) throws Exception {

                        Timber.d("onfilter: " + market.toString());

                        //filtrar cada market emitido pelo flatmap
                        market.coinName = "btc";
                        return true;
                    }
                })
                .toList()
                .toObservable();


        if(requests == null){
            requests = new ArrayList<>();
        }
        requests.add(errorObservable);

    }






    private void callMultipleEndpoint(){

        Observable.merge(requests)

                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Observer<List<Crypto.Market>>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(List<Crypto.Market> markets) {
                    cryptoRecyclerViewAdapter.setData(markets);
                    Timber.d("onNext");
                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onComplete() {

                }
            });

    }



    private void zipMultipleCallsEndpoint(){

        Observable.zip(requests,
                new Function<Object[], List<Crypto.Market>>() {
                    @Override
                    public List<Crypto.Market> apply(Object[] objects) throws Exception {

                        Timber.d("apply: " + objects);

                        ArrayList<Crypto.Market> mm = new ArrayList<>();
                        for (int i = 0; i < objects.length; ++i) {
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

                        cryptoRecyclerViewAdapter.setData(cryptos);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.d("onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Timber.d("onComplete: ");
                    }
                });
        
        
        /*
        zio
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<Object>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Object o) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        })*/
/*
        lolo
                .subscribe(new Observer<List<Crypto.Market>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Timber.d("onSubscribe: ");
                    }

                    @Override
                    public void onNext(List<Crypto.Market> cryptos) {
                        Timber.d("onNext: " + cryptos);

                        cryptoRecyclerViewAdapter.setData(cryptos);

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
        */
    }


    private void zipMultipleDifferentApiCallsEndpoints(){

        Observable.zip(ethObservable, jsonObservable, ethObservable, new Function3<List<Crypto.Market>, List<List<Post>>, List<Crypto.Market>, Object>() {
            @Override
            public List<String> apply(List<Crypto.Market> markets, List<List<Post>> lists, List<Crypto.Market> markets2) throws Exception {
                List<String> list = new ArrayList();
                list.add(markets.size() + " markets");
                list.add(lists.size() + " posts");
                list.add(markets2.size() + " markets2");
                return list;
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Object o) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    private void initRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cryptoRecyclerViewAdapter = new CryptoRecyclerViewAdapter();
        recyclerView.setAdapter(cryptoRecyclerViewAdapter);
    }
}
