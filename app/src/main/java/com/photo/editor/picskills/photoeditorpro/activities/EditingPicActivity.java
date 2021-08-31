package com.photo.editor.picskills.photoeditorpro.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.photo.editor.picskills.photoeditorpro.BuildConfig;
import com.photo.editor.picskills.photoeditorpro.R;
import com.photo.editor.picskills.photoeditorpro.activities.blur_serp_tool.BlurSerpActivity;
import com.photo.editor.picskills.photoeditorpro.activities.black_white_tool.BlackWhiteActivity;
import com.photo.editor.picskills.photoeditorpro.adapter.GradientFilterAdapter;
import com.photo.editor.picskills.photoeditorpro.adapter.SimpleFilterAdapter;
import com.photo.editor.picskills.photoeditorpro.crop_img.newCrop.StoreManager;
import com.photo.editor.picskills.photoeditorpro.model.GradientFilterModel;
import com.photo.editor.picskills.photoeditorpro.model.SimpleFilterModel;
import com.photo.editor.picskills.photoeditorpro.utils.Constants;
import com.photo.editor.picskills.photoeditorpro.utils.support.SupportedClass;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;

import static com.google.android.gms.common.util.CollectionUtils.listOf;
import static com.photo.editor.picskills.photoeditorpro.activities.FilterLabActivity.notifyMediaScannerService;

