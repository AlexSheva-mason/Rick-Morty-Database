package com.shevaalex.android.rickmortydatabase.source.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.shevaalex.android.rickmortydatabase.BaseTest
import com.shevaalex.android.rickmortydatabase.assertEmittedValuesEquals
import com.shevaalex.android.rickmortydatabase.di.TestAppComponent
import com.shevaalex.android.rickmortydatabase.models.RecentQuery
import com.shevaalex.android.rickmortydatabase.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.Executors
import javax.inject.Inject

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class RecentQueryDaoTest : BaseTest() {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var recentQueryDataFactory: RecentQueryDataFactory

    @Inject
    lateinit var recentQueryDao: RecentQueryDao

    init {
        injectTest()
    }

    @Test
    fun saveQuery() = runBlockingTest {
        val expectedRecent = insertRecent(1)
        val fetchedRecent = recentQueryDao.selectAllRecent()
        //test fails if results contain > 1 item
        if (fetchedRecent.size > 1) {
            Assert.fail("results should contain only 1 item," +
                    " fetchedList.size = [${fetchedRecent.size}]")
        }
        assertThat(fetchedRecent).containsExactly(expectedRecent)
    }

    @Test
    fun deleteQuery() = runBlockingTest {
        val testRecent = insertRecent(1)
        recentQueryDao.deleteQuery(testRecent)
        val fetchedList = recentQueryDao.selectAllRecent()
        assertThat(fetchedList).isEmpty()
    }

    @Test
    fun recentCount() = runBlockingTest {
        val testList = insertRecentList(10)
        val expectedList = testList.filter { it.type == RecentQuery.Type.CHARACTER.type }
        val fetchedCount = recentQueryDao.recentCount(RecentQuery.Type.CHARACTER.type)
        assertThat(fetchedCount).isEqualTo(expectedList.size)
    }

    @Test
    fun getOldestSavedQuery() = runBlockingTest {
        val testList = insertRecentList(10)
        val expectedValue = testList.first { it.type == RecentQuery.Type.LOCATION.type }
        val fetchedValue = recentQueryDao.getOldestSavedQuery(RecentQuery.Type.LOCATION.type)
        assertThat(fetchedValue).isEqualTo(expectedValue)
    }

    @Test
    fun getRecentQueries() = runBlockingTest {
        val expectedList = insertRecentList(10)
                .filter { it.type == RecentQuery.Type.EPISODE.type }
                .sortedByDescending { it.id }
                .map { it.name }
        val fetchedList = recentQueryDao.getRecentQueries(RecentQuery.Type.EPISODE.type)
        val testCollector = fetchedList.test(this)
        testCollector.assertEmittedValuesEquals(expectedList)
        testCollector.cancel()
    }

    @Test
    fun deleteQueryWithName() = runBlockingTest {
        val randomRecent = insertRecentList(10).random()
        recentQueryDao.deleteQuery(randomRecent.name, randomRecent.type)
        val fetchedList = recentQueryDao.selectAllRecent()
        assertThat(fetchedList).doesNotContain(randomRecent)
    }

    @Test
    fun insertAndDeleteInTransaction(): Unit = runBlocking {
        //build a separate database for this test (with using setTransactionExecutor)
        val database = Room
                .inMemoryDatabaseBuilder(
                        application,
                        RickMortyDatabase::class.java
                )
                .setTransactionExecutor(Executors.newSingleThreadExecutor())
                .allowMainThreadQueries()
                .build()
        val dao = database.recentQueryDao
        //insert data with specific type in transaction
        val testList = mutableListOf<RecentQuery>()
        for (i in 1..300) {
            val recent = recentQueryDataFactory.produceRecentWithSpecificType(
                    i,
                    RecentQuery.Type.EPISODE.type
            )
            testList.add(recent)
            dao.insertAndDeleteInTransaction(recent)
        }
        val expectedList = testList.subList(290, 300)
        val fetchedList = dao.selectAllRecent()
        //the result list should contain only last 10 values from the testList
        assertThat(fetchedList).isEqualTo(expectedList)
    }

    private suspend fun insertRecentList(numberOfRecent: Int): List<RecentQuery> {
        val testList = mutableListOf<RecentQuery>()
        for (i in 1..numberOfRecent) {
            testList.add(insertRecent(i))
        }
        return testList
    }

    private suspend fun insertRecent(id: Int = 0): RecentQuery {
        return with(recentQueryDataFactory.produceRecent(id)) {
            recentQueryDao.saveQuery(this)
            this
        }
    }

    override fun injectTest() {
        (application.appComponent as TestAppComponent).inject(this)
    }

}