package com.shevaalex.android.rickmortydatabase.repository.character

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.shevaalex.android.rickmortydatabase.EpisodeInitManagerDataFactory
import com.shevaalex.android.rickmortydatabase.LocationInitManagerDataFactory
import com.shevaalex.android.rickmortydatabase.models.episode.EpisodeModel
import com.shevaalex.android.rickmortydatabase.models.location.LocationModel

class FakeCharacterDetailRepo : CharacterDetailRepo {

    private val locationDataFactory = LocationInitManagerDataFactory()
    private val episodeDataFactory = EpisodeInitManagerDataFactory()

    override fun getLocationById(id: Int): LiveData<LocationModel> {
        return MutableLiveData(locationDataFactory.produceObjectModel(id))
    }

    override fun getEpisodes(episodeIds: List<Int>): LiveData<List<EpisodeModel>> {
        val episodesList = mutableListOf<EpisodeModel>()
        episodeIds.forEach {
            episodesList.add(episodeDataFactory.produceObjectModel(it))
        }
        return MutableLiveData(episodesList)
    }

}