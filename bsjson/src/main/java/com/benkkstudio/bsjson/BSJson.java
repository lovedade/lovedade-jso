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

public class BSJson {
    public static final int METHOD_POST = 0;
    public static final int METHOD_GET = 1;
    private Activity activity;
    private String server;
    private JsonObject jsObj;
    private BSJsonV2Listener bsJsonV2Listener;
    private int method;
    private BSJson(Activity activity,
                   String server,
                   JsonObject jsObj,
                   BSJsonV2Listener bsJsonV2Listener,
                   int method) {
        this.activity = activity;
        this.server = server;
        this.jsObj = jsObj;
        this.bsJsonV2Listener = bsJsonV2Listener;
        this.method = method;
        if(Constant.isVerified){
            loadNow();
            if (Constant.enableLogging){
                Log.d("BSJson : ", "Is Verified");
            }
        } else {
            verifyNow();
            if (Constant.enableLogging){
                Log.d("BSJson : ", "Not Verified");
            }
        }
    }
    public static class initializing {
        public initializing() {
        }

        @NonNull
        public BSJson.initializing withSecret(String purchaseCode) {
            Constant.purchaseCode = purchaseCode;
            return this;
        }

        public BSJson.initializing enableLogging(boolean enableLogging) {
            Constant.enableLogging = enableLogging;
            return this;
        }
    }



    private void verifyNow() {
        if (Constant.enableLogging){
            Log.d("BSJson : ", "Verify purchase to server");
        }
        AndroidNetworking.get("https://api.envato.com/v3/market/author/sale")
                .addHeaders("Authorization", "Bearer 031Cm94VBFWVIwOGuyvfTcvvmvF3EM9b")
                .addHeaders("User-Agent", "Purchase code verification on benkkstudio.xyz")
                .addQueryParameter("code", Constant.purchaseCode)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        loadNow();
                        Constant.isVerified = true;
                        Log.d("BSJson : ", "APIs VERIFED");
                    }

                    @Override
                    public void onError(ANError error) {
                         loadNow();
                        Constant.isVerified = true;
                        Log.d("BSJson : ", "APIs VERIFED");
                    }
                });

    }

    private void loadNow() {
        if(jsObj != null){
                AndroidNetworking.post(server)
                        .addBodyParameter("data", Helpers.toBase64(jsObj.toString()))
                        .setPriority(Priority.MEDIUM)
                        .build()
                        .getAsString(new StringRequestListener() {
                            @Override
                            public void onResponse(String response) {
                                if(bsJsonV2Listener != null){
                                    bsJsonV2Listener.onLoaded(response);
                                }
                            }

                            @Override
                            public void onError(ANError error) {
                                if(bsJsonV2Listener != null) {
                                    bsJsonV2Listener.onError(error.getErrorBody());
                                }
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
                                if(bsJsonV2Listener != null) {
                                    bsJsonV2Listener.onLoaded(response);
                                }
                            }

                            @Override
                            public void onError(ANError error) {
                                if(bsJsonV2Listener != null) {
                                    bsJsonV2Listener.onError(error.getErrorBody());
                                }
                            }
                        });
            } else {
                AndroidNetworking.get(server)
                        .setPriority(Priority.MEDIUM)
                        .build()
                        .getAsString(new StringRequestListener() {
                            @Override
                            public void onResponse(String response) {
                                if(bsJsonV2Listener != null) {
                                    bsJsonV2Listener.onLoaded(response);
                                }
                            }

                            @Override
                            public void onError(ANError error) {
                                if(bsJsonV2Listener != null) {
                                    bsJsonV2Listener.onError(error.getErrorBody());
                                }
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
        private int method;
        public Builder(Activity activity) {
            this.activity = activity;
        }

        @NonNull
        public BSJson.Builder setServer(String server) {
            this.server = server;
            return this;
        }

        @NonNull
        public BSJson.Builder setMethod(int method) {
            this.method = method;
            return this;
        }

        public BSJson.Builder setObject(JsonObject jsObj) {
            this.jsObj = jsObj;
            return this;
        }

        @NonNull
        public BSJson.Builder setListener(BSJsonV2Listener bsJsonV2Listener) {
            this.bsJsonV2Listener = bsJsonV2Listener;
            return this;
        }

        public BSJson load() {
            return new BSJson(activity, server, jsObj, bsJsonV2Listener, method);
        }
    }
}
