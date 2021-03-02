package com.shevaalex.android.rickmortydatabase.repository.character

import androidx.lifecycle.LiveData
import com.shevaalex.android.rickmortydatabase.models.episode.EpisodeEntity
import com.shevaalex.android.rickmortydatabase.models.location.LocationEntity

interface CharacterDetailRepo {

    fun getLocationById(id: Int): LiveData<LocationEntity>

    fun getEpisodes(episodeIds: List<Int>): LiveData<List<EpisodeEntity>>

}