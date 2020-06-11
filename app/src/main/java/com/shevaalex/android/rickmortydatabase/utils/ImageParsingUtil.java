package com.shevaalex.android.rickmortydatabase.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public abstract class ImageParsingUtil {
    public static Uri parseBitmapToUri(Bitmap bmp, String characterName, Context context) {
        Uri imageUri = null;
        try {
            File file =  new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), characterName + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            imageUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageUri;
    }
}
