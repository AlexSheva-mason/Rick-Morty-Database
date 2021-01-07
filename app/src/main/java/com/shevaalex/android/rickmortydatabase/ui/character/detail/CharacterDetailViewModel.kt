package com.shevaalex.android.rickmortydatabase.ui.character.detail

import androidx.lifecycle.*
import com.shevaalex.android.rickmortydatabase.models.character.CharacterModel
import com.shevaalex.android.rickmortydatabase.models.episode.EpisodeModel
import com.shevaalex.android.rickmortydatabase.models.location.LocationModel
import com.shevaalex.android.rickmortydatabase.repository.character.CharacterDetailRepo
import com.shevaalex.android.rickmortydatabase.ui.viewmodel.BaseDetailViewModel
import javax.inject.Inject

class CharacterDetailViewModel
@Inject
constructor(
        private val characterDetailRepo: CharacterDetailRepo
): BaseDetailViewModel<CharacterModel>() {

    val episodes: LiveData<List<EpisodeModel>> = _detailObject.switchMap {
        characterDetailRepo.getEpisodes(it.episodeIds)
    }

    val lastLocation: LiveData<LocationModel> = _detailObject.switchMap {
        characterDetailRepo.getLocationById(it.lastLocation.id)
    }

    val originLocation: LiveData<LocationModel> = _detailObject.switchMap {
        characterDetailRepo.getLocationById(it.originLocation.id)
    }

}