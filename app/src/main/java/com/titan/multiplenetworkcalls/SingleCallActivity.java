package com.titan.multiplenetworkcalls;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.titan.multiplenetworkcalls.adapter.RecyclerViewAdapter;
import com.titan.multiplenetworkcalls.api.CryptoCurrencyApi;
import com.titan.multiplenetworkcalls.models.Crypto;
import com.titan.multiplenetworkcalls.util.NetworkService;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
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
                .subscribe(new Observer<Crypto>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                        Timber.d("onSubscribe");
                    }

                    @Override
                    public void onNext(Crypto crypto) {
                        Timber.d("onNext: " + crypto.toString());

                        recyclerViewAdapter.setData(crypto.ticker.markets);
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



    private void initRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAdapter = new RecyclerViewAdapter();
        recyclerView.setAdapter(recyclerViewAdapter);
    }


}
