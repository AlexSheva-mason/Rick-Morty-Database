package com.shevaalex.android.rickmortydatabase.ui.location;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shevaalex.android.rickmortydatabase.databinding.FragmentLocationsListBinding;
import com.shevaalex.android.rickmortydatabase.source.database.Location;
import com.shevaalex.android.rickmortydatabase.ui.FragmentToolbarSimple;


public class LocationsListFragment extends FragmentToolbarSimple implements LocationAdapter.OnLocationClickListener {
    private Activity a;
    private FragmentLocationsListBinding binding;
    private LocationViewModel locationViewModel;
    private LocationAdapter locationAdapter;

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
        locationAdapter = new LocationAdapter(this);
        locationAdapter.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);
        binding.recyclerviewLocation.setAdapter(locationAdapter);
        locationViewModel.getLocationList().observe(getViewLifecycleOwner(), locations -> locationAdapter.submitList(locations));
        return view;
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (locationAdapter != null) {
            locationAdapter = null;
        }
        binding = null;
    }
}
