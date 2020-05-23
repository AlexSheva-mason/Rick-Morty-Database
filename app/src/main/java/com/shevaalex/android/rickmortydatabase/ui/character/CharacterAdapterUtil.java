package com.shevaalex.android.rickmortydatabase.ui.character;

import android.content.Context;

import com.shevaalex.android.rickmortydatabase.R;

abstract class CharacterAdapterUtil {
    private static final String VALUE_ALIVE = "Alive";
    private static final String VALUE_DEAD = "Dead";

    //returns color to set TextView color depending on Character's status
    static int getStatusColour(String status, Context context) {
        int color = context.getResources().getColor(R.color.rm_grey_900);
        switch (status) {
            case VALUE_ALIVE:
                color = context.getResources().getColor(R.color.rm_green_add_500);
                break;
            case VALUE_DEAD:
                color = context.getResources().getColor(R.color.rm_red_add_900);
                break;
        }
        return color;
    }
}
