package bi.lan.lan

import android.app.Application
import bi.lan.lan.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class LightningAgentApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        startKoin {
            androidLogger()
            androidContext(this@LightningAgentApp)
            modules(appModules)
        }
    }
}
