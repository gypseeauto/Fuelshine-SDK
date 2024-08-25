package com.gypsee.sdk.network;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitHelper {


    private String MAIN_URL = "http://ec2-13-54-51-114.ap-southeast-2.compute.amazonaws.com:8080";


    public NetworkActivity getNetworkActivity() {

        final Retrofit retrofit = createRetrofit();
        return retrofit.create(NetworkActivity.class);
    }


    private Retrofit createRetrofit() {


        return new Retrofit.Builder()
                .baseUrl(MAIN_URL)
                .addConverterFactory(GsonConverterFactory.create())
                //.addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // <- add this
                .client(createOkHttpClient())

                .build();
    }


    private OkHttpClient createOkHttpClient() {
        final OkHttpClient.Builder httpClient =
                new OkHttpClient.Builder();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        interceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);


        httpClient.addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        final Request original = chain.request();
                        final HttpUrl originalHttpUrl = original.url();

                        final HttpUrl url = originalHttpUrl.newBuilder()
                                .build();
                        final Request.Builder requestBuilder = original.newBuilder().addHeader("Content-Type", "application/json")
                                .url(url);

                        final Request request = requestBuilder.build();
                        return chain.proceed(request);
                    }
                });
//                .addInterceptor(interceptor);

        return httpClient.build();
    }

}
