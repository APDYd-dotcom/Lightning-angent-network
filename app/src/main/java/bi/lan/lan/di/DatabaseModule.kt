package bi.lan.lan.di

import androidx.room.Room
import bi.lan.lan.data.local.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "lan_database"
        ).fallbackToDestructiveMigration().build()
    }
    single { get<AppDatabase>().remittanceDao() }
}
