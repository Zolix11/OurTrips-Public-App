package hu.bme.aut.ourtrips.model.repository

import android.net.Uri
import kotlinx.coroutines.flow.Flow

interface StorageForProfileRepository {
    val currentProfilePictureUri : Flow<Uri>

    suspend fun upLoadProfileImage(uri : Uri)
}