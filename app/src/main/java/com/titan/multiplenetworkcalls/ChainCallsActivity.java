package com.titan.multiplenetworkcalls;

import com.titan.multiplenetworkcalls.api.CryptoCurrencyApi;
import com.titan.multiplenetworkcalls.models.Crypto;
import com.titan.multiplenetworkcalls.util.NetworkService;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class ChainCallsActivity extends MultipleActivity{

    private Observable<List<Crypto.Market>> baseObservable;

    @Override
    protected void initRequest() {

        initBaseObservable();
        chainCalls();
    }

    private void initBaseObservable(){
        CryptoCurrencyApi cryptoCurrencyApi = NetworkService.getCurrencyApi();


        baseObservable = cryptoCurrencyApi.getCoinData("btc")
                .map(new Function<Crypto, Observable<Crypto.Market>>() {

                    @Override
                    public Observable<Crypto.Market> apply(Crypto crypto) throws Exception {

                        //Transformar a lista de markets numa observable source
                        Timber.d("onMap");
                        return Observable.fromIterable(crypto.ticker.markets);
                    }
                })
                .flatMap(new Function<Observable<Crypto.Market>, Observable<List<Crypto.Market>>>() {
                    @Override
                    public Observable<List<Crypto.Market>> apply(Observable<Crypto.Market> marketObservable) throws Exception {

                        Timber.d("onflatMap");
                        return ethObservable;
                    }
                });
    }

/*
    private Observable<List<Crypto.Market>> secondObservable(){
        List<Observable<PhotoResponse>> requests = new ArrayList<>();
        for(String id : photoIds) {
            requests.add(repository.searchPhoto(id));
        }
        Observable<List<Picture>> observable = Observable.zip(
                requests,
                new Function<Object[], List<Picture>>() {
                    @Override
                    public List<Picture> apply(Object[] photos) throws Exception {
                        Timber.d("apply photo response: " + photos);
                        List<Picture> pictures = new ArrayList<>();
                        //for (PhotoResponse photo : photos) {
                        for (int i = 0; i < photos.length; ++i) {
                            PhotoResponse photo = ((PhotoResponse) photos[i]);
                            pictures.add(new Picture(photo));
                        }
                        return pictures;
                    }
                });
        return observable;
    }

    private void secondObservable(){


    }
*/
    private void chainCalls(){

        baseObservable

                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(


                        new Observer<List<Crypto.Market>>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(List<Crypto.Market> markets) {

                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onComplete() {

                            }
                        }
                );
    }

    /*
    public void searchGallery(String nsid) {
        Timber.d("Searching user " + nsid + " page " + pageNumber + " list of pictures");
        isPerformingQuery = true;
        repository.searchPhotoList(nsid, String.valueOf(pageNumber))
                .map(new Function<PhotoListResponse, List<String>>() {
                    @Override
                    public List<String> apply(PhotoListResponse response) throws Exception {
                        List<String> photoIds = new ArrayList<>();
                        pages = response.photos.pages;
                        for (PhotoListResponse.Photo photo : response.photos.pictures) {
                            photoIds.add(photo.id);
                        }
                        return photoIds; // B.
                    }
                })
                .flatMap(new Function<List<String>, Observable<List<Picture>>>() {
                    @Override
                    public Observable<List<Picture>> apply(List<String> photoIds) throws Exception {
                        return getPicturesObservable(photoIds);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new Observer<List<Picture>>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        disposables.add(disposable);
                        galleryLiveData.setValue(ApiResponse.loading());
                    }
                    @Override
                    public void onNext(List<Picture> pictures) {
                        Timber.d("onNext: " + pictures.toString());
                        galleryLiveData.setValue(ApiResponse.success(pictures));
                    }
                    @Override
                    public void onError(Throwable throwable) {
                        Timber.e("Error on serch user: " + throwable.getMessage());
                        galleryLiveData.setValue(ApiResponse.error(throwable.getMessage()));
                    }
                    @Override
                    public void onComplete() {
                        disposables.clear();
                        isPerformingQuery = false;
                    }
                });
    }
    private Observable<List<Picture>> getPicturesObservable(List<String> photoIds){
        List<Observable<PhotoResponse>> requests = new ArrayList<>();
        for(String id : photoIds) {
            requests.add(repository.searchPhoto(id));
        }
        Observable<List<Picture>> observable = Observable.zip(
                requests,
                new Function<Object[], List<Picture>>() {
                    @Override
                    public List<Picture> apply(Object[] photos) throws Exception {
                        Timber.d("apply photo response: " + photos);
                        List<Picture> pictures = new ArrayList<>();
                        //for (PhotoResponse photo : photos) {
                        for (int i = 0; i < photos.length; ++i) {
                            PhotoResponse photo = ((PhotoResponse) photos[i]);
                            pictures.add(new Picture(photo));
                        }
                        return pictures;
                    }
                });
        return observable;
    }
    */


}
