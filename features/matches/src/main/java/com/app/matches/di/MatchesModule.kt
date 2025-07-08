package com.app.matches.di

import androidx.room.Room
import com.app.matches.data.MatchRepositoryImpl
import com.app.matches.data.local.MatchDatabase
import com.app.matches.domain.repository.MatchRepository
import com.app.matches.domain.usecase.GetMatchesUseCase
import com.app.matches.ui.MatchesViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val matchesModule = module {
    single<MatchRepository> { MatchRepositoryImpl(get(),get()) }
    single { GetMatchesUseCase(get()) }
    single {
        Room.databaseBuilder(
            androidContext(),
            MatchDatabase::class.java,
            "match_db"
        ).build()
    }

    single { get<MatchDatabase>().matchDao() }

    viewModel { MatchesViewModel(get()) }
}