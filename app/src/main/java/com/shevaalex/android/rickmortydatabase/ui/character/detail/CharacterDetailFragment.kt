package com.shevaalex.android.rickmortydatabase.ui.character.detail

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.shevaalex.android.rickmortydatabase.R
import com.shevaalex.android.rickmortydatabase.RmApplication
import com.shevaalex.android.rickmortydatabase.databinding.FragmentCharacterDetailBinding
import com.shevaalex.android.rickmortydatabase.models.character.CharacterModel
import com.shevaalex.android.rickmortydatabase.models.episode.EpisodeModel
import com.shevaalex.android.rickmortydatabase.models.location.LocationModel
import com.shevaalex.android.rickmortydatabase.ui.BaseDetailFragment
import com.shevaalex.android.rickmortydatabase.utils.CustomItemDecoration
import com.shevaalex.android.rickmortydatabase.utils.MyViewModelFactory
import com.shevaalex.android.rickmortydatabase.utils.TextColourUtil
import javax.inject.Inject
import com.shevaalex.android.rickmortydatabase.utils.Constants.Companion as Const

class CharacterDetailFragment: BaseDetailFragment<FragmentCharacterDetailBinding, CharacterModel>() {

    @Inject
    lateinit var viewModelFactory: MyViewModelFactory<CharacterDetailViewModel>

    private var adapter: CharacterDetailAdapter? = null

    override val keyDetailObject: String = Const.KEY_FRAGMENT_CHAR_DETAIL_OBJECT

    override val viewModel: CharacterDetailViewModel by viewModels {
        viewModelFactory
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setRecyclerView()
        setViews()
        registerObservers()
        setBackButton()
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
                val spanCount = it.resources.getInteger(R.integer.grid_span_count)
                val gridLayoutManager = GridLayoutManager(it, spanCount)
                binding.recyclerviewCharacterDetail.layoutManager = gridLayoutManager
                // apply spacing to gridlayout
                val itemDecoration = CustomItemDecoration(it, false)
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
                    override fun onEpisodeClick(episode: EpisodeModel) {
                        navigateEpisodeDetail(episode)
                    }
                }
        )
        adapter?.stateRestorationPolicy =
                RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        binding.recyclerviewCharacterDetail.adapter = adapter
    }

    private fun setViews(){
        // retrieve data from the list fragment
        val args: CharacterDetailFragmentArgs by navArgs()
        val character: CharacterModel? = args.characterObject
        character?.let {
            setShareButton(it)
            viewModel.setDetailObject(it)
            setCharacterImage(it)
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
                setLocationActive(binding.lastLocIcon, binding.characterLastLocValue)
            }
            if (it.originLocation.name != activity?.resources?.getString(R.string.location_unknown)) {
                binding.characterOriginValue.text = it.originLocation.name
                setLocationActive(binding.originIcon, binding.characterOriginValue)
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

    private fun setLocationActive(locIcon: ImageView?, locTv: TextView?) {
        locIcon?.setImageResource(R.drawable.ic_location_defined_24dp)
        activity?.let { activity ->
            val colorPrimary = TextColourUtil.fetchThemeColor(R.attr.colorPrimary, activity)
            locIcon?.setColorFilter(colorPrimary)
            locTv?.setTextColor(colorPrimary)
            locTv?.isAllCaps = true
        }
    }

    private fun setCharacterImage(character: CharacterModel) {
        activity?.let { a ->
            binding.imageCharacter.let { imageView ->
                Glide.with(a)
                        .load(character.imageUrl)
                        .apply(RequestOptions()
                                .placeholder(R.drawable.picasso_placeholder_error)
                                .error(R.drawable.picasso_placeholder_error)
                        )
                        .into(imageView)
            }
        }
    }

    private fun registerObservers() {
        //observe Episode list
        viewModel.episodes.observe(viewLifecycleOwner, {
            it?.let { adapter?.setEpisodeList(it) }
        })
        //observe Locations and set click listeners
        viewModel.lastLocation.observe(viewLifecycleOwner, {
            it?.let { location ->
                binding.characterLastLocValue.setOnClickListener {
                    navigateLocationDetail(location)
                }
            }
        })
        viewModel.originLocation.observe(viewLifecycleOwner, {
            it?.let { location ->
                binding.characterOriginValue.setOnClickListener {
                    navigateLocationDetail(location)
                }
            }
        })
    }


    private fun setBackButton() {
        binding.buttonBack?.setOnClickListener { findNavController().navigateUp() }
    }

    private fun setShareButton(character: CharacterModel) {
        activity?.let { a ->
                binding.buttonShare?.setOnClickListener {
                    shareImageWithGlide(a, character.name, character.imageUrl)
                }
        }
    }

    private fun navigateLocationDetail(location: LocationModel) {
        Toast.makeText(requireContext(), location.name, Toast.LENGTH_SHORT).show()
        // do nothing for now
    }

    private fun navigateEpisodeDetail(episodeModel: EpisodeModel) {
        Toast.makeText(requireContext(), episodeModel.name, Toast.LENGTH_SHORT).show()
        // do nothing for now
    }

    private fun restoreViewState(savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            (it[keyDetailObject] as CharacterModel?)?.let { character ->
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

}