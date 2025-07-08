package com.app.matches.domain.repository

import com.app.core.network.model.NetworkResult


interface MatchRepository {
    suspend fun getMatches(parameters: Map<String, String>): NetworkResult<List<Match>>
}

data class Match(
    val id: Int,
    val homeTeamName: String,
    val homeTeamLogo: String,
    val awayTeamName: String,
    val awayTeamLogo: String,
    val homeScore: Int?,
    val awayScore: Int?,
    val matchDate: String,
    val isFinished: Boolean
)
