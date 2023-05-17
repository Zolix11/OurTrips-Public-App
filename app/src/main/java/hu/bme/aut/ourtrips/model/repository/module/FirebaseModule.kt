package hu.bme.aut.ourtrips.model.repository.module

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import hu.bme.aut.ourtrips.model.locationutils.AccountPreferences
import hu.bme.aut.ourtrips.model.repository.*
import hu.bme.aut.ourtrips.model.repository.impl.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun firebaseAuth(): FirebaseAuth = Firebase.auth

    @Provides
    @Singleton
    fun fireStore(): FirebaseFirestore = Firebase.firestore

    @Provides
    @Singleton
    fun firebaseStorage(): FirebaseStorage = Firebase.storage


    @Singleton
    @Provides
    fun provideAccountPreferences(@ApplicationContext context: Context): AccountPreferences {
        return AccountPreferences(context)
    }



    @Provides
    @Singleton
    fun provideAutRepository(auth: FirebaseAuth, fireStore: FirebaseFirestore,accountPreferences: AccountPreferences): AuthRepository {
        return AuthRepositoryImpl(auth, fireStore,accountPreferences)
    }

    @Provides
    @Singleton
    fun provideMapPostRepository(fireStore: FirebaseFirestore) : MapPostRepostiory{
        return MapPostRepostioryImpl(fireStore)
    }
    @Provides
    @Singleton
    fun provideAccountRepository(
        fireStore: FirebaseFirestore,
        accountPreferences: AccountPreferences,
        authRepository: AuthRepositoryImpl,
    ): AccountRepository {
        return AccountRepositoryImpl(fireStore, accountPreferences, authRepository)
    }

    @Provides
    @Singleton
    fun providePostRepostiory(fireStore: FirebaseFirestore, auth: FirebaseAuth): PostRepository {
        return PostRepositoryImpl(fireStore, auth)
    }

    @Provides
    @Singleton
    fun provideProfileStorageRepository(
        fireStorage: FirebaseStorage,
        accountPreferences: AccountPreferences
    ): StorageForProfileRepository {
        return StorageForProfileRepositoryImp(fireStorage, accountPreferences)
    }

    @Provides
    @Singleton
    fun providePostStorageRepository(fireStorage: FirebaseStorage): StorageForPostRepostiory {
        return StorageForPostRepositoryImpl(fireStorage)
    }

    @Provides
    @Singleton
    fun provideFriendRepository(
        fireStore: FirebaseFirestore,
    ): FriendRepostiory {
        return FriendRepositoryImpl(fireStore)
    }


}