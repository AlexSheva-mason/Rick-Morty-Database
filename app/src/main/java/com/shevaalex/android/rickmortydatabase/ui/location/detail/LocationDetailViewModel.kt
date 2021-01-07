package com.shevaalex.android.rickmortydatabase.ui.location.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.shevaalex.android.rickmortydatabase.models.character.CharacterModel
import com.shevaalex.android.rickmortydatabase.models.location.LocationModel
import com.shevaalex.android.rickmortydatabase.repository.location.LocationDetailRepo
import com.shevaalex.android.rickmortydatabase.ui.viewmodel.BaseDetailViewModel
import javax.inject.Inject

class LocationDetailViewModel
@Inject
constructor(
        private val locationDetailRepo: LocationDetailRepo
): BaseDetailViewModel<LocationModel>() {

    val characters: LiveData<List<CharacterModel>> = _detailObject.switchMap {
        locationDetailRepo.getCharacters(it.characterIds)
    }

}