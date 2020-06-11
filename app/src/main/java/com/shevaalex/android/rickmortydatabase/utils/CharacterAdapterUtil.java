package com.shevaalex.android.rickmortydatabase.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.TypedValue;

import com.shevaalex.android.rickmortydatabase.R;

public abstract class CharacterAdapterUtil {
    private static final String VALUE_ALIVE = "Alive";
    private static final String VALUE_DEAD = "Dead";

    //returns color to set TextView color depending on Character's status
    public static int getStatusColour(String status, Context context) {
        int color = fetchThemeColor(R.attr.colorSecondary, context);
        switch (status) {
            case VALUE_ALIVE:
                color = fetchThemeColor(R.attr.colorPrimary, context);
                break;
            case VALUE_DEAD:
                color = context.getResources().getColor(R.color.rm_red_add);
                break;
        }
        return color;
    }

    //returns theme attribute color
    private static int fetchThemeColor(int colorId, Context context) {
        TypedValue typedValue = new TypedValue();
        TypedArray a = context.obtainStyledAttributes(typedValue.data, new int[] { colorId });
        int color = a.getColor(0, 0);
        a.recycle();
        return color;
    }
}
