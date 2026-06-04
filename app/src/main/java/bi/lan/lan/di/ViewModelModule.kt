package bi.lan.lan.di

import bi.lan.lan.presentation.payment.PaymentViewModel
import bi.lan.lan.presentation.screens.agent.*
import bi.lan.lan.presentation.screens.customer.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val viewModelModule = module {
    // Payment ViewModel
    viewModel { PaymentViewModel(get(), get(), get()) }
    // Customer ViewModels
    viewModel { CustomerHomeViewModel(get(named("customer"))) }
    viewModel { CustomerInvoiceViewModel(get(named("customer"))) }
    viewModel { CustomerPaymentViewModel(get(named("customer")), get(named("agent"))) }
    viewModel { CustomerTransactionsViewModel(get(named("customer"))) }
    viewModel(named("customer")) { NodeInfoViewModel(get(named("customer"))) }

    // Agent ViewModels (use named("agent") for agent repo)
    viewModel { AgentHomeViewModel(get(named("agent"))) }
    viewModel { AgentDepositViewModel(get(named("agent"))) }
    viewModel { AgentWithdrawalViewModel(get(named("agent")), get(named("customer"))) }
    viewModel { AgentTransactionsViewModel(get(named("agent"))) }
    viewModel(named("agent")) { NodeInfoViewModel(get(named("agent"))) }
}
