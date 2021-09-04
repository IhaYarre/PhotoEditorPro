package com.photo.editor.picskills.photoeditorpro.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.app.Dialog;
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
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.photo.editor.picskills.photoeditorpro.BuildConfig;
import com.photo.editor.picskills.photoeditorpro.R;
import com.photo.editor.picskills.photoeditorpro.adapter.MainStatusAdapter;
import com.photo.editor.picskills.photoeditorpro.adapter.ViewPagerAdapter;
import com.photo.editor.picskills.photoeditorpro.fragments.SliderOne;
import com.photo.editor.picskills.photoeditorpro.fragments.SliderTwo;
import com.photo.editor.picskills.photoeditorpro.model.AppDesignModel;
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

public class MainActivity extends ParentActivity implements
        MainStatusAdapter.MainStatusClickListener, MultiplePermissionsListener, View.OnClickListener {
    private ArrayList<AppDesignModel> statusDesignList = new ArrayList();
    private MainStatusAdapter mainStatusAdapter;
    private RecyclerView statusRecycler;

    //open gallery for blur activity
    public String mSelectedImagePath;
    public String mSelectedOutputPath;
    public Uri mSelectedImageUri;
    protected static final int REQUEST_CODE_CAMERA = 0x2;
    protected static final int REQUEST_CODE_GALLERY = 0x3;
    protected static final int REQUEST_CODE_CROPPING = 0x4;
    private int isGallerySelected = 0;
    public static int isModelSelected;


    //Timer variablse
    int currentPage = 0;
    Timer timer;
    final long DELAY_MS = 3000;
    final long PERIOD_MS = 3000;
    int NUM_PAGES = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (this.getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        //ads
        RelativeLayout mAdView = findViewById(R.id.adView);
        loadBannerAds(mAdView);
        statusRecycler = findViewById(R.id.statusRecycler);
        LinearLayout wingsLinear = findViewById(R.id.wings_linear);
        LinearLayout spiralLinearLayout = findViewById(R.id.spiral_linear);
        LinearLayout blurLinear = findViewById(R.id.blur_linear);
        LinearLayout bAndWLinear = findViewById(R.id.b_and_w_linear);
        LinearLayout filterLinear = findViewById(R.id.filter_linear);
        LinearLayout gradientLinear = findViewById(R.id.gradient_linear);
        ViewPager2 viewPager2 = findViewById(R.id.view_pager);

        spiralLinearLayout.setOnClickListener(this);
        wingsLinear.setOnClickListener(this);
        bAndWLinear.setOnClickListener(this);
        blurLinear.setOnClickListener(this);
        filterLinear.setOnClickListener(this);
        gradientLinear.setOnClickListener(this);

        this.setDesignListItem();
        setStatusAdapater();
        ArrayList<Fragment> fragment = new ArrayList();
        fragment.add(new SliderOne());
        fragment.add(new SliderTwo());

        ViewPagerAdapter adapter = new ViewPagerAdapter(this, fragment);
        viewPager2.setAdapter(adapter);

        /*After setting the adapter use the timer */
        final Handler handler = new Handler(Looper.getMainLooper());
        final Runnable Update = () -> {
            if (currentPage == NUM_PAGES - 1) {
                currentPage = 0;
            }
            viewPager2.setCurrentItem(currentPage++, true);
        };

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, DELAY_MS, PERIOD_MS);
    }

    void setStatusAdapater() {
        mainStatusAdapter = new MainStatusAdapter(statusDesignList, this);
        statusRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        statusRecycler.setAdapter(mainStatusAdapter);
    }

    void setDesignListItem() {
        AppDesignModel editPicModel1 = new AppDesignModel();
        editPicModel1.setDrawable(ContextCompat.getDrawable(this, R.drawable.wing_status_icon));
        editPicModel1.setText("Wings");

        AppDesignModel editPicModel2 = new AppDesignModel();
        editPicModel2.setDrawable(ContextCompat.getDrawable(this, R.drawable.spiral_status_icon));
        editPicModel2.setText("Spiral");

        AppDesignModel editPicModel3 = new AppDesignModel();
        editPicModel3.setDrawable(ContextCompat.getDrawable(this, R.drawable.blur_status_icon));
        editPicModel3.setText("Blur");

        AppDesignModel editPicModel4 = new AppDesignModel();
        editPicModel4.setText("B & W");
        editPicModel4.setDrawable(ContextCompat.getDrawable(this, R.drawable.b_and_w_icon));

        AppDesignModel editPicModel5 = new AppDesignModel();
        editPicModel5.setText("Filter");
        editPicModel5.setDrawable(ContextCompat.getDrawable(this, R.drawable.simple_status_icon));

        AppDesignModel editPicModel6 = new AppDesignModel();
        editPicModel6.setText("Gradient");
        editPicModel6.setDrawable(ContextCompat.getDrawable(this, R.drawable.gradient_status_icon));
        statusDesignList.add(editPicModel1);
        statusDesignList.add(editPicModel2);
        statusDesignList.add(editPicModel3);
        statusDesignList.add(editPicModel4);
        statusDesignList.add(editPicModel5);
        statusDesignList.add(editPicModel6);
    }

    private void checkGalleryPermission() {
        Dexter.withContext(this)
                .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA)
                .withListener(this).check();
    }

    @Override
    public void mainStatusClick(AppDesignModel drawable, int position) {
        if (position == 0) {
            moveStatusActivity();
            isModelSelected = position;
        }
        if (position == 1) {
            moveStatusActivity();
            isModelSelected = position;
        }
        if (position == 2) {
            moveStatusActivity();
            isModelSelected = position;
        }
        if (position == 3) {
            moveStatusActivity();
            isModelSelected = position;
        }
        if (position == 4) {
            moveStatusActivity();
            isModelSelected = position;
        }
        if (position == 5) {
            moveStatusActivity();
            isModelSelected = position;
        }
    }

    private void moveStatusActivity() {
        Intent intent = new Intent(MainActivity.this, StatusActivity.class);
        startActivity(intent);
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
                        Intent intent = new Intent(MainActivity.this, CropPhotoActivity.class);
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
                        Intent intent = new Intent(MainActivity.this, CropPhotoActivity.class);
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
                        bitmap = Constants.getBitmapFromUriDrip(MainActivity.this, mSelectedImageUri, 1024.0f, 1024.0f);
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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.wings_linear) {
            isModelSelected = 0;
            showPicImageDialog();
        }
        if (v.getId() == R.id.spiral_linear) {
            isModelSelected = 1;
            showPicImageDialog();
        }
        if (v.getId() == R.id.blur_linear) {
            isModelSelected = 2;
            showPicImageDialog();
        }
        if (v.getId() == R.id.b_and_w_linear) {
            isModelSelected = 3;
            showPicImageDialog();
        }
        if (v.getId() == R.id.filter_linear) {
            isModelSelected = 4;
            showPicImageDialog();
        }
        if (v.getId() == R.id.gradient_linear) {
            isModelSelected = 5;
            showPicImageDialog();
        }

    }

    public void showPicImageDialog() {
        final Dialog pixDialog = new Dialog(this);
        pixDialog.setContentView(R.layout.dialog_select_photo);
        pixDialog.setCancelable(false);
        Window window = pixDialog.getWindow();
        window.setLayout(((SupportedClass.getWidth(MainActivity.this) / 100) * 90), LinearLayout.LayoutParams.WRAP_CONTENT);
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