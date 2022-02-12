package com.asterisk.grocerylist.di

import android.content.Context
import androidx.room.Room
import com.asterisk.grocerylist.data.ItemDao
import com.asterisk.grocerylist.data.ItemDatabase
import com.asterisk.grocerylist.data.ItemRepository
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
    fun provideDatabase(
        @ApplicationContext context: Context
    ): ItemDatabase =
        Room.databaseBuilder(context, ItemDatabase::class.java, "item_db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideDatabaseDao(db: ItemDatabase) = db.getItemDao()


    @Provides
    @Singleton
    fun provideItemRepository(itemDao: ItemDao): ItemRepository =
        ItemRepository(itemDao)

}