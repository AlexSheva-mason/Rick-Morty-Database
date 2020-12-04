package com.shevaalex.android.rickmortydatabase.ui.location.detail

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shevaalex.android.rickmortydatabase.RmApplication
import com.shevaalex.android.rickmortydatabase.databinding.FragmentLocationDetailBinding
import com.shevaalex.android.rickmortydatabase.models.character.CharacterModel
import com.shevaalex.android.rickmortydatabase.models.location.LocationModel
import com.shevaalex.android.rickmortydatabase.ui.BaseDetailFragment
import com.shevaalex.android.rickmortydatabase.ui.CharacterSmallAdapter
import com.shevaalex.android.rickmortydatabase.utils.Constants
import com.shevaalex.android.rickmortydatabase.utils.DiViewModelFactory
import javax.inject.Inject

class LocationDetailFragment : BaseDetailFragment<FragmentLocationDetailBinding, LocationModel>() {

    @Inject
    lateinit var viewModelFactory: DiViewModelFactory<LocationDetailViewModel>

    private var adapter: CharacterSmallAdapter? = null

    override val keyDetailObject: String = Constants.KEY_FRAGMENT_LOCATION_DETAIL_OBJECT

    override val viewModel: LocationDetailViewModel by viewModels {
        viewModelFactory
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getArgs()?.let {
            setupViews(it)
        }
        setRecyclerView()
        registerObservers()
        binding.buttonBack?.let {
            setBackButton(it)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //restore the view state
        savedInstanceState?.let {
            restoreViewState(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
    }

    /**
     * gets passed data from list fragment
     */
    private fun getArgs(): LocationModel? {
        val args: LocationDetailFragmentArgs by navArgs()
        return args.locationObject
    }

    private fun setupViews(location: LocationModel) {
        setShareButton(location)
        viewModel.setDetailObject(location)
        binding.imageLocation?.let {
            setMainImage(location.imageUrl, it)
        }
        binding.locationName.text = location.name
        binding.locationTypeValue.text = location.type
        binding.locationDimensionValue.text = location.dimension
    }

    private fun setRecyclerView() {
        //set the recyclerview
        val layoutManager = LinearLayoutManager(requireContext())
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutManager.orientation = RecyclerView.HORIZONTAL
        }
        binding.recyclerviewLocationDetail.layoutManager = layoutManager
        binding.recyclerviewLocationDetail.setHasFixedSize(true)
        //get recyclerview Adapter and set data to it using ViewModel
        adapter = CharacterSmallAdapter(object : CharacterSmallAdapter.CharacterClickListener {
            override fun onCharacterClick(character: CharacterModel) {
                navigateCharacterDetail(character)
            }
        })
        adapter?.stateRestorationPolicy =
                RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        binding.recyclerviewLocationDetail.adapter = adapter
    }

    private fun registerObservers() {
        viewModel.characters.observe(viewLifecycleOwner, { characters ->
            characters?.let {
                adapter?.setCharacterList(it)
                //explicilty reset the RV adapter, otherwise RV is invisible at the start of transition or landscape
                binding.recyclerviewLocationDetail.adapter = adapter
            }
            binding.locationResidentsNone.visibility = characters?.let {
                View.GONE
            }?: View.VISIBLE
        })
    }

    private fun setShareButton(location: LocationModel) {
        binding.buttonShare?.setOnClickListener {
            shareImageWithGlide(location.name, location.imageUrl)
        }
    }

    private fun navigateCharacterDetail(character: CharacterModel) {
        val action = LocationDetailFragmentDirections.actionGlobalCharacterDetailFragment2(character)
        findNavController().navigate(action)
    }

    private fun restoreViewState(savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            (it[keyDetailObject] as LocationModel?)?.let { location ->
                viewModel.setDetailObject(location)
            }
        }
    }

    override fun injectFragment() {
        activity?.run {
            (application as RmApplication).appComponent
        }?.inject(this)
    }

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?) =
            FragmentLocationDetailBinding.inflate(inflater, container, false)

    /**
     * returns MotionLayout. cast needed due to layout being ConstraintLayout in landscape mode
     */
    @Suppress("USELESS_CAST")
    override fun getMotionLayout(): MotionLayout? {
        return try {
            binding.layoutFragmentLocationDetail as MotionLayout
        } catch (e: Exception) {
            null
        }
    }

}