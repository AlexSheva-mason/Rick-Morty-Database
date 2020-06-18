package com.shevaalex.android.rickmortydatabase.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static androidx.core.content.FileProvider.getUriForFile;

public abstract class ImageParsingUtil {
    public static Uri parseBitmapToUri(Bitmap bmp, String characterName, Context context) {
        Uri imageUri = null;
        try {
            File file =  File.createTempFile(characterName + System.currentTimeMillis(), ".png", context.getCacheDir());
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            imageUri = getUriForFile(context, "com.shevaalex.android.rickmortydatabase.fileprovider", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageUri;
    }
}
