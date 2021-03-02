package com.shevaalex.android.rickmortydatabase.repository.character

import androidx.paging.DataSource
import com.shevaalex.android.rickmortydatabase.CharacterInitManagerDataFactory
import com.shevaalex.android.rickmortydatabase.createMockDataSourceFactory
import com.shevaalex.android.rickmortydatabase.models.character.CharacterEntity
import com.shevaalex.android.rickmortydatabase.repository.BaseListRepository

class FakeCharacterRepository : BaseListRepository(), CharacterRepository {

    private val dataFactory = CharacterInitManagerDataFactory()

    val allCharacters = dataFactory.createFixedIdObjectList(100)
    val filteredCharacters = dataFactory.createFixedIdObjectList(50)

    override fun getAllCharacters(): DataSource.Factory<Int, CharacterEntity> {
        return createMockDataSourceFactory(allCharacters)
    }

    override fun searchOrFilterCharacters(
            query: String,
            filterMap: Map<String, Pair<Boolean, String?>>,
            showsAll: Boolean
    ): DataSource.Factory<Int, CharacterEntity> {
        return createMockDataSourceFactory(filteredCharacters)
    }

}