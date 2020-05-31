package com.titan.multiplenetworkcalls;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.titan.multiplenetworkcalls.api.JsonplaceholderApi;
import com.titan.multiplenetworkcalls.models.Crypto;
import com.titan.multiplenetworkcalls.models.Post;
import com.titan.multiplenetworkcalls.util.NetworkService;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function3;
import io.reactivex.schedulers.Schedulers;

public class MultipleApiCallsActivity extends MultipleActivity {

    private Observable<List<List<Post>>> jsonObservable;

    @Override
    protected void initRequest() {

        initJsonPlaceholderObservable();

        zipMultipleDifferentApiCallsEndpoints();
    }

    private void initJsonPlaceholderObservable() {

        JsonplaceholderApi jsonplaceholderApi = NetworkService.getJsonplaceholderApi();


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


}
