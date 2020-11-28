package com.shevaalex.android.rickmortydatabase.repository.location

import androidx.lifecycle.LiveData
import com.shevaalex.android.rickmortydatabase.models.character.CharacterModel
import com.shevaalex.android.rickmortydatabase.source.database.CharacterModelDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationDetailRepo
@Inject
constructor(
        private val characterDao: CharacterModelDao
) {

    fun getCharacters(characterIds: List<Int>): LiveData<List<CharacterModel>> =
            characterDao.getCharactersByIds(characterIds)

}