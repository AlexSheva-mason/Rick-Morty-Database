package com.shevaalex.android.rickmortydatabase.ui.character.detail

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.widget.Guideline
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shevaalex.android.rickmortydatabase.R
import com.shevaalex.android.rickmortydatabase.RmApplication
import com.shevaalex.android.rickmortydatabase.databinding.FragmentCharacterDetailBinding
import com.shevaalex.android.rickmortydatabase.models.character.CharacterEntity
import com.shevaalex.android.rickmortydatabase.models.episode.EpisodeEntity
import com.shevaalex.android.rickmortydatabase.models.location.LocationEntity
import com.shevaalex.android.rickmortydatabase.ui.base.BaseDetailFragment
import com.shevaalex.android.rickmortydatabase.utils.*
import javax.inject.Inject
import com.shevaalex.android.rickmortydatabase.utils.Constants.Companion as Const

class CharacterDetailFragment : BaseDetailFragment<FragmentCharacterDetailBinding, CharacterEntity>() {

    @Inject
    lateinit var viewModelFactory: DiViewModelFactory<CharacterDetailViewModel>

    private var adapter: CharacterDetailAdapter? = null

    override val keyDetailObject: String = Const.KEY_FRAGMENT_CHAR_DETAIL_OBJECT

    override val viewModel: CharacterDetailViewModel by viewModels {
        viewModelFactory
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRecyclerView()
        setViews()
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

    private fun setRecyclerView() {
        // GridLayout in landscape mode
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            activity?.let {
                val spanCount = calculateNumberOfColumns(requireContext())
                val gridLayoutManager = GridLayoutManager(it, spanCount)
                binding.recyclerviewCharacterDetail.layoutManager = gridLayoutManager
                // apply spacing to gridlayout
                val columnSpacing = getDimensPx(requireContext(), R.dimen.item_grid_spacing)
                val itemDecoration = CustomItemDecoration(
                        spanCount = spanCount,
                        minimalSpacing = columnSpacing
                )
                binding.recyclerviewCharacterDetail.addItemDecoration(itemDecoration)
            }
            // LinearLayout in portrait mode
        } else {
            activity?.let {
                binding.recyclerviewCharacterDetail.layoutManager = LinearLayoutManager(it)
            }
        }
        binding.recyclerviewCharacterDetail.setHasFixedSize(true)
        //set the adapter
        adapter = CharacterDetailAdapter(
                placeHolderString = getString(R.string.episode_name_placeholder),
                episodeListener = object : CharacterDetailAdapter.EpisodeListener {
                    override fun onEpisodeClick(episode: EpisodeEntity) {
                        navigateEpisodeDetail(episode)
                    }
                }
        )
        adapter?.stateRestorationPolicy =
                RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        binding.recyclerviewCharacterDetail.adapter = adapter
    }

    private fun setViews() {
        // retrieve data from the list fragment
        val args: CharacterDetailFragmentArgs by navArgs()
        val character: CharacterEntity? = args.characterObject
        character?.let {
            setShareButton(it)
            viewModel.setDetailObject(it)
            binding.layoutFragmentCharacterDetail.transitionName =
                    Constants.TRANSITION_CHARACTER.plus(it.id)
            setMainImage(
                    imageUrl = it.imageUrl,
                    imageView = binding.imageCharacter,
                    height = 300,
                    width = 300,
                    placeholderDrawableResource = R.drawable.character_placeholder
            )
            binding.characterName.text = it.name
            if (it.status != activity?.resources?.getString(R.string.species_unknown)) {
                val color = TextColourUtil.getStatusColour(it.status, context)
                binding.characterStatus.text = it.status
                binding.characterStatus.setTextColor(color)
                binding.characterStatusDot?.setColorFilter(color)
            }
            if (it.species != activity?.resources?.getString(R.string.species_unknown)) {
                binding.characterSpecies.text = it.species
            }
            if (it.gender != activity?.resources?.getString(R.string.character_gender_unknown)) {
                binding.characterGender.text = it.gender
                setGenderIcon(it.gender)
            }
            if (it.lastLocation.name != activity?.resources?.getString(R.string.location_unknown)) {
                binding.characterLastLocValue.text = it.lastLocation.name
                setLocationActive(binding.lastLocChevron, binding.lastLocIcon, binding.characterLastLocValue)
            } else {
                binding.lastLocChevron.visibility = View.GONE
            }
            if (it.originLocation.name != activity?.resources?.getString(R.string.location_unknown)) {
                binding.characterOriginValue.text = it.originLocation.name
                setLocationActive(binding.originChevron, binding.originLocIcon, binding.characterOriginValue)
            } else {
                binding.originChevron.visibility = View.GONE
            }
        }
    }

