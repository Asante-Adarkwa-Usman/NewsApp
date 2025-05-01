package com.ghost.newsapp.core.di


import androidx.room.Room
import com.ghost.newsapp.core.data.NewsRepositoryImpl
import com.ghost.newsapp.core.data.local.ArticleDatabase
import com.ghost.newsapp.core.domain.NewsRepository
import com.ghost.newsapp.core.presentation.vm.NewsViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.endpoint
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val coreModule = module {

    //Provides NewsRepository
    single<NewsRepository> { NewsRepositoryImpl(get(), get()) }

    //Provides CoroutineDispatcher
    single<CoroutineDispatcher> { Dispatchers.IO }

    //Provides the NewsViewModel
    viewModel { NewsViewModel(get(), get()) }

    //Provides Room
    single {
        Room.databaseBuilder(
            androidApplication(),
            ArticleDatabase::class.java,
            "article_db.db"
        ).build()
    }

    //Provides Article Db
    single {
        get<ArticleDatabase>().dao
    }

    //Provides Ktor
    single {
        HttpClient(CIO) {
            expectSuccess = true

            engine {
                endpoint {
                    keepAliveTime = 5000
                    connectTimeout = 5000
                    connectAttempts = 3
                }
            }

            install(ContentNegotiation) {
                json(
                    Json {
                        prettyPrint = true
                        isLenient = true
                        ignoreUnknownKeys = true
                    }
                )
            }

            install(DefaultRequest) {
                header(HttpHeaders.ContentType, ContentType.Application.Json)
            }

            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        println(message)
                    }
                }
                level = LogLevel.ALL
            }
        }
    }
}