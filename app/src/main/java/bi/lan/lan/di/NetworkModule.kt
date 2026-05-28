package bi.lan.lan.di

import bi.lan.lan.data.api.ApiClient
import bi.lan.lan.data.api.LightningApiService
import org.koin.dsl.module

val networkModule = module {
    single { ApiClient.client }
    single { LightningApiService(get()) }
}
