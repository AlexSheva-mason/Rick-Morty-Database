package com.shevaalex.android.rickmortydatabase.ui.episode.list

import androidx.lifecycle.*
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.shevaalex.android.rickmortydatabase.models.episode.EpisodeEntity
import com.shevaalex.android.rickmortydatabase.repository.episode.EpisodeRepository
import com.shevaalex.android.rickmortydatabase.ui.viewmodel.BaseListViewModel
import com.shevaalex.android.rickmortydatabase.utils.Constants
import com.shevaalex.android.rickmortydatabase.utils.FilterMediatorLiveData
import javax.inject.Inject

class EpisodeListViewModel
@Inject
constructor(
        private val episodeRepository: EpisodeRepository
) : BaseListViewModel() {

    override val recentQueries: LiveData<List<String>> =
            episodeRepository.getRecentQueries().asLiveData()

    private val allEpisodes = episodeRepository.getAllEpisodes()

    override val filterData: MutableLiveData<Map<String, Pair<Boolean, String?>>> =
            MutableLiveData(mapOf(
                    Constants.KEY_MAP_FILTER_EPISODE_S_ALL to Pair(true, null)
            ))

    override val suggestions: LiveData<List<String>> =
            Transformations.switchMap(filterData) {
                if (showsAll()) {
                    episodeRepository.getSuggestionsNames().asLiveData()
                } else episodeRepository.getSuggestionsNamesFiltered(it).asLiveData()
            }


    override val mediatorLiveData = FilterMediatorLiveData(_searchQuery, filterData)

    val episodeList: LiveData<PagedList<EpisodeEntity>> =
            Transformations.switchMap(mediatorLiveData) {
                //if query is blank and filter == showAll -> show all results
                if (it.first.isBlank() && showsAll()) {
                    allEpisodes.toLiveData(Constants.ROOM_PAGE_SIZE)
                }
                // else -> perform search and/or filter the data
                else episodeRepository
                        .searchAndFilterEpisodes(it.first, it.second, showsAll())
                        .toLiveData(Constants.ROOM_PAGE_SIZE)
            }

    override suspend fun saveSearchQuery(query: String) {
        episodeRepository.saveSearchQuery(query)
    }

    override fun showsAll(): Boolean =
            filterData.value?.get(Constants.KEY_MAP_FILTER_EPISODE_S_ALL)?.first!!

}