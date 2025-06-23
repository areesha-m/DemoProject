package com.example.myprofileapp.di

import android.content.Context
import androidx.room.Room
import com.example.myprofileapp.data.AdListingDao
import com.example.myprofileapp.data.AppDatabase
import com.example.myprofileapp.data.IListingRepository
import com.example.myprofileapp.data.IProfileRepository
import com.example.myprofileapp.data.ListingRepository
import com.example.myprofileapp.data.ProfileRepository
import com.example.myprofileapp.data.UserProfileDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import dagger.hilt.android.components.ViewModelComponent
import com.example.myprofileapp.utils.NetworkChecker


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "user_profile_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideUserProfileDao(appDatabase: AppDatabase): UserProfileDao {
        return appDatabase.userProfileDao()
    }

    @Provides
    @Singleton
    fun provideAdListingDao(appDatabase: AppDatabase): AdListingDao {
        return appDatabase.adListingDao()
    }
}

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideProfileRepository(userProfileDao: UserProfileDao): IProfileRepository {

        return ProfileRepository(
            profileReader = userProfileDao,
            profileWriter = userProfileDao
        )
    }

    @Provides
    @Singleton
    fun provideListingRepository(adListingDao: AdListingDao): IListingRepository {
        return ListingRepository(adListingDao)
    }
}

@Module
@InstallIn(ViewModelComponent::class)
object AppModule {

    @Provides
    fun provideNetworkChecker(
        @ApplicationContext context: Context
    ): NetworkChecker {
        return NetworkChecker(context)
    }
}

