package com.tim.listing.app.data

import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.tim.listing.app.R
import com.tim.listing.app.data.api.ScooterApi
import com.tim.listing.app.data.repo.ScooterRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.Cache
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OkHttpModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(@ApplicationContext context: Context): OkHttpClient =
        OkHttpClient.Builder()
            // Can add Logger interceptor here.
            .cache(Cache(context.cacheDir, DEFAULT_CACHE_SIZE))
            .build()

    private const val DEFAULT_CACHE_SIZE = 34L * 1024L
}

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    @OptIn(ExperimentalSerializationApi::class)
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        explicitNulls = false
    }

    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    @Singleton
    fun provideRetrofit(
        @ApplicationContext context: Context,
        okHttpClient: OkHttpClient
    ): Retrofit = Retrofit.Builder()
        .baseUrl(context.getString(R.string.base_url))
        .addConverterFactory(
            json.asConverterFactory("application/json".toMediaType())
        )
        .client(okHttpClient)
        .build()
}

@Module
@InstallIn(SingletonComponent::class)
object ScooterApi {

    @Provides
    @Singleton
    fun provideWeatherApi(retrofit: Retrofit): ScooterApi = retrofit.create(ScooterApi::class.java)
}

@Module
@InstallIn(ViewModelComponent::class)
object ClockRepositoryModule {

    @Provides
    fun scooterRepository(api: ScooterApi): ScooterRepository = ScooterRepository(api)
}