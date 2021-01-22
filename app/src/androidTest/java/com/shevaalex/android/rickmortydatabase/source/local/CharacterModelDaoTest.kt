package com.shevaalex.android.rickmortydatabase.source.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.shevaalex.android.rickmortydatabase.models.character.CharacterModel
import com.shevaalex.android.rickmortydatabase.models.character.LinkedLocationModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class CharacterModelDaoTest {

    @Inject
    lateinit var dataFactory: DataFactory

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: RickMortyDatabase
    private lateinit var characterDao: CharacterModelDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                RickMortyDatabase::class.java
        )
                .allowMainThreadQueries()
                .build()
        characterDao = database.characterModelDao
    }

    @After
    fun clearUp() {
        database.close()
    }

    @Test
    fun insertCharacter() = runBlockingTest {
        val testID = 0
        val testCharacter = dataFactory.produceCharacterModel(testID)
        characterDao.insertCharacters(listOf(testCharacter))
        val retreivedCharacter = characterDao.getCharacterByIdSuspend(testID)
        assertThat(retreivedCharacter == testCharacter)
    }

    @Test
    fun getLastInCharacterTable() = runBlockingTest {
        //insert a dummy list of Characters
    }


}