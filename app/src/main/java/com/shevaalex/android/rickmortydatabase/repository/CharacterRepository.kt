package com.shevaalex.android.rickmortydatabase.repository

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.shevaalex.android.rickmortydatabase.models.character.CharacterModel
import com.shevaalex.android.rickmortydatabase.models.character.RecentQuery
import com.shevaalex.android.rickmortydatabase.source.database.CharacterModelDao
import com.shevaalex.android.rickmortydatabase.source.database.RecentQueryDao
import com.shevaalex.android.rickmortydatabase.utils.Constants
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CharacterRepository
@Inject
constructor(
        private val characterDao: CharacterModelDao,
        private val recentQueryDao: RecentQueryDao
) {

    fun getAllCharacters(): LiveData<PagedList<CharacterModel>> =
            characterDao.getAllCharacters().toLiveData(50)

    fun searchOrFilterCharacters(
            query: String,
            filterMap: Map<String, Pair<Boolean, String?>>,
            showsAll: Boolean
    ): LiveData<PagedList<CharacterModel>> {
        // perform a search without filtering
        return if (query.isNotBlank() && showsAll) {
            searchCharacters(query)
        }
        // perform a search with filtering or perform just filtering
        else {
            searchAndFilter(query, filterMap)
        }
    }

    private fun searchCharacters(query: String): LiveData<PagedList<CharacterModel>> {
        //if query contains more than 1 word -> rearrange the query
        if (query.isNotBlank() && query.contains(" ")) {
            val rearrangedQuery = query.substringAfter(" ").trim()
                    .plus(" ")
                    .plus(query.substringBefore(" ").trim())
            Timber.v("Rearranged query: %s", rearrangedQuery)
            return characterDao.searchCharacters(query, rearrangedQuery).toLiveData(50)
        }
        return characterDao.searchCharacters(query).toLiveData(50)
    }

    private fun searchAndFilter(
            query: String,
            filterMap: Map<String, Pair<Boolean, String?>>
    ): LiveData<PagedList<CharacterModel>> {
        val statusesWithNulls = listOf(
                filterMap[Constants.KEY_MAP_FILTER_STATUS_ALIVE_F]?.second,
                filterMap[Constants.KEY_MAP_FILTER_STATUS_ALIVE_M]?.second,
                filterMap[Constants.KEY_MAP_FILTER_STATUS_DEAD_F]?.second,
                filterMap[Constants.KEY_MAP_FILTER_STATUS_DEAD_M]?.second,
                filterMap[Constants.KEY_MAP_FILTER_STATUS_UNKNOWN]?.second
        )
        val statuses = statusesWithNulls.filterNotNull()
        val gendersWithNulls = listOf(
                filterMap[Constants.KEY_MAP_FILTER_GENDER_FEMALE]?.second,
                filterMap[Constants.KEY_MAP_FILTER_GENDER_MALE]?.second,
                filterMap[Constants.KEY_MAP_FILTER_GENDER_GENDERLESS]?.second,
                filterMap[Constants.KEY_MAP_FILTER_GENDER_UNKNOWN]?.second
        )
        val genders = gendersWithNulls.filterNotNull()
        val speciesWithNulls = listOf(
                filterMap[Constants.KEY_MAP_FILTER_SPECIES_HUMAN]?.second,
                filterMap[Constants.KEY_MAP_FILTER_SPECIES_HUMANOID]?.second,
                filterMap[Constants.KEY_MAP_FILTER_SPECIES_ALIEN]?.second,
                filterMap[Constants.KEY_MAP_FILTER_SPECIES_ANIMAL]?.second,
                filterMap[Constants.KEY_MAP_FILTER_SPECIES_ROBOT]?.second,
                filterMap[Constants.KEY_MAP_FILTER_SPECIES_POOPY]?.second,
                filterMap[Constants.KEY_MAP_FILTER_SPECIES_CRONENBERG]?.second,
                filterMap[Constants.KEY_MAP_FILTER_SPECIES_MYTH]?.second
        )
        val species = speciesWithNulls.filterNotNull()
        Timber.i("statuses: %s \n genders: %s \n species: %s", statuses, genders, species)
        //filter only
        if (query.isBlank()) {
            filterMap[Constants.KEY_MAP_FILTER_SPECIES_ALL]?.first?.let {
                if (it) //perform a filtering without filtering by species (species == ALL)
                    return characterDao
                            .getFilteredNoSpeciesCharacters(statuses, genders)
                            .toLiveData(50)
            }
            //perform a filtering with species filtered
            return characterDao
                    .getFilteredCharacters(statuses, genders, species)
                    .toLiveData(50)
        }
        //search and filter
        else {
            filterMap[Constants.KEY_MAP_FILTER_SPECIES_ALL]?.first?.let {
                if (it) //perform a search + filtering without filtering by species (species == ALL)
                    return characterDao
                            .searchAndFilterNoSpeciesCharacters(query, statuses, genders)
                            .toLiveData(50)
            }
            //perform a filtering with species filtered
            return characterDao
                    .searchAndFilterCharacters(query, statuses, genders, species)
                    .toLiveData(50)
        }
    }

    suspend fun saveSearchQuery(query: String) {
        recentQueryDao.insertAndDeleteInTransaction(RecentQuery(
                id = 0,
                name = query,
                RecentQuery.Type.CHARACTER.type
        ))
    }

    fun getSuggestionsNames(): Flow<List<String>> {
        return characterDao.getSuggestionsNames()
    }

    fun getRecentQueries(): Flow<List<String>> {
        return recentQueryDao.getRecentQueries(RecentQuery.Type.CHARACTER.type)
    }

}