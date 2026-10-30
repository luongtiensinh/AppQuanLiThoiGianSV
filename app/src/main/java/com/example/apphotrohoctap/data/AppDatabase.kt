package com.example.apphotrohoctap.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.migration.Migration

@Database(entities = [
    TaskEntity::class, 
    SubtaskEntity::class, 
    ScheduleEntity::class, 
    NoteEntity::class,
    SubjectGradeEntity::class,
    GradeComponentEntity::class
], version = 6, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
    abstract fun scheduleDao(): ScheduleDao
    abstract fun noteDao(): NoteDao
    abstract fun gradeDao(): GradeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `subject_grades` (
                        `subjectId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `subjectName` TEXT NOT NULL,
                        `credits` INTEGER NOT NULL,
                        `targetGrade` TEXT NOT NULL
                    )
                """.trimIndent())
                
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `grade_components` (
                        `componentId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `subjectId` INTEGER NOT NULL,
                        `componentName` TEXT NOT NULL,
                        `weightPercent` REAL NOT NULL,
                        `achievedScore` REAL,
                        FOREIGN KEY(`subjectId`) REFERENCES `subject_grades`(`subjectId`) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                """.trimIndent())
                
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_grade_components_subjectId` ON `grade_components` (`subjectId`)")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "studyflow_database"
                )
                .addMigrations(MIGRATION_5_6)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
