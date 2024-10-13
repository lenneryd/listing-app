package com.tim.listing.app.domain

import com.tim.listing.app.data.repo.ScooterRepository
import com.tim.listing.app.domain.usecase.ScooterUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object ClockRepositoryModule {

    @Provides
    fun scooterUseCase(repo: ScooterRepository): ScooterUseCase = ScooterUseCase(repo)
}