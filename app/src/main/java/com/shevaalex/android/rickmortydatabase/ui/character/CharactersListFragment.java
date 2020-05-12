package com.shevaalex.android.rickmortydatabase.ui.character;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
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
        connectionLiveData = new ConnectionLiveData(a.getApplication());
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
        binding.progressBar.setVisibility(View.GONE);
        //set LinearLayout and RecyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getActivity());
        binding.recyclerviewCharacter.setLayoutManager(linearLayoutManager);
        binding.recyclerviewCharacter.setHasFixedSize(true);
        //instantiate the adapter and set this fragment as a listener for onClick
        characterAdapter = new CharacterAdapter(CharactersListFragment.this, characterViewModel);
        characterAdapter.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);
        binding.recyclerviewCharacter.setAdapter(characterAdapter);
        characterViewModel.getCharacterList().observe(getViewLifecycleOwner(), characters -> characterAdapter.submitList(characters)
        );
        setHasOptionsMenu(true);
        monitorConnection();
        //TODO fix progressbar visibility
        //observe if Volley requests are still running and update visual state of progressBar accordingly
        characterViewModel.getProgressBarVisibility().observe(getViewLifecycleOwner(), integer -> binding.progressBar.setVisibility(integer));
        return view;
    }

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
            if (searchQuery == null && savedSearchQuery != null) {
                characterViewModel.setNameQuery(savedSearchQuery);
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

    //monitors internet connection, checks if database is up to date
    private void monitorConnection() {
        connectionLiveData.observe(getViewLifecycleOwner(), connectionModel -> {
            if (connectionModel.isConnected() && isAdded()) {
                //observe datbase sync status
                characterViewModel.getDbIsSynced().observe(getViewLifecycleOwner(), dbSynced -> {
                    if (dbSynced) {
                        Toast.makeText(context, "Database synced!", Toast.LENGTH_SHORT).show();
                    } else {
                        characterViewModel.rmRepository.initialiseDataBase();
                        listJumpTo0();
                        Toast.makeText(context, "Updating Database", Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (!connectionModel.isConnected() && isAdded()) {
                //observe datbase sync status
                characterViewModel.getDbIsSynced().observe(getViewLifecycleOwner(), dbSynced -> {
                    if (dbSynced) {
                        Toast.makeText(context, "Database up to date!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, getString(R.string.fragment_character_list_no_connection), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }



    @Override
    public void onPause() {
        super.onPause();
        customSaveState();
    }

    private void customSaveState() {
        savedState = new Bundle();
        savedState.putString(SAVE_STATE_SEARCH_QUERY, searchQuery);
        savedState.putInt(SAVE_STATE_FILTER_KEY, filterListKey);
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
            Log.d(TAG, "list scrolling to 0");
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
                action.setCharacterName(clickedChar.getName()).setId(clickedChar.getId());
                Navigation.findNavController(v).navigate(action);
            }
        }
    }

}
