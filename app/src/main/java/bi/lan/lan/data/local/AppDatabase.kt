package bi.lan.lan.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [RemittanceEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun remittanceDao(): RemittanceDao
}
