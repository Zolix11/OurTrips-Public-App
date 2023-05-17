package hu.bme.aut.ourtrips.model.repository

import android.net.Uri
import kotlinx.coroutines.flow.Flow

interface StorageForPostRepostiory {
    val currentPostPicTureUri : Flow<Uri>

    suspend fun uploadPostPicture(uri: Uri)
}