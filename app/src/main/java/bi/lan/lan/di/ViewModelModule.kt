package bi.lan.lan.di

import bi.lan.lan.presentation.payment.PaymentViewModel
import bi.lan.lan.presentation.payment.PaymentDetailViewModel
import bi.lan.lan.presentation.screens.agent.*
import bi.lan.lan.presentation.screens.customer.*
import bi.lan.lan.presentation.remittance.*
import bi.lan.lan.presentation.history.HistoryViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val viewModelModule = module {
    // Payment ViewModel
    viewModel { PaymentViewModel(get(), get(), get()) }
    viewModel { PaymentDetailViewModel(get(named("customer"))) }
    // Customer ViewModels
    viewModel { CustomerHomeViewModel(get(named("customer"))) }
    viewModel { CustomerInvoiceViewModel(get(named("customer"))) }
    viewModel { CustomerPaymentViewModel(get(named("customer")), get(named("agent")), get()) }
    viewModel { CustomerTransactionsViewModel(get(named("customer"))) }
    viewModel(named("customer")) { NodeInfoViewModel(get(named("customer")), get()) }

    // Agent ViewModels (use named("agent") for agent repo)
    viewModel { AgentHomeViewModel(get(named("agent"))) }
    viewModel { AgentDepositViewModel(get(named("agent"))) }
    viewModel { AgentWithdrawalViewModel(get(named("agent")), get(named("customer"))) }
    viewModel { AgentTransactionsViewModel(get(named("agent")), get()) }
    viewModel(named("agent")) { NodeInfoViewModel(get(named("agent")), get()) }

    // Remittance ViewModels
    viewModel { RemittanceViewModel(get()) }
    viewModel { HistoryViewModel(get()) }
    viewModel { DashboardViewModel(get(), get(named("agent"))) }
    viewModel { AnalyticsViewModel(get()) }
    viewModel { ReceiptViewModel(get()) }
}
