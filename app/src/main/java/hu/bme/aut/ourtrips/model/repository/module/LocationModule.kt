package hu.bme.aut.ourtrips.model.repository.module

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import hu.bme.aut.ourtrips.model.locationutils.AppPreferences
import hu.bme.aut.ourtrips.model.locationutils.LocationSaver
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocationModule {
    @Singleton
    @Provides
    fun provideAppPreferences(@ApplicationContext context: Context): AppPreferences {
        return AppPreferences(context)
    }

    @Provides
    @Singleton
    fun provideFusedLocationProviderClient(@ApplicationContext context: Context): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }

    @Provides
    @Singleton
    fun provideLocationLocationSaver(appPreferences: AppPreferences, fusedLocationProviderClient: FusedLocationProviderClient) : LocationSaver{
        return  LocationSaver(appPreferences,fusedLocationProviderClient)
    }

}

