package com.photo.editor.picskills.photoeditorpro.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.photo.editor.picskills.photoeditorpro.BuildConfig;
import com.photo.editor.picskills.photoeditorpro.R;
import com.photo.editor.picskills.photoeditorpro.adapter.ViewPagerAdapter;
import com.photo.editor.picskills.photoeditorpro.fragments.SliderOne;
import com.photo.editor.picskills.photoeditorpro.fragments.SliderTwo;
import com.photo.editor.picskills.photoeditorpro.utils.Constants;
import com.photo.editor.picskills.photoeditorpro.utils.ImageUtils;
import com.photo.editor.picskills.photoeditorpro.utils.support.SupportedClass;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CameraTestActivity extends AppCompatActivity implements View.OnClickListener, MultiplePermissionsListener {
    private ImageView gallery, cameraImage;
    public String mSelectedImagePath;
    public String mSelectedOutputPath;
    public Uri mSelectedImageUri;
    protected static final int REQUEST_CODE_CAMERA = 0x2;
    protected static final int REQUEST_CODE_GALLERY = 0x3;
    private int isGallerySelected = 0;
    //private LinearLayout linearRate;
    //Timer variablse
   /* int currentPage = 0;
    Timer timer;
    final long DELAY_MS = 3000;//delay in milliseconds before task is to be executed
    final long PERIOD_MS = 3000;
    int NUM_PAGES = 3;*/

    //ads variables
    private static final String TAG = "CameraTestActivity";

    private InterstitialAd interstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_test);
        if (this.getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        gallery = findViewById(R.id.galler_icon);
        cameraImage = findViewById(R.id.camera_icon);
        //linearRate = findViewById(R.id.linear_rate);
        gallery.setOnClickListener(this);
        cameraImage.setOnClickListener(this);
        //linearRate.setOnClickListener(this);
        ViewPager2 viewPager2 = findViewById(R.id.view_pager2);
        ArrayList<Fragment> fragment = new ArrayList();

        //fragment.add(new SliderOne());
        fragment.add(new SliderTwo());

        ViewPagerAdapter adapter = new ViewPagerAdapter(this, fragment);
        viewPager2.setAdapter(adapter);
        /*After setting the adapter use the timer */
/*        final Handler handler = new Handler(Looper.getMainLooper());
        final Runnable Update = () -> {
            if (currentPage == NUM_PAGES - 1) {
                currentPage = 0;
            }
            viewPager2.setCurrentItem(currentPage++, true);
        };

        timer = new Timer(); // This will create a new Thread
        timer.schedule(new TimerTask() { // task to be scheduled
            @Override
            public void run() {
                handler.post(Update);
            }
        }, DELAY_MS, PERIOD_MS);*/

        //load interstitial ad
        if (SupportedClass.checkConnection(this)) {
            loadAd();
        } else {
            Log.e("Interstitial", "Failed to load");
        }

    }

    private void showRateDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_rate);
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView dismiss = (TextView) dialog.findViewById(R.id.btn_dismiss);
        TextView rate = (TextView) dialog.findViewById(R.id.btn_rate);
        TextView share = (TextView) dialog.findViewById(R.id.btn_share);
        dismiss.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        rate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ImageUtils.rateApp(CameraTestActivity.this);
                dialog.dismiss();
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ImageUtils.shareApp(CameraTestActivity.this);
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
    public void onClick(View v) {
        if (v.getId() == R.id.galler_icon) {
            isGallerySelected = 0;
            checkGalleryPermission();
        }
        if (v.getId() == R.id.camera_icon) {
            isGallerySelected = 1;
            checkGalleryPermission();
        }
      /*  if (v.getId() == R.id.linear_rate) {
            //showRateDialog();
        }*/
    }

    private void checkGalleryPermission() {
        Dexter.withContext(this)
                .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA)
                .withListener(this).check();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_CAMERA) {
            try {
                mSelectedImagePath = mSelectedOutputPath;
                save(mSelectedImagePath);
                if (SupportedClass.stringIsNotEmpty(mSelectedImagePath)) {
                    File fileImageClick = new File(mSelectedImagePath);
                    if (fileImageClick.exists()) {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                            mSelectedImageUri = Uri.fromFile(fileImageClick);
                        } else {
                            mSelectedImageUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", fileImageClick);
                        }
                        Intent intent = new Intent(this, EditingPicActivity.class);
                        intent.putExtra("bitmap", mSelectedImageUri.toString());
                        startActivity(intent);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        } else if (resultCode == RESULT_OK && data != null) {
            try {
                if (requestCode == REQUEST_CODE_GALLERY) {
                    mSelectedImageUri = data.getData();
                    if (mSelectedImageUri != null) {
                        mSelectedImagePath = Constants.convertMediaUriToPath(this, mSelectedImageUri);
                    } else {
                        Toast.makeText(this, getString(R.string.please_try_again), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    mSelectedImagePath = mSelectedOutputPath;
                }
                Bitmap bitmap = ImageUtils.compressImage(mSelectedImageUri.toString(), getApplicationContext());
                mSelectedImageUri = getFileUri(bitmap);
              /*  if (SupportedClass.stringIsNotEmpty(mSelectedImagePath)) {
                    Intent intent = new Intent(this, EditingPicActivity.class);
                    intent.putExtra("bitmap", mSelectedImageUri.toString());
                    startActivity(intent);
                }*/
                showInterstitial();

            } catch (Exception ex) {
                ex.printStackTrace();
            }

        } else {
            Log.e("TAG", "");
        }
    }

    public Uri getFileUri(Bitmap inImage) {
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

    public Bitmap resizeBitmap(String photoPath) {

        Log.e("Image", "resizeBitmap()");

        int targetW = 512;
        int targetH = 512;

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = 1;

        scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true; //Deprecated from  API 21

        return BitmapFactory.decodeFile(photoPath, bmOptions);
    }

    public Boolean save(String outFile) {

        Boolean status = true;

        try {

            Bitmap bmp = resizeBitmap(outFile);

            File file = new File(outFile);
            FileOutputStream fOut = new FileOutputStream(file);

            bmp.compress(Bitmap.CompressFormat.JPEG, 90, fOut);
            fOut.flush();
            fOut.close();

        } catch (Exception ex) {

            status = false;

            Log.e("Error", ex.getMessage());
        }

        return status;
    }

    private void openSettings() {
        Intent intent = new Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", "com.photo.editor.picskills.photoeditorpro", null)
        );
        startActivity(intent);
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Need Storage Permission");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.cancel();
        });
        builder.show();

    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.txt_select_picture)), REQUEST_CODE_GALLERY);
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", createImageFile());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        if (intent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CODE_CAMERA);
        }
    }

    private File createImageFile() {
        File storageDir = new File(Environment.getExternalStorageDirectory(), "Android/data/" + BuildConfig.APPLICATION_ID + "/CamPic/");
        storageDir.mkdirs();
        File image = null;
        try {
            image = new File(storageDir, getString(R.string.app_folder3));
            if (image.exists())
                image.delete();
            image.createNewFile();

            mSelectedOutputPath = image.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    @Override
    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
        if (multiplePermissionsReport.areAllPermissionsGranted()) {
            if (isGallerySelected == 0) {
                openGallery();
            }
            if (isGallerySelected == 1) {
                openCamera();
            }
        }
        if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
            showSettingsDialog();
        }
    }

    @Override
    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
        permissionToken.continuePermissionRequest();
    }

    public void loadAd() {
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
                        CameraTestActivity.this.interstitialAd = interstitialAd;
                        Log.i(TAG, "onAdLoaded");
                        interstitialAd.setFullScreenContentCallback(
                                new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        // Called when fullscreen content is dismissed.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        CameraTestActivity.this.interstitialAd = null;
                                        if (SupportedClass.stringIsNotEmpty(mSelectedImagePath)) {
                                            Intent intent = new Intent(getApplicationContext(), EditingPicActivity.class);
                                            intent.putExtra("bitmap", mSelectedImageUri.toString());
                                            startActivity(intent);
                                        }
                                        Log.d("TAG", "The ad was dismissed.");
                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                                        // Called when fullscreen content failed to show.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        CameraTestActivity.this.interstitialAd = null;
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
            Log.e("Ad","Ad did not load");
            if (SupportedClass.stringIsNotEmpty(mSelectedImagePath)) {
                Intent intent = new Intent(this, EditingPicActivity.class);
                intent.putExtra("bitmap", mSelectedImageUri.toString());
                startActivity(intent);
            }
        }
    }
}