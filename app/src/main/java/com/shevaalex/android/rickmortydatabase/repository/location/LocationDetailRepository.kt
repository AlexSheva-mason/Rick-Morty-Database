package com.shevaalex.android.rickmortydatabase.repository.location

import androidx.lifecycle.LiveData
import com.shevaalex.android.rickmortydatabase.models.character.CharacterEntity

interface LocationDetailRepository {

    fun getCharacters(characterIds: List<Int>): LiveData<List<CharacterEntity>>

}