package com.shevaalex.android.rickmortydatabase.repository.init

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.shevaalex.android.rickmortydatabase.utils.networking.StateResource
import com.shevaalex.android.rickmortydatabase.utils.networking.Status
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InitRepository
@Inject
constructor(
        private val characterInit: CharacterInit,
        private val locationInit: LocationInit,
        private val episodeInit: EpisodeInit
) {

    fun getDbStateResource(token: String): LiveData<StateResource> = liveData {
        emit(StateResource(Status.Loading))
        val initFuns = listOf(
                characterInit.initCharacters(token),
                locationInit.initLocations(token),
                episodeInit.initEpisodes(token))
        Timber.i("init funs results: %s, %s, %s",
                initFuns[0].status.toString(),
                initFuns[1].status.toString(),
                initFuns[2].status.toString())
        //if there is at least one error whilst initiating db -> emit Status.Error
        initFuns.find { errorStateResource: StateResource ->
            errorStateResource.status == Status.Error
        }?.let {
            emit(it)
        }
        //if all results are success -> emit Status.Success
        val successStatus = { successStateResource: StateResource ->
            successStateResource.status == Status.Success
        }
        if (initFuns.all(successStatus)) {
            initFuns.find(successStatus)?.let {
                emit(it)
            }
        }
    }

}