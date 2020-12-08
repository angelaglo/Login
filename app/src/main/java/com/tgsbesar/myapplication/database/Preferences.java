package com.tgsbesar.myapplication.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preferences {
    private static final String KEY_NORM = "noRM";
    private static final String KEY_NORM2 = "Status";
    private static final String SHARED_PREF = "PREF_SHARED";
    private Context context;
    private SharedPreferences shared;

    public Preferences(Context context){
        this.context=context;
        this.shared = this.context.getSharedPreferences(SHARED_PREF,Context.MODE_PRIVATE);
    }

    public String getEmailNorm(){
        return shared.getString(KEY_NORM,"");
    }

    public void setEmailNorm(String norm){
        SharedPreferences.Editor editor = shared.edit();
        editor.putString(KEY_NORM,norm);
        editor.apply();
    }
}
