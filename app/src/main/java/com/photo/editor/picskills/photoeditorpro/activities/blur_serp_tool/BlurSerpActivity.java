package com.photo.editor.picskills.photoeditorpro.activities.blur_serp_tool;

import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import androidx.core.view.MotionEventCompat;
import androidx.core.view.ViewCompat;

import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.photo.editor.picskills.photoeditorpro.R;
import com.photo.editor.picskills.photoeditorpro.activities.EditingPicActivity;
import com.photo.editor.picskills.photoeditorpro.activities.ParentActivity;
import com.photo.editor.picskills.photoeditorpro.activities.ShareSerpActivity;
import com.photo.editor.picskills.photoeditorpro.utils.Constants;
import com.photo.editor.picskills.photoeditorpro.utils.support.MyExceptionHandlerPix;
import com.photo.editor.picskills.photoeditorpro.utils.support.SupportedClass;
import com.zomato.photofilters.BuildConfig;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Objects;

import static com.photo.editor.picskills.photoeditorpro.activities.FilterLabActivity.notifyMediaScannerService;

public class BlurSerpActivity extends ParentActivity implements OnClickListener, OnSeekBarChangeListener {
    protected static final int REQUEST_CODE_CAMERA = 0x2;
    protected static final int REQUEST_CODE_GALLERY = 0x3;
    private static final String TAG = "BulrActivity";
    static Bitmap bitmapBlur;
    static Bitmap bitmapClear;
    static SeekBar blurrinessBar;
    static BrushSerpView brushView;
    static int displayHight;
    static int displayWidth;
    static SeekBar offsetBar;
    static ImageView prView;
    static SeekBar radiusBar;
    static String tempDrawPath;
    static File tempDrawPathFile;
    static TouchImageView tiv;
    private String oldSavedFileName;

    static {
        tempDrawPath = Environment.getExternalStorageDirectory().getPath() + "/Auto Background Changer";
    }

    public String mSelectedImagePath;
    public String mSelectedOutputPath;
    public Uri mSelectedImageUri;
    RelativeLayout imageViewContainer;
    private String imageSavePath;
    private ImageView colorBtn, grayBtn, offsetBtn, offsetDemo, zoomBtn;
    private boolean erase;
    private Bitmap hand;
    private LinearLayout blurView, offsetLayout;
    private ProgressDialog progressBlurring;
    private int startBlurSeekbarPosition;

    //editing pic activity things
    private String bitmapString = "";

