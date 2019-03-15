package com.erm.artists.ui.details

import androidx.lifecycle.Observer
import com.erm.artists.data.model.entity.Artist
import com.erm.artists.data.repository.base.Resource
import com.erm.artists.data.repository.impl.BandsInTownArtistRepository
import com.erm.artists.ui.base.StatefulResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import kotlin.coroutines.CoroutineContext

//TODO WIP
class DetailsActivityViewModelImplTest : CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    private lateinit var bandsInTownArtistRepository: BandsInTownArtistRepository
    private lateinit var detailsActivityViewModelImpl: DetailsActivityViewModelImpl

    @Before
    fun setup() = runBlocking {

        bandsInTownArtistRepository = Mockito.mock(BandsInTownArtistRepository::class.java)
        Mockito.`when`(bandsInTownArtistRepository.getArtistByName("FirstArtist")).thenReturn(async {
            Resource<Artist?>().apply {
                data = Artist().apply {
                    id = 1
                    name = "FirstArtist"
                }
            }
        })

        detailsActivityViewModelImpl = DetailsActivityViewModelImpl(bandsInTownArtistRepository)

    }

    //TODO: Attempt to observe different states
    @Test
    fun `Observe Artist Fetch Is Successful`() = runBlocking {
        val artistDetailsObserver: Observer<StatefulResource<Artist?>> =
            Mockito.mock(Observer::class.java) as Observer<StatefulResource<Artist?>>
        detailsActivityViewModelImpl.artistDetails.observeForever(artistDetailsObserver)
        detailsActivityViewModelImpl.getArtistAndEventDetails("FirstArtist")
        Mockito.verify(artistDetailsObserver).onChanged(StatefulResource.loading())
    }

}