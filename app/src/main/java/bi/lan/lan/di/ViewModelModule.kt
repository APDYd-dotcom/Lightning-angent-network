package bi.lan.lan.di

import bi.lan.lan.presentation.screens.agent.*
import bi.lan.lan.presentation.screens.customer.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val viewModelModule = module {
    // Customer ViewModels
    viewModel { CustomerHomeViewModel(get(named("customer"))) }
    viewModel { CustomerInvoiceViewModel(get(named("customer"))) }
    viewModel { CustomerPaymentViewModel(get(named("customer"))) }
    viewModel { CustomerTransactionsViewModel(get(named("customer"))) }
    viewModel { NodeInfoViewModel(get(named("customer"))) }

    // Agent ViewModels (use named("agent") for agent repo)
    viewModel { AgentHomeViewModel(get(named("agent"))) }
    viewModel { AgentDepositViewModel(get(named("agent"))) }
    viewModel { AgentWithdrawalViewModel(get(named("agent"))) }
    viewModel { AgentTransactionsViewModel(get(named("agent"))) }
}
