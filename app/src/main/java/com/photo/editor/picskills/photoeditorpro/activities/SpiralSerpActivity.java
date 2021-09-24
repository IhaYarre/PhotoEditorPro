package com.photo.editor.picskills.photoeditorpro.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.tabs.TabLayout;
import com.photo.editor.picskills.photoeditorpro.R;
import com.photo.editor.picskills.photoeditorpro.adapter.SpiralEffectListAdapter;
import com.photo.editor.picskills.photoeditorpro.adapter.StickerAdapter;
import com.photo.editor.picskills.photoeditorpro.callback.MenuItemClickLister;
import com.photo.editor.picskills.photoeditorpro.callback.StickerClickListener;
import com.photo.editor.picskills.photoeditorpro.crop_img.newCrop.MLCropAsyncTask;
import com.photo.editor.picskills.photoeditorpro.crop_img.newCrop.MLOnCropTaskCompleted;
import com.photo.editor.picskills.photoeditorpro.custom.CustomTextView;
import com.photo.editor.picskills.photoeditorpro.custom.DHANVINE_MultiTouchListener;
import com.photo.editor.picskills.photoeditorpro.custom.StickerView.DrawableSticker;
import com.photo.editor.picskills.photoeditorpro.custom.StickerView.Sticker;
import com.photo.editor.picskills.photoeditorpro.custom.StickerView.StickerView;
import com.photo.editor.picskills.photoeditorpro.utils.Constants;
import com.photo.editor.picskills.photoeditorpro.utils.ImageUtils;
import com.photo.editor.picskills.photoeditorpro.utils.support.FastBlur;
import com.photo.editor.picskills.photoeditorpro.utils.support.SupportedClass;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Objects;

import static com.photo.editor.picskills.photoeditorpro.activities.FilterLabActivity.notifyMediaScannerService;

public class SpiralSerpActivity extends ParentActivity implements MenuItemClickLister, View.OnClickListener {

    public static ImageView setfront;
    public static Bitmap eraserResultBmp;
    private static Bitmap faceBitmap;
    public int mCount = 0;
    boolean isFirstTime = true;
    SeekBar sbBackgroundOpacity;
    private Bitmap selectedBit, cutBit;
    private Context mContext;
    private Animation slideUpAnimation, slideDownAnimation;
    private SpiralEffectListAdapter spiralEffectListAdapter;
    private int spiral = 57,
            shape = 35;
    private TextView tvNeonList, tvShapeList;
    private RecyclerView recyclerNeonEffect, recyclerSticker;
    private ArrayList<String> neonEffect = new ArrayList<String>(), shapeEffect = new ArrayList<String>();
    private ImageView iv_face, setback, setimg;
    private RelativeLayout mContentRootView;
    private Uri savedImageUri;
    private StickerView stickerView;
    private Sticker currentSticker;
    private LinearLayout linThirdDivisionOption, linEffect, linBackgroundBlur;
    private TabLayout tabLayout;
    private String oldSavedFileName;

    //ads variables
    private static final String TAG = "SpiralSerpActvity";
    private InterstitialAd interstitialAd;


    public static void setFaceBitmap(Bitmap bitmap) {
        faceBitmap = bitmap;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_spiral_serp);
        EditingPicActivity.stopAnim();
        if (getSupportActionBar()
                != null) {
            this.getSupportActionBar().hide();
        }
        mContext = this;
        selectedBit = faceBitmap;
        /*RelativeLayout mAdView = findViewById(R.id.adView);
        loadBannerAds(mAdView);*/

        new Handler().postDelayed(new Runnable() {
            public void run() {
                setimg.post(new Runnable() {
                    public void run() {
                        if (isFirstTime && selectedBit != null) {
                            isFirstTime = false;
                            initBMPNew();
                        }
                    }
                });
            }
        }, 1000);


