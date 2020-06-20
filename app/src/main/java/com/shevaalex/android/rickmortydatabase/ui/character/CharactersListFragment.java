package com.shevaalex.android.rickmortydatabase.ui.character;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shevaalex.android.rickmortydatabase.R;
import com.shevaalex.android.rickmortydatabase.databinding.FragmentCharactersListBinding;
import com.shevaalex.android.rickmortydatabase.source.database.CharacterSmall;


public class CharactersListFragment extends Fragment implements CharacterAdapter.OnCharacterListener {
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
        characterViewModel = new ViewModelProvider.AndroidViewModelFactory(a.getApplication()).create(CharacterViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (savedState == null) {
            characterViewModel.setFilter(KEY_SHOW_ALL);
            characterViewModel.setNameQuery(null);
        }
        binding = FragmentCharactersListBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        //set layout manager and RecyclerView
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getActivity(), RecyclerView.HORIZONTAL, false);
            binding.recyclerviewCharacter.setLayoutManager(linearLayoutManager);
        } else {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getActivity(), RecyclerView.VERTICAL, false);
            binding.recyclerviewCharacter.setLayoutManager(linearLayoutManager);
        }
        binding.recyclerviewCharacter.setHasFixedSize(true);
        //instantiate the adapter and set this fragment as a listener for onClick
        characterAdapter = new CharacterAdapter(CharactersListFragment.this, characterViewModel, getContext());
        characterAdapter.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);
        binding.recyclerviewCharacter.setAdapter(characterAdapter);
        characterViewModel.getCharacterList().observe(getViewLifecycleOwner(), characters -> {
            characterAdapter.submitList(characters);
            if (characters.isEmpty() && searchQuery != null) {
                binding.tvNoResults.setVisibility(View.VISIBLE);
            } else {
                binding.tvNoResults.setVisibility(View.GONE);
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        navController = Navigation.findNavController(view);
        //Set the action bar to show appropriate title, set top level destinations
        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(R.id.charactersListFragment, R.id.locationsListFragment, R.id.episodesListFragment).build();
        Toolbar toolbar = binding.toolbarFragmentCharacterList;
        createOptionsMenu(toolbar);
        NavigationUI.setupWithNavController(
                toolbar, navController, appBarConfiguration);
    }

    private void createOptionsMenu(Toolbar toolbar) {
        toolbar.inflateMenu(R.menu.toolbar_fragment_character_list);
        filterCheckBox = toolbar.getMenu().findItem(R.id.filter_button);
        searchMenuItem = toolbar.getMenu().findItem(R.id.search_button);
        searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setQueryHint("Enter your query...");
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
                if (!searchMenuItem.isActionViewExpanded()) {
                    searchMenuItem.expandActionView();
                }
                searchView.setFocusable(false);
                searchView.clearFocus();
                searchIsCommitted = true;
            } else {
                searchView.setIconified(true);
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
                return true;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
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
            searchView.clearFocus();
            searchMenuItem.collapseActionView();
            searchView.setIconified(true);
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
                navController.navigate(CharactersListFragmentDirections.toSettingsFragment());
                return true;
            }
            return false;
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (savedState != null) {
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
    }

    @Override
    public void onPause() {
        super.onPause();
        customSaveState();
    }

    private void customSaveState() {
        savedState = new Bundle();
        savedState.putString(BUNDLE_SAVE_STATE_SEARCH_QUERY, searchQuery);
        savedState.putInt(BUNDLE_SAVE_STATE_FILTER_KEY, filterListKey);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (characterAdapter != null) {
            characterAdapter = null;
        }
        if (binding != null) {
            binding = null;
        }
        if (filterCheckBox != null) {
            filterCheckBox = null;
        }
        if (searchMenuItem != null) {
            searchMenuItem = null;
        }
        if (searchView != null) {
            searchView = null;
        }
    }

    private void listJumpTo0() {
        if (binding.recyclerviewCharacter.getLayoutManager() != null) {
            binding.recyclerviewCharacter.getLayoutManager().scrollToPosition(0);
        }
    }

    @Override
    public void onCharacterClick(int position, @NonNull View v) {
        searchMenuItem.collapseActionView();
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
