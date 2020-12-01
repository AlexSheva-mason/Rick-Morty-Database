package com.shevaalex.android.rickmortydatabase.utils

import android.content.Context
import android.util.TypedValue
import com.shevaalex.android.rickmortydatabase.R


fun calculateNumberOfColumns(context: Context): Int {
    val displayMetrics = context.resources.displayMetrics
    val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density
    //get column width as dimension value in pixels, converted to dp
    val columnWidthDp = context
            .resources
            .getDimensionPixelSize(R.dimen.item_episode_location_width) / displayMetrics.density
    val columnSpacing = context
            .resources
            .getDimensionPixelSize(R.dimen.item_grid_spacing) / displayMetrics.density
    return (screenWidthDp / (columnWidthDp+columnSpacing) ).toInt()
}

fun calculateNumberOfRows(context: Context): Int {
    val displayMetrics = context.resources.displayMetrics
    val screenHeightDp = displayMetrics.heightPixels / displayMetrics.density
    // account for a bottom nav bar height
    val bottomNavHeight = calculateActionBar(context) / displayMetrics.density
    // account for a status bar height
    val statusBarHeight = 24
    val availableScreenHeightDp = screenHeightDp - bottomNavHeight - statusBarHeight
    //get row height as dimension value in pixels, converted to dp
    val rowHeightDp = context
            .resources
            .getDimensionPixelSize(R.dimen.item_episode_location_height) / displayMetrics.density
    val rowSpacing = context
            .resources
            .getDimensionPixelSize(R.dimen.item_grid_spacing) / displayMetrics.density
    return (availableScreenHeightDp / (rowHeightDp+rowSpacing)).toInt()
}

/**
 * gets ActionBar height
 */
private fun calculateActionBar(context: Context): Int {
    val tv = TypedValue()
    return if (context.theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
        TypedValue.complexToDimensionPixelSize(tv.data, context.resources.displayMetrics)
    } else 0
}

/**
 * returns a value from dimens.xml in dp
 */
fun getDimensInDp(context: Context, resourceId: Int): Int {
    val displayMetrics = context.resources.displayMetrics
    return context
            .resources
            .getDimensionPixelSize(resourceId) / displayMetrics.density
            .toInt()
}