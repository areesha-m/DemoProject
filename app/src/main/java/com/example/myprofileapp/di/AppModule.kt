package com.example.myprofileapp.di

import android.content.Context
import androidx.room.Room
import com.example.myprofileapp.data.AppDatabase
import com.example.myprofileapp.data.IProfileRepository
import com.example.myprofileapp.data.ProfileRepository
import com.example.myprofileapp.data.UserProfileDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // Dependencies live as long as the application
object DatabaseModule {

    @Provides
    @Singleton // Ensures a single instance of AppDatabase throughout the app
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "user_profile_db" // Existing database name
        ).build()
    }

    @Provides
    @Singleton // Ensures a single instance of UserProfileDao
    fun provideUserProfileDao(appDatabase: AppDatabase): UserProfileDao {
        return appDatabase.userProfileDao()
    }
}

@Module
@InstallIn(SingletonComponent::class) // Dependencies live as long as the application
object RepositoryModule {

    @Provides
    @Singleton // Ensures a single instance of IProfileRepository
    fun provideProfileRepository(userProfileDao: UserProfileDao): IProfileRepository {
        // UserProfileDao implements UserProfileReader and UserProfileWriter
        return ProfileRepository(
            profileReader = userProfileDao,
            profileWriter = userProfileDao
        )
    }
}