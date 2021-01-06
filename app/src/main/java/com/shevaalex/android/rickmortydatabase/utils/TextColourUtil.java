package com.shevaalex.android.rickmortydatabase.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.TypedValue;

import com.shevaalex.android.rickmortydatabase.R;

public abstract class TextColourUtil {

    //returns colour to set TextView color depending on Character's status
    public static int getStatusColour(String status, Context context) {
        int color = context
                .getResources()
                .getColor(R.color.material_on_background_emphasis_high_type, context.getTheme());
        if (status.equals(context.getResources().getString(R.string.character_status_alive_female))
        ||status.equals(context.getResources().getString(R.string.character_status_alive_male))) {
            color = fetchThemeColor(R.attr.colorPrimaryVariant, context);
        } else if (status.equals(context.getResources().getString(R.string.character_status_dead_female))
                ||status.equals(context.getResources().getString(R.string.character_status_dead_male))) {
            color = context.getResources().getColor(R.color.rm_red_add, context.getTheme());
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
