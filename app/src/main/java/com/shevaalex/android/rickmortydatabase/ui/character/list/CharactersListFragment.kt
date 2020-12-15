package com.shevaalex.android.rickmortydatabase.ui.character.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.google.android.material.card.MaterialCardView
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.play.core.ktx.launchReview
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.testing.FakeReviewManager
import com.shevaalex.android.rickmortydatabase.BuildConfig
import com.shevaalex.android.rickmortydatabase.R
import com.shevaalex.android.rickmortydatabase.RmApplication
import com.shevaalex.android.rickmortydatabase.databinding.FragmentCharactersListBinding
import com.shevaalex.android.rickmortydatabase.models.character.CharacterModel
import com.shevaalex.android.rickmortydatabase.ui.BaseListFragment
import com.shevaalex.android.rickmortydatabase.ui.ReviewViewModel
import com.shevaalex.android.rickmortydatabase.utils.*
import timber.log.Timber
import javax.inject.Inject
import com.shevaalex.android.rickmortydatabase.utils.Constants.Companion as Const


class CharactersListFragment : BaseListFragment<FragmentCharactersListBinding>() {

    @Inject
    lateinit var viewModelFactory: DiViewModelFactory<CharacterListViewModel>

    @Inject
    lateinit var reviewViewmodelFactory: DiViewModelFactory<ReviewViewModel>

    @Inject
    lateinit var reviewManager: ReviewManager

    @Inject
    lateinit var fakeReviewManager: FakeReviewManager

    override val viewModel: CharacterListViewModel by activityViewModels {
        viewModelFactory
    }

    private val reviewViewModel: ReviewViewModel by activityViewModels {
        reviewViewmodelFactory
    }

    override val keyListFilterMap = Const.KEY_FRAGMENT_CHAR_LIST_FILTER_MAP

    override val keyListQuery = Const.KEY_FRAGMENT_CHAR_LIST_QUERY

    override val keyListPosition = Const.KEY_FRAGMENT_CHAR_LIST_LIST_POSITION

