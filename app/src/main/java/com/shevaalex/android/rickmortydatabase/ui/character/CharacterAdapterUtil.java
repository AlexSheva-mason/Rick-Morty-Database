package com.shevaalex.android.rickmortydatabase.ui.character;

import android.content.Context;

import com.shevaalex.android.rickmortydatabase.R;

abstract class CharacterAdapterUtil {
    private static final String stringAliveValue = "Alive";
    private static final String stringDeadValue = "Dead";
    private static final String stringUnknownValue = "unknown";

    //returns color to set TextView color depending on Character's status
    static int getStatusColour(String status, Context context) {
        int color = 0;
        switch (status) {
            case stringAliveValue:
                color = context.getResources().getColor(R.color.rm_green_add_500);
                break;
            case stringDeadValue:
                color = context.getResources().getColor(R.color.rm_red_add_900);
                break;
            case stringUnknownValue:
                color = context.getResources().getColor(R.color.rm_grey_900);
                break;
        }
        return color;
    }
}
