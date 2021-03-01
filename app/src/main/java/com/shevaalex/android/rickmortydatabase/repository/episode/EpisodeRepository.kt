package com.shevaalex.android.rickmortydatabase.repository.episode

import androidx.paging.DataSource
import com.shevaalex.android.rickmortydatabase.models.episode.EpisodeModel
import com.shevaalex.android.rickmortydatabase.repository.ListRepository

interface EpisodeRepository : ListRepository {

    fun getAllEpisodes(): DataSource.Factory<Int, EpisodeModel>

    fun searchAndFilterEpisodes(
            query: String,
            filterMap: Map<String, Pair<Boolean, String?>>,
            showsAll: Boolean
    ): DataSource.Factory<Int, EpisodeModel>

}