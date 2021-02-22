package com.shevaalex.android.rickmortydatabase.repository.init

import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.*
import com.shevaalex.android.rickmortydatabase.CharacterInitManagerDataFactory
import com.shevaalex.android.rickmortydatabase.models.character.CharacterModel
import com.shevaalex.android.rickmortydatabase.source.local.CharacterModelDao
import com.shevaalex.android.rickmortydatabase.source.remote.CharacterApi
import com.shevaalex.android.rickmortydatabase.utils.currentTimeHours
import com.shevaalex.android.rickmortydatabase.utils.networking.Message
import com.shevaalex.android.rickmortydatabase.utils.networking.StateResource
import com.shevaalex.android.rickmortydatabase.utils.networking.Status
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt

@ExperimentalCoroutinesApi
class CharacterInitManagerImplTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    //Test subject
    private lateinit var characterInitManager: InitManager<CharacterModel>

    //Collaborators
    private lateinit var characterDao: CharacterModelDao
    private lateinit var characterApi: CharacterApi
    private lateinit var sharedPref: SharedPreferences
    private lateinit var sharedPrefEditor: SharedPreferences.Editor

    //Utilities
    private lateinit var dataFactory: CharacterInitManagerDataFactory
    private val characterCount = 50

    /**
     * when getting db count assume returns [characterCount]
     * when getting network list count assume returns a list of 50 values
     * when fetching a list from network assume returns successfully list of 50
     * sharedpreferences sharedPref.getInt() to return 0
     */
    @Before
    fun setUp() = runBlockingTest {
        dataFactory = CharacterInitManagerDataFactory()
        //mocking CharacterDao
        characterDao = mock()
        whenever(characterDao.getCharacterByIdSuspend(anyInt())).thenAnswer {
            dataFactory.produceCharacterModel(it.getArgument(0) as Int)
        }
        whenever(characterDao.charactersCount()).thenReturn(characterCount)
        //mocking CharacterApi
        characterApi = mock()
        whenever(characterApi.getCharacterList(any(), eq(true)))
                .thenReturn(dataFactory.produceApiResultCountSuccess())
        whenever(characterApi.getCharacterList(any()))
                .thenReturn(dataFactory.produceApiResultListSuccess())
        //mock SharedPreferences
        sharedPref = mock()
        sharedPrefEditor = mock()
        whenever(sharedPref.edit()).thenReturn(sharedPrefEditor)
        whenever(sharedPref.getInt(any(), any())).thenReturn(0)
        //instantiate initManager
        characterInitManager = CharacterInitManagerImpl(characterDao, characterApi, sharedPref)
    }

    @Test
    fun filterNetworkListShouldReturnEmptyListWhenPassingTwoIdenticalLists() = runBlockingTest {
        val networkList = dataFactory.createFixedIdObjectList(characterCount)
        val result = characterInitManager.filterNetworkList(networkList)
        assertThat(result).isEmpty()
    }

    @Test
    fun filterNetworkListShouldReturnObjectsNotPresentInDatabase() = runBlockingTest {
        whenever(characterDao.getCharacterByIdSuspend(eq(51))).thenAnswer { null }
        whenever(characterDao.getCharacterByIdSuspend(eq(55))).thenAnswer { null }
        val networkList = dataFactory.createFixedIdObjectList(55)
        val result = characterInitManager.filterNetworkList(networkList)
        assertThat(result).containsExactly(
                dataFactory.produceCharacterModel(51),
                dataFactory.produceCharacterModel(55)
        )
    }

    /**
     * via [setUp]:
     * [db count] characterDao.charactersCount() returns [characterCount] number
     * overrides [network count]
     */
    @Test
    fun initTableShouldManageErrorWhenNetworkCountApiResultFailure() = runBlockingTest {
        //override stubbing to return ApiResult.Failure
        whenever(characterApi.getCharacterList(any(), eq(true)))
                .thenReturn(dataFactory.produceApiResultFailure())
        val result = characterInitManager.initTable("token", "character_test")
        //should be called only once
        verify(characterApi, times(1)).getCharacterList(any(), eq(true))
        verify(characterDao, times(1)).charactersCount()
        //should never be called after
        verifyZeroInteractionMocks()
        //assert that result is StateResource with error (Message.ServerError)
        assertThat(result).isEqualTo(StateResource(Status.Error, Message.ServerError(0)))
    }

    /**
     * via [setUp]:
     * [db count] characterDao.charactersCount() returns [characterCount] number
     * overrides [network count]
     */
    @Test
    fun initTableShouldManageErrorWhenNetworkCountApiResultNetworkError() = runBlockingTest {
        //override stubbing to return ApiResult.NetworkError
        whenever(characterApi.getCharacterList(any(), eq(true)))
                .thenReturn(dataFactory.produceApiResultNetworkError())
        val result = characterInitManager.initTable("token", "character_test")
        //should be called only once
        verify(characterApi, times(1)).getCharacterList(any(), eq(true))
        verify(characterDao, times(1)).charactersCount()
        //should never be called after
        verifyZeroInteractionMocks()
        //assert that result is StateResource with error (Message.NetworkError)
        assertThat(result).isEqualTo(StateResource(Status.Error, Message.NetworkError))
    }

    /**
     * via [setUp]:
     * [db count] characterDao.charactersCount() returns [characterCount] number
     * overrides [network count]
     */
    @Test
    fun initTableShouldManageErrorWhenNetworkCountApiResultEmpty() = runBlockingTest {
        //override stubbing to return ApiResult.Empty
        whenever(characterApi.getCharacterList(any(), eq(true)))
                .thenReturn(dataFactory.produceApiResultEmpty())
        val result = characterInitManager.initTable("token", "character_test")
        //should be called only once
        verify(characterApi, times(1)).getCharacterList(any(), eq(true))
        verify(characterDao, times(1)).charactersCount()
        //should never be called after
        verifyZeroInteractionMocks()
        //assert that result is StateResource with error (Message.EmptyResponse)
        assertThat(result).isEqualTo(StateResource(Status.Error, Message.EmptyResponse))
    }

    /**
     * via [setUp]:
     * [db count] characterDao.charactersCount() returns [characterCount] number
     * overrides [network count]
     */
    @Test
    fun initTableShouldManageErrorWhenNetworkCountApiResultNull() = runBlockingTest {
        //override stubbing to return null
        whenever(characterApi.getCharacterList(any(), eq(true)))
                .thenReturn(null)
        val result = characterInitManager.initTable("token", "character_test")
        //should be called only once
        verify(characterApi, times(1)).getCharacterList(any(), eq(true))
        verify(characterDao, times(1)).charactersCount()
        //should never be called after
        verifyZeroInteractionMocks()
        //assert that result is StateResource with error (Message.ServerError(0))
        assertThat(result).isEqualTo(StateResource(Status.Error, Message.ServerError(0)))
    }

    /**
     * via [setUp]:
     * overrides [db count]
     * [network count] characterApi.getCharacterList(shallow = true) returns success result of 50
     * [network list] characterApi.getCharacterList returns success result of 50
     * [db object]characterDao.getCharacterByIdSuspend(anyInt()) returns CharacterModel(id = anyInt())
     * overrides [db object]
     *
     * objectCountNetwork > dbObjectCount
     */
    @Test
    fun initTableCallsFetchFromNetworkAndSaveDbWhenObjectCountNetworkBiggerThanDbObjectCount() = runBlockingTest {
        //override stubbing to return 45 objects in the database
        whenever(characterDao.charactersCount()).thenReturn(45)
        //override stubbing to not return objects with ids above 45
        whenever(characterDao.getCharacterByIdSuspend(eq(45))).thenReturn(null)
        whenever(characterDao.getCharacterByIdSuspend(eq(46))).thenReturn(null)
        whenever(characterDao.getCharacterByIdSuspend(eq(47))).thenReturn(null)
        whenever(characterDao.getCharacterByIdSuspend(eq(48))).thenReturn(null)
        whenever(characterDao.getCharacterByIdSuspend(eq(49))).thenReturn(null)
        whenever(characterDao.getCharacterByIdSuspend(eq(50))).thenReturn(null)
        val result = characterInitManager.initTable("token", "character_test")
        //should be called only once
        verify(characterApi, times(1)).getCharacterList(any(), eq(true))
        verify(characterDao, times(1)).charactersCount()
        verify(characterApi, times(1)).getCharacterList(any())
        verify(characterDao, times(1)).insertCharacters(any())
        verify(sharedPref, times(1)).edit()
        //should be called 50 times
        verify(characterDao, times(50)).getCharacterByIdSuspend(any())
        //should never be called after
        verifyZeroInteractionMocks()
        //verify arguments for characterDao.insertCharacters() to contain 6 objects
        verify(characterDao).insertCharacters(check {
            assertThat(it.size).isEqualTo(6)
        })
        assertThat(result).isEqualTo(StateResource(Status.Success, Message.DbIsUpToDate))
    }

    /**
     * via [setUp]:
     * [db count] characterDao.charactersCount() returns [characterCount] number
     * [network count] characterApi.getCharacterList(shallow = true) returns success result of 50
     * [network list] characterApi.getCharacterList returns success result of 50
     * [db object]characterDao.getCharacterByIdSuspend(anyInt()) returns CharacterModel(id = anyInt())
     * [refetch needed] sharedPref.getInt() returns 0
     *
     * objectCountNetwork <= dbObjectCount
     * initTable() calls isRefetchNeeded() which returns true
     */
    @Test
    fun initTableCallsIsRefetchNeededTrueAndReturnsStateResourceSuccess() = runBlockingTest {
        val result = characterInitManager.initTable("token", "character_test")
        //should be called only once
        verify(characterApi, times(1)).getCharacterList(any(), eq(true))
        verify(characterDao, times(1)).charactersCount()
        verify(sharedPref, times(1)).getInt(any(), any())
        verify(characterApi, times(1)).getCharacterList(any())
        verify(sharedPref, times(1)).edit()
        //should be called 50 times
        verify(characterDao, times(50)).getCharacterByIdSuspend(any())
        //should never be called after
        verifyZeroInteractionMocks()
        assertThat(result).isEqualTo(StateResource(Status.Success, Message.DbIsUpToDate))
    }

    /**
     * via [setUp]:
     * [db count] characterDao.charactersCount() returns [characterCount] number
     * [network count] characterApi.getCharacterList(shallow = true) returns success result of 50
     * [network list] characterApi.getCharacterList returns success result of 50
     * [db object]characterDao.getCharacterByIdSuspend(anyInt()) returns CharacterModel(id = anyInt())
     * overrides [refetch needed]
     *
     * objectCountNetwork <= dbObjectCount
     * initTable() calls isRefetchNeeded() which returns false
     * fetchFromNetworkAndSaveDb() is not called
     */
    @Test
    fun initTableCallsIsRefetchNeededFalseAndReturnsStateResourceSuccess() = runBlockingTest {
        //override stubbing -> sharedpreferences to return current time
        whenever(sharedPref.getInt(any(), any())).thenReturn(currentTimeHours().toInt())
        val result = characterInitManager.initTable("token", "character_test")
        //should be called only once
        verify(characterApi, times(1)).getCharacterList(any(), eq(true))
        verify(characterDao, times(1)).charactersCount()
        verify(sharedPref, times(1)).getInt(any(), any())
        //should never be called after
        verifyZeroInteractionMocks()
        assertThat(result).isEqualTo(StateResource(Status.Success, Message.DbIsUpToDate))
    }

    /**
     * via [setUp]:
     * [db count] characterDao.charactersCount() returns [characterCount] number
     * [network count] characterApi.getCharacterList(shallow = true) returns success result of 50
     * overrides [network list]
     * [refetch needed] sharedPref.getInt() returns 0
     *
     * objectCountNetwork <= dbObjectCount
     * initTable() calls isRefetchNeeded() which returns true
     */
    @Test
    fun fetchFromNetworkAndSaveDbShouldManageErrorWhenGetListFromNetworkApiResultFailure() = runBlockingTest {
        //override stubbing to return ApiResultFailure when fetching a list from network
        whenever(characterApi.getCharacterList(any()))
                .thenReturn(dataFactory.produceApiResultFailure())
        val result = characterInitManager.initTable("token", "character_test")
        //should be called only once
        verify(characterApi, times(1)).getCharacterList(any(), eq(true))
        verify(characterDao, times(1)).charactersCount()
        verify(sharedPref, times(1)).getInt(any(), any())
        verify(characterApi, times(1)).getCharacterList(any())
        //should never be called after
        verifyZeroInteractionMocks()
        //assert that result is StateResource with error (Message.ServerError)
        assertThat(result).isEqualTo(StateResource(Status.Error, Message.ServerError(0)))
    }

    /**
     * via [setUp]:
     * [db count] characterDao.charactersCount() returns [characterCount] number
     * [network count] characterApi.getCharacterList(shallow = true) returns success result of 50
     * overrides [network list]
     * [refetch needed] sharedPref.getInt() returns 0
     *
     * objectCountNetwork <= dbObjectCount
     * initTable() calls isRefetchNeeded() which returns true
     */
    @Test
    fun fetchFromNetworkAndSaveDbShouldManageErrorWhenGetListFromNetworkApiResultNetworkError() = runBlockingTest {
        //override stubbing to return ApiResult.NetworkError when fetching a list from network
        whenever(characterApi.getCharacterList(any()))
                .thenReturn(dataFactory.produceApiResultNetworkError())
        val result = characterInitManager.initTable("token", "character_test")
        //should be called only once
        verify(characterApi, times(1)).getCharacterList(any(), eq(true))
        verify(characterDao, times(1)).charactersCount()
        verify(sharedPref, times(1)).getInt(any(), any())
        verify(characterApi, times(1)).getCharacterList(any())
        //should never be called after
        verifyZeroInteractionMocks()
        //assert that result is StateResource with error (Message.NetworkError)
        assertThat(result).isEqualTo(StateResource(Status.Error, Message.NetworkError))
    }

    /**
     * via [setUp]:
     * [db count] characterDao.charactersCount() returns [characterCount] number
     * [network count] characterApi.getCharacterList(shallow = true) returns success result of 50
     * overrides [network list]
     * [refetch needed] sharedPref.getInt() returns 0
     *
     * objectCountNetwork <= dbObjectCount
     * initTable() calls isRefetchNeeded() which returns true
     */
    @Test
    fun fetchFromNetworkAndSaveDbShouldManageErrorWhenGetListFromNetworkApiResultEmpty() = runBlockingTest {
        //override stubbing to return ApiResultEmpty when fetching a list from network
        whenever(characterApi.getCharacterList(any()))
                .thenReturn(dataFactory.produceApiResultEmpty())
        val result = characterInitManager.initTable("token", "character_test")
        //should be called only once
        verify(characterApi, times(1)).getCharacterList(any(), eq(true))
        verify(characterDao, times(1)).charactersCount()
        verify(sharedPref, times(1)).getInt(any(), any())
        verify(characterApi, times(1)).getCharacterList(any())
        //should never be called after
        verifyZeroInteractionMocks()
        //assert that result is StateResource with error (Message.EmptyResponse)
        assertThat(result).isEqualTo(StateResource(Status.Error, Message.EmptyResponse))
    }

    /**
     * via [setUp]:
     * [db count] characterDao.charactersCount() returns [characterCount] number
     * [network count] characterApi.getCharacterList(shallow = true) returns success result of 50
     * overrides [network list]
     * [refetch needed] sharedPref.getInt() returns 0
     *
     * objectCountNetwork <= dbObjectCount
     * initTable() calls isRefetchNeeded() which returns true
     */
    @Test
    fun fetchFromNetworkAndSaveDbShouldManageErrorWhenGetListFromNetworkApiResultNull() = runBlockingTest {
        //override stubbing to return null when fetching a list from network
        whenever(characterApi.getCharacterList(any())).thenReturn(null)
        val result = characterInitManager.initTable("token", "character_test")
        //should be called only once
        verify(characterApi, times(1)).getCharacterList(any(), eq(true))
        verify(characterDao, times(1)).charactersCount()
        verify(sharedPref, times(1)).getInt(any(), any())
        verify(characterApi, times(1)).getCharacterList(any())
        //should never be called after
        verifyZeroInteractionMocks()
        //assert that result is StateResource with error (Message.ServerError(0))
        assertThat(result).isEqualTo(StateResource(Status.Error, Message.ServerError(0)))
    }

    private fun verifyZeroInteractionMocks() {
        verifyZeroInteractions(characterDao)
        verifyZeroInteractions(characterApi)
        verifyZeroInteractions(sharedPref)
    }

}