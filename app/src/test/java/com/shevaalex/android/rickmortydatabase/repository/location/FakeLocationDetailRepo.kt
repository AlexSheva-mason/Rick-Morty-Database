package com.shevaalex.android.rickmortydatabase.repository.location

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.shevaalex.android.rickmortydatabase.CharacterInitManagerDataFactory
import com.shevaalex.android.rickmortydatabase.models.character.CharacterModel


class FakeLocationDetailRepo : LocationDetailRepository {

    private val dataFactory = CharacterInitManagerDataFactory()

    override fun getCharacters(characterIds: List<Int>): LiveData<List<CharacterModel>> {
        val list = characterIds.map {
            dataFactory.produceCharacterModel(it)
        }.toList()
        return MutableLiveData(list)
    }

}