        neonEffect.add("none");
        shapeEffect.add("none");
        for (int i = 1; i <= spiral; i++) {
            neonEffect.add("b_" + i);
        }
        for (int i = 1; i <= shape; i++) {
            shapeEffect.add("shape_" + i);
        }
        Init();
        setTollbarData();
        if (SupportedClass.checkConnection(this)) {
            loadInterstitialAd();
        } else {
            Log.e("Interstitial", "Failed to load");
        }
    }

    private void initBMPNew() {
        if (faceBitmap != null) {
            selectedBit = ImageUtils.getBitmapResize(mContext, faceBitmap, setimg.getWidth(), setimg.getHeight());
            mContentRootView.setLayoutParams(new LinearLayout.LayoutParams(selectedBit.getWidth(), selectedBit.getHeight()));
            if (selectedBit != null && iv_face != null) {
                iv_face.setImageBitmap(new FastBlur().processBlur(selectedBit, 1, sbBackgroundOpacity.getProgress() == 0 ? 1 : sbBackgroundOpacity.getProgress()));
            }
            cutmaskNew();
        }
    }

    private void Init() {
        findViewById(R.id.ivShowHomeOption).setVisibility(View.GONE);
        stickerView = (StickerView) findViewById(R.id.sticker_view);
        mContentRootView = findViewById(R.id.mContentRootView);
        setfront = findViewById(R.id.setfront);
        setback = findViewById(R.id.setback);
        iv_face = findViewById(R.id.iv_face);
        setimg = findViewById(R.id.setimg);

        iv_face.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (stickerView.getCurrentSticker() != null) {
                    stickerView.getCurrentSticker().release();
                }
            }
        });
        linEffect = (LinearLayout) findViewById(R.id.linEffect);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        createTabIcons();
        tabLayout.getTabAt(0);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                onBottomTabSelected(tab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                onBottomTabSelected(tab);
            }
        });

        recyclerNeonEffect = (RecyclerView) findViewById(R.id.recyclerNeonEffect);
        recyclerNeonEffect.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        linBackgroundBlur = findViewById(R.id.linBackgroundBlur);
        setUpBottomList();

        AppCompatImageView ivCheckMark = (AppCompatImageView) findViewById(R.id.ivCheckMark);
        ivCheckMark.setOnClickListener(this);
        AppCompatImageView ivClose = (AppCompatImageView) findViewById(R.id.ivClose);
        ivClose.setOnClickListener(this);
        recyclerSticker = (RecyclerView) findViewById(R.id.recyclerSticker);
        recyclerSticker.setLayoutManager(new GridLayoutManager(this, 3));
        linThirdDivisionOption = (LinearLayout) findViewById(R.id.linThirdDivisionOption);
        initMainStickerViewMan();
        //sticker list create
        setStickerImages(30);

        tabLayout.setVisibility(View.VISIBLE);
        linEffect.setVisibility(View.GONE);
        linBackgroundBlur.setVisibility(View.GONE);

        findViewById(R.id.ivShowHomeOption).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                onBackPressed();
            }
        });

        tvNeonList = findViewById(R.id.tvNeonList);
//        tvWingsList = findViewById(R.id.tvWingsList);
        tvShapeList = findViewById(R.id.tvShapeList);
        tvNeonList.setOnClickListener(this);
