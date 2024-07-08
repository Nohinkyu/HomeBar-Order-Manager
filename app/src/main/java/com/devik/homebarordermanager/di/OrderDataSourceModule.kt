package com.devik.homebarordermanager.di

import com.devik.homebarordermanager.data.source.database.PreferenceManager
import com.devik.homebarordermanager.data.source.remote.OrderDataSource
import com.devik.homebarordermanager.data.source.remote.OrderRemoteDataSource
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OrderDataSourceModule {

    @Provides
    @Singleton
    fun provideOrderDataSourceModule(
        supabaseClient: SupabaseClient,
        preferenceManager: PreferenceManager,
        moshi: Moshi
    ): OrderDataSource {
        return OrderRemoteDataSource(supabaseClient, preferenceManager,moshi)
    }
}