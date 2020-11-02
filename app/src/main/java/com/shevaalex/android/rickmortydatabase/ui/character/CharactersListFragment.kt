package com.shevaalex.android.rickmortydatabase.ui.character

import android.annotation.SuppressLint
import android.content.Context
import com.shevaalex.android.rickmortydatabase.ui.BaseFragment
import com.google.firebase.analytics.FirebaseAnalytics
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.ui.AppBarConfiguration
import com.shevaalex.android.rickmortydatabase.R
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.*
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.firebase.analytics.ktx.logEvent
import com.shevaalex.android.rickmortydatabase.RmApplication
import com.shevaalex.android.rickmortydatabase.databinding.FragmentCharactersListBinding
import com.shevaalex.android.rickmortydatabase.utils.MyViewModelFactory
import com.shevaalex.android.rickmortydatabase.utils.hideKeyboard
import kotlinx.android.synthetic.main.fragment_characters_list.view.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class CharactersListFragment : BaseFragment(), CharacterAdapter.OnCharacterListener {

    @Inject
    lateinit var viewModelFactory: MyViewModelFactory<CharacterListViewModel>

    private var _binding: FragmentCharactersListBinding? = null

    private val binding get() = _binding!!

    private val characterListViewModel: CharacterListViewModel by activityViewModels() {
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
            characterListViewModel.rvListPosition?.let {
                binding.recyclerviewCharacter.layoutManager
                        ?.onRestoreInstanceState(it)
            }
        })
        //set searchView new query suggestions adapter
        characterListViewModel.suggestions.observe(viewLifecycleOwner, {suggestionList ->
            suggestionList?.let {
                searchSuggestionsAdapter = ArrayAdapter(
                        requireContext(),
                        R.layout.item_search_suggestions,
                        it)
            }
        })
        //set searchView recent suggestions adapter
        characterListViewModel.recentQueries.observe(viewLifecycleOwner, {recentQueries ->
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
    private fun setupSearchView () {
        val toolbar: Toolbar? = binding.toolbarFragmentCharacterList
        toolbar?.let {
            val searchView: SearchView? = it.search_view
            val searchPlate: SearchView.SearchAutoComplete? =
                    searchView?.findViewById(androidx.appcompat.R.id.search_src_text)
            searchPlate?.setTextAppearance(R.style.TextAppearance_RM_SearchView_Hint)
            searchPlate?.threshold = 0
            searchView?.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let{
                        val queryText = query.trim().toLowerCase(Locale.getDefault())
                        if (queryText != characterListViewModel.searchQuery) {
                            // if true - this query has never been logged or saved to db table
                            if(characterListViewModel.addLogQuery(queryText)) {
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
                    newText?.let {query ->
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
            searchView?.setOnSuggestionListener(object: SearchView.OnSuggestionListener {
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
        activity?.let {
            val dialog = MaterialDialog(it)
                    .title(R.string.dialog_title)
                    .positiveButton(text = "Agree")
                    .negativeButton(text = "Disagree")
                    .noAutoDismiss()
                    .customView(
                            viewRes = R.layout.dialog_filter,
                            scrollable = true)
            val view = dialog.getCustomView()

            view.findViewById<MaterialCheckBox>(R.id.status_alive)
                    .setOnCheckedChangeListener { _, isChecked ->
                        when (isChecked) {
                            true -> Timber.i("Checked")
                            false -> Timber.i("Unchecked")
                        }
                    }
            view.findViewById<MaterialCheckBox>(R.id.status_dead)
                    .setOnCheckedChangeListener { _, isChecked ->
                        when (isChecked) {
                            true -> Timber.i("Checked")
                            false -> Timber.i("Unchecked")
                        }
                    }
            dialog.show {
                lifecycleOwner(viewLifecycleOwner)
            }
        }
    }

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