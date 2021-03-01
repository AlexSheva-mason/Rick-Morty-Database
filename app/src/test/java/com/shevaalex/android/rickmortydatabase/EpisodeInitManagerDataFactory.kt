package com.shevaalex.android.rickmortydatabase

import com.shevaalex.android.rickmortydatabase.models.episode.EpisodeModel
import com.shevaalex.android.rickmortydatabase.utils.Constants

class EpisodeInitManagerDataFactory : DataFactory<EpisodeModel>() {

    private val episodeSeasonList = listOf(
            Constants.VALUE_MAP_FILTER_EPISODE_S_01,
            Constants.VALUE_MAP_FILTER_EPISODE_S_02,
            Constants.VALUE_MAP_FILTER_EPISODE_S_03,
            Constants.VALUE_MAP_FILTER_EPISODE_S_04
    )

    override fun produceObjectModel(id: Int): EpisodeModel {
        return EpisodeModel(
                id = id,
                name = "testName$id",
                imageUrl = "testImageUrl$id",
                airDate = "testAirDate$id",
                code = "${episodeSeasonList.random()}E99",
                description = "testDescription$id",
                charactersList = listOf(
                        (id*2).toString(),
                        (id*3).toString(),
                        (id*4).toString()
                )
        )
    }

}