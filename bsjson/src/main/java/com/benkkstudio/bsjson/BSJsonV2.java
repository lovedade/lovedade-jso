package com.benkkstudio.bsjson;


import android.app.Activity;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.benkkstudio.bsjson.Interface.BSJsonV2Listener;
import com.google.gson.JsonObject;

public class BSJsonV2 {
    private Activity activity;
    private String server;
    private JsonObject jsObj;
    private BSJsonV2Listener bsJsonV2Listener;
    private String purchaseCode;
    private BSJsonV2(Activity activity,
                   String server,
                   JsonObject jsObj,
                     BSJsonV2Listener bsJsonV2Listener,
                   String purchaseCode) {
        this.activity = activity;
        this.server = server;
        this.jsObj = jsObj;
        this.bsJsonV2Listener = bsJsonV2Listener;
        this.purchaseCode = purchaseCode;
        verifyNow();
    }

    private void verifyNow() {
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
                    }

                    @Override
                    public void onError(ANError error) {
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
        }

    }

    public static class Builder {
        private Activity activity;
        private String server;
        private JsonObject jsObj;
        private BSJsonV2Listener bsJsonV2Listener;
        private String purchaseCode;
        public Builder(Activity activity) {
            this.activity = activity;
        }

        public BSJsonV2.Builder setServer(String server) {
            this.server = server;
            return this;
        }

        public BSJsonV2.Builder setObject(JsonObject jsObj) {
            this.jsObj = jsObj;
            return this;
        }

        public BSJsonV2.Builder setPurchaseCode(String purchaseCode) {
            this.purchaseCode = purchaseCode;
            return this;
        }

        public BSJsonV2.Builder setListener(BSJsonV2Listener bsJsonV2Listener) {
            this.bsJsonV2Listener = bsJsonV2Listener;
            return this;
        }

        public BSJsonV2 load() {
            return new BSJsonV2(activity, server, jsObj, bsJsonV2Listener, purchaseCode);
        }
    }
}