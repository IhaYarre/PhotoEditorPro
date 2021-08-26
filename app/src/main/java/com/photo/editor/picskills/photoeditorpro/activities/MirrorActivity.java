package com.photo.editor.picskills.photoeditorpro.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.photo.editor.picskills.photoeditorpro.BuildConfig;
import com.photo.editor.picskills.photoeditorpro.R;
import com.photo.editor.picskills.photoeditorpro.adapter.MirrorAdapter;
import com.photo.editor.picskills.photoeditorpro.model.AppDesignModel;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

public class MirrorActivity extends AppCompatActivity implements MirrorAdapter.MirrorClickListener {
    private Bitmap originalBitmap = null;
    private ImageView btnPersonImage, btnBack, btnApply;
    private String bitmapString = "";
    private FrameLayout frameLayout;
    //content uri store
    private Uri sourceUri;
    private ArrayList<AppDesignModel> simpleFilterArrayList = new ArrayList();
    private MirrorAdapter mirrorAdapter;
    private RecyclerView recyclerView;
    private float x = 0, y = 0, dx = 0, dy = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mirror);
        if (getSupportActionBar()
                != null) {
            this.getSupportActionBar().hide();
        }
        btnApply = findViewById(R.id.apply);
        btnBack = findViewById(R.id.back);
        btnPersonImage = findViewById(R.id.person_image);
        frameLayout = findViewById(R.id.frameLayout);
        recyclerView = findViewById(R.id.mirror_rycycler);
        bitmapString = getIntent().getStringExtra("bitmap");
        String fileString = Uri.parse(bitmapString).getPath();
        getSourceProviderUri(new File(fileString));
        frameLayout.post(() -> {
            try {
                getBitmap(bitmapString);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        setMirrorAdapter();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                x = event.getX();
                //y = event.getY();
                dx = x - btnPersonImage.getX();
                //dy = y - btnPersonImage.getY();
            }
            break;
            case MotionEvent.ACTION_MOVE: {
                btnPersonImage.setX(event.getX() - dx);
                //btnPersonImage.setY(event.getY() - dy);
            }
            break;
            case MotionEvent.ACTION_UP: {
                //your stuff
            }
        }
        return true;
    }

    private void getSourceProviderUri(File file) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            sourceUri = Uri.fromFile(file);
        } else {
            sourceUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file);
        }
    }

    void setMirrorAdapter() {
        AppDesignModel model1 = new AppDesignModel();
        model1.setDrawable(AppCompatResources.getDrawable(this, R.drawable.t_f));
        model1.setText("horizontal");
        AppDesignModel model2 = new AppDesignModel();
        model2.setDrawable(AppCompatResources.getDrawable(this, R.drawable.y_t));
        model2.setText("vertical");
        simpleFilterArrayList.add(0, model1);
        simpleFilterArrayList.add(1, model2);
        simpleFilterArrayList.add(2, model2);
        simpleFilterArrayList.add(3, model2);
        simpleFilterArrayList.add(4, model2);
        simpleFilterArrayList.add(5, model2);
        simpleFilterArrayList.add(6, model2);
        simpleFilterArrayList.add(7, model2);
        simpleFilterArrayList.add(8, model2);
        simpleFilterArrayList.add(9, model2);
        simpleFilterArrayList.add(10, model2);
        simpleFilterArrayList.add(11, model2);
        simpleFilterArrayList.add(12, model2);
        simpleFilterArrayList.add(13, model2);
        simpleFilterArrayList.add(14, model2);
        simpleFilterArrayList.add(15, model2);
        simpleFilterArrayList.add(16, model2);


        mirrorAdapter = new MirrorAdapter(simpleFilterArrayList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(mirrorAdapter);
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

    @Override
    public void mirrorClickListener(AppDesignModel model, int position) {
        if (position == 0) {
            // This will not scale but will flip on the Y axis
            Matrix matrix = new Matrix();
            matrix.preScale(1, 1);
            Matrix matrix1 = new Matrix();
            matrix1.preScale(1, -1);
            btnPersonImage.setImageBitmap(flipHorizontally(originalBitmap, matrix, matrix1));
            //Glide.with(this).load(flipHorizontally(originalBitmap, matrix, matrix1)).into(btnPersonImage);
        }
        if (position == 1) {
            // This will not scale but will flip on the Y axis
            Matrix matrix = new Matrix();
            matrix.preScale(-1, 1);
            Matrix matrix1 = new Matrix();
            matrix1.preScale(-1, 1);
            Glide.with(this).load(flipHorizontally(originalBitmap, matrix, matrix1)).into(btnPersonImage);
        }
        if (position == 2) {
            // This will not scale but will flip on the Y axis
            Matrix matrix = new Matrix();
            matrix.preScale(-1, 1);
            Matrix matrix1 = new Matrix();
            matrix1.preScale(1, -1);
            Glide.with(this).load(flipHorizontally(originalBitmap, matrix, matrix1)).into(btnPersonImage);
        }
        if (position == 3) {
            // This will not scale but will flip on the Y axis
            Matrix matrix = new Matrix();
            matrix.preScale(-1, 1);
            Matrix matrix1 = new Matrix();
            matrix1.preScale(-1, -1);
            Glide.with(this).load(flipHorizontally(originalBitmap, matrix, matrix1)).into(btnPersonImage);
        }
        if (position == 4) {
            // This will not scale but will flip on the Y axis
            Matrix matrix = new Matrix();
            matrix.preScale(-1, -1);
            Matrix matrix1 = new Matrix();
            matrix1.preScale(-1, -1);
            Glide.with(this).load(flipHorizontally(originalBitmap, matrix, matrix1)).into(btnPersonImage);
        }
        if (position == 5) {
            // This will not scale but will flip on the Y axis
            Matrix matrix = new Matrix();
            matrix.preScale(-1, -1);
            Matrix matrix1 = new Matrix();
            matrix1.preScale(1, 1);
            Glide.with(this).load(flipHorizontally(originalBitmap, matrix, matrix1)).into(btnPersonImage);
        }
        if (position == 6) {
            // This will not scale but will flip on the Y axis
            Matrix matrix = new Matrix();
            matrix.preScale(-1, -1);
            Matrix matrix1 = new Matrix();
            matrix1.preScale(1, -1);
            Glide.with(this).load(flipHorizontally(originalBitmap, matrix, matrix1)).into(btnPersonImage);
        }
        if (position == 7) {
            // This will not scale but will flip on the Y axis
            Matrix matrix = new Matrix();
            matrix.preScale(1, 1);
            Matrix matrix1 = new Matrix();
            matrix1.preScale(-1, -1);
            Glide.with(this).load(flipHorizontally(originalBitmap, matrix, matrix1)).into(btnPersonImage);
        }


        if (position == 8) {
            Matrix matrix = new Matrix();
            matrix.preScale(1, 1);
            Matrix matrix1 = new Matrix();
            matrix1.preScale(1, -1);
            Matrix matrix2 = new Matrix();
            matrix2.preScale(-1, 1);
            Glide.with(this).load(flipVertically(originalBitmap, matrix, matrix1, matrix2)).into(btnPersonImage);
        }
        if (position == 9) {
            // This will not scale but will flip on the Y axis
            Matrix matrix = new Matrix();
            matrix.preScale(1, 1);
            Matrix matrix1 = new Matrix();
            matrix1.preScale(-1, 1);
            Matrix matrix2 = new Matrix();
            matrix2.preScale(1, -1);
            Matrix matrix3 = new Matrix();
            matrix3.preScale(-1, -1);
            Glide.with(this).load(flipBottomVertically(originalBitmap, matrix, matrix1, matrix2, matrix3)).into(btnPersonImage);
        }
        if (position == 10) {
            // This will not scale but will flip on the Y axis
            Matrix matrix = new Matrix();
            matrix.preScale(-1, 1);
            Matrix matrix1 = new Matrix();
            matrix1.preScale(1, 1);
            Matrix matrix2 = new Matrix();
            matrix2.preScale(1, -1);
            Matrix matrix3 = new Matrix();
            matrix3.preScale(-1, -1);
            Glide.with(this).load(flipBottomVertically(originalBitmap, matrix, matrix1, matrix2, matrix3)).into(btnPersonImage);
        }
        if (position == 11) {
            // This will not scale but will flip on the Y axis
            Matrix matrix = new Matrix();
            matrix.preScale(1, -1);
            Matrix matrix1 = new Matrix();
            matrix1.preScale(-1, -1);
            Matrix matrix2 = new Matrix();
            matrix2.preScale(1, 1);
            Matrix matrix3 = new Matrix();
            matrix3.preScale(-1, 1);
            Glide.with(this).load(flipBottomVertically(originalBitmap, matrix, matrix1, matrix2, matrix3)).into(btnPersonImage);
        }
        if (position == 12) {
            // This will not scale but will flip on the Y axis
            Matrix matrix = new Matrix();
            matrix.preScale(-1, -1);
            Matrix matrix1 = new Matrix();
            matrix1.preScale(1, -1);
            Matrix matrix2 = new Matrix();
            matrix2.preScale(-1, 1);
            Matrix matrix3 = new Matrix();
            matrix3.preScale(1, 1);
            Glide.with(this).load(flipBottomVertically(originalBitmap, matrix, matrix1, matrix2, matrix3)).into(btnPersonImage);
        }
        if (position == 13) {
            // This will not scale but will flip on the Y axis
            Matrix matrix = new Matrix();
            matrix.preScale(-1, -1);
            Matrix matrix1 = new Matrix();
            matrix1.preScale(1, -1);
            Matrix matrix2 = new Matrix();
            matrix2.preScale(-1, 1);
            Matrix matrix3 = new Matrix();
            matrix3.preScale(1, 1);
            Glide.with(this).load(flipBottomVertically(originalBitmap, matrix, matrix1, matrix2, matrix3)).into(btnPersonImage);
        }
        if (position == 14) {
            // This will not scale but will flip on the Y axis
            Matrix matrix = new Matrix();
            matrix.preScale(1, 1);
            Matrix matrix1 = new Matrix();
            matrix1.preScale(-1, 1);
            Matrix matrix2 = new Matrix();
            matrix2.preScale(1, 1);
            Matrix matrix3 = new Matrix();
            matrix3.preScale(-1, 1);
            Glide.with(this).load(flipBottomVertically(originalBitmap, matrix, matrix1, matrix2, matrix3)).into(btnPersonImage);
        }
        if (position == 15) {
            // This will not scale but will flip on the Y axis
            Matrix matrix = new Matrix();
            matrix.preScale(1, 1);
            Matrix matrix1 = new Matrix();
            matrix1.preScale(-1, 1);
            Matrix matrix2 = new Matrix();
            matrix2.preScale(-1, 1);
            Matrix matrix3 = new Matrix();
            matrix3.preScale(1, 1);
            Glide.with(this).load(flipBottomVertically(originalBitmap, matrix, matrix1, matrix2, matrix3)).into(btnPersonImage);
        }
        if (position == 16) {
            // This will not scale but will flip on the Y axis
            Matrix matrix = new Matrix();
            matrix.preScale(-1, 1);
            Matrix matrix1 = new Matrix();
            matrix1.preScale(1, 1);
            Matrix matrix2 = new Matrix();
            matrix2.preScale(1, 1);
            Matrix matrix3 = new Matrix();
            matrix3.preScale(-1, 1);
            Glide.with(this).load(flipBottomVertically(originalBitmap, matrix, matrix1, matrix2, matrix3)).into(btnPersonImage);
        }
    }

    public Bitmap flipHorizontally(Bitmap originalImage, Matrix matrix, Matrix matrix1) {
        // The gap we want between the flipped image and the original image
        final int flipGap = -200;


        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        // Create a Bitmap with the flip matrix applied to it.
        // We only want the bottom half of the image
        Bitmap flipImage = Bitmap.createBitmap(originalImage, 0, 0, width, height, matrix, true);
        Bitmap flipImage1 = Bitmap.createBitmap(originalImage, 0, 0, width, height, matrix1, true);

        // Create a new bitmap with same width but taller to fit reflection
        Bitmap bitmapWithFlip = Bitmap.createBitmap((width + width + flipGap), height, Bitmap.Config.ARGB_8888);

        // Create a new Canvas with the bitmap that's big enough for
        Canvas canvas = new Canvas(bitmapWithFlip);

        //Draw original image
        canvas.drawBitmap(flipImage, 0, 0, null);

        //Draw the Flipped Image
        canvas.drawBitmap(flipImage1, width + flipGap, 0, null);


        return bitmapWithFlip;
    }

    public Bitmap flipVertically(Bitmap originalImage, Matrix matrix1, Matrix matrix2, Matrix matrix3) {
        // The gap we want between the flipped image and the original image
        final int flipGap = -50;


        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        // Create a Bitmap with the flip matrix applied to it.
        // We only want the bottom half of the image
        Bitmap flipImage = Bitmap.createBitmap(originalImage, 0, 0, width, height, matrix1, true);
        Bitmap flipImage1 = Bitmap.createBitmap(originalImage, 0, 0, width, height, matrix2, true);
        Bitmap flipImage2 = Bitmap.createBitmap(originalImage, 0, 0, width, height, matrix3, true);

        // Create a new bitmap with same width but taller to fit reflection
        Bitmap bitmapWithFlip = Bitmap.createBitmap((width + width + width + flipGap), height, Bitmap.Config.ARGB_8888);

        // Create a new Canvas with the bitmap that's big enough for
        Canvas canvas = new Canvas(bitmapWithFlip);

        //Draw original image
        canvas.drawBitmap(flipImage, 0, 0, null);

        //Draw the Flipped Image
        canvas.drawBitmap(flipImage1, width + flipGap, 0, null);
        //Draw the Flipped Image
        canvas.drawBitmap(flipImage2, width + width + flipGap, 0, null);


        return bitmapWithFlip;
    }

    public Bitmap flipBottomVertically(Bitmap originalImage, Matrix matrix, Matrix matrix1, Matrix matrix2, Matrix matrix3) {
        // The gap we want between the flipped image and the original image
        final int flipGap = -50;


        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        // Create a Bitmap with the flip matrix applied to it.
        // We only want the bottom half of the image
        Bitmap flipImage = Bitmap.createBitmap(originalImage, 0, 0, width, height, matrix, true);
        Bitmap flipImage1 = Bitmap.createBitmap(originalImage, 0, 0, width, height, matrix1, true);
        Bitmap flipImage2 = Bitmap.createBitmap(originalImage, 0, 0, width, height, matrix2, true);
        Bitmap flipImage3 = Bitmap.createBitmap(originalImage, 0, 0, width, height, matrix3, true);

        // Create a new bitmap with same width but taller to fit reflection
        Bitmap bitmapWithFlip = Bitmap.createBitmap((width + width + flipGap), (height + height + flipGap), Bitmap.Config.ARGB_8888);

        // Create a new Canvas with the bitmap that's big enough for
        Canvas canvas = new Canvas(bitmapWithFlip);

        //Draw original image
        canvas.drawBitmap(flipImage, 0, 0, null);

        //Draw the Flipped Image
        canvas.drawBitmap(flipImage1, width + flipGap, 0, null);
        //Draw the Flipped Image
        canvas.drawBitmap(flipImage2, 0, height + flipGap, null);
        //Draw the Flipped Image
        canvas.drawBitmap(flipImage3, width + flipGap, height + flipGap, null);


        return bitmapWithFlip;
    }
}