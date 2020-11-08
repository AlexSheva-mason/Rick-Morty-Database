package com.shevaalex.android.rickmortydatabase.ui.character

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.shevaalex.android.rickmortydatabase.R
import com.shevaalex.android.rickmortydatabase.RmApplication
import com.shevaalex.android.rickmortydatabase.databinding.FragmentCharactersListBinding
import com.shevaalex.android.rickmortydatabase.ui.BaseFragment
import com.shevaalex.android.rickmortydatabase.utils.Constants
import com.shevaalex.android.rickmortydatabase.utils.MyViewModelFactory
import com.shevaalex.android.rickmortydatabase.utils.displayErrorDialog
import com.shevaalex.android.rickmortydatabase.utils.hideKeyboard
import kotlinx.android.synthetic.main.fragment_characters_list.view.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import com.shevaalex.android.rickmortydatabase.utils.Constants.Companion as Const


class CharactersListFragment : BaseFragment(), CharacterAdapter.OnCharacterListener {

    @Inject
    lateinit var viewModelFactory: MyViewModelFactory<CharacterListViewModel>

    private var _binding: FragmentCharactersListBinding? = null

    private val binding get() = _binding!!

    private val characterListViewModel: CharacterListViewModel by activityViewModels {
        viewModelFactory
    }

    private var searchSuggestionsAdapter: ArrayAdapter<String>? = null

    private var recentQueriesAdapter: ArrayAdapter<String>? = null

    private var characterAdapter: CharacterAdapter? = null

