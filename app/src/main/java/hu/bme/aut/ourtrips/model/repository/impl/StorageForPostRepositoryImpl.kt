package hu.bme.aut.ourtrips.model.repository.impl

import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import hu.bme.aut.ourtrips.model.repository.StorageForPostRepostiory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.UUID
import javax.inject.Inject

class StorageForPostRepositoryImpl@Inject constructor(
    private val firebaseStorage: FirebaseStorage) : StorageForPostRepostiory {

    private val currentPostFlow = MutableSharedFlow<Uri>()

    override val currentPostPicTureUri: Flow<Uri>
        get() = currentPostFlow.asSharedFlow()

    override suspend fun uploadPostPicture(uri: Uri) {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val uuid = UUID.randomUUID().toString()
        val filename = "$timestamp-$uuid.jpg"
        val uploadRef = storageReference().child(filename)
        val uploadTask = uploadRef.putFile(uri)

        uploadTask.addOnSuccessListener { taskSnapshot ->
            uploadRef.downloadUrl.addOnSuccessListener { newUri ->
                CoroutineScope(Dispatchers.IO).launch {
                    Log.d("URI a storage imp", newUri.toString())
                    currentPostFlow.emit(newUri)
                }
            }
        }
    }
    private fun storageReference() = firebaseStorage.reference.child("posts")
}