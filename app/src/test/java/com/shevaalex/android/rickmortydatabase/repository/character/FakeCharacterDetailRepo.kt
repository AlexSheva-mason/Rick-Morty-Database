package com.shevaalex.android.rickmortydatabase.repository.character

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.shevaalex.android.rickmortydatabase.EpisodeInitManagerDataFactory
import com.shevaalex.android.rickmortydatabase.LocationInitManagerDataFactory
import com.shevaalex.android.rickmortydatabase.models.episode.EpisodeEntity
import com.shevaalex.android.rickmortydatabase.models.location.LocationEntity

class FakeCharacterDetailRepo : CharacterDetailRepo {

    private val locationDataFactory = LocationInitManagerDataFactory()
    private val episodeDataFactory = EpisodeInitManagerDataFactory()

    override fun getLocationById(id: Int): LiveData<LocationEntity> {
        return MutableLiveData(locationDataFactory.produceObjectModel(id))
    }

    override fun getEpisodes(episodeIds: List<Int>): LiveData<List<EpisodeEntity>> {
        val episodesList = mutableListOf<EpisodeEntity>()
        episodeIds.forEach {
            episodesList.add(episodeDataFactory.produceObjectModel(it))
        }
        return MutableLiveData(episodesList)
    }

}