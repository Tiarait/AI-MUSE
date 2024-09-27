package ua.tiar.aim.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android

object HttpClientProvider {
    fun provideHttpClient(): HttpClient {
        return HttpClient(Android) {

        }
    }
}