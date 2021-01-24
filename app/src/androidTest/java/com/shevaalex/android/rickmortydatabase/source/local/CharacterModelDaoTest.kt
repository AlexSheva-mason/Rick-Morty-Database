package com.shevaalex.android.rickmortydatabase.source.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.shevaalex.android.rickmortydatabase.BaseTest
import com.shevaalex.android.rickmortydatabase.di.TestAppComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class CharacterModelDaoTest: BaseTest() {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var dataFactory: DataFactory

    @Inject
    lateinit var characterDao: CharacterModelDao

    init {
        injectTest()
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
        val numberOfCharacters = 50
        val testList = dataFactory.createListOfCharacters(numberOfCharacters)
        characterDao.insertCharacters(testList)
        val lastCharacter = characterDao.getLastInCharacterTable()
        //assert that last character from db should be == to a last object in the list
        assertThat(lastCharacter == testList[numberOfCharacters-1])
    }

    override fun injectTest() {
        (application.appComponent as TestAppComponent)
                .inject(this)
    }

}