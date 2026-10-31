package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.data.model.GoalEntity
import com.example.data.model.GoalWithSubTasks
import com.example.data.model.SubTaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
    @Transaction
    @Query("SELECT * FROM goals ORDER BY deadline ASC")
    fun getAllGoalsWithSubTasks(): Flow<List<GoalWithSubTasks>>

    @Transaction
    @Query("SELECT * FROM goals WHERE id = :id")
    fun getGoalWithSubTasks(id: Long): Flow<GoalWithSubTasks?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: GoalEntity): Long

    @Update
    suspend fun updateGoal(goal: GoalEntity)

    @Delete
    suspend fun deleteGoal(goal: GoalEntity)

    @Query("DELETE FROM goals WHERE id = :id")
    suspend fun deleteGoalById(id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubTask(subTask: SubTaskEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubTasks(subTasks: List<SubTaskEntity>)

    @Update
    suspend fun updateSubTask(subTask: SubTaskEntity)

    @Delete
    suspend fun deleteSubTask(subTask: SubTaskEntity)

    @Query("DELETE FROM subtasks WHERE goalId = :goalId")
    suspend fun deleteSubTasksByGoalId(goalId: Long)

    @Query("DELETE FROM goals")
    suspend fun clearAllGoals()

    @Query("DELETE FROM subtasks")
    suspend fun clearAllSubTasks()
}
