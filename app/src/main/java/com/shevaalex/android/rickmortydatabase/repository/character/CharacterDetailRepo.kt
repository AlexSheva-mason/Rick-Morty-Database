package com.shevaalex.android.rickmortydatabase.repository.character

import androidx.lifecycle.LiveData
import com.shevaalex.android.rickmortydatabase.models.episode.EpisodeModel
import com.shevaalex.android.rickmortydatabase.models.location.LocationModel
import com.shevaalex.android.rickmortydatabase.source.local.EpisodeModelDao
import com.shevaalex.android.rickmortydatabase.source.local.LocationModelDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CharacterDetailRepo
@Inject
constructor(
        private val episodeDao: EpisodeModelDao,
        private val locationDao: LocationModelDao
) {

    fun getLocationById(id: Int): LiveData<LocationModel> = locationDao.getLocationById(id)

    fun getEpisodes(episodeIds: List<Int>): LiveData<List<EpisodeModel>> =
            episodeDao.getEpisodesByIds(episodeIds)

}