    private fun setGenderIcon(gender: String) {
        activity?.let {
            var icon = R.drawable.ic_gender_male_female
            when (gender) {
                it.resources.getString(R.string.character_gender_male) ->
                    icon = R.drawable.ic_gender_male
                it.resources.getString(R.string.character_gender_female) ->
                    icon = R.drawable.ic_gender_female
                it.resources.getString(R.string.character_gender_genderless) ->
                    icon = R.drawable.ic_gender_male_female
            }
            binding.characterGenderIcon?.setImageResource(icon)
        }
    }

    private fun setLocationActive(chevronIcon: ImageView?, locIcon: ImageView?, locTv: TextView?) {
        locIcon?.setImageResource(R.drawable.ic_location_defined_24dp)
        activity?.let { activity ->
            val colorPrimary = TextColourUtil.fetchThemeColor(R.attr.colorPrimary, activity)
            chevronIcon?.setColorFilter(colorPrimary)
            locIcon?.setColorFilter(colorPrimary)
            locTv?.setTextColor(activity.getColor(R.color.material_on_background_emphasis_high_type))
        }
    }

    private fun registerObservers() {
        //observe Episode list
        viewModel.episodes.observe(viewLifecycleOwner, {
            it?.let {
                adapter?.setEpisodeList(it)
                //explicilty reset the RV adapter, otherwise RV is invisible at the start of transition or landscape
                binding.recyclerviewCharacterDetail.adapter = adapter
            }
        })
        //observe Locations and set click listeners
        viewModel.lastLocation.observe(viewLifecycleOwner, {
            it?.let { location ->
                binding.lastLocIconContainer.setOnClickListener {
                    navigateLocationDetail(location)
                }
            }
        })
        viewModel.originLocation.observe(viewLifecycleOwner, {
            it?.let { location ->
                binding.originIconContainer.setOnClickListener {
                    navigateLocationDetail(location)
                }
            }
        })
    }

    private fun setShareButton(character: CharacterEntity) {
        binding.buttonShare?.setOnClickListener {
            shareImageWithGlide(character.name, character.imageUrl)
        }
    }

    private fun navigateLocationDetail(location: LocationEntity) {
        val action = CharacterDetailFragmentDirections.actionGlobalLocationDetailFragment(location)
        safeNavigate(action)
    }

    private fun navigateEpisodeDetail(episode: EpisodeEntity) {
        val action = CharacterDetailFragmentDirections.actionGlobalEpisodeDetailFragment(episode)
        safeNavigate(action)
    }

    private fun restoreViewState(savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            (it[keyDetailObject] as CharacterEntity?)?.let { character ->
                viewModel.setDetailObject(character)
            }
        }
    }

    override fun injectFragment() {
        activity?.run {
            (application as RmApplication).appComponent
        }?.inject(this)
    }

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?) =
            FragmentCharacterDetailBinding.inflate(inflater, container, false)

    /**
     * returns MotionLayout. cast needed due to layout being ConstraintLayout in landscape mode
     */
    @Suppress("USELESS_CAST")
    override fun getMotionLayout(): MotionLayout? {
        return try {
            binding.layoutFragmentCharacterDetail as MotionLayout
        } catch (e: Exception) {
            null
        }
    }

    override fun getToolbarBottomGuideline(): Guideline? {
        return binding.guidelineToolbar
    }

    override fun getToolbarTopGuideline(): Guideline? {
        return binding.guidelineStatusBar
    }

    override fun setupDetailLayoutWithTransparentStatusBar() {
        binding.characterDetails?.setTopPaddingForStatusBar()
    }

}