package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.BadgeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BadgeDao {
    @Query("SELECT * FROM badges")
    fun getAllBadges(): Flow<List<BadgeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBadges(badges: List<BadgeEntity>)

    @Query("UPDATE badges SET isUnlocked = 1, unlockedAt = :unlockedAt WHERE id = :id AND isUnlocked = 0")
    suspend fun unlockBadge(id: String, unlockedAt: Long = System.currentTimeMillis()): Int

    @Query("UPDATE badges SET isUnlocked = 0, unlockedAt = NULL")
    suspend fun resetBadges()
}