    override fun onAttach(context: Context) {
        //inject fragment
        activity?.run {
            (application as RmApplication).appComponent
        }?.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentCharactersListBinding.inflate(inflater, container, false)
        val view = binding.root
        setRecyclerView()
        registerObservers()
        inflateToolbar()
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //restore the view state
        savedInstanceState?.let {
            Timber.v("onActivityCreated savedInstanceState is not null")
            (savedInstanceState[Const.KEY_FRAGMENT_CHAR_LIST_FILTER_MAP] as String?)?.let {
                val type = object: TypeToken<Map<String, Pair<Boolean, String?>>>() {}.type
                val map = Gson().fromJson<Map<String, Pair<Boolean, String?>>>(it, type)
                Timber.v("restoring map: %s", map)
                characterListViewModel.setFilterFlags(map)
            }
            (savedInstanceState[Const.KEY_FRAGMENT_CHAR_LIST_QUERY] as String?)?.let {
                Timber.v("restoring query: %s", it)
                characterListViewModel.setNameQuery(it)
            }
            (savedInstanceState[Const.KEY_FRAGMENT_CHAR_LIST_LIST_POSITION] as Parcelable?)?.let {
                characterListViewModel.setLayoutManagerState(it)
            }
        }?: Timber.e("onActivityCreated savedInstanceState is null")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val navController = findNavController()
        //Set the action bar to show appropriate title, set top level destinations
        val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.charactersListFragment,
                R.id.locationsListFragment,
                R.id.episodesListFragment))
        binding.toolbarFragmentCharacterList.setupWithNavController(
                navController,
                appBarConfiguration
        )
    }

    override fun onResume() {
        restoreSearchViewState()
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        clearUi()
        customSaveState()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.run {
            characterListViewModel.getFilterMap.value?.let {
                val jsonMap = Gson().toJson(it)
                putString(Const.KEY_FRAGMENT_CHAR_LIST_FILTER_MAP, jsonMap)
                Timber.v("putting map to outState: %s", jsonMap)
            }
            characterListViewModel.searchQuery?.let {
                putString(Const.KEY_FRAGMENT_CHAR_LIST_QUERY, it)
                Timber.v("putting query to outState: %s", it)
            }
            characterListViewModel.rvListPosition.value?.let {
                putParcelable(Const.KEY_FRAGMENT_CHAR_LIST_LIST_POSITION, it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        characterAdapter = null
        _binding = null
    }

    private fun setRecyclerView() {
        binding.recyclerviewCharacter.setHasFixedSize(true)
        //instantiate the adapter and set this fragment as a listener for onClick
        characterAdapter = CharacterAdapter(
                requireActivity(),
                this
        )
        characterAdapter?.stateRestorationPolicy =
                RecyclerView.Adapter.StateRestorationPolicy.PREVENT
    }

    private fun registerObservers() {
        //set data for RV
        characterListViewModel.characterList.observe(viewLifecycleOwner, { characters ->
            if (characters.isEmpty()) {
                characterListViewModel.searchQuery?.let {
                    binding.tvNoResults.visibility = View.VISIBLE
                }
            } else {
                binding.tvNoResults.visibility = View.GONE
            }
            //set data to the adapter
            characterAdapter?.submitList(characters)
            //set adapter to the recyclerview
            binding.recyclerviewCharacter.adapter = characterAdapter
            //restore list position
            characterListViewModel.rvListPosition.value?.let {
                binding.recyclerviewCharacter.layoutManager?.onRestoreInstanceState(it)
            }
        })
        //set searchView new query suggestions adapter
        characterListViewModel.suggestions.observe(viewLifecycleOwner, { suggestionList ->
            suggestionList?.let {
                searchSuggestionsAdapter = ArrayAdapter(
                        requireContext(),
                        R.layout.item_search_suggestions,
                        it)
            }
        })
        //set searchView recent suggestions adapter
        characterListViewModel.recentQueries.observe(viewLifecycleOwner, { recentQueries ->
            recentQueries?.let {
                recentQueriesAdapter = ArrayAdapter(
                        requireContext(),
                        R.layout.item_recent_suggestions,
                        it)
            }
        })
    }

    private fun inflateToolbar() {
        val toolbar = binding.toolbarFragmentCharacterList
        toolbar.inflateMenu(R.menu.menu_filter)
        setupSearchView()
        setupMenuButtons()
    }

    @SuppressLint("RestrictedApi")
    private fun setupSearchView() {
        val toolbar: Toolbar? = binding.toolbarFragmentCharacterList
        toolbar?.let {
            val searchView: SearchView? = it.search_view
            val searchPlate: SearchView.SearchAutoComplete? =
                    searchView?.findViewById(androidx.appcompat.R.id.search_src_text)
            searchPlate?.setTextAppearance(R.style.TextAppearance_RM_SearchView_Hint)
            searchPlate?.threshold = 0
            searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let {
                        val queryText = query.trim().toLowerCase(Locale.getDefault())
                        if (queryText != characterListViewModel.searchQuery) {
                            // if true - this query has never been logged or saved to db table
                            if (characterListViewModel.addLogQuery(queryText)) {
                                //log query to Firebase
                                firebaseLogQuery(queryText)
                                //save query for recent suggestions list
                                lifecycleScope.launch {
                                    characterListViewModel.saveSearchQuery(queryText)
                                }
                            }
                            characterListViewModel.setNameQuery(queryText)
                        }
                    }
                    searchView.clearFocus()
                    view?.requestFocus()
                    view?.hideKeyboard()
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    newText?.let { query ->
                        //if query is empty -> show all results in RV and set the suggestions adapter
                        // to show the list of recent queries
                        if (query.isBlank()) {
                            if (query != characterListViewModel.searchQuery) {
                                characterListViewModel.setNameQuery("")
                            }
                            recentQueriesAdapter?.let { adapter ->
                                if (searchPlate?.adapter != adapter) {
                                    searchPlate?.setAdapter(adapter)
                                }
                            }
                        }
                        // else set the suggestions adapter with Character names
                        else {
                            searchSuggestionsAdapter?.let { adapter ->
                                if (searchPlate?.adapter != adapter) {
                                    searchPlate?.setAdapter(adapter)
                                }
                            }
                        }
                    }
                    return false
                }
            })
            searchView?.setOnSuggestionListener(object : SearchView.OnSuggestionListener {
                override fun onSuggestionSelect(position: Int): Boolean {
                    // do nothing
                    return true
                }

                override fun onSuggestionClick(position: Int): Boolean {
                    val newQuery: String? = searchPlate?.adapter?.getItem(position)?.toString()
                    newQuery?.let {
                        searchView.setQuery(newQuery, true)
                    }
                    return true
                }
            })
            //after searchPlate gained focus -> set the adapter to recentQueriesAdapter (if hasn't been set)
            searchPlate?.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus && searchPlate.text.isNullOrBlank()) {
                    recentQueriesAdapter?.let { adapter ->
                        if (searchPlate.adapter != adapter) {
                            searchPlate.setAdapter(adapter)
                        }
                    }
                }
            }
        }
    }

    private fun setupMenuButtons() {
        val toolbar: Toolbar? = binding.toolbarFragmentCharacterList
        toolbar?.setOnMenuItemClickListener { item ->
            when (item?.itemId) {
                R.id.settings_button -> {
                    toolbar.menu?.findItem(R.id.search_view)?.collapseActionView()
                    navigateToSettings()
                    true
                }
                R.id.filter_button -> {
                    showFilterDialog()
                    true
                }
                else -> false
            }
        }
    }

    private fun showFilterDialog() {
        activity?.let { activity ->
            val dialog = MaterialDialog(activity)
                    .title(R.string.dialog_title)
                    .negativeButton(text = getString(R.string.dialog_negative_button)) {
                        it.dismiss()
                    }
                    .noAutoDismiss()
                    .customView(
                            viewRes = R.layout.dialog_filter,
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

    private fun restoreCheckBoxState(dialogView: View) {
        characterListViewModel.getFilterMap.value?.let {
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

    private fun setupFiltration(dialog: MaterialDialog) {
        val dialogView = dialog.getCustomView()

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
                    Pair(true, stringMap[Const.KEY_MAP_FILTER_SPECIES_ALL])
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

        characterListViewModel.setFilterFlags(filterMap.toMap())

        dialog.dismiss()
    }

    /**
     * gets the string resources for filter values and maps them to appropriate key constants
     */
    private fun getStringMap(): Map<String, String> = mapOf(
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

    private fun navigateToSettings() {
        findNavController().navigate(CharactersListFragmentDirections.toSettingsFragment())
    }

    private fun customSaveState() {
        binding.recyclerviewCharacter.layoutManager?.onSaveInstanceState()?.let { lmState ->
            characterListViewModel.setLayoutManagerState(lmState)
        }
    }

    /**
     * restores view state of SearchView and hides softInput keyboard
     */
    private fun restoreSearchViewState() {
        characterListViewModel.searchQuery?.let {
            val searchView: SearchView? = binding.toolbarFragmentCharacterList.search_view
            if (it.isNotBlank()) {
                searchView?.setQuery(characterListViewModel.searchQuery, false)
                searchView?.isIconified = false
                searchView?.clearFocus()
            } else {
                searchView?.isIconified = true
            }
        }
    }

    private fun clearUi() {
        binding.toolbarFragmentCharacterList.search_view?.clearFocus()
        view?.requestFocus()
        view?.hideKeyboard()
    }

    override fun onCharacterClick(position: Int, v: View) {
        val mCharacterList = characterAdapter?.currentList
        if (mCharacterList != null && !mCharacterList.isEmpty()) {
            val clickedChar = mCharacterList[position]
            clickedChar?.let {
                val action = CharactersListFragmentDirections.toCharacterDetailFragmentAction()
                action.id = clickedChar.id
                action.characterName = clickedChar.name
                v.findNavController().navigate(action)
            }
        }
    }

    private fun firebaseLogQuery(query: String) {
        Timber.v("logging query to firebase: %s", query)
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SEARCH) {
            param(FirebaseAnalytics.Param.SEARCH_TERM, query)
        }
    }

}