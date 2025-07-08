package com.app.matches.data

import com.app.core.mapper.mapSuccess
import com.app.core.network.NetworkingClient
import com.app.core.network.model.NetworkResult
import com.app.matches.data.model.FixturesResponseDto
import com.app.matches.data.model.toDomain
import com.app.matches.domain.repository.Match
import com.app.matches.domain.repository.MatchRepository
import com.app.core.network.extensions.toResult
import com.app.matches.data.local.MatchDao
import com.app.matches.data.local.toDomain
import com.app.matches.data.local.toEntity

const val FIXTURES_URI = "fixtures"

class MatchRepositoryImpl(
    private val networkingClient: NetworkingClient,
    private val matchDao: MatchDao
) : MatchRepository {

    override suspend fun getMatches(parameters: Map<String, String>): NetworkResult<List<Match>> {
        return try {
            val response = networkingClient.get(FIXTURES_URI, parameters)
                .toResult<FixturesResponseDto>()
                .mapSuccess { dto ->
                    val matches = dto.response.map { it.toDomain() }
                    matchDao.clear()
                    matchDao.insertAll(matches.map { it.toEntity() })
                    matches
                }

            response
        } catch (e: Exception) {
            val cached = matchDao.getAll().map { it.toDomain() }
            if (cached.isNotEmpty()) {
                NetworkResult.Success(cached)
            } else {
                NetworkResult.Failure.Internal(e)
            }
        }
    }
}

