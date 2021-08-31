package com.photo.editor.picskills.photoeditorpro.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.photo.editor.picskills.photoeditorpro.R;
import com.photo.editor.picskills.photoeditorpro.fragments.CropImageFragmentPix;
import com.photo.editor.picskills.photoeditorpro.utils.support.MyExceptionHandlerPix;

public class CropPhotoActivity extends ParentActivity {

    public Uri currentImgUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_crop_photo);
        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandlerPix(CropPhotoActivity.this));

        RelativeLayout mAdView = findViewById(R.id.adView);
        loadBannerAds(mAdView);
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Intent intent = getIntent();
        currentImgUri = Uri.parse(intent.getStringExtra("cropUri"));

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, CropImageFragmentPix.newInstance()).commit();
        }

    }

    public void cancelCropping() {
        if (isFinishing()) return;
        setResult(RESULT_CANCELED);
        finish();
    }

    public void startResultActivity(Uri uri) {
        if (isFinishing()) return;
        // Start ResultActivity
        Log.e("TECHTAG", "Set Back with Bitmap");
        Intent intent = new Intent();
        intent.putExtra("croppedUri", uri);
        setResult(RESULT_OK, intent);
        finish();
    }
}
