package com.app.matches.domain.usecase

import com.app.core.network.model.NetworkResult
import com.app.matches.domain.repository.Match
import com.app.matches.domain.repository.MatchRepository

class GetMatchesUseCase(
    private val repository: MatchRepository
) {
    suspend operator fun invoke(parameters: Map<String, String>): NetworkResult<List<Match>> {
        return repository.getMatches(parameters)
    }
}
