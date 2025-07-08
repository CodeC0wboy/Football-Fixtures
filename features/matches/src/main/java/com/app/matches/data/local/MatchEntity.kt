package com.app.matches.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.app.matches.domain.repository.Match

@Entity(tableName = "matches")
data class MatchEntity(
    @PrimaryKey val id: Int,
    val homeTeamName: String,
    val homeTeamLogo: String,
    val awayTeamName: String,
    val awayTeamLogo: String,
    val homeScore: Int?,
    val awayScore: Int?,
    val matchDate: String,
    val isFinished: Boolean
)


fun Match.toEntity(): MatchEntity = MatchEntity(
    id,
    homeTeamName,
    homeTeamLogo,
    awayTeamName,
    awayTeamLogo,
    homeScore,
    awayScore,
    matchDate,
    isFinished
)

fun MatchEntity.toDomain(): Match = Match(
    id,
    homeTeamName,
    homeTeamLogo,
    awayTeamName,
    awayTeamLogo,
    homeScore,
    awayScore,
    matchDate,
    isFinished
)
