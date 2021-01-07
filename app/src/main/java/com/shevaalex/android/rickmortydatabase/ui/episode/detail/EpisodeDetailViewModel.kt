package com.shevaalex.android.rickmortydatabase.ui.episode.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.shevaalex.android.rickmortydatabase.models.character.CharacterModel
import com.shevaalex.android.rickmortydatabase.models.episode.EpisodeModel
import com.shevaalex.android.rickmortydatabase.repository.location.LocationDetailRepo
import com.shevaalex.android.rickmortydatabase.ui.viewmodel.BaseDetailViewModel
import javax.inject.Inject

class EpisodeDetailViewModel
@Inject
constructor(
        private val locationDetailRepo: LocationDetailRepo
): BaseDetailViewModel<EpisodeModel>() {

    val characters: LiveData<List<CharacterModel>> = _detailObject.switchMap {
        locationDetailRepo.getCharacters(it.characterIds)
    }

}