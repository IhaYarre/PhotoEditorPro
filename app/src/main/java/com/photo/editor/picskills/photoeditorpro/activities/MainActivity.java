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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.photo.editor.picskills.photoeditorpro.fragments.SliderFour;
import com.photo.editor.picskills.photoeditorpro.fragments.SliderOne;
import com.photo.editor.picskills.photoeditorpro.fragments.SliderThree;
import com.photo.editor.picskills.photoeditorpro.fragments.SliderTwo;
import com.photo.editor.picskills.photoeditorpro.model.AppDesignModel;
import com.photo.editor.picskills.photoeditorpro.utils.Constants;
import com.photo.editor.picskills.photoeditorpro.utils.support.SupportedClass;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        MainStatusAdapter.MainStatusClickListener, MultiplePermissionsListener, View.OnClickListener {
    private Bitmap originalBitmap;
    private ArrayList<AppDesignModel> statusDesignList = new ArrayList();
    private MainStatusAdapter mainStatusAdapter;
    private BottomNavigationView bottomNavigationViewEx;
    private RecyclerView statusRecycler;
    private LinearLayout dripLinearLayout, spiralLinearLayout;

    //open gallery for blur activity
    public String mSelectedImagePath;
    public String mSelectedOutputPath;
    public Uri mSelectedImageUri;
    protected static final int REQUEST_CODE_CAMERA = 0x2;
    protected static final int REQUEST_CODE_GALLERY = 0x3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (this.getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        statusRecycler = findViewById(R.id.statusRecycler);
        bottomNavigationViewEx = findViewById(R.id.bottomNavigationViewEx);
        dripLinearLayout = findViewById(R.id.drip_linear);
        spiralLinearLayout = findViewById(R.id.spiral_linear);
        spiralLinearLayout.setOnClickListener(this);
        dripLinearLayout.setOnClickListener(this);

        ViewPager2 viewPager2 = findViewById(R.id.view_pager);
        setUpBottomNavigationView();
        this.setDesignListItem();
        setStatusAdapater();
        ArrayList<Fragment> fragment = new ArrayList();

        fragment.add(new SliderOne());
        fragment.add(new SliderTwo());
        fragment.add(new SliderThree());
        fragment.add(new SliderFour());

        ViewPagerAdapter adapter = new ViewPagerAdapter(this, fragment);
        viewPager2.setAdapter(adapter);

    }

    void setStatusAdapater() {
        mainStatusAdapter = new MainStatusAdapter(statusDesignList, this);
        statusRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        statusRecycler.setAdapter(mainStatusAdapter);
    }

    void setDesignListItem() {
        AppDesignModel editPicModel1 = new AppDesignModel();
        editPicModel1.setDrawable(ContextCompat.getDrawable(this, R.drawable.elipse_1_s));
        editPicModel1.setText("Filters");
        AppDesignModel editPicModel2 = new AppDesignModel();
        editPicModel2.setDrawable(ContextCompat.getDrawable(this, R.drawable.elipse_1_s));
        editPicModel2.setText("Colleges");
        AppDesignModel editPicModel3 = new AppDesignModel();
        editPicModel3.setDrawable(ContextCompat.getDrawable(this, R.drawable.elipse_1_s));
        editPicModel3.setText("Back");
        AppDesignModel editPicModel4 = new AppDesignModel();
        editPicModel4.setText("Template");
        editPicModel4.setDrawable(ContextCompat.getDrawable(this, R.drawable.elipse_1_s));

        AppDesignModel editPicModel5 = new AppDesignModel();
        editPicModel5.setText("Sticker");
        editPicModel5.setDrawable(ContextCompat.getDrawable(this, R.drawable.elipse_1_s));
        statusDesignList.add(editPicModel1);
        statusDesignList.add(editPicModel2);
        statusDesignList.add(editPicModel3);
        statusDesignList.add(editPicModel4);
        statusDesignList.add(editPicModel5);
    }

    private void setUpBottomNavigationView() {
        bottomNavigationViewEx.setItemIconTintList(null);
        bottomNavigationViewEx.setOnNavigationItemSelectedListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.ic_filters:
                            item.setChecked(true);
                            checkGalleryPermission();
                            //showPicImageDialog();
                            break;

                        case R.id.ic_stickers:
                            item.setChecked(true);
                            break;
                        case R.id.ic_pro:
                            item.setChecked(true);
                            break;
                        case R.id.ic_fonts:
                            item.setChecked(true);
                            break;

                        case R.id.ic_snack_video:
                            item.setChecked(true);
                            break;
                    }
                    return false;
                }

        );
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
            //checkGalleryPermission();
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, Constants.PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
      /*  if (requestCode == Constants.PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri selectedImage = data.getData();
                try {
                    Intent intent = new Intent(this, EditingPicActivity.class);
                    intent.putExtra("bitmap", selectedImage.toString());
                    startActivity(intent);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }*/
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_CAMERA) {
            mSelectedImagePath = mSelectedOutputPath;
            if (SupportedClass.stringIsNotEmpty(mSelectedImagePath)) {
                File fileImageClick = new File(mSelectedImagePath);
                if (fileImageClick.exists()) {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                        mSelectedImageUri = Uri.fromFile(fileImageClick);
                    } else {
                        mSelectedImageUri = FileProvider.getUriForFile(MainActivity.this, BuildConfig.APPLICATION_ID + ".provider", fileImageClick);
                    }
                    Intent intent = new Intent(this, EditingPicActivity.class);
                    intent.putExtra("bitmap", mSelectedImageUri.toString());
                    startActivity(intent);
                }
            }
        } else if (data != null && data.getData() != null) {
            if (requestCode == REQUEST_CODE_GALLERY) {
                mSelectedImageUri = data.getData();
                if (mSelectedImageUri != null) {
                    mSelectedImagePath = Constants.convertMediaUriToPath(MainActivity.this, mSelectedImageUri);
                } else {
                    Toast.makeText(this, getString(R.string.please_try_again), Toast.LENGTH_SHORT).show();
                }
            } else {
                mSelectedImagePath = mSelectedOutputPath;
            }
            if (SupportedClass.stringIsNotEmpty(mSelectedImagePath)) {
                Intent intent = new Intent(this, EditingPicActivity.class);
                intent.putExtra("bitmap", mSelectedImageUri.toString());
                startActivity(intent);
            }

        } else {
            Log.e("TAG", "");
        }
    }

    private void openSettings() {
        Intent intent = new Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", "com.royal.filterapplication", null)
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
        if (v.getId() == R.id.drip_linear) {

        }
        if (v.getId() == R.id.spiral_linear) {

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
        gallery_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivityForResult(Intent.createChooser(intent, getString(R.string.txt_select_picture)), REQUEST_CODE_GALLERY);
                if (pixDialog.isShowing() && !isFinishing()) {
                    pixDialog.dismiss();
                }
            }
        });
        camera_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", createImageFile());
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                if (intent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
                    startActivityForResult(intent, REQUEST_CODE_CAMERA);
                }
                if (pixDialog.isShowing() && !isFinishing()) {
                    pixDialog.dismiss();
                }
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

    @Override
    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
        if (multiplePermissionsReport.areAllPermissionsGranted()) {
            showPicImageDialog();
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