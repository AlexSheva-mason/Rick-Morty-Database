package com.shevaalex.android.rickmortydatabase.ui.base

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.widget.Guideline
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.FutureTarget
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialFadeThrough
import com.google.firebase.analytics.FirebaseAnalytics
import com.shevaalex.android.rickmortydatabase.R
import com.shevaalex.android.rickmortydatabase.models.RmObject
import com.shevaalex.android.rickmortydatabase.ui.viewmodel.BaseDetailViewModel
import com.shevaalex.android.rickmortydatabase.utils.Constants
import com.shevaalex.android.rickmortydatabase.utils.ImageParsingUtil
import com.shevaalex.android.rickmortydatabase.utils.displayErrorDialog
import com.shevaalex.android.rickmortydatabase.utils.getActionBarHeightPx
import kotlinx.coroutines.*
import timber.log.Timber
import java.util.*
import java.util.concurrent.CancellationException
import java.util.concurrent.ExecutionException

abstract class BaseDetailFragment<T : ViewBinding, S : RmObject> : BaseFragment() {

    private var _binding: T? = null
    protected val binding get() = _binding!!

    protected abstract val viewModel: BaseDetailViewModel<S>

    protected abstract val keyDetailObject: String

    override fun onAttach(context: Context) {
        //inject fragment
        injectFragment()
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prepareTransitions()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = setBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.doOnPreDraw {
            startPostponedEnterTransition()
        }
        setupToolbarEdgeToEdge()
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setupDetailLayoutWithTransparentStatusBar()
        }
    }

    override fun onResume() {
        super.onResume()
        restoreMotionState()
    }

    override fun onPause() {
        super.onPause()
        saveMotionState()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        saveViewState(outState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    protected fun shareImageWithGlide(detailObjectName: String, imageUrl: String?) {
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
                logShareEventWithFirebase(detailObjectName)
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

    private fun logShareEventWithFirebase(detailObjectName: String) {
        // fragment's class name for firebase logging
        val className = "_"
                .plus(this.javaClass.simpleName)
                .toLowerCase(Locale.ROOT)
        firebaseLogger.logFirebaseEvent(
                FirebaseAnalytics.Event.SHARE.plus(className),
                FirebaseAnalytics.Param.ITEM_ID,
                detailObjectName
        )
    }

    private suspend fun showErrorToast() {
        withContext(Dispatchers.Main) {
            requireActivity().displayErrorDialog(
                    requireActivity().resources.getString(R.string.error_share_no_network)
            )
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

    /**
     * called in onPause() to save the MotionLayout's transition state
     */
    private fun saveMotionState() {
        viewModel.setMotionStateId(getMotionLayout()?.currentState)
    }

    /**
     * called in onResume() to restore the MotionLayout's transition end state
     */
    private fun restoreMotionState() {
        viewModel.motionStateId.value?.let {
            getMotionLayout()?.setTransitionDuration(1)
            getMotionLayout()?.transitionToState(it)
        }
    }

    protected fun setMainImage(
            imageUrl: String?,
            imageView: ImageView,
            width: Int,
            height: Int,
            placeholderDrawableResource: Int) {
        imageView.apply {
            Glide.with(context)
                    .load(imageUrl)
                    .apply(RequestOptions()
                            .error(placeholderDrawableResource)
                            .dontTransform()
                            .dontAnimate()
                            .override(width, height)
                    )
                    .into(this)
        }
    }

    private fun prepareTransitions() {
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            postponeEnterTransition()
            sharedElementEnterTransition = MaterialContainerTransform().apply {
                drawingViewId = R.id.nav_host_fragment
                duration = resources.getInteger(R.integer.rm_motion_default_large).toLong()
                scrimColor = Color.TRANSPARENT
            }
        } else {
            enterTransition = MaterialFadeThrough().apply {
                duration = resources.getInteger(R.integer.rm_motion_default_large).toLong()
            }
        }
    }

    /**
     * sets constraintGuide_begin for guidelines accroding to window insets (status bar height)
     * to allign toolbar views in the layout
     */
    private fun setupToolbarEdgeToEdge() {
        getToolbarBottomGuideline()?.setOnApplyWindowInsetsListener { _, insets ->
            val insetsCompat = WindowInsetsCompat.toWindowInsetsCompat(insets)
            val systemWindow = insetsCompat.getInsets(
                    WindowInsetsCompat.Type.statusBars()
            )
            val topMargin = getActionBarHeightPx(requireContext()) + systemWindow.top
            getToolbarBottomGuideline()?.setGuidelineBegin(topMargin)
            getToolbarTopGuideline()?.setGuidelineBegin(systemWindow.top)
            insets
        }
    }

    protected fun setBackButton(backBtn: ImageView) {
        backBtn.setOnClickListener { findNavController().navigateUp() }
    }

    protected abstract fun injectFragment()

    protected abstract fun setBinding(inflater: LayoutInflater, container: ViewGroup?): T

    protected abstract fun getMotionLayout(): MotionLayout?

    protected abstract fun getToolbarBottomGuideline(): Guideline?

    protected abstract fun getToolbarTopGuideline(): Guideline?

    protected abstract fun setupDetailLayoutWithTransparentStatusBar()

}