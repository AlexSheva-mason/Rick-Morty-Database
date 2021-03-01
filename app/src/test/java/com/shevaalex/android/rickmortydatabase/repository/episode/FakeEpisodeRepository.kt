package com.shevaalex.android.rickmortydatabase.repository.episode

import androidx.paging.DataSource
import com.shevaalex.android.rickmortydatabase.EpisodeInitManagerDataFactory
import com.shevaalex.android.rickmortydatabase.createMockDataSourceFactory
import com.shevaalex.android.rickmortydatabase.models.episode.EpisodeModel
import com.shevaalex.android.rickmortydatabase.repository.BaseListRepository

class FakeEpisodeRepository : BaseListRepository(), EpisodeRepository {

    private val dataFactory = EpisodeInitManagerDataFactory()

    val allEpisodes = dataFactory.createFixedIdObjectList(100)
    val filteredEpisodes = dataFactory.createFixedIdObjectList(50)

    override fun getAllEpisodes(): DataSource.Factory<Int, EpisodeModel> {
        return createMockDataSourceFactory(allEpisodes)
    }

    override fun searchAndFilterEpisodes(
            query: String,
            filterMap: Map<String, Pair<Boolean, String?>>,
            showsAll: Boolean
    ): DataSource.Factory<Int, EpisodeModel> {
        return createMockDataSourceFactory(filteredEpisodes)
    }

}