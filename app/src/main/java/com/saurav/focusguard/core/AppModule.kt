package com.saurav.focusguard.core

import android.content.Context
import androidx.room.Room
import com.saurav.focusguard.data.db.AnalyticsDao
import com.saurav.focusguard.data.db.FocusGuardDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): FocusGuardDatabase {
        return Room.databaseBuilder(
            context,
            FocusGuardDatabase::class.java,
            "focusguard_analytics.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideAnalyticsDao(database: FocusGuardDatabase): AnalyticsDao {
        return database.analyticsDao()
    }
}
