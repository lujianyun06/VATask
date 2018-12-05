package com.example.lll.va;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.lll.va.task3.Task3;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Task3.runTask3(this);

    }


}
