package com.shevaalex.android.rickmortydatabase.ui.episode

import androidx.lifecycle.*
import androidx.paging.PagedList
import com.shevaalex.android.rickmortydatabase.models.episode.EpisodeModel
import com.shevaalex.android.rickmortydatabase.repository.EpisodeRepository
import com.shevaalex.android.rickmortydatabase.ui.BaseListViewModel
import com.shevaalex.android.rickmortydatabase.utils.Constants
import com.shevaalex.android.rickmortydatabase.utils.FilterMediatorLiveData
import javax.inject.Inject

class EpisodeListViewModel
@Inject
constructor(
        private val episodeRepository: EpisodeRepository
): BaseListViewModel() {

    override val suggestions: LiveData<List<String>> =
            episodeRepository.getSuggestionsNames().asLiveData()

    override val recentQueries: LiveData<List<String>> =
            episodeRepository.getRecentQueries().asLiveData()

    private val allEpisodes = episodeRepository.getAllEpisodes()

    override val filterData: MutableLiveData<Map<String, Pair<Boolean, String?>>> =
            MutableLiveData(mapOf(
                    Constants.KEY_MAP_FILTER_EPISODE_S_ALL to Pair(true, null)
            ))

    override val mediatorLiveData = FilterMediatorLiveData(_searchQuery, filterData)

    val episodeList: LiveData<PagedList<EpisodeModel>> =
            Transformations.switchMap(mediatorLiveData) {
                //if query is blank and filter == showAll -> show all results
                if(it.first.isBlank() && showsAll()) {
                    allEpisodes
                }
                // else -> perform search and/or filter the data
                else episodeRepository.searchAndFilterEpisodes(it.first, it.second, showsAll())
            }

    override suspend fun saveSearchQuery(query: String) {
        episodeRepository.saveSearchQuery(query)
    }

    override fun showsAll(): Boolean =
            filterData.value?.get(Constants.KEY_MAP_FILTER_EPISODE_S_ALL)?.first!!

}