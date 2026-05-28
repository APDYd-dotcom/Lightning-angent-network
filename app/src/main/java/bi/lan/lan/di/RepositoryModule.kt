package bi.lan.lan.di

import bi.lan.lan.data.repository.AgentLightningRepository
import bi.lan.lan.data.repository.CustomerLightningRepository
import bi.lan.lan.domain.repository.LightningRepository
import org.koin.core.qualifier.named
import org.koin.dsl.module

val repositoryModule = module {
    single<LightningRepository>(named("customer")) { CustomerLightningRepository(get()) }
    single<LightningRepository>(named("agent")) { AgentLightningRepository(get()) }
}
