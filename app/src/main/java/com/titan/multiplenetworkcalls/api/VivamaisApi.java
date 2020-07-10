package com.titan.multiplenetworkcalls.api;

import com.titan.multiplenetworkcalls.models.Post;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;

public interface VivamaisApi {


    String BASE_URL = "http://www.vivamais.com/tablet_hsa_ws/service.asmx/GetTiposAnomalia?dataT";

    @GET("")
    Observable<List<Post>> getType();
}
