package com.stegfy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.stegfy.R;
import com.stegfy.utils.p2p.WifiActivity;

import java.io.File;
import java.util.Objects;

public class ChoiceActivity extends AppCompatActivity {
    Button share;
    Button p2p;
    public String file_url;
    public File imageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choice_send);
        share =  findViewById(R.id.send_share);
        p2p = findViewById(R.id.send_p2p);
        if (getIntent().hasExtra("img_file")) {
            imageFile = (File) Objects.requireNonNull(getIntent().getExtras()).get("img_file");

        }

        if (getIntent().hasExtra("file_url")) {
            file_url = (String) Objects.requireNonNull(getIntent().getExtras()).get("file_url");

        }
        share.setOnClickListener(onClickListener);
        p2p.setOnClickListener(onClickListener);
    }


    private View.OnClickListener onClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View arg0) {
            switch (arg0.getId()) {
                case R.id.send_share:
                    Intent sendIntent = new Intent();

                    sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);

                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.setType("file/*");

                    sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    sendIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    sendIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(ChoiceActivity.this , "com.stegfy.fileprovider", imageFile));
                    startActivity(Intent.createChooser(sendIntent, "Send to"));
                    //finish();
                    break;
                case R.id.send_p2p:
                    Intent intent = new Intent(ChoiceActivity.this, WifiActivity.class);
                    intent.putExtra("file_url", file_url);
                    startActivity(intent);
                    break;

            }
        }
    };
}
