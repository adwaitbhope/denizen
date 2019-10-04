package com.township.manager;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import androidx.multidex.MultiDexApplication;

public class GlobalVariables extends MultiDexApplication {
    private RequestQueue queue;

    public RequestQueue getQueue() {
        if (queue == null) {
            queue = Volley.newRequestQueue(this);
        }
        return queue;
    }

}
