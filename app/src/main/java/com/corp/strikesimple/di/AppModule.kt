package com.corp.strikesimple.di

import android.content.Context
import androidx.room.Room
import com.corp.data.db.FrameDao
import com.corp.data.db.GameDao
import com.corp.data.db.GameDatabase
import com.corp.data.service.GamesRepo
import com.corp.strikesimple.util.GameUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideGameDatabase(
        @ApplicationContext app: Context,
    ) = Room.databaseBuilder(
        app,
        GameDatabase::class.java,
        "game_database"
    ).build()

    @Singleton
    @Provides
    fun provideGameDao(db: GameDatabase) = db.gameDao()

    @Singleton
    @Provides
    fun provideFrameDao(db: GameDatabase) = db.frameDao()

    @Provides
    @Singleton
    fun provideGamesRepo(
        gameDao: GameDao,
        frameDao: FrameDao,
    ): GamesRepo = GamesRepo(gameDao, frameDao)

    @Provides
    @Singleton
    fun provideGameUtil(): GameUtil = GameUtil()
}