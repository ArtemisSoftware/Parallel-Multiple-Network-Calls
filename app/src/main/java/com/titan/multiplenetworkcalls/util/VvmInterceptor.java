package com.titan.multiplenetworkcalls.util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class VvmInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();
        Response response = chain.proceed(request);
        ResponseBody body = response.body();

        String responseString = body.string();

        JSONObject data = null;
        String lolo = null;
        try{
            int inicio = responseString.indexOf('{');
            int fim = responseString.indexOf("</string>");
            lolo = responseString.substring(inicio, fim);
            data = new JSONObject(responseString.substring(inicio, fim));
        }
        catch (JSONException e){
            e.printStackTrace();
        }


        MediaType contentType = response.body().contentType();
        ResponseBody newbody = ResponseBody.create(contentType, lolo);
        return response.newBuilder().body(newbody).build();

        //return null;
    }
}
