package com.shevaalex.android.rickmortydatabase.repository.character

import androidx.paging.DataSource
import com.shevaalex.android.rickmortydatabase.models.character.CharacterModel
import com.shevaalex.android.rickmortydatabase.repository.ListRepository

interface CharacterRepository : ListRepository {

    fun getAllCharacters(): DataSource.Factory<Int, CharacterModel>

    fun searchOrFilterCharacters(
            query: String,
            filterMap: Map<String, Pair<Boolean, String?>>,
            showsAll: Boolean
    ): DataSource.Factory<Int, CharacterModel>

}