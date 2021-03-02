package com.shevaalex.android.rickmortydatabase.ui.character.detail

import androidx.lifecycle.*
import com.shevaalex.android.rickmortydatabase.models.character.CharacterEntity
import com.shevaalex.android.rickmortydatabase.models.episode.EpisodeEntity
import com.shevaalex.android.rickmortydatabase.models.location.LocationEntity
import com.shevaalex.android.rickmortydatabase.repository.character.CharacterDetailRepo
import com.shevaalex.android.rickmortydatabase.ui.viewmodel.BaseDetailViewModel
import javax.inject.Inject

class CharacterDetailViewModel
@Inject
constructor(
        private val characterDetailRepo: CharacterDetailRepo
): BaseDetailViewModel<CharacterEntity>() {

    val episodes: LiveData<List<EpisodeEntity>> = _detailObject.switchMap {
        characterDetailRepo.getEpisodes(it.episodeIds)
    }

    val lastLocation: LiveData<LocationEntity> = _detailObject.switchMap {
        characterDetailRepo.getLocationById(it.lastLocation.id)
    }

    val originLocation: LiveData<LocationEntity> = _detailObject.switchMap {
        characterDetailRepo.getLocationById(it.originLocation.id)
    }

}