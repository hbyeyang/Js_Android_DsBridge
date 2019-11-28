package com.xszn.ime.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.xszn.ime.R;


public class MainActivity extends Activity {


    private TextView mTvJump1;
    private TextView mTvJump2;
    private EditText mEditText;
    private TextView mTvFromEditJump;

    private String testUrl = "http://gamecentercdn.hddgood.com/static/test/test.html";
    private String mUrl = "http://t.alpha.channel.45xie.com/beautypic/home.html?appid=B428109E4AA4E883DB8B1877BFF3575F";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mTvJump1 = findViewById(R.id.tv_jump1);
        mTvJump2 = findViewById(R.id.tv_jump2);
        mEditText = findViewById(R.id.edit_text);
        mTvFromEditJump = findViewById(R.id.tv_from_edit_jump);

        mTvJump1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirstActivity.launch(MainActivity.this,testUrl);
            }
        });

        mTvJump2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirstActivity.launch(MainActivity.this,mUrl);
            }
        });

        mTvFromEditJump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEditText.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this,"请输入网址",Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    FirstActivity.launch(MainActivity.this,mEditText.getText().toString());
                }
            }
        });
    }

}
