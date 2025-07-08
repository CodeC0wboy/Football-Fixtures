package com.app.matches.data.model

import com.app.matches.domain.repository.Match
import kotlinx.serialization.Serializable

@Serializable
data class FixturesResponseDto(
    val get: String,
    val parameters: Map<String, String>,
    val errors: List<String> = emptyList(),
    val results: Int,
    val paging: PagingDto,
    val response: List<MatchDto>
)

@Serializable
data class PagingDto(
    val current: Int,
    val total: Int
)

@Serializable
data class MatchDto(
    val fixture: FixtureDto,
    val league: LeagueDto,
    val teams: TeamsDto,
    val goals: GoalsDto,
    val score: ScoreDto
)

fun MatchDto.toDomain(): Match {
    return Match(
        id = fixture.id,
        homeTeamName = teams.home.name,
        homeTeamLogo = teams.home.logo,
        awayTeamName = teams.away.name,
        awayTeamLogo = teams.away.logo,
        homeScore = goals.home,
        awayScore = goals.away,
        matchDate = fixture.date,
        isFinished = fixture.status.short == "FT"
    )
}

@Serializable
data class FixtureDto(
    val id: Int,
    val referee: String? = null,
    val timezone: String,
    val date: String,
    val timestamp: Long,
    val periods: PeriodsDto,
    val venue: VenueDto,
    val status: StatusDto
)

@Serializable
data class PeriodsDto(
    val first: Long? = null,
    val second: Long? = null
)

@Serializable
data class VenueDto(
    val id: Int? = null,
    val name: String? = null,
    val city: String? = null
)

@Serializable
data class StatusDto(
    val long: String? = null,
    val short: String? = null,
    val elapsed: Int? = null,
    val extra: Int? = null
)

@Serializable
data class LeagueDto(
    val id: Int,
    val name: String,
    val country: String? = null,
    val logo: String? = null,
    val flag: String? = null,
    val season: Int,
    val round: String? = null,
    val standings: Boolean
)

@Serializable
data class TeamsDto(
    val home: TeamDto,
    val away: TeamDto
)

@Serializable
data class TeamDto(
    val id: Int,
    val name: String,
    val logo: String,
    val winner: Boolean? = null
)

@Serializable
data class GoalsDto(
    val home: Int? = null,
    val away: Int? = null
)

@Serializable
data class ScoreDto(
    val halftime: ScorePartDto,
    val fulltime: ScorePartDto,
    val extratime: ScorePartDto,
    val penalty: ScorePartDto
)

@Serializable
data class ScorePartDto(
    val home: Int? = null,
    val away: Int? = null
)
