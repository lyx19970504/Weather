package com.fafu.polutionrepo.finished.Activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.fafu.polutionrepo.finished.Fragments.MapFragment;
import com.fafu.polutionrepo.finished.Fragments.MyInfoFragment;
import com.fafu.polutionrepo.finished.Fragments.TopicFragment;
import com.fafu.polutionrepo.finished.R;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class TestActivity extends AppCompatActivity {

    private static final String TAG = "TestActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        TopicFragment topicFragment = new TopicFragment();
//        MyInfoFragment myInfoFragment = new MyInfoFragment();
//        MapFragment fragment = new MapFragment();
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        fragmentManager.beginTransaction().add(R.id.fragment_container, myInfoFragment);


    }


}
