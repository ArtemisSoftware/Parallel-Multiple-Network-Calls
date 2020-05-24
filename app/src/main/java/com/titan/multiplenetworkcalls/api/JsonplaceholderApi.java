package com.titan.multiplenetworkcalls.api;

import com.titan.multiplenetworkcalls.models.Post;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;

public interface JsonplaceholderApi {

    String BASE_URL = "https://jsonplaceholder.typicode.com/";

    @GET("posts")
    Observable<List<Post>> getPosts();
}
