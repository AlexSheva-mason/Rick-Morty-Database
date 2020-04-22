package com.shevaalex.android.rickmortydatabase.ui.character;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
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

import com.futuremind.recyclerviewfastscroll.FastScroller;
import com.shevaalex.android.rickmortydatabase.R;
import com.shevaalex.android.rickmortydatabase.CharacterViewModel;
import com.shevaalex.android.rickmortydatabase.database.Character;
import com.shevaalex.android.rickmortydatabase.networking.ConnectionLiveData;

import java.util.Objects;

public class CharactersListFragment extends Fragment implements CharacterAdapter.OnCharacterListener {

    private static final String TAG = "CharactersListFragment";
    private static final String SAVE_STATE_LIST = "List_state";
    private static final String SAVE_STATE_SEARCH_QUERY = "Query_name";
    private static final String SAVE_STATE_FILTER_KEY = "Filter_key";
    private static final int KEY_FILTER_APPLIED = 101;
    private static final int KEY_SHOW_ALL = 0;
    private CharacterViewModel characterViewModel;
    private CharacterAdapter characterAdapter;
    private Context context;
    private Activity a;
    private RecyclerView recyclerView;
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
        monitorConnection();
        characterViewModel = new ViewModelProvider.AndroidViewModelFactory(a.getApplication()).create(CharacterViewModel.class);
        if (savedState == null) {
            characterViewModel.setFilter(KEY_SHOW_ALL);
            characterViewModel.setNameQuery(null);
        }
        characterViewModel.getCharacterList().observe(this, characters -> characterAdapter.submitList(characters));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_characters_list, container, false);
        setHasOptionsMenu(true);
        recyclerView = view.findViewById(R.id.recyclerview_character);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        //instantiate an adapter and set this fragment as a listener for onClick
        characterAdapter = new CharacterAdapter(CharactersListFragment.this);
        recyclerView.setAdapter(characterAdapter);
        //set the fast scroller for recyclerview
        FastScroller fastScroller = view.findViewById(R.id.fast_scroll);
        fastScroller.setRecyclerView(recyclerView);
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
            Parcelable listState = savedState.getParcelable(SAVE_STATE_LIST);
            if (recyclerView.getLayoutManager() != null) {
                new Handler().postDelayed(() -> {
                    Log.d(TAG, "onResume: list position restored");
                    recyclerView.getLayoutManager().onRestoreInstanceState(listState);
                }, 50);
            }
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
        ConnectionLiveData connectionLiveData = new ConnectionLiveData(Objects.requireNonNull(getActivity()).getApplication());
        connectionLiveData.observe(this, connectionModel -> new Handler().postDelayed(() -> {
            if (connectionModel.isConnected() && isAdded()) {
                if (characterViewModel.dbIsNotSynced()) {
                    characterViewModel.syncDb();
                    Toast.makeText(context, "Updating Database", Toast.LENGTH_SHORT).show();
                }
            } else if (!connectionModel.isConnected() && isAdded()) {
                if (characterViewModel.dbIsNotSynced()) {
                    Toast.makeText(context, "Connect to internet to check for database update", Toast.LENGTH_SHORT).show();
                }
            }
        }, 3000));
    }

    private void customSaveState() {
        savedState = new Bundle();
        savedState.putString(SAVE_STATE_SEARCH_QUERY, searchQuery);
        savedState.putInt(SAVE_STATE_FILTER_KEY, filterListKey);
        if (recyclerView != null && recyclerView.getLayoutManager() != null) {
            savedState.putParcelable(SAVE_STATE_LIST, recyclerView.getLayoutManager().onSaveInstanceState());
        }
        Log.d(TAG, "saveState: has been saved: " + savedState);
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
    }

    private void listJumpTo0() {
        if (recyclerView.getLayoutManager() != null) {
            Log.d(TAG, "onQueryTextSubmit: scrolling to 0");
            recyclerView.getLayoutManager().scrollToPosition(0);
        }
    }

    @Override
    public void onCharacterClick(int position, @NonNull View v) {
        PagedList<Character> mCharacterList = characterAdapter.getCurrentList();
        if (mCharacterList != null && !mCharacterList.isEmpty()) {
            Character clickedChar = mCharacterList.get(position);
            CharactersListFragmentDirections.CharacterDetailAction action =
                    CharactersListFragmentDirections.characterDetailAction();
            if (clickedChar != null) {
                action.setCharacterName(clickedChar.getName()).setImageUrl(clickedChar.getImgUrl());
                Navigation.findNavController(v).navigate(action);
            }
        }
    }

}
