package com.shevaalex.android.rickmortydatabase.ui

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.FutureTarget
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import com.shevaalex.android.rickmortydatabase.R
import com.shevaalex.android.rickmortydatabase.models.ApiObjectModel
import com.shevaalex.android.rickmortydatabase.utils.Constants
import com.shevaalex.android.rickmortydatabase.utils.ImageParsingUtil
import kotlinx.coroutines.*
import timber.log.Timber
import java.util.*
import java.util.concurrent.CancellationException
import java.util.concurrent.ExecutionException

abstract class BaseDetailFragment<T : ViewBinding, S : ApiObjectModel>: BaseFragment() {

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
        _binding = setBinding(inflater, container)
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

    protected fun shareImageWithGlide(detailObjectName: String, imageUrl: String) {
        //get the bitmap from glide
        val futureBitmap: FutureTarget<Bitmap> =
                Glide.with(this)
                .asBitmap()
                .load(imageUrl)
                .submit()
        lifecycleScope.launch(Dispatchers.IO) {
            //try get the bitmap from future object
            try {
                val bitmap = futureBitmap.get()
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
                        .parseBitmapToUri(bitmap, underscoredName, requireActivity())
                shareIntent.data = uri
                //put extra for compatability with older apps
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
                val chooser = Intent
                        .createChooser(
                                shareIntent,
                                requireActivity().getString(R.string.share_title)
                        )
                //get a list of ResolveInfo for this share intent (chooser)
                val resInfoList: List<ResolveInfo> = requireActivity()
                        .packageManager
                        .queryIntentActivities(
                                chooser,
                                PackageManager.MATCH_DEFAULT_ONLY
                        )
                //set the uri permissions for the activity and current ResolveInfo
                resInfoList.forEach {
                    val packageName: String = it.activityInfo.packageName
                    requireActivity().grantUriPermission(
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
                //clear the bitmap
                Glide.with(requireActivity()).clear(futureBitmap)
            }
            // catch the exceptions and show error toast
            catch (e: InterruptedException) {
                Timber.e(e)
                showErrorToast()
            } catch (e: ExecutionException) {
                Timber.e(e)
                showErrorToast()
            } catch (e: CancellationException) {
                Timber.e(e)
                showErrorToast()
            }
        }
    }

    private suspend fun showErrorToast() {
        withContext(Dispatchers.Main) {
            Toast.makeText(
                    requireActivity(),
                    requireActivity().resources.getString(R.string.error_share_no_network),
                    Toast.LENGTH_SHORT
            ).show()
        }
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