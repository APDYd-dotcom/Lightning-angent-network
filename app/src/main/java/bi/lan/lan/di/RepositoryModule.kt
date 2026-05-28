package bi.lan.lan.di

import bi.lan.lan.data.repository.*
import bi.lan.lan.domain.repository.*
import org.koin.dsl.module

val repositoryModule = module {
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single<WalletRepository> { WalletRepositoryImpl(get()) }
    single<AgentRepository> { AgentRepositoryImpl(get()) }
    single<DepositRepository> { DepositRepositoryImpl(get()) }
    single<WithdrawRepository> { WithdrawRepositoryImpl(get()) }
    single<TransactionRepository> { TransactionRepositoryImpl(get()) }
}
