package com.shevaalex.android.rickmortydatabase.ui.location.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.google.android.material.card.MaterialCardView
import com.google.android.material.checkbox.MaterialCheckBox
import com.shevaalex.android.rickmortydatabase.R
import com.shevaalex.android.rickmortydatabase.RmApplication
import com.shevaalex.android.rickmortydatabase.databinding.FragmentLocationsListBinding
import com.shevaalex.android.rickmortydatabase.models.location.LocationModel
import com.shevaalex.android.rickmortydatabase.ui.BaseListFragment
import com.shevaalex.android.rickmortydatabase.utils.*
import javax.inject.Inject

class LocationsListFragment : BaseListFragment<FragmentLocationsListBinding>() {

    @Inject
    lateinit var viewModelFactory: DiViewModelFactory<LocationListViewModel>

    override val viewModel: LocationListViewModel by activityViewModels {
        viewModelFactory
    }

    override val keyListFilterMap = Constants.KEY_FRAGMENT_LOCATION_LIST_FILTER_MAP

    override val keyListQuery = Constants.KEY_FRAGMENT_LOCATION_LIST_QUERY

    override val keyListPosition = Constants.KEY_FRAGMENT_LOCATION_LIST_LIST_POSITION

    private var locationAdapter: LocationAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        locationAdapter = LocationAdapter(object: LocationAdapter.LocationListener{
            override fun onLocationClick(location: LocationModel, locationCard: MaterialCardView) {
                navigateLocationDetail(location, locationCard)
            }
        })
        setGridOrLinearRecyclerView(
                binding.recyclerviewLocation,
                locationAdapter
        )
        registerObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        locationAdapter = null
    }

    private fun registerObservers() {
        viewModel.locationList.observe(viewLifecycleOwner, {
            if (it.isEmpty()) {
                viewModel.searchQuery?.let {
                    binding.tvNoResults.visibility = View.VISIBLE
                }
            } else {
                binding.tvNoResults.visibility = View.GONE
            }
            //set data to the adapter
            locationAdapter?.submitList(it)
            //restore list position, or if it has been nulled -> scroll to position 0
            viewModel.rvListPosition.value?.let {state ->
                binding.recyclerviewLocation.layoutManager?.onRestoreInstanceState(state)
            }?: binding.recyclerviewLocation.layoutManager?.scrollToPosition(0)
        })
    }

    override fun injectFragment() {
        activity?.run {
            (application as RmApplication).appComponent
        }?.inject(this)
    }

    override fun setBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
    ): FragmentLocationsListBinding =
            FragmentLocationsListBinding.inflate(inflater, container, false)

    override fun getToolbar() = binding.toolbarFragmentLocationList as Toolbar?

    override fun saveRvListPosition() {
        binding.recyclerviewLocation.layoutManager?.onSaveInstanceState()?.let { lmState ->
            viewModel.setLayoutManagerState(lmState)
        }
    }

    override fun showFilterDialog() {
        activity?.let { activity ->
            val dialog = MaterialDialog(activity)
                    .title(R.string.dialog_title)
                    .negativeButton(text = getString(R.string.dialog_negative_button)) {
                        it.dismiss()
                    }
                    .noAutoDismiss()
                    .customView(
                            viewRes = R.layout.dialog_filter_location,
                            scrollable = true)
            val dialogView = dialog.getCustomView()

            val typeShowAll = dialogView.findViewById<MaterialCheckBox>(R.id.type_all)
            val typeCustom = listOf(
                    dialogView.findViewById(R.id.type_planet),
                    dialogView.findViewById(R.id.type_space_station),
                    dialogView.findViewById<MaterialCheckBox>(R.id.type_microverse)
            )
            val dimensionShowAll = dialogView.findViewById<MaterialCheckBox>(R.id.dimension_all)
            val dimensionCustom = listOf(
                    dialogView.findViewById(R.id.dimension_replacement),
                    dialogView.findViewById(R.id.dimension_c_137),
                    dialogView.findViewById(R.id.dimension_cronenberg),
                    dialogView.findViewById<MaterialCheckBox>(R.id.dimension_unknown)
            )

            restoreCheckBoxState(dialogView)

            typeShowAll.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    typeCustom.forEach {
                        it.isChecked = false
                    }
                }
            }
            dimensionShowAll.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    dimensionCustom.forEach {
                        it.isChecked = false
                    }
                }
            }
            typeCustom.forEach {
                it.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked && typeShowAll.isChecked) {
                        typeShowAll.isChecked = false
                    }
                }
            }
            dimensionCustom.forEach {
                it.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked && dimensionShowAll.isChecked) {
                        dimensionShowAll.isChecked = false
                    }
                }
            }

            dialog.positiveButton(text = getString(R.string.dialog_positive_button)) { mdialog ->
                if ((typeCustom.any { it.isChecked } || typeShowAll.isChecked)
                        && (dimensionCustom.any { it.isChecked } || dimensionShowAll.isChecked)) {
                    setupFiltration(mdialog)
                } else {
                    val errors: MutableList<String> = mutableListOf()
                    if (typeCustom.all { !it.isChecked } && !typeShowAll.isChecked) {
                        errors.add(getString(R.string.dialog_error_type))
                    }
                    if (dimensionCustom.any { !it.isChecked } && !dimensionShowAll.isChecked) {
                        errors.add(getString(R.string.dialog_error_dimension))
                    }
                    activity.displayErrorDialog(
                            getString(R.string.dialog_error_message)
                                    .plus(errors.joinToString())
                    )
                }
            }

            dialog.show {
                lifecycleOwner(viewLifecycleOwner)
            }
        }
    }

    override fun setupFiltration(mdialog: MaterialDialog) {
        val dialogView = mdialog.getCustomView()

        val stringMap = getStringMap()
        val filterMap = mutableMapOf<String, Pair<Boolean, String?>>()

        //according to the state of a checkbox map the appropriate booleans and string values
        filterMap[Constants.KEY_MAP_FILTER_LOC_TYPE_ALL] =
                if (dialogView.findViewById<MaterialCheckBox>(R.id.type_all).isChecked)
                    Pair(true, null)
                else Pair(false, null)
        filterMap[Constants.KEY_MAP_FILTER_LOC_TYPE_PLANET] =
                if (dialogView.findViewById<MaterialCheckBox>(R.id.type_planet).isChecked)
                    Pair(true, stringMap[Constants.KEY_MAP_FILTER_LOC_TYPE_PLANET])
                else Pair(false, null)
        filterMap[Constants.KEY_MAP_FILTER_LOC_TYPE_SPACE_ST] =
                if (dialogView.findViewById<MaterialCheckBox>(R.id.type_space_station).isChecked)
                    Pair(true, stringMap[Constants.KEY_MAP_FILTER_LOC_TYPE_SPACE_ST])
                else Pair(false, null)
        filterMap[Constants.KEY_MAP_FILTER_LOC_TYPE_MICRO] =
                if (dialogView.findViewById<MaterialCheckBox>(R.id.type_microverse).isChecked)
                    Pair(true, stringMap[Constants.KEY_MAP_FILTER_LOC_TYPE_MICRO])
                else Pair(false, null)
        filterMap[Constants.KEY_MAP_FILTER_LOC_DIMENS_ALL] =
                if (dialogView.findViewById<MaterialCheckBox>(R.id.dimension_all).isChecked)
                    Pair(true, null)
                else Pair(false, null)
        filterMap[Constants.KEY_MAP_FILTER_LOC_DIMENS_REPLACE] =
                if (dialogView.findViewById<MaterialCheckBox>(R.id.dimension_replacement).isChecked)
                    Pair(true, stringMap[Constants.KEY_MAP_FILTER_LOC_DIMENS_REPLACE])
                else Pair(false, null)
        filterMap[Constants.KEY_MAP_FILTER_LOC_DIMENS_C_137] =
                if (dialogView.findViewById<MaterialCheckBox>(R.id.dimension_c_137).isChecked)
                    Pair(true, stringMap[Constants.KEY_MAP_FILTER_LOC_DIMENS_C_137])
                else Pair(false, null)
        filterMap[Constants.KEY_MAP_FILTER_LOC_DIMENS_CRONENBERG] =
                if (dialogView.findViewById<MaterialCheckBox>(R.id.dimension_cronenberg).isChecked)
                    Pair(true, stringMap[Constants.KEY_MAP_FILTER_LOC_DIMENS_CRONENBERG])
                else Pair(false, null)
        filterMap[Constants.KEY_MAP_FILTER_LOC_DIMENS_UNKNOWN] =
                if (dialogView.findViewById<MaterialCheckBox>(R.id.dimension_unknown).isChecked)
                    Pair(true, stringMap[Constants.KEY_MAP_FILTER_LOC_DIMENS_UNKNOWN])
                else Pair(false, null)

        viewModel.setFilterFlags(filterMap.toMap())

        mdialog.dismiss()
    }

    override fun restoreCheckBoxState(dialogView: View) {
        viewModel.getFilterMap()?.let {
            dialogView.findViewById<MaterialCheckBox>(R.id.type_all).isChecked =
                    it[Constants.KEY_MAP_FILTER_LOC_TYPE_ALL]?.first?: false
            dialogView.findViewById<MaterialCheckBox>(R.id.type_planet).isChecked =
                    it[Constants.KEY_MAP_FILTER_LOC_TYPE_PLANET]?.first?: false
            dialogView.findViewById<MaterialCheckBox>(R.id.type_space_station).isChecked =
                    it[Constants.KEY_MAP_FILTER_LOC_TYPE_SPACE_ST]?.first?: false
            dialogView.findViewById<MaterialCheckBox>(R.id.type_microverse).isChecked =
                    it[Constants.KEY_MAP_FILTER_LOC_TYPE_MICRO]?.first?: false
            dialogView.findViewById<MaterialCheckBox>(R.id.dimension_all).isChecked =
                    it[Constants.KEY_MAP_FILTER_LOC_DIMENS_ALL]?.first?: false
            dialogView.findViewById<MaterialCheckBox>(R.id.dimension_replacement).isChecked =
                    it[Constants.KEY_MAP_FILTER_LOC_DIMENS_REPLACE]?.first?: false
            dialogView.findViewById<MaterialCheckBox>(R.id.dimension_c_137).isChecked =
                    it[Constants.KEY_MAP_FILTER_LOC_DIMENS_C_137]?.first?: false
            dialogView.findViewById<MaterialCheckBox>(R.id.dimension_cronenberg).isChecked =
                    it[Constants.KEY_MAP_FILTER_LOC_DIMENS_CRONENBERG]?.first?: false
            dialogView.findViewById<MaterialCheckBox>(R.id.dimension_unknown).isChecked =
                    it[Constants.KEY_MAP_FILTER_LOC_DIMENS_UNKNOWN]?.first?: false
        }
    }

    override fun getStringMap() = mapOf(
            Constants.KEY_MAP_FILTER_LOC_TYPE_PLANET to getString(R.string.location_Planet),
            Constants.KEY_MAP_FILTER_LOC_TYPE_SPACE_ST to getString(R.string.location_Space_station),
            Constants.KEY_MAP_FILTER_LOC_TYPE_MICRO to getString(R.string.location_Microverse),
            Constants.KEY_MAP_FILTER_LOC_DIMENS_REPLACE to getString(R.string.location_Replacement_Dimension),
            Constants.KEY_MAP_FILTER_LOC_DIMENS_C_137 to getString(R.string.location_Dimension_C_137),
            Constants.KEY_MAP_FILTER_LOC_DIMENS_CRONENBERG to getString(R.string.location_Cronenberg_Dimension),
            Constants.KEY_MAP_FILTER_LOC_DIMENS_UNKNOWN to getString(R.string.character_gender_unknown)
    )

    private fun navigateLocationDetail(location: LocationModel, locationCard: MaterialCardView) {
        setExitAndReenterAnimation()
        val extras = FragmentNavigatorExtras(
                locationCard to Constants.TRANSITION_LOCATION.plus(location.id)
        )
        val action = LocationsListFragmentDirections.toLocationDetailFragmentAction(location)
        findNavController().navigate(action, extras)
    }

}