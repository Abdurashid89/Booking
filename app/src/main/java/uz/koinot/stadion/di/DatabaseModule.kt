package uz.koinot.stadion.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


//@Module
//@InstallIn(SingletonComponent::class)
//class DatabaseModule {
//
//    @Provides
//    @Singleton
//    fun getDatabase(@ApplicationContext context: Context): RouteDatabase=
//        Room.databaseBuilder(context,RouteDatabase::class.java,"block").build()
//
//    @Provides
//    @Singleton
//    fun getRouteDao(database: RouteDatabase) = database.getRouteDao()
//}
