package com.shevaalex.android.rickmortydatabase.source.local

import com.shevaalex.android.rickmortydatabase.models.episode.EpisodeModel
import com.shevaalex.android.rickmortydatabase.utils.Constants.Companion.VALUE_MAP_FILTER_EPISODE_S_01
import com.shevaalex.android.rickmortydatabase.utils.Constants.Companion.VALUE_MAP_FILTER_EPISODE_S_02
import com.shevaalex.android.rickmortydatabase.utils.Constants.Companion.VALUE_MAP_FILTER_EPISODE_S_03
import com.shevaalex.android.rickmortydatabase.utils.Constants.Companion.VALUE_MAP_FILTER_EPISODE_S_04
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EpisodeDataFactory
@Inject constructor(): DataFactory<EpisodeModel>() {

    private val episodeSeasonList = listOf(
            VALUE_MAP_FILTER_EPISODE_S_01,
            VALUE_MAP_FILTER_EPISODE_S_02,
            VALUE_MAP_FILTER_EPISODE_S_03,
            VALUE_MAP_FILTER_EPISODE_S_04
    )

    override fun produceObjectModel(id: Int): EpisodeModel {
        return EpisodeModel(
                id = id,
                name = "testName$id",
                imageUrl = "testImageUrl$id",
                airDate = "testAirDate$id",
                code = "${episodeSeasonList.random()}E99",
                description = "testDescription$id",
                charactersList = listOf()
        )
    }

}