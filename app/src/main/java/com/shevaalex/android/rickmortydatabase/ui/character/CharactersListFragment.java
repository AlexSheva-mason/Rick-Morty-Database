package com.shevaalex.android.rickmortydatabase.ui.character;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
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

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.shevaalex.android.rickmortydatabase.R;
import com.shevaalex.android.rickmortydatabase.source.database.Character;
import com.shevaalex.android.rickmortydatabase.databinding.FragmentCharactersListBinding;

import java.util.ArrayList;

public class CharactersListFragment extends Fragment implements CharacterAdapter.OnCharacterListener {
    private static final String BUNDLE_SAVE_STATE_SEARCH_QUERY = "Query_name";
    private static final String BUNDLE_SAVE_STATE_FILTER_KEY = "Filter_key";
    private static final int KEY_FILTER_APPLIED = 101;
    private static final int KEY_SHOW_ALL = 0;
    private static boolean splashScreenShown;
    private static ArrayList<String> snackMessages = new ArrayList<>();
    private static Bundle savedState;
    private Activity a;
    private FragmentCharactersListBinding binding;
    private CharacterViewModel characterViewModel;
    private CharacterAdapter characterAdapter;
    private String searchQuery;
    private int filterListKey;
    private boolean searchIsCommitted;
    private RecyclerView rvCharacterList;
    private NavController navController;


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
        binding.progressBar.progressBar.setVisibility(View.GONE);
        //set LinearLayout and RecyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getActivity());
        rvCharacterList = binding.recyclerviewCharacter;
        rvCharacterList.setLayoutManager(linearLayoutManager);
        rvCharacterList.setHasFixedSize(true);
        //instantiate the adapter and set this fragment as a listener for onClick
        characterAdapter = new CharacterAdapter(CharactersListFragment.this, characterViewModel, getContext());
        characterAdapter.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);
        rvCharacterList.setAdapter(characterAdapter);
        characterViewModel.getCharacterList().observe(getViewLifecycleOwner(), characters -> characterAdapter.submitList(characters));
        monitorConnectionAndDatabase();
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
        //Show splash frgament on app start only
        if (!splashScreenShown) {
            navController.navigate(R.id.toSplashFragment);
            splashScreenShown = true;
        }
    }

    private void createOptionsMenu(Toolbar toolbar) {
        toolbar.inflateMenu(R.menu.toolbar_fragment_character_detail);
        MenuItem filterCheckBox = toolbar.getMenu().findItem(R.id.filter_button);
        MenuItem searchMenuItem = toolbar.getMenu().findItem(R.id.search_button);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();
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
                searchMenuItem.expandActionView();
                searchView.clearFocus();
                searchView.setQuery(searchQuery, false);
                searchIsCommitted = true;
            } else {
                searchView.setIconified(true);
                searchIsCommitted = false;
            }
        });
        searchMenuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
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
                listJumpTo0();
                characterViewModel.setNameQuery(query.trim().toLowerCase());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
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
                return NavigationUI.onNavDestinationSelected(item, navController);
            }
            return false;
        });
    }

    //monitors internet connection, checks if database is up to date
    private void monitorConnectionAndDatabase() {
        characterViewModel.getStatusLiveData().observe(getViewLifecycleOwner(), pair -> {
            String text;
            if (pair.first && pair.second) {
                binding.progressBar.progressBar.setVisibility(View.INVISIBLE);
                text = getString(R.string.fragment_character_list_database_up_to_date);
            } else if (!pair.first && pair.second) {
                binding.progressBar.progressBar.setVisibility(View.VISIBLE);
                characterViewModel.rmRepository.initialiseDataBase();
                new Handler().postDelayed(this::listJumpTo0, 1000);
                text = getString(R.string.fragment_character_list_database_sync);
            } else if (pair.first) {
                text = getString(R.string.fragment_character_list_database_up_to_date);
            } else {
                binding.progressBar.progressBar.setVisibility(View.VISIBLE);
                text = getString(R.string.fragment_character_list_no_connection);
            }
            showSnackBar(text);
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
        binding = null;
    }

    private void listJumpTo0() {
        if (rvCharacterList != null && rvCharacterList.getLayoutManager() != null) {
            rvCharacterList.getLayoutManager().scrollToPosition(0);
        }
    }

    @Override
    public void onCharacterClick(int position, @NonNull View v) {
        PagedList<Character> mCharacterList = characterAdapter.getCurrentList();
        if (mCharacterList != null && !mCharacterList.isEmpty()) {
            Character clickedChar = mCharacterList.get(position);
            CharactersListFragmentDirections.ToCharacterDetailFragmentAction action =
                    CharactersListFragmentDirections.toCharacterDetailFragmentAction();
            if (clickedChar != null) {
                action.setCharacterName(clickedChar.getName()).setId(clickedChar.getId());
                Navigation.findNavController(v).navigate(action);
            }
        }
    }

    private void showSnackBar(String text) {
        if (!text.isEmpty() && !snackMessages.contains(text)) {
            Snackbar mySnackbar = Snackbar.make(binding.fragmentCharacterListLayout, text, BaseTransientBottomBar.LENGTH_SHORT);
            mySnackbar.setTextColor(getResources().getColor(R.color.rm_white_50));
            mySnackbar.setAnchorView(a.findViewById(R.id.bottom_panel));
            mySnackbar.show();
            snackMessages.add(text);
        }
    }

}
