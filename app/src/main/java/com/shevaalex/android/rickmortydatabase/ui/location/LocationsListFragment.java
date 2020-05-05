package com.shevaalex.android.rickmortydatabase.ui.location;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shevaalex.android.rickmortydatabase.databinding.FragmentLocationsListBinding;
import com.shevaalex.android.rickmortydatabase.source.database.Location;

@SuppressWarnings("WeakerAccess")
public class LocationsListFragment extends Fragment implements LocationAdapter.OnLocationClickListener {
    private static final String TAG = "LocationsListFragment";
    private static final String SAVE_STATE_LIST = "List_state";
    private Activity a;
    private FragmentLocationsListBinding binding;
    private LocationViewModel locationViewModel;
    private LocationAdapter locationAdapter;
    private static Bundle savedState;

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
        locationViewModel = new ViewModelProvider.AndroidViewModelFactory(a.getApplication()).create(LocationViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLocationsListBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getActivity());
        binding.recyclerviewLocation.setLayoutManager(linearLayoutManager);
        binding.recyclerviewLocation.setHasFixedSize(true);
        //instantiate an adapter and set this fragment as a listener for onClick
        locationAdapter = new LocationAdapter(LocationsListFragment.this);
        binding.recyclerviewLocation.setAdapter(locationAdapter);
        if (binding.recyclerviewLocation.getAdapter() != null) {
            binding.recyclerviewLocation.getAdapter().setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);
        }
        //set the fast scroller for recyclerview
        binding.fastScroll.setRecyclerView(binding.recyclerviewLocation);
        //moved from onCreate to prevent adapter list from being null
        locationViewModel.getLocationList().observe(getViewLifecycleOwner(), locations -> locationAdapter.submitList(locations));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (savedState != null) {
            Parcelable listState = savedState.getParcelable(SAVE_STATE_LIST);
            if (binding.recyclerviewLocation.getLayoutManager() != null) {
                new Handler().postDelayed(() ->
                binding.recyclerviewLocation.getLayoutManager().onRestoreInstanceState(listState), 50);
            }
        }
    }

    @Override
    public void onLocationClick(int position, View v) {
        PagedList<Location> locationList = locationAdapter.getCurrentList();
        if (locationList != null && !locationList.isEmpty()) {
            Location clickedLocation = locationList.get(position);
            LocationsListFragmentDirections.ToLocationDetailFragmentAction action =
                    LocationsListFragmentDirections.toLocationDetailFragmentAction();
            if (clickedLocation != null) {
                action.setLocationName(clickedLocation.getName()).setLocationDimension(clickedLocation.getDimension())
                        .setLocationType(clickedLocation.getType()).setLocationResidents(clickedLocation.getResidentsList())
                        .setLocationId(clickedLocation.getId());
                Navigation.findNavController(v).navigate(action);
            }
        }
    }

    private void customSaveState() {
        savedState = new Bundle();
        if (binding.recyclerviewLocation.getLayoutManager() != null) {
            savedState.putParcelable(SAVE_STATE_LIST, binding.recyclerviewLocation.getLayoutManager().onSaveInstanceState());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        customSaveState();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (locationAdapter != null) {
            locationAdapter = null;
        }
        binding = null;
    }
}
