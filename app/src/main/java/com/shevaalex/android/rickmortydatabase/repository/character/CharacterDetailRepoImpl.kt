package com.shevaalex.android.rickmortydatabase.repository.character

import androidx.lifecycle.LiveData
import com.shevaalex.android.rickmortydatabase.models.episode.EpisodeEntity
import com.shevaalex.android.rickmortydatabase.models.location.LocationEntity
import com.shevaalex.android.rickmortydatabase.source.local.EpisodeDao
import com.shevaalex.android.rickmortydatabase.source.local.LocationDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CharacterDetailRepoImpl
@Inject
constructor(
        private val episodeDao: EpisodeDao,
        private val locationDao: LocationDao
) : CharacterDetailRepo {

    override fun getLocationById(id: Int): LiveData<LocationEntity> = locationDao.getLocationById(id)

    override fun getEpisodes(episodeIds: List<Int>): LiveData<List<EpisodeEntity>> =
            episodeDao.getEpisodesByIds(episodeIds)

}