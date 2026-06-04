package bi.lan.lan.di

import bi.lan.lan.domain.usecase.*
import org.koin.core.qualifier.named
import org.koin.dsl.module

val useCaseModule = module {
    factory { CreateInvoiceUseCase(get(named("agent"))) }
    factory { PayInvoiceUseCase(get(named("customer"))) }
    factory { GetBalanceUseCase(get(named("customer"))) }
}