public class EditingPicActivity extends AppCompatActivity implements View.OnClickListener,
        SeekBar.OnSeekBarChangeListener, GradientFilterAdapter.GradientFilterClickListener,
        SimpleFilterAdapter.MirrorClickListener {

    //simple filter list and packs list
    private ArrayList<SimpleFilterModel> simpleFilterArrayList = new ArrayList();

    private ArrayList<GradientFilterModel> gradientList = new ArrayList();
    //simple filters
    private SimpleFilterAdapter simpleFilterAdapter;
    private RecyclerView simpleFilterRecycler, gradientFilterRecycler;

    private GradientFilterAdapter gradientFilterAdapter;

    private Handler handler = new Handler(Looper.getMainLooper());
    private Bitmap originalBitmap = null;
    private ImageView btnPersonImage, btnBack, btnApply;
    private TextView txtFilter, txtGradient, txtBlur, txtBlackWhite, txtSpiral, txtWings;// txtDrip;
    private AppCompatSeekBar seekBar;
    private LinearLayout linearLayout;
    private FrameLayout frameLayout;
    private RelativeLayout relativeLayout;
    //flip bitmap
    private Bitmap bOutput = null;
    private String bitmapString = "";
    public static String bitmapReturnString = "";
    //send image uri to blur activity
    private Bitmap result = null;
    private boolean isFilterUri = false;
    //stop double clicking
    public static KProgressHUD prog;
    //for spiral effect
    public static int screenHeight;
    public static int screenWidth;

    //content uri store
    private Uri sourceUri;
    private Uri destinationUri;

    //ads variables
    private static final String TAG = "EditingPicActivity";

    private InterstitialAd interstitialAd;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editing_pic);
        prog = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setLabel("Processing...")
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
        if (getSupportActionBar()
                != null) {
            this.getSupportActionBar().hide();
        }
        btnApply = findViewById(R.id.apply);
        btnBack = findViewById(R.id.back);
        btnPersonImage = findViewById(R.id.person_image);
        txtFilter = findViewById(R.id.filter);
        txtGradient = findViewById(R.id.gradient);
        txtSpiral = findViewById(R.id.spiral);
        //txtDrip = findViewById(R.id.drip);
        txtWings = findViewById(R.id.wings);
        seekBar = findViewById(R.id.seekbar);
        linearLayout = findViewById(R.id.mAdView);
        simpleFilterRecycler = findViewById(R.id.simpleFilterRecycler);
        gradientFilterRecycler = findViewById(R.id.gradient_filter_recycler);
        frameLayout = findViewById(R.id.frameLayout);
        relativeLayout = findViewById(R.id.relative_layout);
        txtBlur = findViewById(R.id.blur);
        txtBlackWhite = findViewById(R.id.black_white);
        //for Spiral calculate width and height
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels - Constants.dpToPx(this, 4);
        screenHeight = displayMetrics.heightPixels - Constants.dpToPx(this, 109);

        bitmapString = getIntent().getStringExtra("bitmap");
        String fileString = Uri.parse(bitmapString).getPath();
        getSourceProviderUri(new File(fileString));
        relativeLayout.post(() -> {
            try {
                getBitmap(bitmapString);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        SpannableString content = new SpannableString(getResources().getString(R.string.txt_filters));
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        txtFilter.setText(content);
        btnPersonImage.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnApply.setOnClickListener(this);
        txtFilter.setOnClickListener(this);
        txtGradient.setOnClickListener(this);
        txtBlur.setOnClickListener(this);
        txtBlackWhite.setOnClickListener(this);
        txtSpiral.setOnClickListener(this);
        txtWings.setOnClickListener(this);
        //txtDrip.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(this);
        //add items in list
        addItemSimpleList();
        gradientListItem();
        //addItemInFilterList()
        setSimpleFilterAdapter();
        setGradientFilterAdapter();
        loadAd();
        if (SupportedClass.checkConnection(this)) {
            loadInterstitialAd();
        } else {
            Log.e("Interstitial", "Failed to load");
        }
    }

    private void getSourceProviderUri(File file) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            sourceUri = Uri.fromFile(file);
        } else {
            sourceUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file);
        }
    }

    private void getDestProviderUri(File file) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            destinationUri = Uri.fromFile(file);
        } else {
            destinationUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file);
        }
    }

    public static void startAnim() {
        prog.show();
    }

    public static void stopAnim() {
        prog.dismiss();
    }

    void loadAd() {
        //ad
        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(getString(R.string.admob_banner_ads_id));
        linearLayout.addView(adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                Log.e("ADSTAG", "Banner onAdClosed()");
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                //super.onAdFailedToLoad(loadAdError);
                Log.e("ADSTAG", "Banner onAdFailedToLoad()" + loadAdError.getCode());
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }

            @Override
            public void onAdLoaded() {
                linearLayout.setVisibility(View.VISIBLE);
                super.onAdLoaded();
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
                Log.e("ADSTAG", "Banner onAdClicked()");
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!bitmapReturnString.equals("")) {
            bitmapString = bitmapReturnString;
            if (result != null) {
                getDestProviderUri((new File(Uri.parse(bitmapString).getPath())));
            } else {
                getSourceProviderUri((new File(Uri.parse(bitmapString).getPath())));
            }
            getBitmap(bitmapReturnString);
            bitmapReturnString = "";
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    void addItemSimpleList() {
        // add data in array list
        try {

            JSONArray jsonArrayColorFilter = new JSONArray(loadJSONFromAsset("color_filter.json"));
            simpleFilterArrayList.clear();
            for (int j = 0; j < jsonArrayColorFilter.length(); j++) {
                SimpleFilterModel dataModel = new Gson().fromJson(jsonArrayColorFilter.get(j).toString(),
                        SimpleFilterModel.class);

                simpleFilterArrayList.addAll(listOf(dataModel));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String loadJSONFromAsset(String fileName) {
        String json = null;
        try {
            InputStream is = this.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                json = new String(buffer, StandardCharsets.UTF_8);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    void gradientListItem() {
        try {
            gradientList.addAll(GradientFilterModel.gradientList);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    void getBitmap(String stringImage) {
        Uri selectedImage = Uri.parse(stringImage);
        try {
            InputStream imageStream = getContentResolver().openInputStream(selectedImage);
            originalBitmap = BitmapFactory.decodeStream(imageStream);//ImageUtils.getResizedBitmap(filterImage, 1024)
            //originalBitmap = ImageUtils.getBitmapResize(this,originalBitmap,originalBitmap.getWidth(),originalBitmap.getHeight());*/
            //originalBitmap = bitmapImage;
            //btnPersonImage.setImageBitmap(originalBitmap);
            Glide.with(this).load(originalBitmap).into(btnPersonImage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Bitmap viewToBitmap(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    void saveBitmapImage(Bitmap bitmap, Context context) {
        try {
            String fileName = getString(R.string.app_file) + System.currentTimeMillis() + Constants.KEY_JPG;
            if (Build.VERSION.SDK_INT >= 29) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                values.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
                values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + getString(R.string.app_folder2));
                /*values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" + folderName);
                values.put(MediaStore.Images.Media.IS_PENDING, true);*/
                // RELATIVE_PATH and IS_PENDING are introduced in API 29.

                uri =
                        context.getContentResolver().insert(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                values
                        );
                if (uri != null) {
                    saveImageToStream(bitmap, context.getContentResolver().openOutputStream(uri));
                    values.put(MediaStore.Images.Media.IS_PENDING, false);
                    context.getContentResolver().update(uri, values, null, null);
                    showInterstitial();
                }
                notifyMediaScannerService(EditingPicActivity.this, uri.getPath());
                Toast.makeText(getApplicationContext(), "Image Saved Successfully!", Toast.LENGTH_SHORT)
                        .show();
            } else {
                File directory =
                        new File(Environment.getExternalStorageDirectory().toString() + getString(R.string.app_folder));
                // getExternalStorageDirectory is deprecated in API 29

                if (!directory.exists()) {
                    directory.mkdirs();
                }
                //String fileName = System.currentTimeMillis() + ".png";
                File file = new File(directory, fileName);
                saveImageToStream(bitmap, new FileOutputStream(file));
                if (file.getAbsolutePath() != null) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
                    // .DATA is deprecated in API 29
                    uri = context.getContentResolver().insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            values
                    );
                    if (uri != null) {
                        showInterstitial();
                    }
                    notifyMediaScannerService(EditingPicActivity.this, directory.getAbsolutePath());
                }

                Toast.makeText(getApplicationContext(), "Image Saved Successfully!", Toast.LENGTH_SHORT)
                        .show();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void saveImageToStream(Bitmap bitmap, OutputStream outputStream) {
        if (outputStream != null) {
            try {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    void setSimpleFilterAdapter() {
        simpleFilterAdapter = new SimpleFilterAdapter(simpleFilterArrayList, this);
        simpleFilterRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        simpleFilterRecycler.setAdapter(simpleFilterAdapter);
    }

    void setGradientFilterAdapter() {
        gradientFilterAdapter = new GradientFilterAdapter(gradientList, this);
        gradientFilterRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        gradientFilterRecycler.setAdapter(gradientFilterAdapter);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back) {
            finish();
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        }
        if (v.getId() == R.id.filter) {
            gradientFilterRecycler.setVisibility(View.GONE);
            simpleFilterRecycler.setVisibility(View.VISIBLE);
            SpannableString content = new SpannableString(getResources().getString(R.string.txt_filters));
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            txtFilter.setText(content);
            txtGradient.setText(getResources().getString(R.string.gradient));
            txtBlackWhite.setText(getResources().getString(R.string.back_whaite));
            txtBlur.setText(getResources().getString(R.string.blur));
            txtSpiral.setText(getResources().getString(R.string.spiral));
            txtWings.setText(getResources().getString(R.string.wings));
            //txtDrip.setText(getResources().getString(R.string.dp));
        }
        if (v.getId() == R.id.gradient) {
            gradientFilterRecycler.setVisibility(View.VISIBLE);
            simpleFilterRecycler.setVisibility(View.GONE);
            txtFilter.setText(getResources().getString(R.string.txt_filters));
            txtBlackWhite.setText(getResources().getString(R.string.back_whaite));
            txtBlur.setText(getResources().getString(R.string.blur));
            txtSpiral.setText(getResources().getString(R.string.spiral));
            txtWings.setText(getResources().getString(R.string.wings));
            //txtDrip.setText(getResources().getString(R.string.dp));
            SpannableString content = new SpannableString(getResources().getString(R.string.gradient));
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            txtGradient.setText(content);
        }
        if (v.getId() == R.id.apply) {
            try {
                saveBitmapImage(viewToBitmap(relativeLayout), getApplicationContext());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (v.getId() == R.id.blur) {

            startAnim();
            txtFilter.setText(getResources().getString(R.string.txt_filters));
            txtGradient.setText(getResources().getString(R.string.gradient));
            txtBlackWhite.setText(getResources().getString(R.string.back_whaite));
            txtSpiral.setText(getResources().getString(R.string.spiral));
            txtWings.setText(getResources().getString(R.string.wings));
            //txtDrip.setText(getResources().getString(R.string.dp));
            SpannableString content = new SpannableString(getResources().getString(R.string.blur));
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            txtBlur.setText(content);

            //if filter is applied.
            if (result != null && isFilterUri) {
                Intent intent = new Intent(this, BlurSerpActivity.class);
                //Uri imageUri = getFileUri(result);
                if (destinationUri != null) {
                    intent.putExtra("uri", destinationUri.toString());
                    startActivity(intent);
                }
            }
            //send image without filter applied.
            if (!bitmapString.equals("") && !isFilterUri) {
                Intent intent = new Intent(this, BlurSerpActivity.class);
                intent.putExtra("uri", bitmapString);
                startActivity(intent);
            }
        }
        if (v.getId() == R.id.black_white) {
            startAnim();
            txtFilter.setText(getResources().getString(R.string.txt_filters));
            txtGradient.setText(getResources().getString(R.string.gradient));
            txtBlur.setText(getResources().getString(R.string.blur));
            txtSpiral.setText(getResources().getString(R.string.spiral));
            txtWings.setText(getResources().getString(R.string.wings));
            //txtDrip.setText(getResources().getString(R.string.dp));
            SpannableString content = new SpannableString(getResources().getString(R.string.back_whaite));
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            txtBlackWhite.setText(content);
            //if filter is applied.
            if (result != null && isFilterUri) {
                Intent intent = new Intent(this, BlackWhiteActivity.class);
                //Uri imageUri = getFileUri(result);
                if (destinationUri != null) {
                    intent.putExtra("uri", destinationUri.toString());
                    startActivity(intent);
                }
            }
            //send image without filter applied.
            if (!bitmapString.equals("") && !isFilterUri) {
                Intent intent = new Intent(this, BlackWhiteActivity.class);
                intent.putExtra("uri", bitmapString);
                startActivity(intent);
            }
        }
        if (v.getId() == R.id.spiral) {
            startAnim();
            txtFilter.setText(getResources().getString(R.string.txt_filters));
            txtGradient.setText(getResources().getString(R.string.gradient));
            txtBlur.setText(getResources().getString(R.string.blur));
            txtBlackWhite.setText(getResources().getString(R.string.back_whaite));
            txtWings.setText(getResources().getString(R.string.wings));
            //txtDrip.setText(getResources().getString(R.string.dp));
            SpannableString content = new SpannableString(getResources().getString(R.string.spiral));
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            txtSpiral.setText(content);
            //if filter is applied.
            if (result != null && isFilterUri) {
                try {
                    StoreManager.setCurrentCropedBitmap(EditingPicActivity.this, (Bitmap) null);
                    StoreManager.setCurrentCroppedMaskBitmap(EditingPicActivity.this, (Bitmap) null);

                    Bitmap bitmap = Constants.getBitmapFromUri(EditingPicActivity.this, destinationUri, (float) screenWidth, (float) screenHeight);
                    SpiralSerpActivity.setFaceBitmap(bitmap);
                    StoreManager.setCurrentOriginalBitmap(this, bitmap);
                    Intent intent = new Intent(this, SpiralSerpActivity.class);
                    //intent.putExtra("uri", imageUri.toString());
                    startActivity(intent);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            //send image without filter applied.
            if (!bitmapString.equals("") && !isFilterUri) {
                try {
                    StoreManager.setCurrentCropedBitmap(EditingPicActivity.this, (Bitmap) null);
                    StoreManager.setCurrentCroppedMaskBitmap(EditingPicActivity.this, (Bitmap) null);

                    Bitmap bitmap = Constants.getBitmapFromUri(EditingPicActivity.this, sourceUri, (float) screenWidth, (float) screenHeight);
                    SpiralSerpActivity.setFaceBitmap(bitmap);
                    StoreManager.setCurrentOriginalBitmap(this, bitmap);
                    Intent intent = new Intent(this, SpiralSerpActivity.class);
                    //intent.putExtra("uri", bitmapString);
                    startActivity(intent);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        if (v.getId() == R.id.wings) {
            startAnim();
            txtFilter.setText(getResources().getString(R.string.txt_filters));
            txtGradient.setText(getResources().getString(R.string.gradient));
            txtBlur.setText(getResources().getString(R.string.blur));
            txtBlackWhite.setText(getResources().getString(R.string.back_whaite));
            txtSpiral.setText(getResources().getString(R.string.spiral));
            //txtDrip.setText(getResources().getString(R.string.dp));
            SpannableString content = new SpannableString(getResources().getString(R.string.wings));
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            txtWings.setText(content);
            //if filter is applied.
            if (result != null && isFilterUri) {
                try {
                    StoreManager.setCurrentCropedBitmap(EditingPicActivity.this, (Bitmap) null);
                    StoreManager.setCurrentCroppedMaskBitmap(EditingPicActivity.this, (Bitmap) null);

                    Bitmap bitmap = Constants.getBitmapFromUri(EditingPicActivity.this, destinationUri, (float) screenWidth, (float) screenHeight);
                    WingsActivity.setFaceBitmap(bitmap);
                    StoreManager.setCurrentOriginalBitmap(this, bitmap);
                    Intent intent = new Intent(this, WingsActivity.class);
                    //intent.putExtra("uri", imageUri.toString());
                    startActivity(intent);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            //send image without filter applied.
            if (!bitmapString.equals("") && !isFilterUri) {
                try {
                    StoreManager.setCurrentCropedBitmap(EditingPicActivity.this, (Bitmap) null);
                    StoreManager.setCurrentCroppedMaskBitmap(EditingPicActivity.this, (Bitmap) null);

                    Bitmap bitmap = Constants.getBitmapFromUri(EditingPicActivity.this, sourceUri, (float) screenWidth, (float) screenHeight);
                    WingsActivity.setFaceBitmap(bitmap);
                    StoreManager.setCurrentOriginalBitmap(this, bitmap);
                    Intent intent = new Intent(this, WingsActivity.class);
                    //intent.putExtra("uri", bitmapString);
                    startActivity(intent);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private Uri getFileUri(Bitmap inImage) {
        try {
            File tempDir = Environment.getExternalStorageDirectory();
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


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void gradientFilterItemClick(GradientFilterModel gradientModel, int position) {
        if (position == 0) {
            applyJsonFilters("None", new PorterDuffXfermode(PorterDuff.Mode.SCREEN));
        }
        if (position == 1) {
            //linearGradient()
            addGradient(
                    originalBitmap,
                    ContextCompat.getColor(getApplicationContext(), R.color.amazon_shade),
                    ContextCompat.getColor(getApplicationContext(), R.color.purple_shade),
                    new PorterDuffXfermode(PorterDuff.Mode.DARKEN)
            );
        }
        if (position == 2) {
            addGradient(
                    originalBitmap,
                    ContextCompat.getColor(getApplicationContext(), R.color.shade_22_1),
                    ContextCompat.getColor(getApplicationContext(), R.color.shade_22_2),
                    new PorterDuffXfermode(PorterDuff.Mode.DARKEN)
            );
        }
        if (position == 3) {
            addGradient(
                    originalBitmap,
                    ContextCompat.getColor(getApplicationContext(), R.color.sky_blue),
                    ContextCompat.getColor(getApplicationContext(), R.color.grey_shade),
                    new PorterDuffXfermode(PorterDuff.Mode.OVERLAY)
            );
        }
        if (position == 4) {
            addGradient(
                    originalBitmap,
                    ContextCompat.getColor(getApplicationContext(), R.color.skin_two),
                    ContextCompat.getColor(getApplicationContext(), R.color.green_shade),
                    new PorterDuffXfermode(PorterDuff.Mode.SCREEN)
            );
        }
        if (position == 5) {
            addGradient(
                    originalBitmap,
                    ContextCompat.getColor(getApplicationContext(), R.color.skin_light),
                    ContextCompat.getColor(getApplicationContext(), R.color.dim_blue),
                    new PorterDuffXfermode(PorterDuff.Mode.OVERLAY)
            );
        }
        if (position == 6) {
            addGradient(
                    originalBitmap,
                    ContextCompat.getColor(getApplicationContext(), R.color.dim_grey),
                    ContextCompat.getColor(getApplicationContext(), R.color.dim_red),
                    new PorterDuffXfermode(PorterDuff.Mode.SCREEN)
            );
        }
        if (position == 7) {
            addGradient(
                    originalBitmap,
                    ContextCompat.getColor(getApplicationContext(), R.color.dim_purple),
                    ContextCompat.getColor(getApplicationContext(), R.color.dim_sky),
                    new PorterDuffXfermode(PorterDuff.Mode.OVERLAY)
            );
        }
        if (position == 8) {
            addGradient(
                    originalBitmap,
                    ContextCompat.getColor(getApplicationContext(), R.color.dim_purple),
                    ContextCompat.getColor(getApplicationContext(), R.color.dim_sky),
                    new PorterDuffXfermode(PorterDuff.Mode.DARKEN)
            );
        }
        if (position == 9) {
            addGradient(
                    originalBitmap,
                    ContextCompat.getColor(getApplicationContext(), R.color.shade_18_1),
                    ContextCompat.getColor(getApplicationContext(), R.color.shade_18_2),
                    new PorterDuffXfermode(PorterDuff.Mode.OVERLAY)
            );
        }
        if (position == 10) {
            addGradient(
                    originalBitmap,
                    ContextCompat.getColor(getApplicationContext(), R.color.shade_19_1),
                    ContextCompat.getColor(getApplicationContext(), R.color.shade_19_2),
                    new PorterDuffXfermode(PorterDuff.Mode.SCREEN)
            );
        }
        if (position == 11) {
            addGradient(
                    originalBitmap,
                    ContextCompat.getColor(getApplicationContext(), R.color.shade_20_1),
                    ContextCompat.getColor(getApplicationContext(), R.color.shade_20_2),
                    new PorterDuffXfermode(PorterDuff.Mode.SCREEN)
            );
        }
        if (position == 12) {
            addGradient(
                    originalBitmap,
                    ContextCompat.getColor(getApplicationContext(), R.color.shade_21_1),
                    ContextCompat.getColor(getApplicationContext(), R.color.shade_21_2),
                    new PorterDuffXfermode(PorterDuff.Mode.DARKEN)
            );
        }
        if (position == 13) {
            addGradient(
                    originalBitmap,
                    ContextCompat.getColor(getApplicationContext(), R.color.shade_23_1),
                    ContextCompat.getColor(getApplicationContext(), R.color.shade_23_2),
                    new PorterDuffXfermode(PorterDuff.Mode.LIGHTEN)
            );
        }
        if (position == 14) {
            addGradient(
                    originalBitmap,
                    ContextCompat.getColor(getApplicationContext(), R.color.shade_24_1),
                    ContextCompat.getColor(getApplicationContext(), R.color.shade_24_2),
                    new PorterDuffXfermode(PorterDuff.Mode.SCREEN)
            );
        }
        if (position == 15) {
            addGradient(
                    originalBitmap,
                    ContextCompat.getColor(getApplicationContext(), R.color.shade_25_1),
                    ContextCompat.getColor(getApplicationContext(), R.color.shade_25_2),
                    new PorterDuffXfermode(PorterDuff.Mode.OVERLAY)
            );
        }
        if (position == 16) {
            addGradient(
                    originalBitmap,
                    ContextCompat.getColor(getApplicationContext(), R.color.shade_26_1),
                    ContextCompat.getColor(getApplicationContext(), R.color.shade_26_2),
                    new PorterDuffXfermode(PorterDuff.Mode.OVERLAY)
            );
        }
        if (position == 17) {
            addGradient(
                    originalBitmap,
                    ContextCompat.getColor(getApplicationContext(), R.color.shade_27_1),
                    ContextCompat.getColor(getApplicationContext(), R.color.shade_27_2),
                    new PorterDuffXfermode(PorterDuff.Mode.SCREEN)
            );
        }
        if (position == 18) {
            addGradient(
                    originalBitmap,
                    ContextCompat.getColor(getApplicationContext(), R.color.shade_28_1),
                    ContextCompat.getColor(getApplicationContext(), R.color.shade_28_2),
                    new PorterDuffXfermode(PorterDuff.Mode.OVERLAY)
            );
        }
        if (position == 19) {
            addGradient(
                    originalBitmap,
                    ContextCompat.getColor(getApplicationContext(), R.color.shade_29_1),
                    ContextCompat.getColor(getApplicationContext(), R.color.shade_29_2),
                    new PorterDuffXfermode(PorterDuff.Mode.DARKEN)
            );
        }
        if (position == 20) {
        /*    Bitmap bitmap = (BitmapDrawable)(ContextCompat.getDrawable(
                    getApplicationContext(),
                    R.drawable.chk_radial
            ));
            lightGradient(
                    originalBitmap,
                    bitmap
            )*/
            /*  addSticker(
                  originalBitmap,
                  bitmap,
                  R.color.shade_30_1,
                  R.color.shade_30_2,
                  PorterDuffXfermode(PorterDuff.Mode.LIGHTEN)
              )*/
            addGradient(
                    originalBitmap,
                    ContextCompat.getColor(getApplicationContext(), R.color.shade_30_1),
                    ContextCompat.getColor(getApplicationContext(), R.color.shade_30_2),
                    new PorterDuffXfermode(PorterDuff.Mode.DARKEN)
            );
        }
        if (position == 21) {
            addGradient(
                    originalBitmap,
                    ContextCompat.getColor(getApplicationContext(), R.color.shade_31_1),
                    ContextCompat.getColor(getApplicationContext(), R.color.shade_31_2),
                    new PorterDuffXfermode(PorterDuff.Mode.LIGHTEN)
            );
        }
    }

    @Override
    public void simpleFilterItemClick(SimpleFilterModel drawable, int position) {
        if (position >= 0 && position <= 10) {
            if (position == 10) {
                applyJsonFilters(drawable.getFilterCategory(), new PorterDuffXfermode(PorterDuff.Mode.OVERLAY));
            } else {
                applyJsonFilters(drawable.getFilterCategory(), new PorterDuffXfermode(PorterDuff.Mode.SCREEN));
            }
        }
        if (position == 11 || position == 12 || position == 21 || position == 23 || position == 36 || position == 37) {
            applyJsonFilters(drawable.getFilterCategory(), new PorterDuffXfermode(PorterDuff.Mode.SCREEN));
        }
        if (position == 13 || position == 22 || position == 31 || position == 41) {
            applyJsonFilters(drawable.getFilterCategory(), new PorterDuffXfermode(PorterDuff.Mode.LIGHTEN));
        }
        if (position == 14 || position == 15 || position == 16 || position == 32 || position == 33) {
            applyJsonFilters(drawable.getFilterCategory(), new PorterDuffXfermode(PorterDuff.Mode.SCREEN));
        }
        if (position == 17 || position == 20 || position == 29) {
            applyJsonFilters(drawable.getFilterCategory(), new PorterDuffXfermode(PorterDuff.Mode.OVERLAY));
        }
        if (position == 18 || position == 19 || position == 30 || position == 34) {
            applyJsonFilters(drawable.getFilterCategory(), new PorterDuffXfermode(PorterDuff.Mode.DARKEN));
        }
        if (position == 24 || position == 25 || position == 26 || position == 27 || position == 28 || position == 35) {
            applyJsonFilters(drawable.getFilterCategory(), new PorterDuffXfermode(PorterDuff.Mode.SCREEN));
        }
        if (position == 39 || position == 40 || position == 42 || position == 43) {
            applyJsonFilters(drawable.getFilterCategory(), new PorterDuffXfermode(PorterDuff.Mode.SCREEN));
        }
        if (position == 44 || position == 38) {
            applyNormalJsonFilters(drawable.getFilterCategory());
        }
        if (position == 45 || position == 46 || position == 47 || position == 48 || position == 49 || position == 52) {
            applyJsonFilters(drawable.getFilterCategory(), new PorterDuffXfermode(PorterDuff.Mode.SCREEN));
        }
        if (position == 53) {
            applyJsonFilters(drawable.getFilterCategory(), new PorterDuffXfermode(PorterDuff.Mode.LIGHTEN));
        }
        if (position == 51) {
            applyJsonFilters(drawable.getFilterCategory(), new PorterDuffXfermode(PorterDuff.Mode.DARKEN));
        }
        if (position == 50 || position == 54) {
            applyJsonFilters(drawable.getFilterCategory(), new PorterDuffXfermode(PorterDuff.Mode.OVERLAY));
        }
    }

    private void applyJsonFilters(String categoryName, PorterDuffXfermode xfermode) {
        try {
            if (originalBitmap != null) {
                btnPersonImage.setAlpha(1.0f);
                btnPersonImage.clearColorFilter();
                //filterStatus = true

                if (!simpleFilterArrayList.isEmpty() && simpleFilterArrayList.size() > 0) {

                    for (int i = 0; i < simpleFilterArrayList.size(); i++) {

                        if (simpleFilterArrayList.get(i).getFilterCategory().equals(categoryName)) {

                            Log.e("myThread", Thread.currentThread().getName());

                            float alpha = 1.0f; // by default
                            float Rr =
                                    Float.parseFloat(simpleFilterArrayList.get(i).getTotalNumberOfFilter().get(0).getRgbArray().get(
                                            0
                                    ).getrColor());
                            float Rg =
                                    Float.parseFloat(simpleFilterArrayList.get(i).getTotalNumberOfFilter().get(0).getRgbArray().get(
                                            0
                                    ).getgColor());
                            float Rb =
                                    Float.parseFloat(simpleFilterArrayList.get(i).getTotalNumberOfFilter().get(0).getRgbArray().get(
                                            0
                                    ).getbColor());

                            float Gr =
                                    Float.parseFloat(simpleFilterArrayList.get(i).getTotalNumberOfFilter().get(1).getRgbArray().get(
                                            1
                                    ).getrColor());
                            float Gg =
                                    Float.parseFloat(simpleFilterArrayList.get(i).getTotalNumberOfFilter().get(1).getRgbArray().get(
                                            1
                                    ).getgColor());
                            float Gb =
                                    Float.parseFloat(simpleFilterArrayList.get(i).getTotalNumberOfFilter().get(1).getRgbArray().get(
                                            1
                                    ).getbColor());

                            float Br =
                                    Float.parseFloat(simpleFilterArrayList.get(i).getTotalNumberOfFilter().get(2).getRgbArray().get(
                                            2
                                    ).getrColor());
                            float Bg =
                                    Float.parseFloat(simpleFilterArrayList.get(i).getTotalNumberOfFilter().get(2).getRgbArray().get(
                                            2
                                    ).getgColor());
                            float Bb =
                                    Float.parseFloat(simpleFilterArrayList.get(i).getTotalNumberOfFilter().get(2).getRgbArray().get(
                                            2
                                    ).getbColor());

                            Log.e(
                                    "myColorValues",
                                    "\n ${Rr} -- ${Rg} -- ${Rb} \n ${Gr} -- ${Gg} -- ${Gb} \n ${Br} -- ${Bg} -- ${Bb}"
                            );

                            float[] src = new float[]{
                                    Rr, Rg, Rb, 0f, 0f,  //red
                                    Gr, Gg, Gb, 0f, 0f,  //green
                                    Br, Bg, Bb, 0f, 0f,  //blue
                                    0f, 0f, 0f, alpha, 0f // alpha
                            };

                            // The definition of ColorMatrix, and specify the RGBA matrix
                            ColorMatrix colorMatrix = new ColorMatrix();
                            colorMatrix.set(src);

                                /*val paint = Paint()
                            paint.colorFilter = ColorMatrixColorFilter(src)*/

                            Paint paint = new Paint();
                            //paint.blendMode = BlendMode.OVERLAY
                            paint.setXfermode(xfermode);
                            //paint.blendMode =
                            paint.setColorFilter(new ColorMatrixColorFilter(src));
                                /*   val originalBitmap: Bitmap? = ImageUtils.createRankingImg(
                                applicationContext,
                                R.drawable.person_image
                            )*/
                            //ImageUtils.getResizedBitmap(,1024)
                            //val originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.person_image)
                            result =
                                    originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
                            Canvas canvas = new Canvas(result);
                            canvas.drawBitmap(result, 0f, 0f, paint);

                            handler.post(() -> {
                                //btnPersonImage.setImageBitmap(result);
                                Glide.with(this).load(result).into(btnPersonImage);
                                getDestProviderUri(new File(Objects.requireNonNull(getFileUri(result)).getPath()));
                                isFilterUri = true;
                                // bgFilter.colorFilter = ColorMatrixColorFilter(src)
                            });

                                /* if (orginalBitmap != null) {

                                 filterBitmap = orginalBitmap!!.copy(Bitmap.Config.ARGB_8888, true)
                                 // val ret = Bitmap.createBitmap(copyMainBitmap.width, copyMainBitmap.height, copyMainBitmap.config)
                                 val canvas = Canvas(filterBitmap!!)
                                 canvas.drawBitmap(filterBitmap!!, 0f, 0f, paint)

                                 handler.post {
                                     Log.e("myThread", Thread.currentThread().name.toString())
                                     bgFilter.setImageBitmap(filterBitmap)
                                 }

                             }*/

                        }

                    }

                } else {
                    Toast.makeText(
                            this,
                            "${getString(R.string.file_not_found)}",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            } else {
                Toast.makeText(
                        getApplicationContext(),
                        "Tap to Select Image from gallery!",
                        Toast.LENGTH_SHORT
                ).show();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void applyNormalJsonFilters(String categoryName) {
        try {
            if (originalBitmap != null) {
                btnPersonImage.setAlpha(1.0f);
                btnPersonImage.clearColorFilter();
                //filterStatus = true

                if (!simpleFilterArrayList.isEmpty() && simpleFilterArrayList.size() > 0) {

                    for (int i = 0; i < simpleFilterArrayList.size(); i++) {

                        if (simpleFilterArrayList.get(i).getFilterCategory().equals(categoryName)) {

                            Log.e("myThread", Thread.currentThread().getName());

                            float alpha = 1.0f; // by default
                            float Rr =
                                    Float.parseFloat(simpleFilterArrayList.get(i).getTotalNumberOfFilter().get(0).getRgbArray().get(
                                            0
                                    ).getrColor());
                            float Rg =
                                    Float.parseFloat(simpleFilterArrayList.get(i).getTotalNumberOfFilter().get(0).getRgbArray().get(
                                            0
                                    ).getgColor());
                            float Rb =
                                    Float.parseFloat(simpleFilterArrayList.get(i).getTotalNumberOfFilter().get(0).getRgbArray().get(
                                            0
                                    ).getbColor());

                            float Gr =
                                    Float.parseFloat(simpleFilterArrayList.get(i).getTotalNumberOfFilter().get(1).getRgbArray().get(
                                            1
                                    ).getrColor());
                            float Gg =
                                    Float.parseFloat(simpleFilterArrayList.get(i).getTotalNumberOfFilter().get(1).getRgbArray().get(
                                            1
                                    ).getgColor());
                            float Gb =
                                    Float.parseFloat(simpleFilterArrayList.get(i).getTotalNumberOfFilter().get(1).getRgbArray().get(
                                            1
                                    ).getbColor());

                            float Br =
                                    Float.parseFloat(simpleFilterArrayList.get(i).getTotalNumberOfFilter().get(2).getRgbArray().get(
                                            2
                                    ).getrColor());
                            float Bg =
                                    Float.parseFloat(simpleFilterArrayList.get(i).getTotalNumberOfFilter().get(2).getRgbArray().get(
                                            2
                                    ).getgColor());
                            float Bb =
                                    Float.parseFloat(simpleFilterArrayList.get(i).getTotalNumberOfFilter().get(2).getRgbArray().get(
                                            2
                                    ).getbColor());

                            Log.e(
                                    "myColorValues",
                                    "\n ${Rr} -- ${Rg} -- ${Rb} \n ${Gr} -- ${Gg} -- ${Gb} \n ${Br} -- ${Bg} -- ${Bb}"
                            );

                            float[] src = new float[]{
                                    Rr, Rg, Rb, 0f, 0f,  //red
                                    Gr, Gg, Gb, 0f, 0f,  //green
                                    Br, Bg, Bb, 0f, 0f,  //blue
                                    0f, 0f, 0f, alpha, 0f // alpha
                            };

                            // The definition of ColorMatrix, and specify the RGBA matrix
                            ColorMatrix colorMatrix = new ColorMatrix();
                            colorMatrix.set(src);

                                /*val paint = Paint()
                            paint.colorFilter = ColorMatrixColorFilter(src)*/

                            Paint paint = new Paint();
                            //paint.blendMode = BlendMode.OVERLAY
                            //paint.setXfermode(xfermode);
                            //paint.blendMode =
                            paint.setColorFilter(new ColorMatrixColorFilter(src));
                                /*   val originalBitmap: Bitmap? = ImageUtils.createRankingImg(
                                applicationContext,
                                R.drawable.person_image
                            )*/
                            //ImageUtils.getResizedBitmap(,1024)
                            //val originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.person_image)
                            result =
                                    originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
                            Canvas canvas = new Canvas(result);
                            canvas.drawBitmap(result, 0f, 0f, paint);

                            handler.post(() -> {
                                //btnPersonImage.setImageBitmap(result);
                                Glide.with(this).load(result).into(btnPersonImage);
                                getDestProviderUri(new File(Objects.requireNonNull(getFileUri(result)).getPath()));

                                isFilterUri = true;
                                // bgFilter.colorFilter = ColorMatrixColorFilter(src)
                            });

                                /* if (orginalBitmap != null) {

                                 filterBitmap = orginalBitmap!!.copy(Bitmap.Config.ARGB_8888, true)
                                 // val ret = Bitmap.createBitmap(copyMainBitmap.width, copyMainBitmap.height, copyMainBitmap.config)
                                 val canvas = Canvas(filterBitmap!!)
                                 canvas.drawBitmap(filterBitmap!!, 0f, 0f, paint)

                                 handler.post {
                                     Log.e("myThread", Thread.currentThread().name.toString())
                                     bgFilter.setImageBitmap(filterBitmap)
                                 }

                             }*/

                        }

                    }

                } else {
                    Toast.makeText(
                            this,
                            "${getString(R.string.file_not_found)}",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            } else {
                Toast.makeText(
                        getApplicationContext(),
                        "Tap to Select Image from gallery!",
                        Toast.LENGTH_SHORT
                ).show();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void addGradient(Bitmap src, int color1, int color2, PorterDuffXfermode xfermode) {
        try {
            if (src != null) {
                btnPersonImage.setAlpha(1.0f);
                btnPersonImage.clearColorFilter();
                int w = src.getWidth();
                int h = src.getHeight();
                result = src.copy(Bitmap.Config.ARGB_8888, true);
                Canvas canvas = new Canvas(result);
                //canvas.drawBitmap(src, 0f, 0f, null)
                Paint paint = new Paint();
                LinearGradient shader = new LinearGradient(
                        0f, 0f, 0f, h,
                        color1, color2, Shader.TileMode.CLAMP
                );
                paint.setShader(shader);
                paint.setXfermode(xfermode);
                canvas.drawBitmap(src, 0f, 0f, paint);
                canvas.drawRect(0f, 0f, w, h, paint);
                handler.post(() -> {
                    //btnPersonImage.setImageBitmap(result);
                    Glide.with(this).load(result).into(btnPersonImage);
                    getDestProviderUri(new File(Objects.requireNonNull(getFileUri(result)).getPath()));
                    isFilterUri = true;
                });
            } else {
                Toast.makeText(
                        getApplicationContext(),
                        "Tap above image to select gallery Image!",
                        Toast.LENGTH_SHORT
                ).show();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
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
                        EditingPicActivity.this.interstitialAd = interstitialAd;
                        Log.i(TAG, "onAdLoaded");
                        interstitialAd.setFullScreenContentCallback(
                                new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        // Called when fullscreen content is dismissed.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        EditingPicActivity.this.interstitialAd = null;
                                        if (uri != null) {
                                            Log.e("Ad", "Ad did not load");
                                            Intent intent = new Intent(EditingPicActivity.this, ShareSerpActivity.class);
                                            intent.putExtra(Constants.KEY_URI_IMAGE, uri.toString());
                                            startActivity(intent);
                                            finish();
                                            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                                        }
                                        Log.d("TAG", "The ad was dismissed.");
                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                                        // Called when fullscreen content failed to show.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        EditingPicActivity.this.interstitialAd = null;
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
                    }
                });
    }

    private void showInterstitial() {
        // Show the ad if it's ready. Otherwise toast and restart the game.
        if (interstitialAd != null) {
            interstitialAd.show(this);
        } else {
            if (uri != null) {
                Log.e("Ad", "Ad did not load");
                Intent intent = new Intent(EditingPicActivity.this, ShareSerpActivity.class);
                intent.putExtra(Constants.KEY_URI_IMAGE, uri.toString());
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
            }
        }
    }

}