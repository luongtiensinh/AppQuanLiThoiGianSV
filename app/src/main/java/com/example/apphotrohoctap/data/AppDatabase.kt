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
    SemesterEntity::class,
    SubjectGradeEntity::class,
    GradeComponentEntity::class,
], version = 7, exportSchema = false)
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

                // 2. Tạo một học kỳ mặc định (Ví dụ: "Học kỳ 1") với id = 1
                db.execSQL("INSERT INTO `semesters` (`semesterId`, `semesterName`, `isCurrent`) VALUES (1, 'Học kỳ mặc định', 1)")

                // 3. Tạo bảng subject_grades_new với cấu trúc mới (thêm semesterId)
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `subject_grades_new` (
                        `subjectId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `semesterId` INTEGER NOT NULL,
                        `subjectName` TEXT NOT NULL,
                        `credits` INTEGER NOT NULL,
                        `targetGrade` TEXT NOT NULL,
                        FOREIGN KEY(`semesterId`) REFERENCES `semesters`(`semesterId`) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                """.trimIndent())
                
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_subject_grades_new_semesterId` ON `subject_grades_new` (`semesterId`)")

                // 4. Copy dữ liệu từ bảng cũ sang bảng mới, gán semesterId = 1 cho tất cả các môn cũ
                db.execSQL("""
                    INSERT INTO `subject_grades_new` (`subjectId`, `semesterId`, `subjectName`, `credits`, `targetGrade`)
                    SELECT `subjectId`, 1, `subjectName`, `credits`, `targetGrade` FROM `subject_grades`
                """.trimIndent())

                // 5. Xóa bảng cũ
                db.execSQL("DROP TABLE `subject_grades`")

                // 6. Đổi tên bảng mới thành tên bảng cũ
                db.execSQL("ALTER TABLE `subject_grades_new` RENAME TO `subject_grades`")
                
                // 7. Tạo lại index cho bảng subject_grades (sau khi rename, index cũ trên bảng new có thể bị mất tuỳ version sqlite, tốt nhất là tạo lại với tên chuẩn)
                db.execSQL("DROP INDEX IF EXISTS `index_subject_grades_new_semesterId`")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_subject_grades_semesterId` ON `subject_grades` (`semesterId`)")
            }
        }


        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "studyflow_database"
                )
                .addMigrations(MIGRATION_5_6, MIGRATION_6_7)
                // Chỉ áp dụng xóa DB (Destructive Migration) từ các bản cũ 1,2,3,4
                // để tránh văng app do không có kịch bản Migration. 
                // Không xóa dữ liệu nếu người dùng đang ở bản 5 lên 6.
                .fallbackToDestructiveMigrationFrom(1, 2, 3, 4)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
