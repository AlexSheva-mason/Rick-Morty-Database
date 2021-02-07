package com.shevaalex.android.rickmortydatabase.ui.episode.list

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
import com.shevaalex.android.rickmortydatabase.databinding.FragmentEpisodesListBinding
import com.shevaalex.android.rickmortydatabase.models.episode.EpisodeModel
import com.shevaalex.android.rickmortydatabase.ui.base.BaseListFragment
import com.shevaalex.android.rickmortydatabase.utils.*
import javax.inject.Inject

class EpisodesListFragment : BaseListFragment<FragmentEpisodesListBinding>() {

    @Inject
    lateinit var viewModelFactory: DiViewModelFactory<EpisodeListViewModel>

    override val viewModel: EpisodeListViewModel by activityViewModels {
        viewModelFactory
    }

    override val keyListFilterMap = Constants.KEY_FRAGMENT_EPISODE_LIST_FILTER_MAP

    override val keyListQuery = Constants.KEY_FRAGMENT_EPISODE_LIST_QUERY

    override val keyListPosition = Constants.KEY_FRAGMENT_EPISODE_LIST_LIST_POSITION

    private var episodeAdapter: EpisodeAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        episodeAdapter = EpisodeAdapter(
                placeHolderString = getString(R.string.episode_name_placeholder),
                episodeListener = object : EpisodeAdapter.EpisodeListener {
                    override fun onEpisodeClick(
                            episode: EpisodeModel,
                            episodeCard: MaterialCardView
                    ) {
                        navigateEpisodeDetail(episode, episodeCard)
                    }
                }
        )
        setGridOrLinearRecyclerView(
                binding.recyclerviewEpisode,
                episodeAdapter
        )
        registerObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        episodeAdapter = null
    }

    private fun registerObservers() {
        viewModel.episodeList.observe(viewLifecycleOwner, {
            if (it.isEmpty()) {
                viewModel.searchQuery?.let {
                    binding.tvNoResults.visibility = View.VISIBLE
                }
            } else {
                binding.tvNoResults.visibility = View.GONE
            }
            //set data to the adapter
            episodeAdapter?.submitList(it)
            //restore list position, or if it has been nulled -> scroll to position 0
            viewModel.rvListPosition.value?.let { state ->
                binding.recyclerviewEpisode.layoutManager?.onRestoreInstanceState(state)
            } ?: binding.recyclerviewEpisode.layoutManager?.scrollToPosition(0)
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
    ): FragmentEpisodesListBinding =
            FragmentEpisodesListBinding.inflate(inflater, container, false)

    override fun getToolbar() = binding.toolbarFragmentEpisodeList as Toolbar?

    override fun saveRvListPosition() {
        binding.recyclerviewEpisode.layoutManager?.onSaveInstanceState()?.let { lmState ->
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
                            viewRes = R.layout.dialog_filter_episode,
                            scrollable = true)
            val dialogView = dialog.getCustomView()

            val seasonShowAll = dialogView.findViewById<MaterialCheckBox>(R.id.season_all)
            val seasonCustom = listOf(
                    dialogView.findViewById(R.id.season_1),
                    dialogView.findViewById(R.id.season_2),
                    dialogView.findViewById(R.id.season_3),
                    dialogView.findViewById<MaterialCheckBox>(R.id.season_4)
            )

            restoreCheckBoxState(dialogView)

            seasonShowAll.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    seasonCustom.forEach {
                        it.isChecked = false
                    }
                }
            }
            seasonCustom.forEach {
                it.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked && seasonShowAll.isChecked) {
                        seasonShowAll.isChecked = false
                    }
                }
            }

            dialog.positiveButton(text = getString(R.string.dialog_positive_button)) { mdialog ->
                if (seasonCustom.any { it.isChecked } || seasonShowAll.isChecked) {
                    setupFiltration(mdialog)
                } else {
                    val errors: MutableList<String> = mutableListOf()
                    if (seasonCustom.all { !it.isChecked } && !seasonShowAll.isChecked) {
                        errors.add(getString(R.string.dialog_error_season))
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
        filterMap[Constants.KEY_MAP_FILTER_EPISODE_S_ALL] =
                if (dialogView.findViewById<MaterialCheckBox>(R.id.season_all).isChecked)
                    Pair(true, null)
                else Pair(false, null)
        filterMap[Constants.KEY_MAP_FILTER_EPISODE_S_01] =
                if (dialogView.findViewById<MaterialCheckBox>(R.id.season_1).isChecked)
                    Pair(true, stringMap[Constants.KEY_MAP_FILTER_EPISODE_S_01])
                else Pair(false, null)
        filterMap[Constants.KEY_MAP_FILTER_EPISODE_S_02] =
                if (dialogView.findViewById<MaterialCheckBox>(R.id.season_2).isChecked)
                    Pair(true, stringMap[Constants.KEY_MAP_FILTER_EPISODE_S_02])
                else Pair(false, null)
        filterMap[Constants.KEY_MAP_FILTER_EPISODE_S_03] =
                if (dialogView.findViewById<MaterialCheckBox>(R.id.season_3).isChecked)
                    Pair(true, stringMap[Constants.KEY_MAP_FILTER_EPISODE_S_03])
                else Pair(false, null)
        filterMap[Constants.KEY_MAP_FILTER_EPISODE_S_04] =
                if (dialogView.findViewById<MaterialCheckBox>(R.id.season_4).isChecked)
                    Pair(true, stringMap[Constants.KEY_MAP_FILTER_EPISODE_S_04])
                else Pair(false, null)

        viewModel.setFilterFlags(filterMap.toMap())

        mdialog.dismiss()
    }

    override fun restoreCheckBoxState(dialogView: View) {
        viewModel.getFilterMap()?.let {
            dialogView.findViewById<MaterialCheckBox>(R.id.season_all).isChecked =
                    it[Constants.KEY_MAP_FILTER_EPISODE_S_ALL]?.first ?: false
            dialogView.findViewById<MaterialCheckBox>(R.id.season_1).isChecked =
                    it[Constants.KEY_MAP_FILTER_EPISODE_S_01]?.first ?: false
            dialogView.findViewById<MaterialCheckBox>(R.id.season_2).isChecked =
                    it[Constants.KEY_MAP_FILTER_EPISODE_S_02]?.first ?: false
            dialogView.findViewById<MaterialCheckBox>(R.id.season_3).isChecked =
                    it[Constants.KEY_MAP_FILTER_EPISODE_S_03]?.first ?: false
            dialogView.findViewById<MaterialCheckBox>(R.id.season_4).isChecked =
                    it[Constants.KEY_MAP_FILTER_EPISODE_S_04]?.first ?: false
        }
    }

    override fun getStringMap() = mapOf(
            Constants.KEY_MAP_FILTER_EPISODE_S_01 to Constants.VALUE_MAP_FILTER_EPISODE_S_01,
            Constants.KEY_MAP_FILTER_EPISODE_S_02 to Constants.VALUE_MAP_FILTER_EPISODE_S_02,
            Constants.KEY_MAP_FILTER_EPISODE_S_03 to Constants.VALUE_MAP_FILTER_EPISODE_S_03,
            Constants.KEY_MAP_FILTER_EPISODE_S_04 to Constants.VALUE_MAP_FILTER_EPISODE_S_04
    )

    private fun navigateEpisodeDetail(episode: EpisodeModel, episodeCard: MaterialCardView) {
        setExitAndReenterAnimation()
        val extras = FragmentNavigatorExtras(
                episodeCard to Constants.TRANSITION_EPISODE.plus(episode.id)
        )
        val action = EpisodesListFragmentDirections.toEpisodeDetailFragmentAction(episode)
        findNavController().safeNavigate<EpisodesListFragment>(action, extras)
    }

}