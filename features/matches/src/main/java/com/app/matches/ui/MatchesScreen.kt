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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchesScreen(
    viewModel: MatchesViewModel = koinViewModel(),
    onSettingsClick: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            if (state is MatchesState.Success) {
                val s = state as MatchesState.Success
                MatchesTopBar(
                    timeFilters = s.timeFilters,
                    onFilterSelected = viewModel::applyFilter,
                    onSettingsClick = onSettingsClick
                )
            }
        }
    ) { paddingValues ->
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
                val listState = remember(s.selectedFilter) { LazyListState() }
                Column(modifier = Modifier.padding(paddingValues)) {
                    Text(
                        text = stringResource(
                            id = R.string.filter_label,
                            stringResource(id = s.selectedFilter.labelResId)
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    PullToRefreshBox(
                        isRefreshing = s.isRefreshing,
                        onRefresh = viewModel::refresh,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        MatchesList(s.matches, listState)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchesTopBar(
    timeFilters: List<TimeFilter>,
    onFilterSelected: (TimeFilter) -> Unit,
    onSettingsClick: () -> Unit
) {
    var isFilterMenuExpanded by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.matches_title),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )
        },
        actions = {
            IconButton(onClick = { isFilterMenuExpanded = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = "")
            }
            DropdownMenu(
                expanded = isFilterMenuExpanded,
                onDismissRequest = { isFilterMenuExpanded = false }
            ) {
                timeFilters.forEach { filter ->
                    DropdownMenuItem(
                        text = { Text(stringResource(filter.labelResId)) },
                        onClick = {
                            isFilterMenuExpanded = false
                            onFilterSelected(filter)
                        }
                    )
                }
            }
            IconButton(onClick = onSettingsClick) {
                Icon(Icons.Default.Settings, contentDescription = "")
            }
        }
    )
}


@Composable
fun MatchesList(
    matches: List<Match>,
    state: LazyListState = rememberLazyListState()
) {
    LazyColumn(
        state = state,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(matches, key = { it.id }) { match ->
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
                    text = formatTime(match.matchDate),
                    style = MaterialTheme.typography.labelMedium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = getScoreText(match, stringResource(R.string.score_unavailable)),
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

fun getScoreText(match: Match, scorePlaceholder: String): String {
    return if (match.isFinished) {
        val home = match.homeScore?.toString() ?: "-"
        val away = match.awayScore?.toString() ?: "-"
        "$home : $away"
    } else {
        scorePlaceholder
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