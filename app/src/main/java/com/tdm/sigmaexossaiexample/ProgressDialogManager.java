package com.tdm.sigmaexossaiexample;

import android.app.ProgressDialog;
import android.content.Context;

public class ProgressDialogManager {
    public static ProgressDialogManager instance = null;
    public static ProgressDialog progressDialog;
    public static Context context;

    public static ProgressDialogManager getInstance() {
        if (instance == null) {
            instance = new ProgressDialogManager();
        }
        return instance;
    }
    public void init (Context context){
        if(this.context == null){
            this.context = context;
        }
    }
    public void showLoading() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);  // Prevent dismissing by tapping outside
        }
        progressDialog.show();
    }

    // Hide the loading spinner
    public void hideLoading() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

}

