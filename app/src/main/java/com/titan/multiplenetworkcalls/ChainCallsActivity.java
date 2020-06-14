package com.titan.multiplenetworkcalls;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.titan.multiplenetworkcalls.adapter.PostRecyclerViewAdapter;
import com.titan.multiplenetworkcalls.api.CryptoCurrencyApi;
import com.titan.multiplenetworkcalls.api.JsonplaceholderApi;
import com.titan.multiplenetworkcalls.models.Crypto;
import com.titan.multiplenetworkcalls.models.Post;
import com.titan.multiplenetworkcalls.util.NetworkService;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class ChainCallsActivity extends MultipleActivity{

    private Observable<List<Post>> postsObservable;
    PostRecyclerViewAdapter postsRecyclerViewAdapter;

    @Override
    protected void initRequest() {

        initPostRecyclerView();
        initBaseObservable();
        chainCalls();
    }

    private void initBaseObservable(){
        JsonplaceholderApi jsonplaceholderApi = NetworkService.getJsonplaceholderApi();


        postsObservable = jsonplaceholderApi.getPosts()
                .map(new Function<List<Post>, List<String>>() {
                    @Override
                    public List<String> apply(List<Post> posts) throws Exception {

                        List<String> ids = new ArrayList<>();

                        Random r = new Random();
                        int low = 1, high = 10;
                        ids.add((r.nextInt(high-low) + low) + "");
                        ids.add((r.nextInt(high-low) + low) + "");

                        return ids; // B.
                    }
                })
                .flatMap(new Function<List<String>, Observable<List<Post>>>() {
                    @Override
                    public Observable<List<Post>> apply(List<String> ids) throws Exception {
                        return searchPostsById(ids);
                    }
                });
    }

    private Observable<List<Post>> searchPostsById(List<String> ids){

        JsonplaceholderApi jsonplaceholderApi = NetworkService.getJsonplaceholderApi();

        List<Observable<List<Post>>> requests = new ArrayList<>();

        for(String id : ids) {
            requests.add(jsonplaceholderApi.getPostsUserId(id));
        }

        Observable<List<Post>> observable = Observable.zip(requests,

                new Function<Object[], List<Post>>() {
                    @Override
                    public List<Post> apply(Object[] responses) throws Exception {

                        Timber.d("apply: posts response: " + responses);

                        List<Post> posts = new ArrayList<>();

                        for (int i = 0; i < responses.length; ++i) {
                            posts.add(((Post) responses[i]));
                        }

                        return posts;
                    }
                });
        return observable;
    }


    private void chainCalls(){

        postsObservable

                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(

                        new Observer<List<Post>>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(List<Post> posts) {

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


    private void initPostRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        postsRecyclerViewAdapter = new PostRecyclerViewAdapter();
        recyclerView.setAdapter(postsRecyclerViewAdapter);
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
