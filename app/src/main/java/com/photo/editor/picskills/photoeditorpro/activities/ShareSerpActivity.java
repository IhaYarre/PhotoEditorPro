package com.photo.editor.picskills.photoeditorpro.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.photo.editor.picskills.photoeditorpro.R;
import com.photo.editor.picskills.photoeditorpro.custom.MyBounceInterpolator;
import com.photo.editor.picskills.photoeditorpro.utils.Constants;
import com.photo.editor.picskills.photoeditorpro.utils.support.MyExceptionHandlerPix;
import com.photo.editor.picskills.photoeditorpro.utils.support.SupportedClass;

public class ShareSerpActivity extends ParentActivity {
    Uri mImgUri;
    ImageView mIvCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        if (this.getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandlerPix(ShareSerpActivity.this));
        init();

        (findViewById(R.id.iv_home)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*FullScreenAdManager.fullScreenAdsCheckPref(ShareActivity.this, FullScreenAdManager.ALL_PREFS.ATTR_ON_HOME_SCREEN, new FullScreenAdManager.GetBackPointer() {
                    @Override
                    public void returnAction() {*/
                Intent finishIntent = new Intent(ShareSerpActivity.this, MainActivity.class);
                finishIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(finishIntent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
              /*      }
                });*/
            }
        });
        (findViewById(R.id.iv_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        (findViewById(R.id.iv_share)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareOnOthersApp();
            }
        });

    }

    public void init() {
        mIvCreate = (ImageView) findViewById(R.id.iv_create);
        mImgUri = Uri.parse(getIntent().getStringExtra(Constants.KEY_URI_IMAGE));
        RelativeLayout mAdView = findViewById(R.id.adView);
        loadBannerAds(mAdView);

        Glide.with(ShareSerpActivity.this)
                .asBitmap()
                .load(mImgUri)
                .into(mIvCreate);
        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);

        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);

        mIvCreate.startAnimation(myAnim);
    }

    private void shareOnOthersApp() {
        SupportedClass.shareWithOther(ShareSerpActivity.this, getString(R.string.txt_app_share_info), mImgUri);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        this.finish();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

}
