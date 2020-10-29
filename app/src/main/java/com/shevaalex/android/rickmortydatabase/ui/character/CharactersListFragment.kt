package com.shevaalex.android.rickmortydatabase.ui.character

import com.shevaalex.android.rickmortydatabase.ui.BaseFragment
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.ktx.Firebase
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
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.firebase.analytics.ktx.analytics
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
    lateinit var viewModelFactory: MyViewModelFactory<CharacterListViewModelKotlin>

    private var _binding: FragmentCharactersListBinding? = null

    private val binding get() = _binding!!

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private val characterListViewModel: CharacterListViewModelKotlin by viewModels {
        viewModelFactory
    }

    private var characterAdapter: CharacterAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAnalytics = Firebase.analytics
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        //inject fragment
        activity?.run {
            (application as RmApplication).appComponent
        }?.inject(this)
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
        view?.requestFocus()
        view?.hideKeyboard()
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
        characterAdapter = CharacterAdapter(requireActivity(), this@CharactersListFragment)
        //TODO change rv restoration policy
        characterAdapter?.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT
    }

    private fun registerObservers() {
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
            binding.recyclerviewCharacter.layoutManager
                    ?.onRestoreInstanceState(characterListViewModel.rvListPosition.value)
        })
    }

    private fun inflateToolbar() {
        val toolbar = binding.toolbarFragmentCharacterList
        toolbar.inflateMenu(R.menu.menu_filter)
        setupSearchView()
        setupMenuButtons()
    }

    private fun setupSearchView () {
        val toolbar: Toolbar? = binding.toolbarFragmentCharacterList
        toolbar?.let {
            val searchView: SearchView? = it.search_view
            val searchPlate: SearchView.SearchAutoComplete? =
                    searchView?.findViewById(androidx.appcompat.R.id.search_src_text)
            searchPlate?.setTextAppearance(R.style.TextAppearance_RM_SearchView_Hint)
            //set the suggestions list
            lifecycleScope.launch {
                val suggestions: Array<String> = characterListViewModel.getSuggestionsNames().toTypedArray()
                ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, suggestions).also { adapter ->
                    searchPlate?.setAdapter(adapter)
                }
            }
            searchView?.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let{
                        if (query != characterListViewModel.searchQuery) {
                            characterListViewModel.setNameQuery(
                                    query.trim().toLowerCase(Locale.getDefault())
                            )
                        }
                    }
                    searchPlate?.clearFocus()
                    return false
                }
                override fun onQueryTextChange(newText: String?): Boolean {
                    newText?.let {query ->
                        if (query.isEmpty()) {
                            characterListViewModel.setNameQuery(
                                    query.trim().toLowerCase(Locale.getDefault())
                            )
                            listJumpTo0()
                        }
                    }
                    return false
                }
            })
            //if searchPlate has lost focus -> user submitted the search -> log it to Firebase
            searchPlate?.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    characterListViewModel.searchQuery?.let { text ->
                        if (text.isNotBlank()) {
                            // if true - this query has never been logged
                            if(characterListViewModel.addLogQuery(text)) {
                                firebaseLogQuery(text)
                            }
                        }
                    }
                }
            }
            searchPlate?.setOnItemClickListener { parent, _, position, _ ->
                val newQuery: String? = parent?.getItemAtPosition(position).toString()
                newQuery?.let {
                    searchView.setQuery(newQuery, true)
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

    //TODO redo
    private fun customSaveState() {
        binding.recyclerviewCharacter.layoutManager?.onSaveInstanceState()?.let { lmState ->
            characterListViewModel.setLayoutManagerState(lmState)
        }
    }

    private fun listJumpTo0() {
        Timber.w("listJumpTo0")
        binding.recyclerviewCharacter.layoutManager?.scrollToPosition(0)
    }

    /**
     * restores view state of SearchView and hides softInput keyboard
     */
    private fun restoreSearchViewState() {
        characterListViewModel.searchQuery?.let {
            if (it != "") {
                Timber.v("restoring query in searchview")
                val searchView = binding.toolbarFragmentCharacterList.search_view
                searchView?.isIconified = false
                searchView?.setQuery(characterListViewModel.searchQuery, false)
                searchView?.clearFocus()
                view?.requestFocus()
                view?.hideKeyboard()
            }
        }
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