//        tvWingsList.setOnClickListener(this);
        tvShapeList.setOnClickListener(this);
        tvNeonList.performClick();
        iv_face.setRotationY(0.0f);

        setimg.post(new Runnable() {
            public void run() {
                initBMPNew();
            }
        });

        SeekBar sbFrameOpacity = findViewById(R.id.sbFrameOpacity);
        sbFrameOpacity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                if (setback != null && setfront != null) {
                    float f = ((float) i) * 0.01f;
                    setback.setAlpha(f);
                    setfront.setAlpha(f);
                }
            }
        });
        sbBackgroundOpacity = findViewById(R.id.sbBackgroundOpacity);
        sbBackgroundOpacity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                if (selectedBit != null && iv_face != null) {
                    iv_face.setImageBitmap(new FastBlur().processBlur(selectedBit, 1, seekBar.getProgress() == 0 ? 1 : seekBar.getProgress()));
                }
            }

            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
            }
        });
    }

    final ArrayList<Integer> stickerArrayList = new ArrayList<>();

    public void setStickerImages(int size) {


        for (int i = 1; i <= size; i++) {
            Resources resources = getResources();
            stickerArrayList.add(Integer.valueOf(resources.getIdentifier("sticker_n" + i, "drawable", getPackageName())));
        }
        recyclerSticker = findViewById(R.id.recyclerSticker);
        recyclerSticker.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerSticker.setAdapter(new StickerAdapter(this, stickerArrayList, new StickerClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                itemSelectFromList(linThirdDivisionOption, recyclerSticker, false);
                Drawable drawable = getResources().getDrawable(stickerArrayList.get(position));
                stickerView.addSticker(new DrawableSticker(drawable));
            }
        }));

    }


    private void onBottomTabSelected(TabLayout.Tab tab) {
        if (tab.getPosition() == 0) {
            findViewById(R.id.ivShowHomeOption).setVisibility(View.VISIBLE);
            viewSlideUpDown(linEffect, tabLayout);

        } else if (tab.getPosition() == 1) {
            itemSelectFromList(linThirdDivisionOption, recyclerSticker, true);

        } else if (tab.getPosition() == 2) {
            findViewById(R.id.ivShowHomeOption).setVisibility(View.VISIBLE);
            viewSlideUpDown(linBackgroundBlur, tabLayout);

        } else if (tab.getPosition() == 3) {
            /* StickerEraseActivity.b = cutBit;
            Intent intent = new Intent(this, StickerEraseActivity.class);
            intent.putExtra(Constants.KEY_OPEN_FROM, Constants.VALUE_OPEN_FROM_NEON);
            startActivityForResult(intent, 1024);*/
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1024) {
            if (eraserResultBmp != null) {
                cutBit = eraserResultBmp;
                setimg.setImageBitmap(cutBit);
            }
        }
    }

    private void createTabIcons() {
        View view = LayoutInflater.from(this).inflate(R.layout.custom_neon_tab, null);
        CustomTextView textOne = (CustomTextView) view.findViewById(R.id.text);
        ImageView ImageOne = (ImageView) view.findViewById(R.id.image);
        textOne.setText(getString(R.string.txt_effect));
        ImageOne.setImageResource(R.drawable.ic_neon_effect_svg);
        tabLayout.addTab(tabLayout.newTab().setCustomView(view));

        View view3 = LayoutInflater.from(this).inflate(R.layout.custom_neon_tab, null);
        CustomTextView text3 = (CustomTextView) view3.findViewById(R.id.text);
        ImageView Image3 = (ImageView) view3.findViewById(R.id.image);
        text3.setText(getString(R.string.txt_stickers));
        Image3.setImageResource(R.drawable.ic_stickers);
        tabLayout.addTab(tabLayout.newTab().setCustomView(view3));

       /* View view2 = LayoutInflater.from(this).inflate(R.layout.custom_neon_tab, null);
        CustomTextView textTwo = (CustomTextView) view2.findViewById(R.id.text);
        ImageView ImageTwo = (ImageView) view2.findViewById(R.id.image);
        textTwo.setText(getString(R.string.txt_erase));
        ImageTwo.setImageResource(R.drawable.ic_erase);
        tabLayout.addTab(tabLayout.newTab().setCustomView(view2));*/

        View view4 = LayoutInflater.from(this).inflate(R.layout.custom_neon_tab, null);
        CustomTextView text4 = (CustomTextView) view4.findViewById(R.id.text);
        ImageView Image4 = (ImageView) view4.findViewById(R.id.image);
        text4.setText(getString(R.string.txt_background));
        Image4.setImageResource(R.drawable.ic_backchange);
        tabLayout.addTab(tabLayout.newTab().setCustomView(view4));
    }

    public void itemSelectFromList(final LinearLayout linLayout, final RecyclerView recyclerView, boolean upAnimation) {
        //recyclerView.setVisibility(View.VISIBLE);
        if (upAnimation) {
            linLayout.setVisibility(View.VISIBLE);
            slideUpAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
            linLayout.startAnimation(slideUpAnimation);
            recyclerView.scrollToPosition(0);
        } else {
            slideDownAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
            linLayout.startAnimation(slideDownAnimation);
            slideDownAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    linLayout.setVisibility(View.GONE);
                    // recyclerView.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        }
    }

    public void viewSlideUpDown(final View showLayout, final View hideLayout) {
        showLayout.setVisibility(View.VISIBLE);
        slideUpAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
        showLayout.startAnimation(slideUpAnimation);
        slideDownAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        hideLayout.startAnimation(slideDownAnimation);
        slideDownAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                hideLayout.setVisibility(View.GONE);
                // recyclerView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }


    private void initMainStickerViewMan() {
        stickerView.setLocked(false);
        stickerView.setConstrained(true);
        stickerView.setOnStickerOperationListener(new StickerView.OnStickerOperationListener() {
            @Override
            public void onStickerAdded(@NonNull Sticker sticker) {
                Log.e("TAG", "onStickerAdded");
            }

            @Override
            public void onStickerClicked(@NonNull Sticker sticker) {
                Log.e("TAG", "onStickerClicked");
            }

            @Override
            public void onStickerDeleted(@NonNull Sticker sticker) {
                removeStickerWithDeleteIcon();
                Log.e("TAG", "onStickerDeleted");
            }

            @Override
            public void onStickerDragFinished(@NonNull Sticker sticker) {
                Log.e("TAG", "onStickerDragFinished");
            }

            @Override
            public void onStickerTouchedDown(@NonNull final Sticker sticker) {
                stickerOptionTaskPerformMan(sticker);
            }

            @Override
            public void onStickerZoomFinished(@NonNull Sticker sticker) {
                Log.e("TAG", "onStickerZoomFinished");
            }

            @Override
            public void onStickerFlipped(@NonNull Sticker sticker) {
                Log.e("TAG", "onStickerFlipped");
            }

            @Override
            public void onStickerDoubleTapped(@NonNull Sticker sticker) {
                Log.e("TAG", "onDoubleTapped: double tap will be with two click");
            }
        });
    }

    @Override
    public void onBackPressed() {
        findViewById(R.id.ivShowHomeOption).setVisibility(View.GONE);
        if (linEffect.getVisibility() == View.VISIBLE) {
            viewSlideUpDown(tabLayout, linEffect);
        } else if (linBackgroundBlur.getVisibility() == View.VISIBLE) {
            viewSlideUpDown(tabLayout, linBackgroundBlur);
        } else if (linThirdDivisionOption.getVisibility() == View.VISIBLE) {
            findViewById(R.id.ivClose).performClick();
        } else {
            showBackDialog();
        }
    }

    public void stickerOptionTaskPerformMan(Sticker sticker) {
        stickerView.setLocked(false);
        currentSticker = sticker;
        stickerView.sendToLayer(stickerView.getStickerPosition(currentSticker));
        Log.e("TAG", "onStickerTouchedDown");
    }

    private void removeStickerWithDeleteIcon() {
        stickerView.remove(currentSticker);
        currentSticker = null;
        if (stickerView.getStickerCount() == 0) {

        } else {
            currentSticker = stickerView.getLastSticker();
        }
    }

    public void setTollbarData() {
        TextView textView = findViewById(R.id.tv_title);
        textView.setText(getResources().getString(R.string.txt_spiral_editor));
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                showBackDialog();
            }
        });
        findViewById(R.id.tv_applay).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                showSaveDialog();
            }
        });
    }

    private void showBackDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_leave);
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView button = (TextView) dialog.findViewById(R.id.btn_yes);
        TextView button2 = (TextView) dialog.findViewById(R.id.btn_no);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finish();
                dialog.dismiss();
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Bitmap bitmap = new saveImageTaskMaking().getBitmapFromView(mContentRootView);
                Uri uri = getFileUri(bitmap);
                assert uri != null;
                EditingPicActivity.bitmapReturnString = uri.toString();
                finish();
                dialog.dismiss();
                overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            public void onDismiss(DialogInterface dialogInterface) {
            }
        });
        dialog.show();
    }

    public void openShareActivity() {
        Intent intent = new Intent(SpiralSerpActivity.this, ShareSerpActivity.class);
        intent.putExtra(Constants.KEY_URI_IMAGE, savedImageUri.toString());
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.enter, R.anim.exit);
        Toast.makeText(getApplicationContext(), "Image Saved Successfully!", Toast.LENGTH_SHORT)
                .show();
    }

    public void setUpBottomList() {
        spiralEffectListAdapter = new SpiralEffectListAdapter(mContext);
        spiralEffectListAdapter.setClickListener(this);
        recyclerNeonEffect.setAdapter(spiralEffectListAdapter);
        spiralEffectListAdapter.addData(neonEffect);
    }

    public void onMenuListClick(View view, int i) {
        if (i != 0) {
            Bitmap backspiral = ImageUtils.getBitmapFromAsset(mContext, "spiral/back/" + spiralEffectListAdapter.getItemList().get(i)
                    + "_back.png");
            Bitmap fronspiral = ImageUtils.getBitmapFromAsset(mContext, "spiral/front/" + spiralEffectListAdapter.getItemList().get(i) + "_front.png");
            setback.setImageBitmap(backspiral);
            setfront.setImageBitmap(fronspiral);
        } else {
            setback.setImageResource(0);
            setfront.setImageResource(0);
        }
        setback.setOnTouchListener(new DHANVINE_MultiTouchListener(this, true));
    }

    public void cutmaskNew() {

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.crop_progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        final ProgressBar progressBar2 = progressBar;
        new CountDownTimer(5000, 1000) {
            public void onFinish() {
            }

            public void onTick(long j) {
                int unused = mCount = mCount + 1;
                if (progressBar2.getProgress() <= 90) {
                    progressBar2.setProgress(mCount * 5);
                }
            }
        }.start();

        new MLCropAsyncTask(new MLOnCropTaskCompleted() {
            public void onTaskCompleted(Bitmap bitmap, Bitmap bitmap2, int left, int top) {
                int[] iArr = {0, 0, selectedBit.getWidth(), selectedBit.getHeight()};
                int width = selectedBit.getWidth();
                int height = selectedBit.getHeight();
                int i = width * height;
                selectedBit.getPixels(new int[i], 0, width, 0, 0, width, height);
                int[] iArr2 = new int[i];
                Bitmap createBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                createBitmap.setPixels(iArr2, 0, width, 0, 0, width, height);
                cutBit = ImageUtils.getMask(mContext, selectedBit, createBitmap, width, height);
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(
                        bitmap, cutBit.getWidth(), cutBit.getHeight(), false);
                cutBit = resizedBitmap;

                runOnUiThread(new Runnable() {
                    public void run() {
                        Palette p = Palette.from(cutBit).generate();
                        if (p.getDominantSwatch() == null) {
                            Toast.makeText(SpiralSerpActivity.this, getString(R.string.txt_not_detect_human), Toast.LENGTH_SHORT).show();
                        }
                        setimg.setImageBitmap(cutBit);
                    }
                });


            }
        }, this, progressBar).execute(new Void[0]);
    }

    private Uri getFileUri(Bitmap inImage) {
        try {
            File tempDir = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                tempDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            } else {
                tempDir = Environment.getExternalStorageDirectory();
            }
            tempDir = new File(tempDir.getAbsolutePath() + "/.temp/");
            tempDir.mkdir();
            File tempFile = File.createTempFile("IMG_" + System.currentTimeMillis(), ".jpg", tempDir);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            byte[] bitmapData = bytes.toByteArray();

            //write the bytes in file

            FileOutputStream fos = new FileOutputStream(tempFile);
            fos.write(bitmapData);
            fos.flush();
            fos.close();
            return Uri.fromFile(tempFile);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private void showSaveDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_saved);
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView button = (TextView) dialog.findViewById(R.id.btn_continue);
        TextView button2 = (TextView) dialog.findViewById(R.id.btn_download);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Bitmap bitmap = new saveImageTaskMaking().getBitmapFromView(mContentRootView);
                Uri uri = getFileUri(bitmap);
                assert uri != null;
                EditingPicActivity.bitmapReturnString = uri.toString();
                finish();
                dialog.dismiss();
                overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                new saveImageTaskMaking().execute();
                dialog.dismiss();
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            public void onDismiss(DialogInterface dialogInterface) {
            }
        });
        dialog.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvNeonList:
                spiralEffectListAdapter.addData(neonEffect);
                tabSelected(1);
                break;
            case R.id.tvShapeList:
                spiralEffectListAdapter.addData(shapeEffect);
                tabSelected(2);
                break;
            case R.id.ivCheckMark:
            case R.id.ivClose:
                if (linThirdDivisionOption.getVisibility() == View.VISIBLE) {
                    if (currentSticker == null)
                        currentSticker = stickerView.getCurrentSticker();

                    if (recyclerSticker.getVisibility() == View.VISIBLE) {
                        itemSelectFromList(linThirdDivisionOption, recyclerSticker, false);
                    }
                }
                break;
        }
    }

    private void tabSelected(int i) {
        tvNeonList.setBackground(ContextCompat.getDrawable(this, R.drawable.bottom_tab_back_unselected));
        tvNeonList.setTextColor(ContextCompat.getColor(this, R.color.white));
        tvShapeList.setBackground(ContextCompat.getDrawable(this, R.drawable.bottom_tab_back_unselected));
        tvShapeList.setTextColor(ContextCompat.getColor(this, R.color.white));
        if (i == 1) {
            tvNeonList.setTextColor(ContextCompat.getColor(this, R.color.black));
            tvNeonList.setBackground(ContextCompat.getDrawable(this, R.drawable.bottom_tab_back_selected));
        } else if (i == 2) {
            tvShapeList.setTextColor(ContextCompat.getColor(this, R.color.black));
            tvShapeList.setBackground(ContextCompat.getDrawable(this, R.drawable.bottom_tab_back_selected));
        }
    }

    private class saveImageTaskMaking extends android.os.AsyncTask<String, String, Exception> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            findViewById(R.id.ivShowHomeOption).setVisibility(View.GONE);
            stickerView.setLocked(true);
        }

        public Bitmap getBitmapFromView(View view) {
            Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            view.draw(canvas);
            return bitmap;
        }


        @Override
        protected Exception doInBackground(String... strings) {
            mContentRootView.setDrawingCacheEnabled(true);
            Bitmap bitmap = getBitmapFromView(mContentRootView);
            String fileName = getString(R.string.app_file) + System.currentTimeMillis() + Constants.KEY_JPG;
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    ContentResolver contentResolver = getContentResolver();
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                    contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
                    contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + getString(R.string.app_folder2));

                    Uri uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

                    FileOutputStream fos = (FileOutputStream) contentResolver.openOutputStream(Objects.requireNonNull(uri));
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    Objects.requireNonNull(fos);
                    if (uri != null)
                        savedImageUri = uri;
                    notifyMediaScannerService(SpiralSerpActivity.this, uri.getPath());

                } else {
                    File myDir = new File(Environment.getExternalStorageDirectory().toString() + getString(R.string.app_folder));
                    if (!myDir.exists())
                        myDir.mkdirs();

                    File file = new File(myDir, fileName);
                    if (oldSavedFileName != null) {
                        File oldFile = new File(myDir, oldSavedFileName);
                        if (oldFile.exists()) oldFile.delete();
                    }
                    oldSavedFileName = fileName;

                    FileOutputStream out = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                    out.close();
                    //
                    Uri uri = SupportedClass.addImageToGallery(SpiralSerpActivity.this, file.getAbsolutePath());
                    if (uri != null)
                        savedImageUri = uri;
                    notifyMediaScannerService(SpiralSerpActivity.this, myDir.getAbsolutePath());
                }
            } catch (Exception e) {
                return e;
            } finally {
                mContentRootView.setDrawingCacheEnabled(false);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Exception e) {
            super.onPostExecute(e);
            findViewById(R.id.ivShowHomeOption).setVisibility(View.VISIBLE);
            if (e == null) {
                /*FullScreenAdManager.fullScreenAdsCheckPref(SpiralSerpActivity.this, FullScreenAdManager.ALL_PREFS.ATTR_ON_SHARE_SCREEN, new FullScreenAdManager.GetBackPointer() {
                    @Override
                    public void returnAction() {*/
                showInterstitial();
                /*    }
                });*/
            } else {
                Toast.makeText(SpiralSerpActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void loadInterstitialAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(
                this,
                getString(R.string.admob_interstitial_ads_id),
                adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        SpiralSerpActivity.this.interstitialAd = interstitialAd;
                        Log.i(TAG, "onAdLoaded");
                        interstitialAd.setFullScreenContentCallback(
                                new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        // Called when fullscreen content is dismissed.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        SpiralSerpActivity.this.interstitialAd = null;
                                        if (savedImageUri != null) {
                                            Log.e("Ad", "Ad did not load");
                                            openShareActivity();
                                        }
                                        Log.d("TAG", "The ad was dismissed.");
                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                                        // Called when fullscreen content failed to show.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        SpiralSerpActivity.this.interstitialAd = null;
                                        Log.d("TAG", "The ad failed to show.");
                                    }

                                    @Override
                                    public void onAdShowedFullScreenContent() {
                                        // Called when fullscreen content is shown.
                                        Log.d("TAG", "The ad was shown.");
                                    }
                                });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i(TAG, loadAdError.getMessage());
                        interstitialAd = null;
                        String error =
                                String.format(
                                        "domain: %s, code: %d, message: %s",
                                        loadAdError.getDomain(), loadAdError.getCode(), loadAdError.getMessage());
                        Log.e("Interstitial", error);
                    }
                });
    }

    private void showInterstitial() {
        // Show the ad if it's ready. Otherwise toast and restart the game.
        if (interstitialAd != null) {
            interstitialAd.show(this);
        } else {
            if (savedImageUri != null) {
                Log.e("Ad", "Ad did not load");
                openShareActivity();
            }
        }
    }
}