    public BlurSerpActivity() {
        this.erase = true;
        this.imageSavePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/Auto Background Changer";
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static Bitmap blur(Context context, Bitmap bitmap, int i) {
        Bitmap copy = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Bitmap createBitmap = Bitmap.createBitmap(copy);
        RenderScript create = RenderScript.create(context);
        ScriptIntrinsicBlur create2 = ScriptIntrinsicBlur.create(create, Element.U8_4(create));
        Allocation createFromBitmap = Allocation.createFromBitmap(create, copy);
        Allocation createFromBitmap2 = Allocation.createFromBitmap(create, createBitmap);
        create2.setRadius((float) i);
        create2.setInput(createFromBitmap);
        create2.forEach(createFromBitmap2);
        createFromBitmap2.copyTo(createBitmap);
        return createBitmap;
    }

    public static Bitmap blurify(Bitmap bitmap, int i) {
        int i2 = i;
        Bitmap copy = bitmap.copy(bitmap.getConfig(), true);
        if (i2 < 1) {
            return null;
        }
        int width = copy.getWidth();
        int height = copy.getHeight();
        int i3 = width * height;
        int[] iArr = new int[i3];
        copy.getPixels(iArr, 0, width, 0, 0, width, height);
        int i4 = width - 1;
        int i5 = height - 1;
        int i6 = i2 + i2 + 1;
        int[] iArr2 = new int[i3];
        int[] iArr3 = new int[i3];
        int[] iArr4 = new int[i3];
        int[] iArr5 = new int[Math.max(width, height)];
        int i7 = (i6 + 1) >> 1;
        int i8 = i7 * i7;
        int i9 = i8 * 256;
        int[] iArr6 = new int[i9];
        for (int i10 = 0; i10 < i9; i10++) {
            iArr6[i10] = i10 / i8;
        }
        int[][] iArr7 = (int[][]) Array.newInstance(Integer.TYPE, new int[]{i6, 3});
        int i11 = i2 + 1;
        int i12 = 0;
        int i13 = 0;
        int i14 = 0;
        int p;
        int[] sir;
        while (i12 < height) {
            Bitmap bitmap2 = copy;
            int i15 = -i2;
            int i16 = 0;
            int i17 = 0;
            int i18 = 0;
            int i19 = 0;
            int i20 = 0;
            int i21 = 0;
            int i22 = 0;
            int i23 = 0;
            int i24 = 0;
            while (i15 <= i2) {
                int i25 = i5;
                int i26 = height;
                int i27 = iArr[Math.min(i4, Math.max(i15, 0)) + i13];
                int[] iArr8 = iArr7[i15 + i2];
                iArr8[0] = (i27 & 16711680) >> 16;
                iArr8[1] = (i27 & MotionEventCompat.ACTION_POINTER_INDEX_MASK) >> 8;
                iArr8[2] = i27 & 255;
                int abs = i11 - Math.abs(i15);
                i16 += iArr8[0] * abs;
                i17 += iArr8[1] * abs;
                i18 += iArr8[2] * abs;
                if (i15 > 0) {
                    i22 += iArr8[0];
                    i23 += iArr8[1];
                    i24 += iArr8[2];
                } else {
                    i19 += iArr8[0];
                    i20 += iArr8[1];
                    i21 += iArr8[2];
                }
                i15++;
                height = i26;
                i5 = i25;
            }
            int i28 = i5;
            int i29 = height;
            int i30 = i2;
            int i31 = 0;
            while (i31 < width) {
                iArr2[i13] = iArr6[i16];
                iArr3[i13] = iArr6[i17];
                iArr4[i13] = iArr6[i18];
                int i32 = i16 - i19;
                int i33 = i17 - i20;
                int i34 = i18 - i21;
                sir = iArr7[((i30 - i2) + i6) % i6];
                int[] iArr9 = sir;
                int i35 = i19 - iArr9[0];
                int i36 = i20 - iArr9[1];
                int i37 = i21 - iArr9[2];
                if (i12 == 0) {
                    iArr5[i31] = Math.min(i31 + i2 + 1, i4);
                }
                p = iArr[iArr5[i31] + i14];
                int[] iArr10 = sir;
                int i38 = i4;
                int i39 = p;
                iArr10[0] = (i39 & 16711680) >> 16;
                iArr10[1] = (i39 & MotionEventCompat.ACTION_POINTER_INDEX_MASK) >> 8;
                iArr10[2] = i39 & 255;
                int i40 = i22 + iArr10[0];
                int i41 = i23 + iArr10[1];
                int i42 = i24 + iArr10[2];
                i16 = i32 + i40;
                i17 = i33 + i41;
                i18 = i34 + i42;
                i30 = (i30 + 1) % i6;
                sir = iArr7[i30 % i6];
                int[] iArr11 = sir;
                i19 = i35 + iArr11[0];
                i20 = i36 + iArr11[1];
                i21 = i37 + iArr11[2];
                i22 = i40 - iArr11[0];
                i23 = i41 - iArr11[1];
                i24 = i42 - iArr11[2];
                i13++;
                i31++;
                i4 = i38;
            }
            int i43 = i4;
            i14 += width;
            i12++;
            copy = bitmap2;
            height = i29;
            i5 = i28;
        }
        Bitmap bitmap3 = copy;
        int i44 = i5;
        int i45 = height;
        int i46 = 0;
        while (i46 < width) {
            int i47 = 0;
            int bsum = 0;
            int gsum = 0;
            int rsum = 0;
            int boutsum = 0;
            int goutsum = 0;
            int routsum = 0;
            int binsum = 0;
            int ginsum = 0;
            int rinsum = 0;
            int i48 = -i2;
            int i49 = i48 * width;
            while (i48 <= i2) {
                int max = Math.max(i47, i49) + i46;
                sir = iArr7[i48 + i2];
                int[] iArr12 = sir;
                iArr12[i47] = iArr2[max];
                iArr12[1] = iArr3[max];
                iArr12[2] = iArr4[max];
                int rbs = i11 - Math.abs(i48);
                int i50 = rsum;
                int i51 = iArr2[max];
                rsum = i50 + (i51 * rbs);
                gsum += iArr3[max] * rbs;
                bsum += iArr4[max] * rbs;
                if (i48 > 0) {
                    int i53 = rinsum;
                    rinsum = i53 + sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    int i54 = routsum;
                    routsum = i54 + sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
                int i55 = i44;
                if (i48 < i55) {
                    i49 += width;
                }
                i48++;
                i44 = i55;
                i47 = 0;
            }
            int i56 = i44;
            int stackpointer = i2;
            int i57 = i46;
            int i58 = i45;
            int i59 = 0;
            while (i59 < i58) {
                int i60 = iArr[i57] & ViewCompat.MEASURED_STATE_MASK;
                int i61 = rsum;
                int i62 = i60 | (iArr6[i61] << 16);
                int i63 = gsum;
                int i64 = i62 | (iArr6[i63] << 8);
                int i65 = bsum;
                iArr[i57] = i64 | iArr6[i65];
                int i66 = routsum;
                rsum = i61 - i66;
                int i67 = goutsum;
                gsum = i63 - i67;
                int i68 = boutsum;
                bsum = i65 - i68;
                sir = iArr7[((stackpointer - i2) + i6) % i6];
                int[] iArr15 = sir;
                routsum = i66 - iArr15[0];
                goutsum = i67 - iArr15[1];
                boutsum = i68 - iArr15[2];
                if (i46 == 0) {
                    iArr5[i59] = Math.min(i59 + i11, i56) * width;
                }
                p = iArr5[i59] + i46;
                int[] iArr16 = sir;
                int i69 = p;
                iArr16[0] = iArr2[i69];
                iArr16[1] = iArr3[i69];
                iArr16[2] = iArr4[i69];
                rinsum += iArr16[0];
                ginsum += iArr16[1];
                binsum += iArr16[2];
                int i70 = rsum;
                int i71 = rinsum;
                rsum = i70 + i71;
                int i72 = gsum;
                int i73 = ginsum;
                gsum = i72 + i73;
                int i74 = bsum;
                int i75 = binsum;
                bsum = i74 + i75;
                stackpointer = (stackpointer + 1) % i6;
                sir = iArr7[stackpointer];
                int i76 = routsum;
                int[] iArr17 = sir;
                routsum = i76 + iArr17[0];
                goutsum += iArr17[1];
                boutsum += iArr17[2];
                rinsum = i71 - iArr17[0];
                ginsum = i73 - iArr17[1];
                binsum = i75 - iArr17[2];
                i57 += width;
                i59++;
                i2 = i;
            }
            i46++;
            i2 = i;
            i45 = i58;
            i44 = i56;
        }
        bitmap3.setPixels(iArr, 0, width, 0, 0, width, i45);
        return bitmap3;
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

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        setContentView(R.layout.activity_blur);
        EditingPicActivity.stopAnim();
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        bitmapString = getIntent().getStringExtra("uri");
        mSelectedImageUri = Uri.parse(bitmapString);
        //getMediaUri( new File(mSelectedImageUri.getPath()));

        getWindow().setFlags(1024, 1024);
        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandlerPix(BlurSerpActivity.this));
        Display defaultDisplay = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        defaultDisplay.getSize(point);
        displayWidth = point.x;
        displayHight = point.y;

        imageViewContainer = findViewById(R.id.imageViewContainer);
        /*RelativeLayout mAdView = findViewById(R.id.adView);
        loadBannerAds(mAdView);*/
        this.hand = BitmapFactory.decodeResource(getResources(), R.drawable.hand);
        this.hand = Bitmap.createScaledBitmap(this.hand, 120, 120, true);
        this.blurView = (LinearLayout) findViewById(R.id.blur_view);
        tiv = (TouchImageView) findViewById(R.id.drawingImageView);
        prView = (ImageView) findViewById(R.id.preview);
        this.offsetDemo = (ImageView) findViewById(R.id.offsetDemo);
        this.offsetLayout = (LinearLayout) findViewById(R.id.offsetLayout);
        bitmapClear = BitmapFactory.decodeResource(getResources(), R.drawable.me_test);
        bitmapBlur = blur(this, bitmapClear, tiv.opacity);
        //ImageView newBtn = (ImageView) findViewById(R.id.newBtn);
        ImageView resetBtn = (ImageView) findViewById(R.id.resetBtn);
        ImageView undoBtn = (ImageView) findViewById(R.id.undoBtn);
        ImageView fitBtn = (ImageView) findViewById(R.id.fitBtn);
        ImageView saveBtn = (ImageView) findViewById(R.id.saveBtn);
        ImageView shareBtn = (ImageView) findViewById(R.id.shareBtn);
        this.colorBtn = (ImageView) findViewById(R.id.colorBtn);
        this.grayBtn = (ImageView) findViewById(R.id.grayBtn);
        this.zoomBtn = (ImageView) findViewById(R.id.zoomBtn);
        this.offsetBtn = (ImageView) findViewById(R.id.offsetBtn);
        Button offsetOk = (Button) findViewById(R.id.offsetOk);
        ImageView ic_back = (ImageView) findViewById(R.id.ic_back);
        //newBtn.setOnClickListener(this);
        resetBtn.setOnClickListener(this);
        undoBtn.setOnClickListener(this);
        fitBtn.setOnClickListener(this);
        saveBtn.setOnClickListener(this);
        shareBtn.setOnClickListener(this);
        this.colorBtn.setOnClickListener(this);
        this.grayBtn.setOnClickListener(this);
        this.zoomBtn.setOnClickListener(this);
        this.offsetBtn.setOnClickListener(this);
        offsetOk.setOnClickListener(this);
        ic_back.setOnClickListener(this);
        offsetBar = (SeekBar) findViewById(R.id.offsetBar);
        radiusBar = (SeekBar) findViewById(R.id.widthSeekBar);
        blurrinessBar = (SeekBar) findViewById(R.id.blurrinessSeekBar);
        brushView = (BrushSerpView) findViewById(R.id.magnifyingView);
        brushView.setShapeRadiusRatio(((float) radiusBar.getProgress()) / ((float) radiusBar.getMax()));
        radiusBar.setMax(300);
        radiusBar.setProgress((int) tiv.radius);
        blurrinessBar.setMax(24);
        blurrinessBar.setProgress(tiv.opacity);
        offsetBar.setMax(100);
        offsetBar.setProgress(0);
        radiusBar.setOnSeekBarChangeListener(this);
        blurrinessBar.setOnSeekBarChangeListener(this);
        offsetBar.setOnSeekBarChangeListener(this);
        File file = new File(this.imageSavePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        clearTempBitmap();
        tiv.initDrawing();
        this.progressBlurring = new ProgressDialog(this);
        CustomDialog customDialog = new CustomDialog(this);
        String str = "yes";
        if (PreferenceManager.getDefaultSharedPreferences(this).getString("show", str).equals(str)) {
            customDialog.show();
        }
        getProviderUri( new File(mSelectedImageUri.getPath()));
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
    private void getMediaUri(File file) {
        MediaScannerConnection.scanFile(this,
                new String[]{file.getAbsolutePath()}, null,
                (path, uri) -> {
                    Log.i("onScanCompleted", uri.getPath());
                    mSelectedImageUri = uri;
                    if (SupportedClass.stringIsNotEmpty(bitmapString)) {
                        onPhotoTakenApp();
                    }
                });
    }

    public void clearTempBitmap() {
        tempDrawPathFile = new File(tempDrawPath);
        if (!tempDrawPathFile.exists()) {
            tempDrawPathFile.mkdirs();
        }
        if (tempDrawPathFile.isDirectory()) {
            for (String file : tempDrawPathFile.list()) {
                new File(tempDrawPathFile, file).delete();
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


    public void onClick(View view) {
        Log.wtf("Click : ", "Inside onclick");
        switch (view.getId()) {
            case R.id.colorBtn:
                this.erase = true;
                tiv.mode = 0;
                this.grayBtn.setBackgroundColor(getResources().getColor(R.color.design_back));
                this.zoomBtn.setBackgroundColor(getResources().getColor(R.color.design_back));
                this.offsetBtn.setBackgroundColor(getResources().getColor(R.color.design_back));
                this.colorBtn.setBackgroundColor(getResources().getColor(R.color.black));
                TouchImageView touchImageView = tiv;
                touchImageView.splashBitmap = bitmapClear;
                touchImageView.updateRefMetrix();
                tiv.changeShaderBitmap();
                tiv.coloring = true;
                return;
            case R.id.fitBtn:
                TouchImageView touchImageView2 = tiv;
                touchImageView2.saveScale = 1.0f;
                touchImageView2.radius = ((float) (radiusBar.getProgress() + 50)) / tiv.saveScale;
                brushView.setShapeRadiusRatio(((float) (radiusBar.getProgress() + 50)) / tiv.saveScale);
                tiv.fitScreen();
                tiv.updatePreviewPaint();
                return;
            case R.id.grayBtn:
                this.erase = false;
                tiv.mode = 0;
                this.colorBtn.setBackgroundColor(getResources().getColor(R.color.design_back));
                this.zoomBtn.setBackgroundColor(getResources().getColor(R.color.design_back));
                this.offsetBtn.setBackgroundColor(getResources().getColor(R.color.design_back));
                this.grayBtn.setBackgroundColor(getResources().getColor(R.color.black));
                TouchImageView touchImageView3 = tiv;
                touchImageView3.splashBitmap = bitmapBlur;
                touchImageView3.updateRefMetrix();
                tiv.changeShaderBitmap();
                tiv.coloring = false;
                return;
            case R.id.ic_back:
                onBackPressed();
                return;
            /*case R.id.newBtn:
                //showPicImageDialog();
                return;*/
            case R.id.offsetBtn:
                this.offsetLayout.setVisibility(View.VISIBLE);
                this.offsetBtn.setBackgroundColor(getResources().getColor(R.color.black));
                this.colorBtn.setBackgroundColor(getResources().getColor(R.color.design_back));
                this.zoomBtn.setBackgroundColor(getResources().getColor(R.color.design_back));
                this.grayBtn.setBackgroundColor(getResources().getColor(R.color.design_back));

                return;
            case R.id.offsetOk:
                this.offsetLayout.setVisibility(View.INVISIBLE);
                this.offsetBtn.setBackgroundColor(0);
                return;
            case R.id.resetBtn:
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
                        clearTempBitmap();
                        BlurSerpActivity.tiv.initDrawing();
                        BlurSerpActivity.tiv.saveScale = 1.0f;
                        BlurSerpActivity.tiv.fitScreen();
                        BlurSerpActivity.tiv.updatePreviewPaint();
                        BlurSerpActivity.tiv.updatePaintBrush();
                    }
                });
                progressDialog.show();
                return;
            case R.id.saveBtn:
                showSaveDialog();
                return;
            case R.id.undoBtn:
                String sb2 = tempDrawPath + "/canvasLog" + (tiv.currentImageIndex - 1) + ".jpg";
                Log.wtf("Current Image ", sb2);
                if (new File(sb2).exists()) {
                    tiv.drawingBitmap = null;
                    Options options = new Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    options.inMutable = true;
                    tiv.drawingBitmap = BitmapFactory.decodeFile(sb2, options);
                    TouchImageView touchImageView4 = tiv;
                    touchImageView4.setImageBitmap(touchImageView4.drawingBitmap);
                    tiv.canvas.setBitmap(tiv.drawingBitmap);
                    String sb3 = tempDrawPath + "canvasLog" + tiv.currentImageIndex + ".jpg";
                    File file = new File(sb3);
                    if (file.exists()) {
                        file.delete();
                    }
                    TouchImageView touchImageView5 = tiv;
                    touchImageView5.currentImageIndex--;
                }
                return;
            case R.id.zoomBtn:
                tiv.mode = 1;
                this.zoomBtn.setBackgroundColor(getResources().getColor(R.color.black));
                this.offsetBtn.setBackgroundColor(getResources().getColor(R.color.design_back));
                this.colorBtn.setBackgroundColor(getResources().getColor(R.color.design_back));
                this.grayBtn.setBackgroundColor(getResources().getColor(R.color.design_back));
                return;
            default:
                return;
        }
    }

    public void saveImage() {

/*        FullScreenAdManager.fullScreenAdsCheckPref(BlurActivity.this, FullScreenAdManager.ALL_PREFS.ATTR_SAVED_IMAGE_CLICKED, new FullScreenAdManager.GetBackPointer() {
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

                    Uri uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

                    FileOutputStream fos = (FileOutputStream) contentResolver.openOutputStream(Objects.requireNonNull(uri));
                    tiv.drawingBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    Objects.requireNonNull(fos);
                    if (uri != null) {
                        Intent intent = new Intent(BlurSerpActivity.this, ShareSerpActivity.class);
                        intent.putExtra(Constants.KEY_URI_IMAGE, uri.toString());
                        startActivity(intent);
                        finish();
                    }
                    notifyMediaScannerService(BlurSerpActivity.this, uri.getPath());
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
                    Uri uri = SupportedClass.addImageToGallery(BlurSerpActivity.this, file.getAbsolutePath());
                    if (uri != null) {
                        Intent intent = new Intent(BlurSerpActivity.this, ShareSerpActivity.class);
                        intent.putExtra(Constants.KEY_URI_IMAGE, uri.toString());
                        startActivity(intent);
                        finish();
                    }
                    notifyMediaScannerService(BlurSerpActivity.this, myDir.getAbsolutePath());
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

    public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
        int id = seekBar.getId();
        notifyMediaScannerService(this,mSelectedImageUri.getPath());
        if (id == R.id.blurrinessSeekBar) {
            BrushSerpView brushView2 = brushView;
            brushView2.isBrushSize = false;
            brushView2.setShapeRadiusRatio(tiv.radius);
            brushView.brushSerpSize.setPaintOpacity(blurrinessBar.getProgress());
            brushView.invalidate();
            TouchImageView touchImageView = tiv;
            touchImageView.opacity = i + 1;
            touchImageView.updatePaintBrush();
        } else if (id == R.id.offsetBar) {
            Bitmap copy = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888).copy(Bitmap.Config.ARGB_8888, true);
            Canvas canvas = new Canvas(copy);
            Paint paint = new Paint(1);
            paint.setColor(-16711936);
            canvas.drawCircle(150.0f, (float) (150 - offsetBar.getProgress()), 30.0f, paint);
            canvas.drawBitmap(this.hand, 95.0f, 150.0f, null);
            this.offsetDemo.setImageBitmap(copy);
        } else if (id == R.id.widthSeekBar) {
            BrushSerpView brushView3 = brushView;
            brushView3.isBrushSize = true;
            brushView3.brushSerpSize.setPaintOpacity(255);
            brushView.setShapeRadiusRatio(((float) (radiusBar.getProgress() + 50)) / tiv.saveScale);
            brushView.invalidate();
            tiv.radius = ((float) (radiusBar.getProgress() + 50)) / tiv.saveScale;
            tiv.updatePaintBrush();
        }
    }

    void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
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
                    Uri uri = getFileUri(bitmap);
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

    private void showBackDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_leave);
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView button = (TextView) dialog.findViewById(R.id.btn_yes);
        TextView button2 = (TextView) dialog.findViewById(R.id.btn_no);
        button.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (tempDrawPathFile.exists()) {
                    deleteRecursive(tempDrawPathFile);
                }
                finish();
                dialog.dismiss();
                overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
            }
        });
        button2.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (tiv.drawingBitmap != null) {
                    Bitmap bitmap = tiv.drawingBitmap;
                    Uri uri = getFileUri(bitmap);
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
        dialog.setOnDismissListener(new OnDismissListener() {
            public void onDismiss(DialogInterface dialogInterface) {

            }
        });
        dialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_CAMERA) {
            mSelectedImagePath = mSelectedOutputPath;
            if (SupportedClass.stringIsNotEmpty(mSelectedImagePath)) {
                File fileImageClick = new File(mSelectedImagePath);
                if (fileImageClick.exists()) {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                        mSelectedImageUri = Uri.fromFile(fileImageClick);
                    } else {
                        mSelectedImageUri = FileProvider.getUriForFile(BlurSerpActivity.this, BuildConfig.APPLICATION_ID + ".provider", fileImageClick);
                    }
                    onPhotoTakenApp();
                }
            }
        } else if (data != null && data.getData() != null) {
            if (requestCode == REQUEST_CODE_GALLERY) {
                mSelectedImageUri = data.getData();
                if (mSelectedImageUri != null) {
                    mSelectedImagePath = Constants.convertMediaUriToPath(BlurSerpActivity.this, mSelectedImageUri);
                } else {
                    Toast.makeText(this, getString(R.string.please_try_again), Toast.LENGTH_SHORT).show();
                }
            } else {
                mSelectedImagePath = mSelectedOutputPath;
            }
            if (SupportedClass.stringIsNotEmpty(mSelectedImagePath)) {
                onPhotoTakenApp();
            }

        } else {
            Log.e("TAG", "");
        }
    }

    public void onPhotoTakenApp() {
        imageViewContainer.post(new Runnable() {
            @Override
            public void run() {
                try {
                    bitmapClear = Constants.getBitmapFromUri(BlurSerpActivity.this, mSelectedImageUri, (float) imageViewContainer.getMeasuredWidth(), (float) imageViewContainer.getMeasuredHeight());
                    bitmapBlur = blur(getApplicationContext(), bitmapClear, tiv.opacity);
                    clearTempBitmap();
                    tiv.initDrawing();
                    tiv.saveScale = 1.0f;
                    tiv.fitScreen();
                    tiv.updatePreviewPaint();
                    tiv.updatePaintBrush();
                    grayBtn.setBackgroundColor(getResources().getColor(R.color.design_back));
                    zoomBtn.setBackgroundColor(getResources().getColor(R.color.design_back));
                    colorBtn.setBackgroundColor(getResources().getColor(R.color.black));
                    offsetBtn.setBackgroundColor(getResources().getColor(R.color.design_back));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        showBackDialog();
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
        int id = seekBar.getId();
        if (id == R.id.blurrinessSeekBar) {
            this.blurView.setVisibility(View.VISIBLE);
            this.startBlurSeekbarPosition = blurrinessBar.getProgress();
        } else if (id == R.id.offsetBar) {
            this.offsetDemo.setVisibility(View.VISIBLE);
            Bitmap copy = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888).copy(Bitmap.Config.ARGB_8888, true);
            Canvas canvas = new Canvas(copy);
            Paint paint = new Paint(1);
            paint.setColor(-16711936);
            canvas.drawCircle(150.0f, (float) (150 - offsetBar.getProgress()), 30.0f, paint);
            canvas.drawBitmap(this.hand, 95.0f, 150.0f, null);
            this.offsetDemo.setImageBitmap(copy);
        } else if (id == R.id.widthSeekBar) {
            brushView.setVisibility(View.VISIBLE);
        }
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
        this.blurView.setVisibility(View.INVISIBLE);
        if (seekBar.getId() == R.id.blurrinessSeekBar) {
            AlertDialog create = new Builder(this).create();
            create.setTitle("Warning");
            create.setMessage("Changing Bluriness will lose your current drawing progress!");
            create.setButton(-1, "Continue", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    new BlurUpdater().execute();
                    //colorBtn.setBackgroundColor(getResources().getColor(R.color.selected));
                    //grayBtn.setBackgroundColor(-1);
                }
            });
            create.setButton(-2, "Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    BlurSerpActivity.blurrinessBar.setProgress(startBlurSeekbarPosition);
                }
            });
            create.show();
        } else if (seekBar.getId() == R.id.offsetBar) {
            this.offsetDemo.setVisibility(View.INVISIBLE);
        } else if (seekBar.getId() == R.id.widthSeekBar) {
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

    private class BlurUpdater extends AsyncTask<String, Integer, Bitmap> {

        public void onPreExecute() {
            super.onPreExecute();
            progressBlurring.setMessage("Blurring...");
            progressBlurring.setIndeterminate(true);
            progressBlurring.setCancelable(false);
            progressBlurring.show();
        }

        public Bitmap doInBackground(String... strArr) {
            BlurSerpActivity.bitmapBlur = BlurSerpActivity.blur(getApplicationContext(), BlurSerpActivity.bitmapClear, BlurSerpActivity.tiv.opacity);
            return BlurSerpActivity.bitmapBlur;
        }

        public void onProgressUpdate(Integer... numArr) {
            super.onProgressUpdate(numArr);
        }

        public void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (!erase) {
                BlurSerpActivity.tiv.splashBitmap = BlurSerpActivity.bitmapBlur;
                BlurSerpActivity.tiv.updateRefMetrix();
                BlurSerpActivity.tiv.changeShaderBitmap();
            }
            clearTempBitmap();
            BlurSerpActivity.tiv.initDrawing();
            BlurSerpActivity.tiv.saveScale = 1.0f;
            BlurSerpActivity.tiv.fitScreen();
            BlurSerpActivity.tiv.updatePreviewPaint();
            BlurSerpActivity.tiv.updatePaintBrush();
            if (progressBlurring.isShowing()) {
                progressBlurring.dismiss();
            }
        }
    }

    class CustomDialog extends Dialog {
        Context ctx;

        public CustomDialog(Context context) {
            super(context);
            this.ctx = context;
        }

        public CustomDialog(Context context, int i) {
            super(context, i);
            this.ctx = context;
        }

        protected CustomDialog(Context context, boolean z, OnCancelListener onCancelListener) {
            super(context, z, onCancelListener);
            this.ctx = context;
        }

        public void show() {
            PreferenceManager.getDefaultSharedPreferences(BlurSerpActivity.this).edit().putString("show", "no").commit();
        }
    }
}
