package com.photo.editor.picskills.photoeditorpro.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.photo.editor.picskills.photoeditorpro.BuildConfig;
import com.photo.editor.picskills.photoeditorpro.R;
import com.photo.editor.picskills.photoeditorpro.utils.Constants;
import com.photo.editor.picskills.photoeditorpro.utils.ImageUtils;
import com.photo.editor.picskills.photoeditorpro.utils.support.SupportedClass;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import jp.shts.android.storiesprogressview.StoriesProgressView;

public class StatusActivity extends AppCompatActivity implements StoriesProgressView.StoriesListener, View.OnTouchListener, View.OnClickListener
        , MultiplePermissionsListener {
    private ImageView imageView, arrowTop;
    private StoriesProgressView storiesProgressView;
    private long pressTime = 0L;
    long limit = 500L;
    private Button tryNow;
    //open gallery for blur activity
    public String mSelectedImagePath;
    public String mSelectedOutputPath;
    public Uri mSelectedImageUri;
    protected static final int REQUEST_CODE_CAMERA = 0x2;
    protected static final int REQUEST_CODE_GALLERY = 0x3;
    protected static final int REQUEST_CODE_CROPPING = 0x4;
    private int isGallerySelected = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_status);
        if (getSupportActionBar()
                != null) {
            this.getSupportActionBar().hide();
        }
        imageView = findViewById(R.id.status_image);
        arrowTop = findViewById(R.id.arrow_top);
        tryNow = findViewById(R.id.try_now);
        tryNow.setOnClickListener(this);
        arrowTop.setOnClickListener(this);
        storiesProgressView = findViewById(R.id.stories);
        setModelStory();
        storiesProgressView.setStoriesCount(1); // <- set stories
        storiesProgressView.setStoryDuration(10000); // <- set a story duration  1200L
        storiesProgressView.setStoriesListener(this); // <- set listener
        storiesProgressView.startStories(); // <- start progress
       /* Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            public void run() {
                finish();
            }
        }, 10000);*/
        imageView.setOnTouchListener(this);
        arrowTop.setOnClickListener(this);
    }

    private void setModelStory() {
        if (MainActivity.isModelSelected == 0) {
            imageView.setImageDrawable(ContextCompat.getDrawable(
                    this,
                    R.drawable.wings_status
            ));
        }
        if (MainActivity.isModelSelected == 1) {
            imageView.setImageDrawable(ContextCompat.getDrawable(
                    this,
                    R.drawable.spiral_status
            ));
        }
        if (MainActivity.isModelSelected == 2) {
            imageView.setImageDrawable(ContextCompat.getDrawable(
                    this,
                    R.drawable.blur_status
            ));
        }
        if (MainActivity.isModelSelected == 3) {
            imageView.setImageDrawable(ContextCompat.getDrawable(
                    this,
                    R.drawable.b_and_w_status
            ));
        }
        if (MainActivity.isModelSelected == 4) {
            imageView.setImageDrawable(ContextCompat.getDrawable(
                    this,
                    R.drawable.filter_status
            ));
        }
        if (MainActivity.isModelSelected == 5) {
            imageView.setImageDrawable(ContextCompat.getDrawable(
                    this,
                    R.drawable.gradient_status
            ));
        }
    }

    @Override
    public void onNext() {
        Toast.makeText(this, "onNext", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPrev() {
        Toast.makeText(this, "onPrev", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onComplete() {
        finish();
    }

    @Override
    protected void onDestroy() {
        // Very important !
        storiesProgressView.destroy();
        super.onDestroy();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.getId() == R.id.status_image) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    pressTime = System.currentTimeMillis();
                    storiesProgressView.pause();
                    return true;
                case MotionEvent.ACTION_UP:
                    long now = System.currentTimeMillis();
                    storiesProgressView.resume();
                    return limit < now - pressTime;
            }
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.try_now) {
            itemClick();
        }
        if (v.getId() == R.id.arrow_top) {
            itemClick();
        }
    }

    private void itemClick() {
        if (MainActivity.isModelSelected == 0) {
            showPicImageDialog();
        }
        if (MainActivity.isModelSelected == 1) {
            showPicImageDialog();
        }
        if (MainActivity.isModelSelected == 2) {
            showPicImageDialog();
        }
        if (MainActivity.isModelSelected == 3) {
            showPicImageDialog();
        }
        if (MainActivity.isModelSelected == 4) {
            showPicImageDialog();
        }
        if (MainActivity.isModelSelected == 5) {
            showPicImageDialog();
        }
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
                        Intent intent = new Intent(StatusActivity.this, CropPhotoActivity.class);
                        intent.putExtra("cropUri", mSelectedImageUri.toString());
                        startActivityForResult(intent, REQUEST_CODE_CROPPING);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        } else if (resultCode == RESULT_OK && data != null && requestCode == REQUEST_CODE_GALLERY) {
            try {
                mSelectedImageUri = data.getData();
                if (mSelectedImageUri != null) {
                    mSelectedImagePath = Constants.convertMediaUriToPath(this, mSelectedImageUri);
                    Bitmap bitmap = ImageUtils.compressImage(mSelectedImageUri.toString(), getApplicationContext());
                    mSelectedImageUri = getFileUri(bitmap);
                    if (SupportedClass.stringIsNotEmpty(mSelectedImagePath)) {
                        Intent intent = new Intent(StatusActivity.this, CropPhotoActivity.class);
                        intent.putExtra("cropUri", mSelectedImageUri.toString());
                        startActivityForResult(intent, REQUEST_CODE_CROPPING);
                    }
                } else {
                    Toast.makeText(this, getString(R.string.please_try_again), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        } else if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_CROPPING && data != null) {
            try {
                if (data.hasExtra("croppedUri")) {
                    mSelectedImageUri = data.getParcelableExtra("croppedUri");
                    Bitmap bitmap = null;
                    try {
                        bitmap = Constants.getBitmapFromUriDrip(StatusActivity.this, mSelectedImageUri, 1024.0f, 1024.0f);
                        mSelectedImageUri = getFileUri(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (bitmap != null) {
                        Intent intent = new Intent(this, EditingPicActivity.class);
                        intent.putExtra("bitmap", mSelectedImageUri.toString());
                        startActivity(intent);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                    }
                }

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

    public void showPicImageDialog() {
        final Dialog pixDialog = new Dialog(this);
        pixDialog.setContentView(R.layout.dialog_select_photo);
        pixDialog.setCancelable(false);
        Window window = pixDialog.getWindow();
        window.setLayout(((SupportedClass.getWidth(StatusActivity.this) / 100) * 90), LinearLayout.LayoutParams.WRAP_CONTENT);
        pixDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        LinearLayout camera_item = pixDialog.findViewById(R.id.camera_item);
        LinearLayout gallery_item = pixDialog.findViewById(R.id.gallery_item);
        ImageView btnDismiss = pixDialog.findViewById(R.id.cancel);
        gallery_item.setOnClickListener(v -> {
            isGallerySelected = 0;
            checkGalleryPermission();
            if (pixDialog.isShowing() && !isFinishing()) {
                pixDialog.dismiss();
            }
        });
        camera_item.setOnClickListener(v -> {
            isGallerySelected = 1;
            checkGalleryPermission();
            if (pixDialog.isShowing() && !isFinishing()) {
                pixDialog.dismiss();
            }
        });
        btnDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pixDialog.dismiss();
            }
        });

        pixDialog.show();
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
}
