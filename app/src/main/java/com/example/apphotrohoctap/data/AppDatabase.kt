package com.example.apphotrohoctap.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.migration.Migration
import java.util.UUID

@Database(entities = [
    TaskEntity::class, 
    SubtaskEntity::class, 
    ScheduleEntity::class, 
    NoteEntity::class,
    SemesterEntity::class,
    SubjectGradeEntity::class,
    GradeComponentEntity::class,
], version = 8, exportSchema = false)
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
        
        private val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // 1. Tạo bảng semesters
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `semesters` (
                        `semesterId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `semesterName` TEXT NOT NULL,
                        `isCurrent` INTEGER NOT NULL
                    )
                """.trimIndent())

                // 2. Tạo một học kỳ mặc định
                db.execSQL("INSERT INTO `semesters` (`semesterId`, `semesterName`, `isCurrent`) VALUES (1, 'Học kỳ mặc định', 1)")

                // 3. Đổi tên các bảng cũ để tránh trigger CASCADE delete khi Drop
                db.execSQL("ALTER TABLE `grade_components` RENAME TO `grade_components_old`")
                db.execSQL("ALTER TABLE `subject_grades` RENAME TO `subject_grades_old`")

                // 4. Tạo bảng subject_grades mới (thêm semesterId)
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `subject_grades` (
                        `subjectId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `semesterId` INTEGER NOT NULL,
                        `subjectName` TEXT NOT NULL,
                        `credits` INTEGER NOT NULL,
                        `targetGrade` TEXT NOT NULL,
                        FOREIGN KEY(`semesterId`) REFERENCES `semesters`(`semesterId`) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                """.trimIndent())

                // 5. Copy dữ liệu vào subject_grades, gán semesterId = 1
                db.execSQL("""
                    INSERT INTO `subject_grades` (`subjectId`, `semesterId`, `subjectName`, `credits`, `targetGrade`)
                    SELECT `subjectId`, 1, `subjectName`, `credits`, `targetGrade` FROM `subject_grades_old`
                """.trimIndent())

                // 6. Tạo bảng grade_components mới
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

                // 7. Copy dữ liệu vào grade_components
                db.execSQL("""
                    INSERT INTO `grade_components` (`componentId`, `subjectId`, `componentName`, `weightPercent`, `achievedScore`)
                    SELECT `componentId`, `subjectId`, `componentName`, `weightPercent`, `achievedScore` FROM `grade_components_old`
                """.trimIndent())

                // 8. Xóa các bảng cũ
                db.execSQL("DROP TABLE `grade_components_old`")
                db.execSQL("DROP TABLE `subject_grades_old`")
                
                // 9. Tạo lại index
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_subject_grades_semesterId` ON `subject_grades` (`semesterId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_grade_components_subjectId` ON `grade_components` (`subjectId`)")
            }
        }

        private val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                val currentTime = System.currentTimeMillis()
                
                // Bảng tasks
                db.execSQL("ALTER TABLE `tasks` ADD COLUMN `syncId` TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE `tasks` ADD COLUMN `updatedAt` INTEGER NOT NULL DEFAULT $currentTime")
                db.execSQL("ALTER TABLE `tasks` ADD COLUMN `isDeleted` INTEGER NOT NULL DEFAULT 0")
                db.execSQL("UPDATE `tasks` SET `syncId` = lower(hex(randomblob(16))) WHERE `syncId` = ''")

                // Bảng subtasks
                db.execSQL("ALTER TABLE `subtasks` ADD COLUMN `syncId` TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE `subtasks` ADD COLUMN `updatedAt` INTEGER NOT NULL DEFAULT $currentTime")
                db.execSQL("ALTER TABLE `subtasks` ADD COLUMN `isDeleted` INTEGER NOT NULL DEFAULT 0")
                db.execSQL("UPDATE `subtasks` SET `syncId` = lower(hex(randomblob(16))) WHERE `syncId` = ''")

                // Bảng schedules
                db.execSQL("ALTER TABLE `schedules` ADD COLUMN `syncId` TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE `schedules` ADD COLUMN `updatedAt` INTEGER NOT NULL DEFAULT $currentTime")
                db.execSQL("ALTER TABLE `schedules` ADD COLUMN `isDeleted` INTEGER NOT NULL DEFAULT 0")
                db.execSQL("UPDATE `schedules` SET `syncId` = lower(hex(randomblob(16))) WHERE `syncId` = ''")

                // Bảng notes
                db.execSQL("ALTER TABLE `notes` ADD COLUMN `syncId` TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE `notes` ADD COLUMN `isDeleted` INTEGER NOT NULL DEFAULT 0")
                db.execSQL("UPDATE `notes` SET `syncId` = lower(hex(randomblob(16))) WHERE `syncId` = ''")

                // Bảng semesters
                db.execSQL("ALTER TABLE `semesters` ADD COLUMN `syncId` TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE `semesters` ADD COLUMN `updatedAt` INTEGER NOT NULL DEFAULT $currentTime")
                db.execSQL("ALTER TABLE `semesters` ADD COLUMN `isDeleted` INTEGER NOT NULL DEFAULT 0")
                db.execSQL("UPDATE `semesters` SET `syncId` = lower(hex(randomblob(16))) WHERE `syncId` = ''")

                // Bảng subject_grades
                db.execSQL("ALTER TABLE `subject_grades` ADD COLUMN `syncId` TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE `subject_grades` ADD COLUMN `updatedAt` INTEGER NOT NULL DEFAULT $currentTime")
                db.execSQL("ALTER TABLE `subject_grades` ADD COLUMN `isDeleted` INTEGER NOT NULL DEFAULT 0")
                db.execSQL("UPDATE `subject_grades` SET `syncId` = lower(hex(randomblob(16))) WHERE `syncId` = ''")

                // Bảng grade_components
                db.execSQL("ALTER TABLE `grade_components` ADD COLUMN `syncId` TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE `grade_components` ADD COLUMN `updatedAt` INTEGER NOT NULL DEFAULT $currentTime")
                db.execSQL("ALTER TABLE `grade_components` ADD COLUMN `isDeleted` INTEGER NOT NULL DEFAULT 0")
                db.execSQL("UPDATE `grade_components` SET `syncId` = lower(hex(randomblob(16))) WHERE `syncId` = ''")
            }
        }


        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "studyflow_database"
                )
                .addMigrations(MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8)
                // Chỉ áp dụng xóa DB (Destructive Migration) từ các bản cũ 1,2,3,4
                // để tránh văng app do không có kịch bản Migration. 
                .fallbackToDestructiveMigrationFrom(1, 2, 3, 4)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
