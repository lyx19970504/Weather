package com.fafu.polutionrepo.finished.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.fafu.polutionrepo.finished.R;

public class UpdateNickNameActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_nickname_layout);
        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        editText = findViewById(R.id.nick_name);
        editText.setSelection(editText.getText().toString().length());

        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.edit_finished).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.edit_finished:
                Intent intent = new Intent();
                intent.putExtra("nickname", editText.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }
}
