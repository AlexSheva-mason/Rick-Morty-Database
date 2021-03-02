package com.shevaalex.android.rickmortydatabase.ui.location.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.shevaalex.android.rickmortydatabase.models.character.CharacterEntity
import com.shevaalex.android.rickmortydatabase.models.location.LocationEntity
import com.shevaalex.android.rickmortydatabase.repository.location.LocationDetailRepo
import com.shevaalex.android.rickmortydatabase.ui.viewmodel.BaseDetailViewModel
import javax.inject.Inject

class LocationDetailViewModel
@Inject
constructor(
        private val locationDetailRepo: LocationDetailRepo
): BaseDetailViewModel<LocationEntity>() {

    val characters: LiveData<List<CharacterEntity>> = _detailObject.switchMap {
        locationDetailRepo.getCharacters(it.characterIds)
    }

}