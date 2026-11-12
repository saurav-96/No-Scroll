package com.saurav.focusguard.data.db

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "scroll_sessions")
data class ScrollSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val platformId: String,
    val packageName: String,
    val dateKey: String, // YYYY-MM-DD
    val startedAtMillis: Long,
    val endedAtMillis: Long,
    val durationMillis: Long
)

@Entity(tableName = "block_events")
data class BlockEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val platformId: String,
    val packageName: String,
    val dateKey: String, // YYYY-MM-DD
    val timestampMillis: Long,
    val reason: String
)

data class PlatformUsageAggregate(
    val platformId: String,
    val totalDurationMillis: Long,
    val sessionCount: Int
)

@Dao
interface AnalyticsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: ScrollSessionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlockEvent(event: BlockEventEntity)

    @Query("SELECT COUNT(*) FROM scroll_sessions WHERE dateKey = :dateKey")
    fun observeSessionCountForDate(dateKey: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM block_events WHERE dateKey = :dateKey")
    fun observeBlockedCountForDate(dateKey: String): Flow<Int>

    @Query("SELECT COALESCE(SUM(durationMillis), 0) FROM scroll_sessions WHERE dateKey = :dateKey")
    fun observeScrollTimeMillisForDate(dateKey: String): Flow<Long>

    @Query("SELECT * FROM block_events ORDER BY timestampMillis DESC LIMIT 50")
    fun observeRecentBlockEvents(): Flow<List<BlockEventEntity>>

    @Query("SELECT * FROM scroll_sessions ORDER BY startedAtMillis DESC LIMIT 50")
    fun observeRecentSessions(): Flow<List<ScrollSessionEntity>>

    @Query(
        "SELECT platformId, COALESCE(SUM(durationMillis), 0) AS totalDurationMillis, COUNT(*) AS sessionCount " +
            "FROM scroll_sessions WHERE dateKey = :dateKey GROUP BY platformId ORDER BY totalDurationMillis DESC"
    )
    fun observePlatformBreakdownForDate(dateKey: String): Flow<List<PlatformUsageAggregate>>
}

@Database(
    entities = [ScrollSessionEntity::class, BlockEventEntity::class],
    version = 1,
    exportSchema = false
)
abstract class FocusGuardDatabase : RoomDatabase() {
    abstract fun analyticsDao(): AnalyticsDao
}
