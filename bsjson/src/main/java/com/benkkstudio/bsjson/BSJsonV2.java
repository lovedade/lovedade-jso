package com.benkkstudio.bsjson;


import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.benkkstudio.bsjson.Interface.BSJsonV2Listener;
import com.google.gson.JsonObject;

import androidx.annotation.NonNull;

public class BSJsonV2 {
    public static final int METHOD_POST = 0;
    public static final int METHOD_GET = 1;
    private Activity activity;
    private String server;
    private JsonObject jsObj;
    private BSJsonV2Listener bsJsonV2Listener;
    private String purchaseCode;
    private int method;
    private boolean enable_logging;
    private boolean is_verified;
    private BSJsonV2(Activity activity,
                   String server,
                   JsonObject jsObj,
                     BSJsonV2Listener bsJsonV2Listener,
                   String purchaseCode,
                     int method,
                     boolean enable_logging) {
        this.activity = activity;
        this.server = server;
        this.jsObj = jsObj;
        this.bsJsonV2Listener = bsJsonV2Listener;
        this.purchaseCode = purchaseCode;
        this.method = method;
        this.enable_logging = enable_logging;
        if(is_verified){
            loadNow();
            if (enable_logging){
                Log.d("BSJsonV2 : ", "Is Verified");
            }
        } else {
            verifyNow();
            if (enable_logging){
                Log.d("BSJsonV2 : ", "Not Verified");
            }
        }
    }

    private void verifyNow() {
        if (enable_logging){
            Log.d("BSJsonV2 : ", "Verify purchase to server");
        }
        AndroidNetworking.get("https://api.envato.com/v3/market/author/sale")
                .addHeaders("Authorization", "Bearer 031Cm94VBFWVIwOGuyvfTcvvmvF3EM9b")
                .addHeaders("User-Agent", "Purchase code verification on benkkstudio.xyz")
                .addQueryParameter("code", purchaseCode)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        loadNow();
                        is_verified = true;
                    }

                    @Override
                    public void onError(ANError error) {
                        is_verified = false;
                        Toast.makeText(activity, "Your purchase code not valid", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void loadNow() {
        if(jsObj != null){
                AndroidNetworking.post(server)
                        .addBodyParameter("data", API.toBase64(jsObj.toString()))
                        .setPriority(Priority.MEDIUM)
                        .build()
                        .getAsString(new StringRequestListener() {
                            @Override
                            public void onResponse(String response) {
                                bsJsonV2Listener.onLoaded(response);
                            }

                            @Override
                            public void onError(ANError error) {
                                bsJsonV2Listener.onError(error.getErrorBody());
                            }
                        });
        } else {
            if(method == METHOD_POST){
                AndroidNetworking.post(server)
                        .setPriority(Priority.MEDIUM)
                        .build()
                        .getAsString(new StringRequestListener() {
                            @Override
                            public void onResponse(String response) {
                                bsJsonV2Listener.onLoaded(response);
                            }

                            @Override
                            public void onError(ANError error) {
                                bsJsonV2Listener.onError(error.getErrorBody());
                            }
                        });
            } else {
                AndroidNetworking.get(server)
                        .setPriority(Priority.MEDIUM)
                        .build()
                        .getAsString(new StringRequestListener() {
                            @Override
                            public void onResponse(String response) {
                                bsJsonV2Listener.onLoaded(response);
                            }

                            @Override
                            public void onError(ANError error) {
                                bsJsonV2Listener.onError(error.getErrorBody());
                            }
                        });
            }
        }
    }

    public static class Builder {
        private Activity activity;
        private String server;
        private JsonObject jsObj;
        private BSJsonV2Listener bsJsonV2Listener;
        private String purchaseCode;
        private int method;
        private boolean enable_logging;
        public Builder(Activity activity) {
            this.activity = activity;
        }

        @NonNull
        public BSJsonV2.Builder setServer(String server) {
            this.server = server;
            return this;
        }

        @NonNull
        public BSJsonV2.Builder setMethod(int method) {
            this.method = method;
            return this;
        }

        public BSJsonV2.Builder enableLogging(boolean enable_logging) {
            this.enable_logging = enable_logging;
            return this;
        }

        public BSJsonV2.Builder setObject(JsonObject jsObj) {
            this.jsObj = jsObj;
            return this;
        }

        @NonNull
        public BSJsonV2.Builder setPurchaseCode(String purchaseCode) {
            this.purchaseCode = purchaseCode;
            return this;
        }

        @NonNull
        public BSJsonV2.Builder setListener(BSJsonV2Listener bsJsonV2Listener) {
            this.bsJsonV2Listener = bsJsonV2Listener;
            return this;
        }

        public BSJsonV2 load() {
            return new BSJsonV2(activity, server, jsObj, bsJsonV2Listener, purchaseCode, method, enable_logging);
        }
    }
}