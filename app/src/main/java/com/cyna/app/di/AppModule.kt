package com.cyna.app.di

import com.cyna.app.BuildConfig
import com.cyna.app.data.local.SessionManager
import com.cyna.app.data.remote.*
import com.cyna.app.data.repository.*
import com.cyna.app.data.util.*
import com.cyna.app.domain.repository.*
import com.cyna.app.mock.registry.buildMockEngine
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.okhttp.OkHttp
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

val appModule = module {

    single { SessionManager(androidContext()) }
    single { VibrationHelper(androidContext()) }

    /**
     * Sélection du moteur HTTP selon l'environnement :
     * - MOCK_API=true → MockEngine (mémoire, pas de réseau).
     * - DEBUG=true    → OkHttp avec SSL bypass (émulateur + certificat auto-signé).
     * - Production    → CIO.
     */
    single<HttpClientEngine> {
        when {
            BuildConfig.MOCK_API -> buildMockEngine(delayMs = 400L)
            BuildConfig.DEBUG -> {
                val tm = trustAllTrustManager()
                val sslContext = SSLContext.getInstance("TLS").apply {
                    init(null, arrayOf(tm), SecureRandom())
                }
                OkHttp.create {
                    preconfigured = OkHttpClient.Builder()
                        .sslSocketFactory(sslContext.socketFactory, tm)
                        .hostnameVerifier { _, _ -> true }
                        .build()
                }
            }
            else -> CIO.create()
        }
    }

    single<HttpClient> {
        createHttpClient(
            baseUrl         = BuildConfig.BASE_URL,
            engine          = get(),
            vibrationHelper = get(),
            sessionManager  = get()
        )
    }

    // ── API layer ─────────────────────────────────────────────────────────────
    single { AuthAPI(get()) }
    single { UserAPI(get()) }
    single { OrderHistoryAPI(get()) }
    single { TwoFactorAPI(get()) }

    // ── Repository layer ──────────────────────────────────────────────────────
    single<AuthRepository>         { AuthRepositoryImpl(get(), get(), get()) }
    single<UserRepository>         { UserRepositoryImpl(get()) }
    single<OrderHistoryRepository> { OrderHistoryRepositoryImpl(get()) }
    single<TwoFactorRepository>    { TwoFactorRepositoryImpl(get()) }
}

private fun trustAllTrustManager(): X509TrustManager = object : X509TrustManager {
    override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) = Unit
    override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) = Unit
    override fun getAcceptedIssuers(): Array<X509Certificate> = emptyArray()
}