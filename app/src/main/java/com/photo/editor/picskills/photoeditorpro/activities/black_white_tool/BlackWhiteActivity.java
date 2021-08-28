package com.photo.editor.picskills.photoeditorpro.activities.black_white_tool;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Shader;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.photo.editor.picskills.photoeditorpro.R;
import com.photo.editor.picskills.photoeditorpro.activities.EditingPicActivity;
import com.photo.editor.picskills.photoeditorpro.activities.ParentActivity;
import com.photo.editor.picskills.photoeditorpro.activities.ShareSerpActivity;
import com.photo.editor.picskills.photoeditorpro.utils.Constants;
import com.photo.editor.picskills.photoeditorpro.utils.support.MyExceptionHandlerPix;
import com.photo.editor.picskills.photoeditorpro.utils.support.SupportedClass;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Vector;

import yuku.ambilwarna.AmbilWarnaDialog;

import static com.photo.editor.picskills.photoeditorpro.activities.FilterLabActivity.notifyMediaScannerService;


public class BlackWhiteActivity extends ParentActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    public static BrushSerpView brushView;
    public static Bitmap colorBitmap;
    public static int displayHight;
    public static int displayWidth;
    public static Bitmap grayBitmap;
    public static SeekBar offsetBar;
    public static SeekBar opacityBar;
    public static ImageView prView;
    public static SeekBar radiusBar;
    public static String tempDrawPath;
    public static File tempDrawPathFile;
    public static TouchImageView tiv;
    public static Vector vector;
    private String oldSavedFileName;
    //editing pic activity things
    private String bitmapString = "";

    static {
        String sb = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/Auto Background Changer";
        tempDrawPath = sb;
    }

    public Uri mSelectedImageUri;
    private RelativeLayout imageViewContainer;
    private ImageView colorBtn, grayBtn, offsetBtn, offsetDemo, recolorBtn, zoomBtn;
    private String imageSavePath;
    private Bitmap hand;
    private LinearLayout offsetLayout;
    private Runnable runnableCode;
    private ProgressDialog saveLoader;
    //ads variables
    private static final String TAG = "BlackWhiteActivity";
    private InterstitialAd interstitialAd;
    private Uri uri;


    public BlackWhiteActivity() {
        this.imageSavePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/Auto Background Changer";
    }

    public static Uri getFileUri(Activity activity, String filePath) {
        Uri outputUri;
        if (Build.VERSION.SDK_INT >= 24) {
            outputUri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".provider", new File(filePath));
        } else {
            outputUri = Uri.fromFile(new File(filePath));
        }
        return outputUri;

    }

    public void update() {
    }


    @SuppressLint("ApplySharedPref")
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        try {
            requestWindowFeature(1);
            getWindow().setFlags(1024, 1024);
            setContentView(R.layout.activity_color_splash);
            EditingPicActivity.stopAnim();
            if (getSupportActionBar()
                    != null) {
                this.getSupportActionBar().hide();
            }
            bitmapString = getIntent().getStringExtra("uri");
            mSelectedImageUri = Uri.parse(bitmapString);
            Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandlerPix(BlackWhiteActivity.this));

            /*RelativeLayout mAdView = findViewById(R.id.adView);
            loadBannerAds(mAdView);*/
            imageViewContainer = findViewById(R.id.imageViewContainer);
            this.saveLoader = new ProgressDialog(this);
            ProgressDialog videoLoader = new ProgressDialog(this);
            videoLoader.setMessage("Video is LOADING");
            videoLoader.setIndeterminate(true);
            videoLoader.setCancelable(false);
            videoLoader.setTitle("Please wait...");
            vector = new Vector();
            Display defaultDisplay = getWindowManager().getDefaultDisplay();
            Point point = new Point();
            defaultDisplay.getSize(point);
            displayWidth = point.x;
            displayHight = point.y;
            this.hand = BitmapFactory.decodeResource(getResources(), R.drawable.hand);
            this.hand = Bitmap.createScaledBitmap(this.hand, 120, 120, true);
            tiv = (TouchImageView) findViewById(R.id.drawingImageView);
            prView = (ImageView) findViewById(R.id.preview);
            colorBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.me_test);
            grayBitmap = toGrayScale(colorBitmap);

            this.offsetLayout = (LinearLayout) findViewById(R.id.offsetLayout);
            ImageView resetBtn = (ImageView) findViewById(R.id.resetBtn);
            ImageView undoBtn = (ImageView) findViewById(R.id.undoBtn);
            ImageView fitBtn = (ImageView) findViewById(R.id.fitBtn);
            ImageView saveBtn = (ImageView) findViewById(R.id.saveBtn);
            ImageView shareBtn = (ImageView) findViewById(R.id.shareBtn);
            this.colorBtn = (ImageView) findViewById(R.id.colorBtn);
            this.recolorBtn = (ImageView) findViewById(R.id.recolorBtn);
            this.grayBtn = (ImageView) findViewById(R.id.grayBtn);
            this.zoomBtn = (ImageView) findViewById(R.id.zoomBtn);
            ImageView ic_back = (ImageView) findViewById(R.id.ic_back);
            this.offsetBtn = (ImageView) findViewById(R.id.offsetBtn);
            Button offsetOk = (Button) findViewById(R.id.offsetOk);
            this.offsetDemo = (ImageView) findViewById(R.id.offsetDemo);
            resetBtn.setOnClickListener(this);
            undoBtn.setOnClickListener(this);
            fitBtn.setOnClickListener(this);
            saveBtn.setOnClickListener(this);
            shareBtn.setOnClickListener(this);
            this.colorBtn.setOnClickListener(this);
            this.recolorBtn.setOnClickListener(this);
            this.grayBtn.setOnClickListener(this);
            this.zoomBtn.setOnClickListener(this);
            this.offsetBtn.setOnClickListener(this);
            offsetOk.setOnClickListener(this);
            ic_back.setOnClickListener(this);
            offsetBar = (SeekBar) findViewById(R.id.offsetBar);
            radiusBar = (SeekBar) findViewById(R.id.widthSeekBar);
            opacityBar = (SeekBar) findViewById(R.id.opacitySeekBar);
            brushView = (BrushSerpView) findViewById(R.id.magnifyingView);
            brushView.setShapeRadiusRatio(((float) radiusBar.getProgress()) / ((float) radiusBar.getMax()));
            radiusBar.setMax(300);
            radiusBar.setProgress((int) tiv.radius);
            offsetBar.setProgress(0);
            offsetBar.setMax(100);
            opacityBar.setMax(240);
            opacityBar.setProgress(tiv.opacity);
            radiusBar.setOnSeekBarChangeListener(this);
            opacityBar.setOnSeekBarChangeListener(this);
            offsetBar.setOnSeekBarChangeListener(this);
            File file = new File(this.imageSavePath);
            if (!file.exists()) {
                file.mkdirs();
            }
            clearTempBitmap();
            tiv.initDrawing();
            PreferenceManager.getDefaultSharedPreferences(this).edit().putString("show", "no").commit();
            update();
            final Handler handler = new Handler();
            this.runnableCode = new Runnable() {
                public void run() {
                    handler.postDelayed(runnableCode, 2000);
                }
            };
            handler.post(this.runnableCode);
            getProviderUri(new File(mSelectedImageUri.getPath()));
            if (SupportedClass.checkConnection(this)) {
                loadInterstitialAd();
            } else {
                Log.e("Interstitial", "Failed to load");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    private void getProviderUri(File file){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            mSelectedImageUri = Uri.fromFile(file);
            if (SupportedClass.stringIsNotEmpty(bitmapString)) {
                onPhotoTakenApp();
            }
        } else {
            mSelectedImageUri = FileProvider.getUriForFile(this, com.photo.editor.picskills.photoeditorpro.BuildConfig.APPLICATION_ID + ".provider", file);
            if (SupportedClass.stringIsNotEmpty(bitmapString)) {
                onPhotoTakenApp();
            }
        }
    }


    public void clearTempBitmap() {
        tempDrawPathFile = new File(tempDrawPath);
        if (!tempDrawPathFile.exists()) {
            tempDrawPathFile.mkdirs();
        }
        if (tempDrawPathFile.isDirectory() || tempDrawPathFile.list() != null) {
            for (String file : tempDrawPathFile.list()) {
                new File(tempDrawPathFile, file).delete();
            }
        }
    }

    public Bitmap toGrayScale(Bitmap bitmap) {
        Bitmap createBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(createBitmap);
        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0.0f);
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        canvas.drawBitmap(bitmap, 0.0f, 0.0f, paint);
        return createBitmap;
    }

    private Uri getUriFromFile(Bitmap inImage) {
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

    public void onClick(View view) {
        Log.wtf("Click : ", "Inside onclick");
        switch (view.getId()) {
            case R.id.colorBtn:
                break;
            case R.id.fitBtn:
                TouchImageView touchImageView = tiv;
                touchImageView.saveScale = 1.0f;
                touchImageView.radius = ((float) (radiusBar.getProgress() + 50)) / tiv.saveScale;
                brushView.setShapeRadiusRatio(((float) (radiusBar.getProgress() + 50)) / tiv.saveScale);
                tiv.fitScreen();
                tiv.updatePreviewPaint();
                return;
            case R.id.grayBtn:
                tiv.mode = 0;
                this.recolorBtn.setBackgroundColor(getResources().getColor(R.color.design_back));
                this.colorBtn.setBackgroundColor(getResources().getColor(R.color.design_back));
                this.zoomBtn.setBackgroundColor(getResources().getColor(R.color.design_back));
                this.grayBtn.setBackgroundColor(getResources().getColor(R.color.black));
                this.offsetBtn.setBackgroundColor(getResources().getColor(R.color.design_back));
                tiv.splashBitmap = toGrayScale(colorBitmap);
                tiv.updateRefMetrix();
                tiv.changeShaderBitmap();
                tiv.coloring = -2;
                return;
            case R.id.ic_back:
                showBackDialog();
                return;
            case R.id.offsetBtn:
                this.recolorBtn.setBackgroundColor(getResources().getColor(R.color.design_back));
                this.colorBtn.setBackgroundColor(getResources().getColor(R.color.design_back));
                this.zoomBtn.setBackgroundColor(getResources().getColor(R.color.design_back));
                this.grayBtn.setBackgroundColor(getResources().getColor(R.color.design_back));
                this.offsetBtn.setBackgroundColor(getResources().getColor(R.color.black));
                this.offsetLayout.setVisibility(View.VISIBLE);
                return;
            case R.id.offsetOk:
                this.offsetLayout.setVisibility(View.INVISIBLE);
                return;
            case R.id.recolorBtn:
                tiv.mode = 0;
                this.recolorBtn.setBackgroundColor(getResources().getColor(R.color.black));
                this.colorBtn.setBackgroundColor(getResources().getColor(R.color.design_back));
                this.zoomBtn.setBackgroundColor(getResources().getColor(R.color.design_back));
                this.grayBtn.setBackgroundColor(getResources().getColor(R.color.design_back));
                this.offsetBtn.setBackgroundColor(getResources().getColor(R.color.design_back));
                new AmbilWarnaDialog(this, Color.parseColor("#4149b6"), true, new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    public void onCancel(AmbilWarnaDialog ambilWarnaDialog) {
                    }

                    public void onOk(AmbilWarnaDialog ambilWarnaDialog, int i) {
                        BlackWhiteActivity blackWhiteActivity = BlackWhiteActivity.this;
                        BlackWhiteActivity.grayBitmap = blackWhiteActivity.toGrayScale(BlackWhiteActivity.colorBitmap);
                        Canvas canvas = new Canvas(BlackWhiteActivity.grayBitmap);
                        Paint paint = new Paint();
                        paint.setColorFilter(new ColorMatrixColorFilter(new float[]{((float) ((i >> 16) & 255)) / 256.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, ((float) ((i >> 8) & 255)) / 256.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, ((float) (i & 255)) / 256.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, ((float) ((i >> 24) & 255)) / 256.0f, 0.0f}));
                        canvas.drawBitmap(BlackWhiteActivity.grayBitmap, 0.0f, 0.0f, paint);
                        BlackWhiteActivity.tiv.splashBitmap = BlackWhiteActivity.grayBitmap;
                        BlackWhiteActivity.tiv.updateRefMetrix();
                        BlackWhiteActivity.tiv.changeShaderBitmap();
                        BlackWhiteActivity.tiv.coloring = i;
                    }
                }).show();
                return;
            case R.id.resetBtn:
                resetImage();
                return;
            case R.id.saveBtn:
                showSaveDialog();
                return;
            case R.id.undoBtn:
                int i = tiv.currentImageIndex - 1;
                String sb2 = tempDrawPath + "/canvasLog" + i + ".jpg";
                Log.wtf("Current Image ", sb2);
                if (new File(sb2).exists()) {
                    tiv.drawingBitmap = null;
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    options.inMutable = true;
                    tiv.drawingBitmap = BitmapFactory.decodeFile(sb2, options);
                    TouchImageView touchImageView2 = tiv;
                    touchImageView2.setImageBitmap(touchImageView2.drawingBitmap);
                    tiv.canvas.setBitmap(tiv.drawingBitmap);
                    String sb3 = tempDrawPath + "canvasLog" + tiv.currentImageIndex + ".jpg";
                    File file = new File(sb3);
                    if (file.exists()) {
                        file.delete();
                    }
                    TouchImageView touchImageView3 = tiv;
                    touchImageView3.currentImageIndex--;
                    Vector vector2 = vector;
                    vector2.remove(vector2.size() - 1);
                }
                return;
            case R.id.zoomBtn:
                tiv.mode = 1;
                this.recolorBtn.setBackgroundColor(getResources().getColor(R.color.design_back));
                this.colorBtn.setBackgroundColor(getResources().getColor(R.color.design_back));
                this.zoomBtn.setBackgroundColor(getResources().getColor(R.color.black));
                this.grayBtn.setBackgroundColor(getResources().getColor(R.color.design_back));
                this.offsetBtn.setBackgroundColor(getResources().getColor(R.color.design_back));
                return;
            default:
                return;
        }
        tiv.mode = 0;
        this.recolorBtn.setBackgroundColor(getResources().getColor(R.color.design_back));
        this.colorBtn.setBackgroundColor(getResources().getColor(R.color.black));
        this.zoomBtn.setBackgroundColor(getResources().getColor(R.color.design_back));
        this.grayBtn.setBackgroundColor(getResources().getColor(R.color.design_back));
        this.offsetBtn.setBackgroundColor(getResources().getColor(R.color.design_back));
        TouchImageView touchImageView4 = tiv;
        touchImageView4.splashBitmap = colorBitmap;
        touchImageView4.updateRefMetrix();
        tiv.changeShaderBitmap();
        tiv.coloring = -1;
    }

    public void saveImage() {

        /*FullScreenAdManager.fullScreenAdsCheckPref(ColorSplashActivity.this, FullScreenAdManager.ALL_PREFS.ATTR_SAVED_IMAGE_CLICKED, new FullScreenAdManager.GetBackPointer() {
            @Override
            public void returnAction() {*/
        if (tiv.drawingBitmap != null) {
            String fileName = getString(R.string.app_file) + System.currentTimeMillis() + Constants.KEY_JPG;
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                    ContentResolver contentResolver = getContentResolver();
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                    contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
                    contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + getString(R.string.app_folder2));

                    uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

                    FileOutputStream fos = (FileOutputStream) contentResolver.openOutputStream(Objects.requireNonNull(uri));
                    tiv.drawingBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    Objects.requireNonNull(fos);
                    if (uri != null) {
                       showInterstitial();
                    }
                    notifyMediaScannerService(BlackWhiteActivity.this, uri.getPath());
                    Toast.makeText(getApplicationContext(), "Image Saved Successfully!", Toast.LENGTH_SHORT)
                            .show();

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
                    tiv.drawingBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                    out.close();
                    //
                    uri = SupportedClass.addImageToGallery(BlackWhiteActivity.this, file.getAbsolutePath());
                    if (uri != null) {
                       showInterstitial();
                    }
                    notifyMediaScannerService(BlackWhiteActivity.this, myDir.getAbsolutePath());
                    Toast.makeText(getApplicationContext(), "Image Saved Successfully!", Toast.LENGTH_SHORT)
                            .show();
                }
                if (tempDrawPathFile.exists()) {
                    deleteRecursive(tempDrawPathFile);
                }

            } catch (Exception e) {
                return;
//                        Log.i("Error",e.getMessage());
            } finally {
                imageViewContainer.setDrawingCacheEnabled(false);
            }
        }

       /*     }
        });*/

    }

    public void saveImage(Bitmap bitmap) {
        String currentPath = this.imageSavePath + "/" + System.currentTimeMillis() + ".jpg";
        File file = new File(currentPath);
        Bitmap grayScale = toGrayScale(bitmap);
        Bitmap copy = grayScale.copy(Bitmap.Config.ARGB_8888, true);
        float width = ((float) copy.getWidth()) / ((float) tiv.drawingBitmap.getWidth());
        Canvas canvas = new Canvas(copy);
        Paint paint4 = new Paint(1);
        int i = -10;
        int r4 = 1;
        Paint paint;
        Paint paint2;
        Paint paint3 = null;
        while (vector.size() > 0) {
            MyPath myPath = (MyPath) vector.elementAt(0);
            if (i == myPath.color) {
                paint = paint4;
            } else {
                if (myPath.color == -1) {
                    paint3 = new Paint();
                    paint3.setShader(new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
                } else {
                    if (myPath.color == -2) {
                        paint3 = new Paint();
                        paint3.setShader(new BitmapShader(grayScale, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
                    } else {
                        Bitmap copy2 = grayScale.copy(Bitmap.Config.ARGB_8888, true);
                        Canvas canvas2 = new Canvas(copy2);
                        Paint paint5 = new Paint();
                        float[] fArr = new float[20];
                        fArr[0] = ((float) ((myPath.color >> 16) & 255)) / 256.0f;
                        fArr[r4] = 0.0f;
                        fArr[2] = 0.0f;
                        fArr[3] = 0.0f;
                        fArr[4] = 0.0f;
                        fArr[5] = 0.0f;
                        fArr[6] = ((float) ((myPath.color >> 8) & 255)) / 256.0f;
                        fArr[7] = 0.0f;
                        fArr[8] = 0.0f;
                        fArr[9] = 0.0f;
                        fArr[10] = 0.0f;
                        fArr[11] = 0.0f;
                        fArr[12] = ((float) (myPath.color & 255)) / 256.0f;
                        fArr[13] = 0.0f;
                        fArr[14] = 0.0f;
                        fArr[15] = 0.0f;
                        fArr[16] = 0.0f;
                        fArr[17] = 0.0f;
                        fArr[18] = ((float) ((myPath.color >> 24) & 255)) / 256.0f;
                        fArr[19] = 0.0f;
                        paint5.setColorFilter(new ColorMatrixColorFilter(fArr));
                        canvas2.drawBitmap(grayScale, 0.0f, 0.0f, paint5);
                        Paint paint6 = new Paint(1);
                        paint6.setShader(new BitmapShader(copy2, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
                        int i2 = myPath.color;
                        paint6.setStyle(Paint.Style.STROKE);
                        paint6.setStrokeWidth(myPath.r * width);
                        paint6.setStrokeCap(Paint.Cap.ROUND);
                        paint6.setStrokeJoin(Paint.Join.ROUND);
                        paint6.setMaskFilter(new BlurMaskFilter(width * 30.0f, BlurMaskFilter.Blur.NORMAL));
                        canvas.drawPath(myPath.convertPath(width), paint6);
                    }
                }
                paint2 = paint3;
                paint = paint2;
            }
            i = myPath.color;
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(myPath.r * width);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setMaskFilter(new BlurMaskFilter(width * 30.0f, BlurMaskFilter.Blur.NORMAL));
            canvas.drawPath(myPath.convertPath(width), paint);
            paint4 = paint;
            r4 = 1;
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            copy.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            grayScale.recycle();
            copy.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (file.exists()) {
            MyMediaConnectorClient myMediaConnectorClient = new MyMediaConnectorClient(currentPath);
            MediaScannerConnection mediaScannerConnection = new MediaScannerConnection(this, myMediaConnectorClient);
            myMediaConnectorClient.setScanner(mediaScannerConnection);
            mediaScannerConnection.connect();
            Intent intent = new Intent(BlackWhiteActivity.this, ShareSerpActivity.class);
            intent.putExtra(Constants.KEY_URI_IMAGE, currentPath);
            startActivity(intent);
        }
    }

    public void resetImage() {
        final Dialog progressDialog = new Dialog(this);
        progressDialog.setContentView(R.layout.dialog_reset);
        progressDialog.setCancelable(false);
        Window window = progressDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView button = (TextView) progressDialog.findViewById(R.id.cancel);
        TextView button2 = (TextView) progressDialog.findViewById(R.id.save);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                progressDialog.dismiss();
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                progressDialog.dismiss();
                BlackWhiteActivity.grayBitmap = toGrayScale(BlackWhiteActivity.colorBitmap);
                clearTempBitmap();
                BlackWhiteActivity.tiv.initDrawing();
                BlackWhiteActivity.tiv.saveScale = 1.0f;
                BlackWhiteActivity.tiv.fitScreen();
                BlackWhiteActivity.tiv.updatePreviewPaint();
                BlackWhiteActivity.tiv.updatePaintBrush();
                BlackWhiteActivity.vector.clear();
            }
        });
        progressDialog.show();
    }

    public void onBackPressed() {
        showBackDialog();
    }

    public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
        int id = seekBar.getId();
        if (id == R.id.offsetBar) {
            Bitmap copy = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888).copy(Bitmap.Config.ARGB_8888, true);
            Canvas canvas = new Canvas(copy);
            Paint paint = new Paint(1);
            paint.setColor(-16711936);
            canvas.drawCircle(150.0f, (float) (150 - offsetBar.getProgress()), 30.0f, paint);
            canvas.drawBitmap(this.hand, 95.0f, 150.0f, null);
            this.offsetDemo.setImageBitmap(copy);
        } else if (id == R.id.opacitySeekBar) {
            BrushSerpView brushView2 = brushView;
            brushView2.isBrushSize = false;
            brushView2.setShapeRadiusRatio(tiv.radius);
            brushView.brushSize.setPaintOpacity(opacityBar.getProgress());
            brushView.invalidate();
            TouchImageView touchImageView = tiv;
            touchImageView.opacity = i + 15;
            touchImageView.updatePaintBrush();
        } else if (id == R.id.widthSeekBar) {
            BrushSerpView brushView3 = brushView;
            brushView3.isBrushSize = true;
            brushView3.brushSize.setPaintOpacity(255);
            brushView.setShapeRadiusRatio(((float) (radiusBar.getProgress() + 50)) / tiv.saveScale);
            String sb = radiusBar.getProgress() + "";
            Log.wtf("radious :", sb);
            brushView.invalidate();
            tiv.radius = ((float) (radiusBar.getProgress() + 50)) / tiv.saveScale;
            tiv.updatePaintBrush();
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
                if (tiv.drawingBitmap != null) {
                    Bitmap bitmap = tiv.drawingBitmap;
                    Uri uri = getUriFromFile(bitmap);
                    assert uri != null;
                    EditingPicActivity.bitmapReturnString = uri.toString();
                    if (tempDrawPathFile.exists()) {
                        deleteRecursive(tempDrawPathFile);
                    }
                    finish();
                }
                dialog.dismiss();
                overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                saveImage();
                dialog.dismiss();
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            public void onDismiss(DialogInterface dialogInterface) {
            }
        });
        dialog.show();
    }

    void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
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
                if (tempDrawPathFile.exists()) {
                    deleteRecursive(tempDrawPathFile);
                }
                finish();
                dialog.dismiss();
                overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (tiv.drawingBitmap != null) {
                    Bitmap bitmap = tiv.drawingBitmap;
                    Uri uri = getUriFromFile(bitmap);
                    assert uri != null;
                    EditingPicActivity.bitmapReturnString = uri.toString();
                    if (tempDrawPathFile.exists()) {
                        deleteRecursive(tempDrawPathFile);
                    }
                    finish();
                }
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

    public void onPhotoTakenApp() {
        imageViewContainer.post(new Runnable() {
            @Override
            public void run() {
                try {
                    colorBitmap = Constants.getBitmapFromUri(BlackWhiteActivity.this, mSelectedImageUri, (float) imageViewContainer.getMeasuredWidth(), (float) imageViewContainer.getMeasuredHeight());
                    grayBitmap = toGrayScale(colorBitmap);
                    clearTempBitmap();
                    tiv.initDrawing();
                    tiv.saveScale = 1.0f;
                    tiv.fitScreen();
                    tiv.updatePreviewPaint();
                    tiv.updatePaintBrush();
                    vector.clear();
                    grayBtn.setBackgroundColor(getResources().getColor(R.color.design_back));
                    zoomBtn.setBackgroundColor(getResources().getColor(R.color.design_back));
                    colorBtn.setBackgroundColor(getResources().getColor(R.color.black));
                    offsetBtn.setBackgroundColor(getResources().getColor(R.color.design_back));
                    recolorBtn.setBackgroundColor(getResources().getColor(R.color.design_back));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
        if (seekBar.getId() != R.id.offsetBar) {
            brushView.setVisibility(View.VISIBLE);
            return;
        }
        this.offsetDemo.setVisibility(View.VISIBLE);
        Bitmap copy = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888).copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(copy);
        Paint paint = new Paint(1);
        paint.setColor(-16711936);
        canvas.drawCircle(150.0f, (float) (150 - offsetBar.getProgress()), 30.0f, paint);
        canvas.drawBitmap(this.hand, 95.0f, 150.0f, null);
        this.offsetDemo.setImageBitmap(copy);
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
        if (seekBar.getId() == R.id.offsetBar) {
            this.offsetDemo.setVisibility(View.INVISIBLE);
        } else {
            brushView.setVisibility(View.INVISIBLE);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public void onResume() {
//        this.busWrapper.register(this);
//        this.networkEvents.register();
        super.onResume();
    }

    public void onPause() {
//        this.busWrapper.unregister(this);
//        this.networkEvents.unregister();
        super.onPause();
    }

    public void onDestroy() {
        super.onDestroy();
    }

    class ResetDialog extends Dialog {
        Context ctx;

        public ResetDialog(Context context) {
            super(context);
            this.ctx = context;
        }

        public ResetDialog(Context context, int i) {
            super(context, i);
            this.ctx = context;
        }

        protected ResetDialog(Context context, boolean z, OnCancelListener onCancelListener) {
            super(context, z, onCancelListener);
            this.ctx = context;
        }

        public void show() {
            requestWindowFeature(1);
            View inflate = LayoutInflater.from(this.ctx).inflate(R.layout.dialog_reset, null);
            setCanceledOnTouchOutside(false);
            setCancelable(false);
            super.setContentView(inflate);
            TextView button = (TextView) inflate.findViewById(R.id.cancel);
            TextView button2 = (TextView) inflate.findViewById(R.id.save);

            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    ResetDialog.this.dismiss();
                }
            });
            button2.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    ResetDialog.this.dismiss();
                    BlackWhiteActivity.grayBitmap = toGrayScale(BlackWhiteActivity.colorBitmap);
                    clearTempBitmap();
                    BlackWhiteActivity.tiv.initDrawing();
                    BlackWhiteActivity.tiv.saveScale = 1.0f;
                    BlackWhiteActivity.tiv.fitScreen();
                    BlackWhiteActivity.tiv.updatePreviewPaint();
                    BlackWhiteActivity.tiv.updatePaintBrush();
                    BlackWhiteActivity.vector.clear();
                }
            });
            super.show();
        }
    }

    private class SaveThread extends AsyncTask<Bitmap, Integer, Void> {
        private SaveThread() {
        }


        public void onPreExecute() {
            super.onPreExecute();
            saveLoader.setMessage("Saving in HD quality");
            saveLoader.setIndeterminate(true);
            saveLoader.setCancelable(false);
            saveLoader.show();
        }


        public Void doInBackground(Bitmap... bitmapArr) {
            saveImage(bitmapArr[0]);
            return null;
        }


        public void onProgressUpdate(Integer... numArr) {
            super.onProgressUpdate(numArr);
        }


        public void onPostExecute(Void voidR) {
            super.onPostExecute(voidR);
            if (saveLoader.isShowing()) {
                saveLoader.dismiss();
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
                        BlackWhiteActivity.this.interstitialAd = interstitialAd;
                        Log.i(TAG, "onAdLoaded");
                        interstitialAd.setFullScreenContentCallback(
                                new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        // Called when fullscreen content is dismissed.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        BlackWhiteActivity.this.interstitialAd = null;
                                        if (uri != null) {
                                            Log.e("Ad", "Ad did not load");
                                            Intent intent = new Intent(BlackWhiteActivity.this, ShareSerpActivity.class);
                                            intent.putExtra(Constants.KEY_URI_IMAGE, uri.toString());
                                            startActivity(intent);
                                            finish();
                                        }
                                        Log.d("TAG", "The ad was dismissed.");
                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                                        // Called when fullscreen content failed to show.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        BlackWhiteActivity.this.interstitialAd = null;
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
            if (uri != null) {
                Log.e("Ad", "Ad did not load");
                Intent intent = new Intent(BlackWhiteActivity.this, ShareSerpActivity.class);
                intent.putExtra(Constants.KEY_URI_IMAGE, uri.toString());
                startActivity(intent);
                finish();
            }
        }
    }

}
