package com.example.user.criminalintent;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;


import com.example.user.criminalintent.R;

/**
 * Created by user on 30-12-2017.
 */

public abstract class SingleFragment extends AppCompatActivity {

    protected abstract android.support.v4.app.Fragment createFragment();

    @LayoutRes
    protected int getLayoutResId(){
        return R.layout.activity_fragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());

        FragmentManager fm = getSupportFragmentManager();
        android.support.v4.app.Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if(fragment == null){
            fragment = createFragment();
            fm.beginTransaction().add(R.id.fragment_container,fragment).commit();
        }
    }

}
