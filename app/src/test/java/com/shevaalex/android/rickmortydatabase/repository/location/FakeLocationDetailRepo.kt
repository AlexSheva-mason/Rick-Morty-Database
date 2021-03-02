package com.shevaalex.android.rickmortydatabase.repository.location

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.shevaalex.android.rickmortydatabase.CharacterInitManagerDataFactory
import com.shevaalex.android.rickmortydatabase.models.character.CharacterEntity


class FakeLocationDetailRepo : LocationDetailRepository {

    private val dataFactory = CharacterInitManagerDataFactory()

    override fun getCharacters(characterIds: List<Int>): LiveData<List<CharacterEntity>> {
        val list = characterIds.map {
            dataFactory.produceCharacterEntity(it)
        }.toList()
        return MutableLiveData(list)
    }

}