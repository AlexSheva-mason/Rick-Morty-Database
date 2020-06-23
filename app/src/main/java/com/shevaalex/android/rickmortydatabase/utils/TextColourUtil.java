package com.shevaalex.android.rickmortydatabase.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.TypedValue;

import com.shevaalex.android.rickmortydatabase.R;

public abstract class TextColourUtil {

    //returns colour to set TextView color depending on Character's status
    public static int getStatusColour(String status, Context context) {
        int color = fetchThemeColor(R.attr.colorSecondary, context);
        if (status.equals(context.getResources().getString(R.string.character_status_alive_female))
        ||status.equals(context.getResources().getString(R.string.character_status_alive_male))) {
            color = fetchThemeColor(R.attr.colorPrimary, context);
        } else if (status.equals(context.getResources().getString(R.string.character_status_dead_female))
                ||status.equals(context.getResources().getString(R.string.character_status_dead_male))) {
            color = context.getResources().getColor(R.color.rm_red_add);
        }
        return color;
    }

    //returns theme attribute colour
    public static int fetchThemeColor(int colorId, Context context) {
        TypedValue typedValue = new TypedValue();
        TypedArray a = context.obtainStyledAttributes(typedValue.data, new int[] { colorId });
        int color = a.getColor(0, 0);
        a.recycle();
        return color;
    }
}
