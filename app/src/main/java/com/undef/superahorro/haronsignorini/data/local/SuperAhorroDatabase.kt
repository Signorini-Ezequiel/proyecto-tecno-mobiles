package com.undef.superahorro.haronsignorini.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        PurchaseEntity::class,
        ProductEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class SuperAhorroDatabase : RoomDatabase() {
    abstract fun purchaseDao(): PurchaseDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE purchases ADD COLUMN time TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE products ADD COLUMN code TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE products ADD COLUMN description TEXT NOT NULL DEFAULT ''")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE purchases ADD COLUMN userEmail TEXT NOT NULL DEFAULT ''")
            }
        }
    }
}
