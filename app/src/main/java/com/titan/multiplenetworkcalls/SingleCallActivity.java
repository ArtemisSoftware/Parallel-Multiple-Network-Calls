package com.titan.multiplenetworkcalls;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.titan.multiplenetworkcalls.adapter.RecyclerViewAdapter;
import com.titan.multiplenetworkcalls.api.CryptoCurrencyApi;
import com.titan.multiplenetworkcalls.models.Crypto;
import com.titan.multiplenetworkcalls.util.NetworkService;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;


public class SingleCallActivity extends AppCompatActivity {

    RecyclerViewAdapter recyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_calls);

        initRecyclerView();
        callSingleEndpoint();
    }

    private void callSingleEndpoint(){

        CryptoCurrencyApi cryptoCurrencyApi = NetworkService.getCurrencyApi();

        cryptoCurrencyApi.getCoinData("btc")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

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
                .toObservable()
                .subscribe(new Observer<List<Crypto.Market>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<Crypto.Market> markets) {

                        recyclerViewAdapter.setData(markets);
                        Timber.d("onNext");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getApplicationContext(), "ERROR IN FETCHING API RESPONSE. Try again", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onComplete() {
                        Timber.d("onComplete");
                    }
                });

    }



    private void initRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAdapter = new RecyclerViewAdapter();
        recyclerView.setAdapter(recyclerViewAdapter);
    }


}
