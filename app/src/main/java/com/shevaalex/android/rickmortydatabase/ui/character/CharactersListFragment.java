package com.shevaalex.android.rickmortydatabase.ui.character;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shevaalex.android.rickmortydatabase.R;
import com.shevaalex.android.rickmortydatabase.source.database.Character;
import com.shevaalex.android.rickmortydatabase.databinding.FragmentCharactersListBinding;
import com.shevaalex.android.rickmortydatabase.utils.networking.ConnectionLiveData;

public class CharactersListFragment extends Fragment implements CharacterAdapter.OnCharacterListener {
    private static final String TAG = "CharactersListFragment";
    private static final String SAVE_STATE_SEARCH_QUERY = "Query_name";
    private static final String SAVE_STATE_FILTER_KEY = "Filter_key";
    private static final int KEY_FILTER_APPLIED = 101;
    private static final int KEY_SHOW_ALL = 0;
    private FragmentCharactersListBinding binding;
    private CharacterViewModel characterViewModel;
    private ConnectionLiveData connectionLiveData;
    private CharacterAdapter characterAdapter;
    private Context context;
    private Activity a;
    private static Bundle savedState;
    private String searchQuery;
    private int filterListKey;
    private boolean searchIsCommitted;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof Activity) {
            a = (Activity) context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        characterViewModel = new ViewModelProvider.AndroidViewModelFactory(a.getApplication()).create(CharacterViewModel.class);
        if (savedState == null) {
            characterViewModel.setFilter(KEY_SHOW_ALL);
            characterViewModel.setNameQuery(null);
        }
        characterViewModel.getCharacterList().observe(this, characters -> characterAdapter.submitList(characters)
        );
        connectionLiveData = new ConnectionLiveData(a.getApplication());
        monitorConnection();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCharactersListBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getActivity());
        binding.recyclerviewCharacter.setLayoutManager(linearLayoutManager);
        binding.recyclerviewCharacter.setHasFixedSize(true);
        //instantiate an adapter and set this fragment as a listener for onClick
        characterAdapter = new CharacterAdapter(CharactersListFragment.this, characterViewModel);
        characterAdapter.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);
        binding.recyclerviewCharacter.setAdapter(characterAdapter);
        //set the fast scroller for recyclerview
        binding.fastScroll.setRecyclerView(binding.recyclerviewCharacter);
        setHasOptionsMenu(true);
        return view;
    }

    // Restore saved state in this method
    @Override
    public void onResume() {
        super.onResume();
        if (savedState != null) {
            String savedSearchQuery = savedState.getString(SAVE_STATE_SEARCH_QUERY);
            int savedFilterKey = savedState.getInt(SAVE_STATE_FILTER_KEY);
            if (savedFilterKey == KEY_FILTER_APPLIED) {
                characterViewModel.setFilter(KEY_FILTER_APPLIED);
            } else {
                characterViewModel.setFilter(KEY_SHOW_ALL);
            }
            characterViewModel.setNameQuery(savedSearchQuery);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.app_bar, menu);
        MenuItem filterCheckBox = menu.findItem(R.id.filter_button);
        MenuItem searchMenuItem = menu.findItem(R.id.search_button);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setQueryHint("Enter your query...");
        ImageView closeButton = searchView.findViewById(R.id.search_close_btn);
        characterViewModel.getFilterResultKey().observe(this, integer -> {
            filterListKey = integer;
            if (integer == KEY_FILTER_APPLIED) {
                filterCheckBox.setChecked(true);
            } else {
                filterCheckBox.setChecked(false);
            }
        });
        characterViewModel.getSearchQuery().observe(this, string -> {
            searchQuery = string;
            if (string != null) {
                searchMenuItem.expandActionView();
                searchView.setQuery(searchQuery, false);
                searchView.clearFocus();
                searchIsCommitted = true;
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
                    searchIsCommitted = false;
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
                // set search flag to true to enable X and Back button press to reset list position
                searchIsCommitted = true;
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
                searchIsCommitted = false;
                characterViewModel.setNameQuery(null);
            }
            searchView.clearFocus();
            searchMenuItem.collapseActionView();
            searchView.setIconified(true);
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.filter_button) {
            if (item.isChecked()) {
                characterViewModel.setFilter(KEY_SHOW_ALL);
            } else {
                characterViewModel.setFilter(KEY_FILTER_APPLIED);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //monitors internet connection and checks if database is up to date
    private void monitorConnection() {
        connectionLiveData.observe(this, connectionModel -> new Handler().postDelayed(() -> {
            if (connectionModel.isConnected() && isAdded()) {
                if (characterViewModel.dbIsNotSynced()) {
                    characterViewModel.syncDb();
                    listJumpTo0();
                    Toast.makeText(context, "Updating Database", Toast.LENGTH_SHORT).show();
                }
            } else if (!connectionModel.isConnected() && isAdded()) {
                if (characterViewModel.dbIsNotSynced()) {
                    Toast.makeText(context, getString(R.string.fragment_character_list_no_connection), Toast.LENGTH_SHORT).show();
                }
            }
        }, 3000));
    }

    private void customSaveState() {
        savedState = new Bundle();
        savedState.putString(SAVE_STATE_SEARCH_QUERY, searchQuery);
        savedState.putInt(SAVE_STATE_FILTER_KEY, filterListKey);
    }

    @Override
    public void onPause() {
        super.onPause();
        customSaveState();
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
        if (binding.recyclerviewCharacter.getLayoutManager() != null) {
            Log.d(TAG, "onQueryTextSubmit: scrolling to 0");
            binding.recyclerviewCharacter.getLayoutManager().scrollToPosition(0);
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
                action.setCharacterName(clickedChar.getName()).setImageUrl(clickedChar.getImgUrl())
                .setCharacterStatus(clickedChar.getStatus()).setCharacterSpecies(clickedChar.getSpecies())
                        .setCharacterType(clickedChar.getType()).setCharacterGender(clickedChar.getGender())
                        .setCharacterOrigin(clickedChar.getOriginLocation()).setCharacterLastLocation(clickedChar.getLastKnownLocation())
                        .setId(clickedChar.getId());
                Navigation.findNavController(v).navigate(action);
            }
        }
    }

}
