package bi.lan.lan.di

import bi.lan.lan.data.repository.BlinkLightningRepository
import bi.lan.lan.domain.repository.LightningRepository
import org.koin.core.qualifier.named
import org.koin.dsl.module

val repositoryModule = module {
    single<LightningRepository>(named("customer")) { BlinkLightningRepository(get()) }
    single<LightningRepository>(named("agent")) { BlinkLightningRepository(get()) }
}
