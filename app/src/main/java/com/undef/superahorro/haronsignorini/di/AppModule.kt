package com.undef.superahorro.haronsignorini.di

import android.content.Context
import androidx.room.Room
import com.undef.superahorro.haronsignorini.data.SessionManager
import com.undef.superahorro.haronsignorini.data.local.PurchaseDao
import com.undef.superahorro.haronsignorini.data.local.SuperAhorroDatabase
import com.undef.superahorro.haronsignorini.data.remote.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    private const val BASE_URL = "https://jsonplaceholder.typicode.com/"

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SuperAhorroDatabase {
        return Room.databaseBuilder(
            context,
            SuperAhorroDatabase::class.java,
            "super_ahorro.db"
        )
            .addMigrations(SuperAhorroDatabase.MIGRATION_1_2)
            .addMigrations(SuperAhorroDatabase.MIGRATION_2_3)
            .build()
    }

    @Provides
    @Singleton
    fun provideSessionManager(@ApplicationContext context: Context): SessionManager {
        return SessionManager(context)
    }

    @Provides
    fun providePurchaseDao(database: SuperAhorroDatabase): PurchaseDao {
        return database.purchaseDao()
    }

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}
