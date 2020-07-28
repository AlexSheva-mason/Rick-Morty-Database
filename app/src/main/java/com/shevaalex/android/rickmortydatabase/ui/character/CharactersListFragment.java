package com.shevaalex.android.rickmortydatabase.ui.character;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.shevaalex.android.rickmortydatabase.R;
import com.shevaalex.android.rickmortydatabase.databinding.FragmentCharactersListBinding;
import com.shevaalex.android.rickmortydatabase.source.database.CharacterSmall;
import com.shevaalex.android.rickmortydatabase.utils.StringParsing;

import java.util.ArrayList;


public class CharactersListFragment extends Fragment implements CharacterAdapter.OnCharacterListener {
    private static final String BUNDLE_SAVE_STATE_LIST = "List_state";
    private static final String BUNDLE_SAVE_STATE_SEARCH_QUERY = "Query_name";
    private static final String BUNDLE_SAVE_STATE_FILTER_KEY = "Filter_key";
    private static final int KEY_FILTER_APPLIED = 101;
    private static final int KEY_SHOW_ALL = 0;
    private static Bundle savedState;
    private Activity a;
    private FragmentCharactersListBinding binding;
    private CharacterViewModel characterViewModel;
    private CharacterAdapter characterAdapter;
    private String searchQuery;
    private int filterListKey;
    private boolean searchIsCommitted;
    private NavController navController;
    private MenuItem filterCheckBox;
    private MenuItem searchMenuItem;
    private SearchView searchView;
    private Toolbar toolbar;
    private static ArrayList<String> searchQueries = new ArrayList<>();
    private FirebaseAnalytics mFirebaseAnalytics;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            a = (Activity) context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        characterViewModel = new ViewModelProvider
                .AndroidViewModelFactory(a.getApplication())
                .create(CharacterViewModel.class);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(a);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (savedState == null) {
            characterViewModel.setFilter(KEY_SHOW_ALL);
            characterViewModel.setNameQuery(null);
        } else {
            restoreState();
        }
        binding = FragmentCharactersListBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        setRecyclerView();
        registerObservers();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        navController = Navigation.findNavController(view);
        //Set the action bar to show appropriate title, set top level destinations
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.charactersListFragment,
                R.id.locationsListFragment,
                R.id.episodesListFragment)
                .build();
        toolbar = binding.toolbarFragmentCharacterList;
        createOptionsMenu();
        NavigationUI.setupWithNavController(
                toolbar, navController, appBarConfiguration);
    }

    @Override
    public void onResume() {
        super.onResume();
        mFirebaseAnalytics.setCurrentScreen(
                requireActivity(),
                this.getClass().getSimpleName(),
                this.getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        hideKeyboard();
        customSaveState();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        filterCheckBox = null;
        searchMenuItem = null;
        searchView = null;
        toolbar = null;
        characterAdapter = null;
        binding = null;
    }

    private void setRecyclerView() {
        binding.recyclerviewCharacter.setHasFixedSize(true);
        FirebaseCrashlytics.getInstance().log("CLF: setRecyclerView()");
        //instantiate the adapter and set this fragment as a listener for onClick
        characterAdapter = new CharacterAdapter(
                CharactersListFragment.this,
                characterViewModel,
                a.getApplicationContext());
        characterAdapter
                .setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT);
    }

    private void registerObservers() {
        characterViewModel.getCharacterList().observe(getViewLifecycleOwner(), characters -> {
            //re-arrange search query if contains 2 words and didn't bring any results
            if (characters.isEmpty() && searchQuery != null
                    && searchQuery.contains(" ")
                    && !searchQueries.contains(searchQuery)) {
                searchQueries.add(searchQuery);
                characterViewModel.setNameQuery(StringParsing.rearrangeSearchQuery(searchQuery));
            }
            if (characters.isEmpty() && searchQuery != null) {
                binding.tvNoResults.setVisibility(View.VISIBLE);
            } else {
                searchQueries.clear();
                binding.tvNoResults.setVisibility(View.GONE);
            }
            //set data to the adapter
            characterAdapter.submitList(characters);
            //set adapter to the recyclerview
            binding.recyclerviewCharacter.setAdapter(characterAdapter);
            FirebaseCrashlytics.getInstance().log("CLF: registerObservers()");
            //restore list position
            if (savedState != null) {
                Parcelable listState = savedState.getParcelable(BUNDLE_SAVE_STATE_LIST);
                new Handler().postDelayed(() -> {
                    FirebaseCrashlytics.getInstance().log("CLF: Handler().postDelayed");
                    if (binding.recyclerviewCharacter.getLayoutManager() != null) {
                        binding.recyclerviewCharacter
                                .getLayoutManager()
                                .onRestoreInstanceState(listState);
                        FirebaseCrashlytics.getInstance().log("CLF: RV.getLayoutManager()");
                        savedState = new Bundle();
                    }
                }, 100);
            }
        });
    }

    private void createOptionsMenu() {
        toolbar.inflateMenu(R.menu.toolbar_fragment_character_list);
        filterCheckBox = toolbar.getMenu().findItem(R.id.filter_button);
        searchMenuItem = toolbar.getMenu().findItem(R.id.search_button);
        searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setQueryHint(a.getResources().getString(R.string.clf_searchview_query_hint));
        AutoCompleteTextView searchText = searchView.findViewById(R.id.search_src_text);
        searchText.setTextAppearance(getContext(), R.style.TextAppearance_RM_SearchView_Hint);
        ImageView closeButton = searchView.findViewById(R.id.search_close_btn);
        characterViewModel.getFilterResultKey().observe(getViewLifecycleOwner(), integer -> {
            filterListKey = integer;
            if (integer == KEY_FILTER_APPLIED) {
                filterCheckBox.setChecked(true);
            } else {
                filterCheckBox.setChecked(false);
            }
        });
        characterViewModel.getSearchQuery().observe(getViewLifecycleOwner(), string -> {
            searchQuery = string;
            if (string != null) {
                hideKeyboard();
                searchIsCommitted = true;
            } else {
                searchIsCommitted = false;
            }
        });
        searchMenuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                if (searchQuery != null) {
                    searchView.post(() -> searchView.setQuery(searchQuery, false));
                }
                return true;
            }
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                hideKeyboard ();
                if (searchIsCommitted) {
                    listJumpTo0();
                    characterViewModel.setNameQuery(null);
                }
                return true;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //log search event with firebase
                Bundle searchBundle = new Bundle();
                searchBundle.putString(FirebaseAnalytics.Param.SEARCH_TERM, query);
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SEARCH, searchBundle);
                //process the query
                listJumpTo0();
                characterViewModel.setNameQuery(query.trim().toLowerCase());
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
        closeButton.setOnClickListener(v -> {
            if (searchIsCommitted) {
                listJumpTo0();
                characterViewModel.setNameQuery(null);
            }
            searchMenuItem.collapseActionView();
        });
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.filter_button) {
                if (item.isChecked()) {
                    characterViewModel.setFilter(KEY_SHOW_ALL);
                } else {
                    characterViewModel.setFilter(KEY_FILTER_APPLIED);
                }
                return true;
            }
            if (item.getItemId() == R.id.settingsFragment) {
                searchIsCommitted = false;
                searchMenuItem.collapseActionView();
                navController.navigate(CharactersListFragmentDirections.toSettingsFragment());
                return true;
            }
            return false;
        });
    }

    private void restoreState() {
        String savedSearchQuery = savedState.getString(BUNDLE_SAVE_STATE_SEARCH_QUERY);
        int savedFilterKey = savedState.getInt(BUNDLE_SAVE_STATE_FILTER_KEY);
        if (savedFilterKey == KEY_FILTER_APPLIED) {
            characterViewModel.setFilter(KEY_FILTER_APPLIED);
        } else {
            characterViewModel.setFilter(KEY_SHOW_ALL);
        }
        if (searchQuery == null && savedSearchQuery != null) {
            characterViewModel.setNameQuery(savedSearchQuery);
        }
    }

    private void customSaveState() {
        savedState = new Bundle();
        savedState.putString(BUNDLE_SAVE_STATE_SEARCH_QUERY, searchQuery);
        savedState.putInt(BUNDLE_SAVE_STATE_FILTER_KEY, filterListKey);
        if (binding.recyclerviewCharacter.getLayoutManager() != null) {
            savedState.putParcelable(
                    BUNDLE_SAVE_STATE_LIST,
                    binding.recyclerviewCharacter.getLayoutManager().onSaveInstanceState());
        }
    }

    private void hideKeyboard () {
        InputMethodManager inputMethodManager =
                (InputMethodManager) a.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null && searchView != null) {
            inputMethodManager.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
            searchView.clearFocus();
        }
    }

    private void listJumpTo0() {
        new Handler().postDelayed(() -> {
            if (binding.recyclerviewCharacter.getLayoutManager() != null) {
                binding.recyclerviewCharacter.getLayoutManager().scrollToPosition(0);
            }
        },300);
    }

    @Override
    public void onCharacterClick(int position, @NonNull View v) {
        hideKeyboard();
        PagedList<CharacterSmall> mCharacterList = characterAdapter.getCurrentList();
        if (mCharacterList != null && !mCharacterList.isEmpty()) {
            CharacterSmall clickedChar = mCharacterList.get(position);
            CharactersListFragmentDirections.ToCharacterDetailFragmentAction action =
                    CharactersListFragmentDirections.toCharacterDetailFragmentAction();
            if (clickedChar != null) {
                action.setCharacterName(clickedChar.getName()).setId(clickedChar.getId());
                Navigation.findNavController(v).navigate(action);
            }
        }
    }
}
