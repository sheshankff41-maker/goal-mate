package com.example.data.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

enum class GoalCategory(val displayName: String, val iconName: String) {
    EDUCATION("Education", "School"),
    FITNESS("Fitness", "FitnessCenter"),
    PERSONAL_DEV("Personal Development", "Psychology"),
    CAREER("Career", "Work"),
    FINANCE("Finance", "AccountBalanceWallet"),
    GAMING("Gaming", "SportsEsports"),
    OTHER("Other", "Star")
}

enum class Priority(val displayName: String, val level: Int) {
    LOW("Low", 1),
    MEDIUM("Medium", 2),
    HIGH("High", 3)
}

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val category: GoalCategory = GoalCategory.PERSONAL_DEV,
    val priority: Priority = Priority.MEDIUM,
    val startDate: Long = System.currentTimeMillis(),
    val deadline: Long,
    val progress: Int = 0, // 0 - 100
    val isCompleted: Boolean = false,
    val completedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "subtasks",
    foreignKeys = [
        ForeignKey(
            entity = GoalEntity::class,
            parentColumns = ["id"],
            childColumns = ["goalId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("goalId")]
)
data class SubTaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val goalId: Long,
    val title: String,
    val isCompleted: Boolean = false,
    val orderIndex: Int = 0
)

data class GoalWithSubTasks(
    @Embedded val goal: GoalEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "goalId"
    )
    val subTasks: List<SubTaskEntity> = emptyList()
) {
    val calculatedProgress: Int
        get() {
            if (goal.isCompleted) return 100
            if (subTasks.isEmpty()) return goal.progress
            val completedCount = subTasks.count { it.isCompleted }
            return ((completedCount.toFloat() / subTasks.size) * 100).toInt()
        }
}
