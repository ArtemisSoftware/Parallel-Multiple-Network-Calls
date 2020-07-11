package com.titan.multiplenetworkcalls.api;

import com.titan.multiplenetworkcalls.models.Post;
import com.titan.multiplenetworkcalls.models.TypeVvm;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;

public interface VivamaisApi {


    String BASE_URL = "http://www.vivamais.com/tablet_hsa_ws/service.asmx/";

    @GET("GetTiposAnomalia?dataT=")
    Observable<TypeVvm> getTiposAnomalia();

    @GET("GetCrossSellingTpTipo?dataT=")
    Observable<TypeVvm> getCrossSellingTpTipo();

    @GET("GetUtilizadores?dataT=")
    Observable<TypeVvm> getUtilizadores();
}
