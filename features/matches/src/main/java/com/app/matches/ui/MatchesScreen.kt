package com.app.matches.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.app.core.utils.formatTime
import com.app.matches.R
import com.app.matches.domain.repository.Match
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchesScreen(
    viewModel: MatchesViewModel = koinViewModel(),
    onSettingsClick: () -> Unit
) {
    LaunchedEffect(Unit) {
        val today = LocalDate.now().toString()
        viewModel.onEvent(MatchesEvent.LoadMatches(today))
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.matches_title)) },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = "")
                    }
                }
            )
        }
    ) { paddingValues ->
        val state by viewModel.state.collectAsState()

        when (val s = state) {
            is MatchesState.Idle,
            is MatchesState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is MatchesState.Error -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = s.message, color = Color.Red)
                }
            }

            is MatchesState.Success -> {
                PullToRefreshBox(
                    isRefreshing = s.isRefreshing,
                    onRefresh = viewModel::refresh,
                    modifier = Modifier.fillMaxSize()
                        .padding(paddingValues)
                ) {
                    MatchesList(s.matches)
                }
            }
        }
    }
}



@Composable
fun MatchesList(matches: List<Match>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(
            matches,
            key = { it.id },
        ) { match ->
            MatchCard(match)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun MatchCard(match: Match) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            TeamInfo(
                name = match.homeTeamName,
                logoUrl = match.homeTeamLogo,
                modifier = Modifier.weight(1f)
            )

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (match.isFinished)
                        "${match.homeScore ?: 0} : ${match.awayScore ?: 0}"
                    else formatTime(match.matchDate),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            TeamInfo(
                name = match.awayTeamName,
                logoUrl = match.awayTeamLogo,
                modifier = Modifier.weight(1f)
            )
        }
    }
}


@Composable
fun TeamInfo(name: String, logoUrl: String, modifier: Modifier = Modifier) {
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(logoUrl)
            .crossfade(true)
            .diskCachePolicy(CachePolicy.ENABLED)
            .build()
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

