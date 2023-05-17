package hu.bme.aut.ourtrips.model.repository.impl

import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import hu.bme.aut.ourtrips.model.locationutils.AccountPreferences
import hu.bme.aut.ourtrips.model.repository.StorageForProfileRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class StorageForProfileRepositoryImp @Inject constructor(
    private val firebaseStorage: FirebaseStorage,
    private val accountPreferences: AccountPreferences
) : StorageForProfileRepository {

    private val authUID = accountPreferences.getFireStoreUser().userUID

    private val currentPictureFlow = MutableSharedFlow<Uri>()

    override val currentProfilePictureUri: Flow<Uri>
        get() = currentPictureFlow.asSharedFlow()
    override suspend fun upLoadProfileImage(uri: Uri){
        val fileID = authUID
        val uploadRef = storageReference().child("$fileID")

        val uploadTask = uploadRef.putFile(uri)
        uploadTask.addOnSuccessListener { taskSnapshot ->
            uploadRef.downloadUrl.addOnSuccessListener {newUri->
                CoroutineScope(Dispatchers.IO).launch {
                    Log.d("URI a storage imp",uri.toString())
                    currentPictureFlow.emit(newUri)
                }
            }
        }.addOnFailureListener { exception ->
            // Upload failed
        }.addOnProgressListener { taskSnapshot ->
            // Update progress
        }
    }

    private fun storageReference() = firebaseStorage.reference.child("users/profile_images")
}