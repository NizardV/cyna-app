package com.cyna.app.di

import com.cyna.app.data.remote.LoginAPI
import com.cyna.app.data.repository.*
import com.cyna.app.domain.repository.*
import com.cyna.app.data.remote.createHttpClient
import org.koin.dsl.module
import io.ktor.client.HttpClient

private const val RMAPI_URL = "http://98.66.234.231:8000/api/"

/**
 * Koin dependency injection module for the application.
 *
 * This module defines all the dependencies that will be available for injection
 * throughout the application. It configures how dependencies are created and
 * their lifecycle (singleton, factory, etc.).
 *
 * ## Usage
 * This module should be loaded when initializing the Koin container in the application.
 *
 * ## Dependencies Included
 * - [LoginRepository] as singleton using [LoginRepositoryImpl] implementation
 *
 * @see org.koin.dsl.module
 * @see single
 * @see factory
 *
 * @sample
 * // Initialize Koin with this module
 * startKoin {
 *     modules(appModule)
 * }
 */
val appModule = module {
    single<HttpClient> {
        createHttpClient(
            baseUrl = RMAPI_URL
        )
    }

    // Single instance (singleton) of LoginService
    single { LoginAPI(get()) }
    single<LoginRepository> { LoginRepositoryImpl(get()) }


    // Add other dependencies here as needed
    // single { YourRepository() }
    // factory { YourUseCase() } // new instance each time
}