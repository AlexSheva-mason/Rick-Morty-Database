package com.shevaalex.android.rickmortydatabase.repository.character

import androidx.paging.DataSource
import com.shevaalex.android.rickmortydatabase.models.character.CharacterModel
import com.shevaalex.android.rickmortydatabase.models.RecentQuery
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
        private val recentQueryDao: RecentQueryDao,
) {

    fun getAllCharacters(): DataSource.Factory<Int, CharacterModel> =
            characterDao.getAllCharacters()

    fun searchOrFilterCharacters(
            query: String,
            filterMap: Map<String, Pair<Boolean, String?>>,
            showsAll: Boolean
    ): DataSource.Factory<Int, CharacterModel> {
        // perform a search without filtering
        return if (query.isNotBlank() && showsAll) {
            searchCharacters(query)
        }
        // perform a search with filtering or perform just filtering
        else {
            searchAndFilter(query, filterMap)
        }
    }

    private fun searchCharacters(query: String): DataSource.Factory<Int, CharacterModel> {
        //if query contains more than 1 word -> rearrange the query
        if (query.isNotBlank() && query.contains(" ")) {
            val rearrangedQuery = query.substringAfter(" ").trim()
                    .plus(" ")
                    .plus(query.substringBefore(" ").trim())
            Timber.v("Rearranged query: %s", rearrangedQuery)
            return characterDao.searchCharacters(query, rearrangedQuery)
        }
        return characterDao.searchCharacters(query)
    }

    private fun searchAndFilter(
            query: String,
            filterMap: Map<String, Pair<Boolean, String?>>
    ): DataSource.Factory<Int, CharacterModel> {
        val statuses = getStatusList(filterMap)
        val genders = getGenderList(filterMap)
        val species = getSpeciesList(filterMap)
        Timber.i("statuses: %s \n genders: %s \n species: %s", statuses, genders, species)
        //filter only
        if (query.isBlank()) {
            filterMap[Constants.KEY_MAP_FILTER_SPECIES_ALL]?.first?.let {
                if (it) //perform a filtering without filtering by species (species == ALL)
                    return characterDao
                            .getFilteredNoSpeciesCharacters(statuses, genders)
            }
            //perform a filtering with species filtered
            return characterDao
                    .getFilteredCharacters(statuses, genders, species)
        }
        //search and filter
        else {
            filterMap[Constants.KEY_MAP_FILTER_SPECIES_ALL]?.first?.let {
                if (it) //perform a search + filtering without filtering by species (species == ALL)
                    return characterDao
                            .searchAndFilterNoSpeciesCharacters(query, statuses, genders)
            }
            //perform a filtering with species filtered
            return characterDao
                    .searchAndFilterCharacters(query, statuses, genders, species)
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

    fun getSuggestionsNamesFiltered(filterMap: Map<String, Pair<Boolean, String?>>): Flow<List<String>> {
        val statuses = getStatusList(filterMap)
        val genders = getGenderList(filterMap)
        val species = getSpeciesList(filterMap)
        //check if key KEY_MAP_FILTER_SPECIES_ALL == true
        filterMap[Constants.KEY_MAP_FILTER_SPECIES_ALL]?.first?.let {
            //perform a filtering without filtering by species (species == ALL)
            if (it)
                return characterDao.getSuggestionsNamesFiltered(statuses, genders)
        }
        //perform a filtering with species filtered
        return characterDao.getSuggestionsNamesFiltered(statuses, genders, species)
    }

    fun getRecentQueries(): Flow<List<String>> {
        return recentQueryDao.getRecentQueries(RecentQuery.Type.CHARACTER.type)
    }

    private fun getStatusList(filterMap: Map<String, Pair<Boolean, String?>>): List<String> {
        val statusesWithNulls = listOf(
                filterMap[Constants.KEY_MAP_FILTER_STATUS_ALIVE_F]?.second,
                filterMap[Constants.KEY_MAP_FILTER_STATUS_ALIVE_M]?.second,
                filterMap[Constants.KEY_MAP_FILTER_STATUS_DEAD_F]?.second,
                filterMap[Constants.KEY_MAP_FILTER_STATUS_DEAD_M]?.second,
                filterMap[Constants.KEY_MAP_FILTER_STATUS_UNKNOWN]?.second
        )
        return statusesWithNulls.filterNotNull()
    }

    private fun getGenderList(filterMap: Map<String, Pair<Boolean, String?>>): List<String> {
        val gendersWithNulls = listOf(
                filterMap[Constants.KEY_MAP_FILTER_GENDER_FEMALE]?.second,
                filterMap[Constants.KEY_MAP_FILTER_GENDER_MALE]?.second,
                filterMap[Constants.KEY_MAP_FILTER_GENDER_GENDERLESS]?.second,
                filterMap[Constants.KEY_MAP_FILTER_GENDER_UNKNOWN]?.second
        )
        return gendersWithNulls.filterNotNull()
    }

    private fun getSpeciesList(filterMap: Map<String, Pair<Boolean, String?>>): List<String> {
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
        return speciesWithNulls.filterNotNull()
    }

}