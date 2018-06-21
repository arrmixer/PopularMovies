package com.ae.andriod.popularmovies.View;


import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;

import com.ae.andriod.popularmovies.R;


public class MainActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {

        return new MovieFragment();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("main activity", "onCreate: ");


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("main activity", "onDestroy: ");

    }


}
