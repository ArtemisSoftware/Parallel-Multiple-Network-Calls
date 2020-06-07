package com.titan.multiplenetworkcalls.api;

import com.titan.multiplenetworkcalls.models.Post;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface JsonplaceholderApi {

    String BASE_URL = "https://jsonplaceholder.typicode.com/";

    @GET("posts")
    Observable<List<Post>> getPosts();

    @GET("posts?userId={id}")
    Observable<List<Post>> getPostsUserId(@Path("id") String id);


}
