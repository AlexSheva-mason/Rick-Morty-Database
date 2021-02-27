package com.shevaalex.android.rickmortydatabase.repository.character

import androidx.lifecycle.LiveData
import com.shevaalex.android.rickmortydatabase.models.episode.EpisodeModel
import com.shevaalex.android.rickmortydatabase.models.location.LocationModel

interface CharacterDetailRepo {

    fun getLocationById(id: Int): LiveData<LocationModel>

    fun getEpisodes(episodeIds: List<Int>): LiveData<List<EpisodeModel>>

}