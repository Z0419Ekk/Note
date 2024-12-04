package com.example.note;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import com.google.gson.Gson;

import java.io.IOException;

public class JokeFetcher {
    private static final String API_URL = "http://v.juhe.cn/joke/content/list.php?sort=&page=&pagesize=1&time=1418816972&key=3f3c78283a95500c46ac93aa1f18f897";

    private OkHttpClient client;
    public Gson gson;

    public JokeFetcher() {
        client = new OkHttpClient();
        gson = new Gson();
    }

    public void fetchJoke(Callback callback) {
        Request request = new Request.Builder()
                .url(API_URL)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static class JokeResponse {
        String reason;
        Result result;
        int error_code;

        public static class Result {
            Data[] data;

            public static class Data {
                String content;
                String hashId;
                long unixtime;
                String updatetime;
            }
        }
    }
}

