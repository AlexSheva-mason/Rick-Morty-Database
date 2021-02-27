package com.shevaalex.android.rickmortydatabase.repository.location

import androidx.lifecycle.LiveData
import com.shevaalex.android.rickmortydatabase.models.character.CharacterModel

interface LocationDetailRepository {

    fun getCharacters(characterIds: List<Int>): LiveData<List<CharacterModel>>

}