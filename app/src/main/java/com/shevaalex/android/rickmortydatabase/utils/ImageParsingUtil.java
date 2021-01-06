package com.shevaalex.android.rickmortydatabase.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import timber.log.Timber;

public abstract class ImageParsingUtil {
    public static Uri parseBitmapToUri(Bitmap bmp, String characterName, Context context) {
        Uri imageUri = null;
        try {
            File file = File.createTempFile(characterName + System.currentTimeMillis(), ".png", context.getCacheDir());
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            imageUri = FileProvider.getUriForFile(context, "com.shevaalex.android.rickmortydatabase.fileprovider", file);
        } catch (IOException | IllegalArgumentException | SecurityException e) {
            Timber.e(e);
        }
        return imageUri;
    }
}