    private var characterAdapter: CharacterAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRecyclerView()
        registerObservers()
    }

    override fun onResume() {
        super.onResume()
        attemptShowReviewDialog()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        characterAdapter = null
    }

    private fun setRecyclerView() {
        binding.recyclerviewCharacter.setHasFixedSize(true)
        //instantiate the adapter and set this fragment as a listener for onClick
        characterAdapter = CharacterAdapter(
                object: CharacterAdapter.CharacterListener{
                    override fun onCharacterClick(character: CharacterModel,
                                                  characterCard: MaterialCardView
                    ) {
                        navigateCharacterDetail(character, characterCard)
                    }
                }
        )
        characterAdapter?.stateRestorationPolicy =
                RecyclerView.Adapter.StateRestorationPolicy.PREVENT
        //set the adapter to recyclerview
        binding.recyclerviewCharacter.adapter = characterAdapter
    }

    private fun registerObservers() {
        //set data for RV
        viewModel.characterList.observe(viewLifecycleOwner, { characters ->
            if (characters.isEmpty()) {
                viewModel.searchQuery?.let {
                    binding.tvNoResults.visibility = View.VISIBLE
                }
            } else {
                binding.tvNoResults.visibility = View.GONE
            }
            //set data to the adapter
            characterAdapter?.submitList(characters)
            //restore list position, or if it has been nulled -> scroll to position 0
            viewModel.rvListPosition.value?.let {
                binding.recyclerviewCharacter.layoutManager?.onRestoreInstanceState(it)
            }?: binding.recyclerviewCharacter.layoutManager?.scrollToPosition(0)
        })
    }

    private fun attemptShowReviewDialog() {
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            val reviewInfo = reviewViewModel.obtainReviewInfo()
            reviewInfo?.let {
                if (BuildConfig.DEBUG) {
                    Timber.w("starting launch review flow")
                    val flow = fakeReviewManager.launchReviewFlow(requireActivity(), it)
                    reviewViewModel.notifyReviewFlowLaunched()
                    flow.addOnCompleteListener {
                        if (flow.isSuccessful) {
                            //show "fake review dialog"
                            activity?.displayErrorDialog(
                                    "fakeReviewManager.launchReviewFlow == SUCCESS"
                            )
                        }
                    }
                } else {
                    reviewManager.launchReview(requireActivity(), it)
                    reviewViewModel.notifyReviewFlowLaunched()
                }
            }
        }
    }

    override fun injectFragment() {
        activity?.run {
            (application as RmApplication).appComponent
        }?.inject(this)
    }

    override fun setBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
    ): FragmentCharactersListBinding =
            FragmentCharactersListBinding.inflate(inflater, container, false)

    override fun getToolbar() = binding.toolbarFragmentCharacterList as Toolbar?

    override fun showFilterDialog() {
        activity?.let { activity ->
            val dialog = MaterialDialog(activity)
                    .title(R.string.dialog_title)
                    .negativeButton(text = getString(R.string.dialog_negative_button)) {
                        it.dismiss()
                    }
                    .noAutoDismiss()
                    .customView(
                            viewRes = R.layout.dialog_filter_character,
                            scrollable = true)
            val dialogView = dialog.getCustomView()

            val statusCategory = listOf(
                    dialogView.findViewById(R.id.status_alive),
                    dialogView.findViewById(R.id.status_dead),
                    dialogView.findViewById<MaterialCheckBox>(R.id.status_unknown)
            )
            val genderCategory = listOf(
                    dialogView.findViewById(R.id.gender_female),
                    dialogView.findViewById(R.id.gender_male),
                    dialogView.findViewById(R.id.gender_genderless),
                    dialogView.findViewById<MaterialCheckBox>(R.id.gender_unknown)
            )
            val speciesCustom = listOf(
                    dialogView.findViewById(R.id.species_human),
                    dialogView.findViewById(R.id.species_humanoid),
                    dialogView.findViewById(R.id.species_alien),
                    dialogView.findViewById(R.id.species_animal),
                    dialogView.findViewById(R.id.species_robot),
                    dialogView.findViewById(R.id.species_poopy),
                    dialogView.findViewById(R.id.species_cronenberg),
                    dialogView.findViewById<MaterialCheckBox>(R.id.species_mythological))
            val cbSpeciesAll = dialogView.findViewById<MaterialCheckBox>(R.id.species_all)

            restoreCheckBoxState(dialogView)

            cbSpeciesAll.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    speciesCustom.forEach {
                        it.isChecked = false
                    }
                }
            }
            speciesCustom.forEach {
                it.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked && cbSpeciesAll.isChecked) {
                        cbSpeciesAll.isChecked = false
                    }
                }
            }

            dialog.positiveButton(text = getString(R.string.dialog_positive_button)) { mdialog ->
                if (statusCategory.any { it.isChecked }
                        && genderCategory.any { it.isChecked }
                        && (speciesCustom.any { it.isChecked } || cbSpeciesAll.isChecked)) {
                    setupFiltration(mdialog)
                } else {
                    val errors: MutableList<String> = mutableListOf()
                    if (statusCategory.all { !it.isChecked }) {
                        errors.add(getString(R.string.dialog_error_status))
                    }
                    if (genderCategory.all { !it.isChecked }) {
                        errors.add(getString(R.string.dialog_error_gender))
                    }
                    if (speciesCustom.all { !it.isChecked } && !cbSpeciesAll.isChecked) {
                        errors.add(getString(R.string.dialog_error_species))
                    }
                    activity.displayErrorDialog(
                            getString(R.string.dialog_error_message)
                                    .plus(errors.joinToString())
                    )
                }
            }

            dialog.show {
                lifecycleOwner(viewLifecycleOwner)
            }
        }
    }

    override fun restoreCheckBoxState(dialogView: View) {
        viewModel.getFilterMap()?.let {
            dialogView.findViewById<MaterialCheckBox>(R.id.status_alive).isChecked =
                    it[Constants.KEY_MAP_FILTER_STATUS_ALIVE_F]?.first?: false
            dialogView.findViewById<MaterialCheckBox>(R.id.status_dead).isChecked =
                    it[Constants.KEY_MAP_FILTER_STATUS_DEAD_F]?.first?: false
            dialogView.findViewById<MaterialCheckBox>(R.id.status_unknown).isChecked =
                    it[Constants.KEY_MAP_FILTER_STATUS_UNKNOWN]?.first?: false
            dialogView.findViewById<MaterialCheckBox>(R.id.gender_female).isChecked =
                    it[Constants.KEY_MAP_FILTER_GENDER_FEMALE]?.first?: false
            dialogView.findViewById<MaterialCheckBox>(R.id.gender_male).isChecked =
                    it[Constants.KEY_MAP_FILTER_GENDER_MALE]?.first?: false
            dialogView.findViewById<MaterialCheckBox>(R.id.gender_genderless).isChecked =
                    it[Constants.KEY_MAP_FILTER_GENDER_GENDERLESS]?.first?: false
            dialogView.findViewById<MaterialCheckBox>(R.id.gender_unknown).isChecked =
                    it[Constants.KEY_MAP_FILTER_GENDER_UNKNOWN]?.first?: false
            dialogView.findViewById<MaterialCheckBox>(R.id.species_all).isChecked =
                    it[Constants.KEY_MAP_FILTER_SPECIES_ALL]?.first?: false
            dialogView.findViewById<MaterialCheckBox>(R.id.species_human).isChecked =
                    it[Constants.KEY_MAP_FILTER_SPECIES_HUMAN]?.first?: false
            dialogView.findViewById<MaterialCheckBox>(R.id.species_humanoid).isChecked =
                    it[Constants.KEY_MAP_FILTER_SPECIES_HUMANOID]?.first?: false
            dialogView.findViewById<MaterialCheckBox>(R.id.species_alien).isChecked =
                    it[Constants.KEY_MAP_FILTER_SPECIES_ALIEN]?.first?: false
            dialogView.findViewById<MaterialCheckBox>(R.id.species_animal).isChecked =
                    it[Constants.KEY_MAP_FILTER_SPECIES_ANIMAL]?.first?: false
            dialogView.findViewById<MaterialCheckBox>(R.id.species_robot).isChecked =
                    it[Constants.KEY_MAP_FILTER_SPECIES_ROBOT]?.first?: false
            dialogView.findViewById<MaterialCheckBox>(R.id.species_poopy).isChecked =
                    it[Constants.KEY_MAP_FILTER_SPECIES_POOPY]?.first?: false
            dialogView.findViewById<MaterialCheckBox>(R.id.species_cronenberg).isChecked =
                    it[Constants.KEY_MAP_FILTER_SPECIES_CRONENBERG]?.first?: false
            dialogView.findViewById<MaterialCheckBox>(R.id.species_mythological).isChecked =
                    it[Constants.KEY_MAP_FILTER_SPECIES_MYTH]?.first?: false
        }
    }

    override fun setupFiltration(mdialog: MaterialDialog) {
        val dialogView = mdialog.getCustomView()

        val stringMap = getStringMap()
        val filterMap = mutableMapOf<String, Pair<Boolean, String?>>()

        //according to the state of a checkbox map the appropriate booleans and string values
        if (dialogView.findViewById<MaterialCheckBox>(R.id.status_alive).isChecked) {
            filterMap[Const.KEY_MAP_FILTER_STATUS_ALIVE_F] =
                    Pair(true, stringMap[Const.KEY_MAP_FILTER_STATUS_ALIVE_F])
            filterMap[Const.KEY_MAP_FILTER_STATUS_ALIVE_M] =
                    Pair(true, stringMap[Const.KEY_MAP_FILTER_STATUS_ALIVE_M])
        } else {
            filterMap[Const.KEY_MAP_FILTER_STATUS_ALIVE_F] =
                    Pair(false, null)
            filterMap[Const.KEY_MAP_FILTER_STATUS_ALIVE_M] =
                    Pair(false, null)
        }
        if (dialogView.findViewById<MaterialCheckBox>(R.id.status_dead).isChecked) {
            filterMap[Const.KEY_MAP_FILTER_STATUS_DEAD_F] =
                    Pair(true, stringMap[Const.KEY_MAP_FILTER_STATUS_DEAD_F])
            filterMap[Const.KEY_MAP_FILTER_STATUS_DEAD_M] =
                    Pair(true, stringMap[Const.KEY_MAP_FILTER_STATUS_DEAD_M])
        } else {
            filterMap[Const.KEY_MAP_FILTER_STATUS_DEAD_F] =
                    Pair(false, null)
            filterMap[Const.KEY_MAP_FILTER_STATUS_DEAD_M] =
                    Pair(false, null)
        }
        filterMap[Const.KEY_MAP_FILTER_STATUS_UNKNOWN] =
                if (dialogView.findViewById<MaterialCheckBox>(R.id.status_unknown).isChecked)
                    Pair(true, stringMap[Const.KEY_MAP_FILTER_STATUS_UNKNOWN])
                else Pair(false, null)
        filterMap[Const.KEY_MAP_FILTER_GENDER_FEMALE] =
                if (dialogView.findViewById<MaterialCheckBox>(R.id.gender_female).isChecked)
                    Pair(true, stringMap[Const.KEY_MAP_FILTER_GENDER_FEMALE])
                else Pair(false, null)
        filterMap[Const.KEY_MAP_FILTER_GENDER_MALE] =
                if (dialogView.findViewById<MaterialCheckBox>(R.id.gender_male).isChecked)
                    Pair(true, stringMap[Const.KEY_MAP_FILTER_GENDER_MALE])
                else Pair(false, null)
        filterMap[Const.KEY_MAP_FILTER_GENDER_GENDERLESS] =
                if (dialogView.findViewById<MaterialCheckBox>(R.id.gender_genderless).isChecked)
                    Pair(true, stringMap[Const.KEY_MAP_FILTER_GENDER_GENDERLESS])
                else Pair(false, null)
        filterMap[Const.KEY_MAP_FILTER_GENDER_UNKNOWN] =
                if (dialogView.findViewById<MaterialCheckBox>(R.id.gender_unknown).isChecked)
                    Pair(true, stringMap[Const.KEY_MAP_FILTER_GENDER_UNKNOWN])
                else Pair(false, null)
        filterMap[Const.KEY_MAP_FILTER_SPECIES_ALL] =
                if (dialogView.findViewById<MaterialCheckBox>(R.id.species_all).isChecked)
                    Pair(true, null)
                else Pair(false, null)
        filterMap[Const.KEY_MAP_FILTER_SPECIES_HUMAN] =
                if (dialogView.findViewById<MaterialCheckBox>(R.id.species_human).isChecked)
                    Pair(true, stringMap[Const.KEY_MAP_FILTER_SPECIES_HUMAN])
                else Pair(false, null)
        filterMap[Const.KEY_MAP_FILTER_SPECIES_HUMANOID] =
                if (dialogView.findViewById<MaterialCheckBox>(R.id.species_humanoid).isChecked)
                    Pair(true, stringMap[Const.KEY_MAP_FILTER_SPECIES_HUMANOID])
                else Pair(false, null)
        filterMap[Const.KEY_MAP_FILTER_SPECIES_ALIEN] =
                if (dialogView.findViewById<MaterialCheckBox>(R.id.species_alien).isChecked)
                    Pair(true, stringMap[Const.KEY_MAP_FILTER_SPECIES_ALIEN])
                else Pair(false, null)
        filterMap[Const.KEY_MAP_FILTER_SPECIES_ANIMAL] =
                if (dialogView.findViewById<MaterialCheckBox>(R.id.species_animal).isChecked)
                    Pair(true, stringMap[Const.KEY_MAP_FILTER_SPECIES_ANIMAL])
                else Pair(false, null)
        filterMap[Const.KEY_MAP_FILTER_SPECIES_ROBOT] =
                if (dialogView.findViewById<MaterialCheckBox>(R.id.species_robot).isChecked)
                    Pair(true, stringMap[Const.KEY_MAP_FILTER_SPECIES_ROBOT])
                else Pair(false, null)
        filterMap[Const.KEY_MAP_FILTER_SPECIES_POOPY] =
                if (dialogView.findViewById<MaterialCheckBox>(R.id.species_poopy).isChecked)
                    Pair(true, stringMap[Const.KEY_MAP_FILTER_SPECIES_POOPY])
                else Pair(false, null)
        filterMap[Const.KEY_MAP_FILTER_SPECIES_CRONENBERG] =
                if (dialogView.findViewById<MaterialCheckBox>(R.id.species_cronenberg).isChecked)
                    Pair(true, stringMap[Const.KEY_MAP_FILTER_SPECIES_CRONENBERG])
                else Pair(false, null)
        filterMap[Const.KEY_MAP_FILTER_SPECIES_MYTH] =
                if (dialogView.findViewById<MaterialCheckBox>(R.id.species_mythological).isChecked)
                    Pair(true, stringMap[Const.KEY_MAP_FILTER_SPECIES_MYTH])
                else Pair(false, null)

        viewModel.setFilterFlags(filterMap.toMap())

        mdialog.dismiss()
    }

    /**
     * gets the string resources for filter values and maps them to appropriate key constants
     */
    override fun getStringMap(): Map<String, String> = mapOf(
            Const.KEY_MAP_FILTER_STATUS_ALIVE_F to getString(R.string.character_status_alive_female),
            Const.KEY_MAP_FILTER_STATUS_ALIVE_M to getString(R.string.character_status_alive_male),
            Const.KEY_MAP_FILTER_STATUS_DEAD_F to getString(R.string.character_status_dead_female),
            Const.KEY_MAP_FILTER_STATUS_DEAD_M to getString(R.string.character_status_dead_male),
            Const.KEY_MAP_FILTER_STATUS_UNKNOWN to getString(R.string.character_gender_unknown),
            Const.KEY_MAP_FILTER_GENDER_FEMALE to getString(R.string.character_gender_female),
            Const.KEY_MAP_FILTER_GENDER_MALE to getString(R.string.character_gender_male),
            Const.KEY_MAP_FILTER_GENDER_GENDERLESS to getString(R.string.character_gender_genderless),
            Const.KEY_MAP_FILTER_GENDER_UNKNOWN to getString(R.string.character_gender_unknown),
            Const.KEY_MAP_FILTER_SPECIES_HUMAN to getString(R.string.species_Human),
            Const.KEY_MAP_FILTER_SPECIES_HUMANOID to getString(R.string.species_Humanoid),
            Const.KEY_MAP_FILTER_SPECIES_ALIEN to getString(R.string.species_Alien),
            Const.KEY_MAP_FILTER_SPECIES_ANIMAL to getString(R.string.species_Animal),
            Const.KEY_MAP_FILTER_SPECIES_ROBOT to getString(R.string.species_Robot),
            Const.KEY_MAP_FILTER_SPECIES_POOPY to getString(R.string.species_Poopybutthole),
            Const.KEY_MAP_FILTER_SPECIES_CRONENBERG to getString(R.string.species_Cronenberg),
            Const.KEY_MAP_FILTER_SPECIES_MYTH to getString(R.string.species_Mythological_Creature),
    )

    override fun saveRvListPosition() {
        binding.recyclerviewCharacter.layoutManager?.onSaveInstanceState()?.let { lmState ->
            viewModel.setLayoutManagerState(lmState)
        }
    }

    private fun navigateCharacterDetail(character: CharacterModel, characterCard: MaterialCardView) {
        setExitAndReenterAnimation()
        val extras = FragmentNavigatorExtras(
                characterCard to Constants.TRANSITION_CHARACTER.plus(character.id)
        )
        val action = CharactersListFragmentDirections.toCharacterDetailFragmentAction(character)
        findNavController().navigate(action , extras)
    }

}