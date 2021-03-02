package com.shevaalex.android.rickmortydatabase.repository.character

import androidx.paging.DataSource
import com.shevaalex.android.rickmortydatabase.models.character.CharacterEntity
import com.shevaalex.android.rickmortydatabase.repository.ListRepository

interface CharacterRepository : ListRepository {

    fun getAllCharacters(): DataSource.Factory<Int, CharacterEntity>

    fun searchOrFilterCharacters(
            query: String,
            filterMap: Map<String, Pair<Boolean, String?>>,
            showsAll: Boolean
    ): DataSource.Factory<Int, CharacterEntity>

}