package com.photo.editor.picskills.photoeditorpro.crop_img.callback;

import android.graphics.Bitmap;

public interface CropCallbackElegantPhoto extends CallbackElegantPhoto {
    void onSuccess(Bitmap cropped);
}
