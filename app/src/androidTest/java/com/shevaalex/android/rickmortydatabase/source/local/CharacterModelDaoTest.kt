package com.shevaalex.android.rickmortydatabase.source.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.toLiveData
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.shevaalex.android.rickmortydatabase.*
import com.shevaalex.android.rickmortydatabase.di.TestAppComponent
import com.shevaalex.android.rickmortydatabase.models.character.CharacterModel
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject
import kotlin.random.Random

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class CharacterModelDaoTest : BaseTest() {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var characterDataFactory: CharacterDataFactory

    @Inject
    lateinit var characterDao: CharacterModelDao

    init {
        injectTest()
    }

    @Test
    fun insertCharacter() = runBlockingTest {
        val testID = 0
        val testCharacter = characterDataFactory.produceCharacterModel(testID)
        characterDao.insertCharacters(listOf(testCharacter))
        val retreivedCharacter = characterDao.getCharacterByIdSuspend(testID)
        assertThat(retreivedCharacter).isEqualTo(testCharacter)
    }

    @Test
    fun getLastInCharacterTable() = runBlockingTest {
        //insert a dummy list of Characters
        val testList = insertCharacterList()
        //assert that last character from db should be == to an object with max Id in the list
        val lastCharacter = characterDao.getLastInCharacterTable()
        assertThat(testList.maxByOrNull { it.id }).isEqualTo(lastCharacter)
    }

    @Test
    fun charactersCount() = runBlockingTest {
        //insert a dummy list of Characters.
        val testList = insertCharacterList()
        //get the data from db and check that list size == numberOfCharacters
        val charactersCount = characterDao.charactersCount()
        assertThat(testList.size).isEqualTo(charactersCount)
    }

    @Test
    fun getSuggestionsNames() = runBlockingTest {
        //insert a dummy list of Characters. Sort and map just as if it was returned from the db
        val testList = insertCharacterList().sortedBy { it.id }.map { it.name }
        //get the data from db, assert returned value == testList
        val namesFlow = characterDao.getSuggestionsNames()
        val testCollector = namesFlow.test(this)
        testCollector.assertEmittedValuesEquals(testList)
        testCollector.cancel()
    }

    @Test
    fun getSuggestionsNamesFilteredAll() = runBlockingTest {
        //insert a dummy list of Characters
        val numberOfCharacters = 100
        insertCharacterList(numberOfCharacters, false)
        //filter it by 3 parameters to return 3 object
        val filterStatus = listOf("testStatus10", "testStatus20", "testStatus30")
        val filterGender = listOf("testGender10", "testGender20", "testGender30")
        val filterSpecies = listOf("testSpecies10", "testSpecies20", "testSpecies30")
        val namesFlow = characterDao.getSuggestionsNamesFiltered(
                filterStatus,
                filterGender,
                filterSpecies)
        val testCollector = namesFlow.test(this)
        //assert that returned list is of size 3
        assertThat(testCollector.values.last().size).isEqualTo(3)
        testCollector.cancel()
    }

    @Test
    fun getSuggestionsNamesFilteredStatusGender() = runBlockingTest {
        //insert a dummy list of Characters
        val numberOfCharacters = 100
        insertCharacterList(numberOfCharacters, false)
        //filter it by 2 parameters to return 4 objects
        val filterStatus = listOf("testStatus10", "testStatus20", "testStatus30", "testStatus40")
        val filterGender = listOf("testGender10", "testGender20", "testGender30", "testGender40")
        val namesFlow = characterDao.getSuggestionsNamesFiltered(filterStatus, filterGender)
        val testCollector = namesFlow.test(this)
        //assert that returned list is of size 4
        assertThat(testCollector.values.last().size).isEqualTo(4)
        testCollector.cancel()
    }

    @Test
    fun getAllCharacters() = runBlockingTest {
        //insert a dummy list of Characters
        val testList = insertCharacterList()
        //get the data from db, assert returned list.size == testList.size
        val fetchedList = characterDao.getAllCharacters().toLiveData(10).getOrAwaitValue()
        assertThat(fetchedList.size).isEqualTo(testList.size)
    }

    @Test
    fun searchCharactersSingle() = runBlockingTest {
        //insert a dummy list of Characters
        val numberOfCharacters = 100
        insertCharacterList(numberOfCharacters, false)
        //make a query within bounds of inserted list
        val randomNameQuery = "testName${Random.nextInt(1, numberOfCharacters)}"
        val fetchedList = characterDao
                .searchCharacters(randomNameQuery)
                .toLiveData(10)
                .getOrAwaitValue()
        //assert that returned list contains object with a requested name
        assertThat(fetchedList[0]?.name).isEqualTo(randomNameQuery)
    }

    @Test
    fun searchCharactersDouble() = runBlockingTest {
        //insert a dummy list of Characters
        val numberOfCharacters = 100
        insertCharacterList(numberOfCharacters, false)
        //make a query within bounds of inserted list
        val randomId = Random.nextInt(1, numberOfCharacters)
        val randomName = "testName$randomId"
        //create an object with a randomName for cross-reference
        val checkCharacter = characterDataFactory.produceCharacterModel(id = randomId)
        val fetchedList = characterDao
                .searchCharacters(randomName, randomName)
                .toLiveData(10)
                .getOrAwaitValue()
        //assert that returned list contains only 1 object with a requested name
        assertThat(fetchedList.contains(checkCharacter) && fetchedList.size == 1)
    }

    @Test
    fun getFilteredCharacters() = runBlockingTest {
        //insert a dummy list of Characters
        val numberOfCharacters = 100
        insertCharacterList(numberOfCharacters, false)
        //filter it by 3 parameters to return 4 objects
        val filterStatus = listOf("testStatus10", "testStatus20", "testStatus30", "testStatus40")
        val filterGender = listOf("testGender10", "testGender20", "testGender30", "testGender40")
        val filterSpecies = listOf("testSpecies10", "testSpecies20", "testSpecies30", "testSpecies40")
        //expected objects for cross-reference
        val checkCharacterA = characterDataFactory.produceCharacterModel(id = 10)
        val checkCharacterB = characterDataFactory.produceCharacterModel(id = 20)
        val checkCharacterC = characterDataFactory.produceCharacterModel(id = 30)
        val checkCharacterD = characterDataFactory.produceCharacterModel(id = 40)
        val fetchedList = characterDao.getFilteredCharacters(
                filterStatus,
                filterGender,
                filterSpecies
        )
                .toLiveData(10)
                .getOrAwaitValue()
        //assert that returned list contains expected objects
        assertThat(fetchedList)
                .containsExactly(checkCharacterA, checkCharacterB, checkCharacterC, checkCharacterD)
    }

    @Test
    fun getFilteredNoSpeciesCharacters() = runBlockingTest {
        //insert a dummy list of Characters
        val numberOfCharacters = 100
        insertCharacterList(numberOfCharacters, false)
        //filter it by 2 parameters to return 4 objects
        val filterStatus = listOf("testStatus15", "testStatus25", "testStatus35", "testStatus45")
        val filterGender = listOf("testGender15", "testGender25", "testGender35", "testGender45")
        //expected objects for cross-reference
        val checkCharacterA = characterDataFactory.produceCharacterModel(id = 15)
        val checkCharacterB = characterDataFactory.produceCharacterModel(id = 25)
        val checkCharacterC = characterDataFactory.produceCharacterModel(id = 35)
        val checkCharacterD = characterDataFactory.produceCharacterModel(id = 45)
        val fetchedList = characterDao.getFilteredNoSpeciesCharacters(
                statuses = filterStatus,
                genders = filterGender
        )
                .toLiveData(10)
                .getOrAwaitValue()
        //assert that returned list contains expected objects
        assertThat(fetchedList)
                .containsExactly(checkCharacterA, checkCharacterB, checkCharacterC, checkCharacterD)
    }

    @Test
    fun searchAndFilterCharacters() = runBlockingTest {
        //insert a dummy list of Characters
        val numberOfCharacters = 100
        insertCharacterList(numberOfCharacters, false)
        //filter it by 3 parameters to return 3 objects
        val filterStatus = listOf("testStatus10", "testStatus20", "testStatus30")
        val filterGender = listOf("testGender10", "testGender20", "testGender30")
        val filterSpecies = listOf("testSpecies10", "testSpecies20", "testSpecies30")
        //query
        val nameQuery = "testName"
        //expected objects for cross-reference
        val checkCharacterA = characterDataFactory.produceCharacterModel(id = 10)
        val checkCharacterB = characterDataFactory.produceCharacterModel(id = 20)
        val checkCharacterC = characterDataFactory.produceCharacterModel(id = 30)
        val fetchedList = characterDao.searchAndFilterCharacters(
                name = nameQuery,
                statuses = filterStatus,
                genders = filterGender,
                species = filterSpecies
        )
                .toLiveData(10)
                .getOrAwaitValue()
        //assert that returned list contains expected objects
        assertThat(fetchedList)
                .containsExactly(checkCharacterA, checkCharacterB, checkCharacterC)
    }

    @Test
    fun searchAndFilterNoSpeciesCharacters() = runBlockingTest {
        //insert a dummy list of Characters
        val numberOfCharacters = 100
        insertCharacterList(numberOfCharacters, false)
        //filter it by 2 parameters to return 3 objects
        val filterStatus = listOf("testStatus10", "testStatus20", "testStatus30")
        val filterGender = listOf("testGender10", "testGender20", "testGender30")
        //query
        val nameQuery = "testName"
        //expected objects for cross-reference
        val checkCharacterA = characterDataFactory.produceCharacterModel(id = 10)
        val checkCharacterB = characterDataFactory.produceCharacterModel(id = 20)
        val checkCharacterC = characterDataFactory.produceCharacterModel(id = 30)
        val fetchedList = characterDao.searchAndFilterNoSpeciesCharacters(
                name = nameQuery,
                statuses = filterStatus,
                genders = filterGender
        )
                .toLiveData(10)
                .getOrAwaitValue()
        //assert that returned list contains expected objects
        assertThat(fetchedList)
                .containsExactly(checkCharacterA, checkCharacterB, checkCharacterC)
    }

    @Test
    fun getCharactersByIds() = runBlockingTest {
        //insert a dummy list of Characters
        val numberOfCharacters = 100
        insertCharacterList(numberOfCharacters, false)
        val listOfIds = listOf(12, 35, 59, 73, 99)
        //expected objects for cross-reference
        val expectedCharacterList = listOf(
                characterDataFactory.produceCharacterModel(listOfIds[0]),
                characterDataFactory.produceCharacterModel(listOfIds[1]),
                characterDataFactory.produceCharacterModel(listOfIds[2]),
                characterDataFactory.produceCharacterModel(listOfIds[3]),
                characterDataFactory.produceCharacterModel(listOfIds[4])
        )
        val fetchedList = characterDao.getCharactersByIds(listOfIds).getOrAwaitValue()
        //assert that returned list contains expected objects
        assertThat(fetchedList)
                .containsExactly(expectedCharacterList[0],
                        expectedCharacterList[1],
                        expectedCharacterList[2],
                        expectedCharacterList[3],
                        expectedCharacterList[4]
                )
    }

    @Test
    fun getCharacterByIdInRange() = runBlockingTest {
        //insert a dummy list of Characters
        val numberOfCharacters = 100
        insertCharacterList(numberOfCharacters, false)
        //generate a random id
        val randomId = Random.nextInt(1, numberOfCharacters)
        val fetchedCharacter = characterDao.getCharacterByIdSuspend(randomId)
        //returned character should have id that matches [randomId]
        assertThat(fetchedCharacter?.id).isEqualTo(randomId)
    }

    @Test
    fun getCharacterByIdOutOfRange() = runBlockingTest {
        //insert a dummy list of Characters
        val numberOfCharacters = 100
        insertCharacterList(numberOfCharacters, false)
        //use id that is out of range
        val randomId = Random.nextInt(1, numberOfCharacters).plus(numberOfCharacters)
        val fetchedCharacter = characterDao.getCharacterByIdSuspend(randomId)
        //there should be no returned results
        assertThat(fetchedCharacter).isEqualTo(null)
    }

    /**
     * inserts fixed [number] or random number of CharacterModels
     * [isRandom] specifies if list should contain objects with random or fixed IDs
     */
    private suspend fun insertCharacterList(
            number: Int = Random.nextInt(50, 100),
            isRandom: Boolean = true
    ): List<CharacterModel> {
        val testList = if (isRandom) {
            characterDataFactory.createRandomIdObjectList(number)
        } else characterDataFactory.createFixedIdObjectList(number)
        characterDao.insertCharacters(testList)
        return testList
    }

    override fun injectTest() {
        (application.appComponent as TestAppComponent)
                .inject(this)
    }

}