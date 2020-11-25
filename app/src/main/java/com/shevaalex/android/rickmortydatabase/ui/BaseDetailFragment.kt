package com.shevaalex.android.rickmortydatabase.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import com.shevaalex.android.rickmortydatabase.R
import com.shevaalex.android.rickmortydatabase.models.ApiObjectModel
import com.shevaalex.android.rickmortydatabase.utils.Constants
import com.shevaalex.android.rickmortydatabase.utils.ImageParsingUtil
import java.util.*

abstract class BaseDetailFragment<T: ViewBinding, S: ApiObjectModel>: BaseFragment() {

    private var _binding: T? = null
    protected val binding get() = _binding!!

    protected abstract val viewModel: BaseDetailViewModel<S>

    protected abstract val keyDetailObject: String

    override fun onAttach(context: Context) {
        //inject fragment
        injectFragment()
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = setBinding(inflater,container)
        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        saveViewState(outState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    protected fun shareImageWithGlide(a: Activity, detailObjectName: String, imageUrl: String) {
        Glide.with(a)
                .asBitmap()
                .load(imageUrl)
                .apply(RequestOptions()
                        .placeholder(R.drawable.picasso_placeholder_error)
                        .error(R.drawable.picasso_placeholder_error)
                )
                .into(object : CustomTarget<Bitmap>() {

                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        //replace whitespace in the name with underscore
                        val underscoredName = detailObjectName
                                .trim()
                                .replace(" ", "_")
                                .plus("_")
                        //set the share intent
                        val shareIntent = Intent()
                        shareIntent.action = Intent.ACTION_SEND
                        shareIntent.type = Constants.SHARE_TYPE
                        shareIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                        shareIntent.flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        val uri: Uri = ImageParsingUtil
                                .parseBitmapToUri(resource, underscoredName, a)
                        shareIntent.data = uri
                        //put extra for compatability with older apps
                        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
                        val chooser = Intent
                                .createChooser(
                                        shareIntent,
                                        a.getString(R.string.share_title)
                                )
                        //get a list of ResolveInfo for this share intent (chooser)
                        val resInfoList: List<ResolveInfo> = a
                                .packageManager
                                .queryIntentActivities(
                                        chooser,
                                        PackageManager.MATCH_DEFAULT_ONLY
                                )
                        //set the uri permissions for the activity and current ResolveInfo
                        resInfoList.forEach {
                            val packageName: String = it.activityInfo.packageName
                            a.grantUriPermission(
                                    packageName,
                                    uri,
                                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                                            or Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        //start share intent
                        startActivity(chooser)
                        //log share intent with firebase
                        val className = "_"
                                .plus(this.javaClass.simpleName)
                                .toLowerCase(Locale.ROOT)
                        firebaseAnalytics
                                .logEvent(FirebaseAnalytics.Event.SHARE.plus(className)) {
                                    param(FirebaseAnalytics.Param.CONTENT_TYPE, Constants.SHARE_TYPE)
                                    param(FirebaseAnalytics.Param.ITEM_ID, detailObjectName)
                                }
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        Toast.makeText(
                                a,
                                a.resources.getString(R.string.error_share_no_network),
                                Toast.LENGTH_SHORT
                        ).show()
                        super.onLoadFailed(errorDrawable)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        //nothing here
                    }

                })
    }

    /**
     * called in onSaveInstanceState to save fragment's view state (saves an object for detail screen)
     */
    private fun saveViewState(outState: Bundle) {
        outState.run {
            viewModel.detailObject.value?.let {
                putParcelable(keyDetailObject, it)
            }
        }
    }

    protected abstract fun injectFragment()

    protected abstract fun setBinding(inflater: LayoutInflater, container: ViewGroup?): T

}