package com.shevaalex.android.rickmortydatabase.utils

import android.content.Context
import android.util.TypedValue
import com.shevaalex.android.rickmortydatabase.R


fun calculateNumberOfColumns(context: Context): Int {
    val screenDensity = getScreenDensity(context)
    val screenWidthDp = getScreenWidthPx(context) / screenDensity
    //get column width as dimension value in pixels, converted to dp
    val columnWidthDp = getDimensPx(context, R.dimen.item_episode_location_width) / screenDensity
    val columnSpacingDp = getDimensPx(context, R.dimen.item_grid_spacing) / screenDensity
    return (screenWidthDp / (columnWidthDp + columnSpacingDp)).toInt()
}

fun calculateNumberOfRows(context: Context): Int {
    val screenDensity = getScreenDensity(context)
    val screenHeightDp = getScreenHeightPx(context) / screenDensity
    // account for a bottom nav bar height
    val bottomNavHeightDp = getActionBarHeightPx(context) / screenDensity
    // account for a status bar height (24dp)
    val statusBarHeightDp = getDimensPx(context, R.dimen.status_bar_height_standard) / screenDensity
    val availableScreenHeightDp = screenHeightDp - bottomNavHeightDp - statusBarHeightDp
    //get row height as dimension value in pixels, converted to dp
    val rowHeightDp = getDimensPx(context, R.dimen.item_episode_location_height) / screenDensity
    val rowSpacingDp = getDimensPx(context, R.dimen.item_grid_spacing) / screenDensity
    return (availableScreenHeightDp / (rowHeightDp + rowSpacingDp)).toInt()
}

/**
 * gets ActionBar height
 */
fun getActionBarHeightPx(context: Context): Int {
    val tv = TypedValue()
    return if (context.theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
        TypedValue.complexToDimensionPixelSize(tv.data, context.resources.displayMetrics)
    } else 0
}

/**
 * get current screen width in pixels
 */
fun getScreenWidthPx(context: Context): Int {
    val displayMetrics = context.resources.displayMetrics
    return displayMetrics.widthPixels
}

/**
 * get current screen height in pixels
 */
fun getScreenHeightPx(context: Context): Int {
    val displayMetrics = context.resources.displayMetrics
    return displayMetrics.heightPixels
}

/**
 * returns screen density
 */
fun getScreenDensity(context: Context): Float {
    val displayMetrics = context.resources.displayMetrics
    return displayMetrics.density
}

/**
 * gets dimens value in pixels
 */
fun getDimensPx(context: Context, resourceId: Int): Int {
    return context.resources.getDimensionPixelSize(resourceId)
}
