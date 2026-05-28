package bi.lan.lan.di

import bi.lan.lan.presentation.screens.agent.AgentDashboardViewModel
import bi.lan.lan.presentation.screens.auth.LoginViewModel
import bi.lan.lan.presentation.screens.auth.OtpViewModel
import bi.lan.lan.presentation.screens.deposit.DepositViewModel
import bi.lan.lan.presentation.screens.home.HomeViewModel
import bi.lan.lan.presentation.screens.home.NearbyAgentsViewModel
import bi.lan.lan.presentation.screens.transactions.TransactionHistoryViewModel
import bi.lan.lan.presentation.screens.withdraw.WithdrawViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { LoginViewModel(get()) }
    viewModel { OtpViewModel(get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { DepositViewModel(get()) }
    viewModel { WithdrawViewModel(get()) }
    viewModel { AgentDashboardViewModel(get()) }
    viewModel { TransactionHistoryViewModel(get()) }
    viewModel { NearbyAgentsViewModel(get()) }
}
