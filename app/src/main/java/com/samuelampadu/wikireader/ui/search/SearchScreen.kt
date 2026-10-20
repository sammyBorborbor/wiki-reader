package com.samuelampadu.wikireader.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.samuelampadu.wikireader.R
import com.samuelampadu.wikireader.domain.SearchResult
import com.samuelampadu.wikireader.domain.WikiLanguage

@Composable
fun SearchRoute(
    onArticleClick: (WikiLanguage, String) -> Unit,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val query by viewModel.query.collectAsStateWithLifecycle()
    val language by viewModel.language.collectAsStateWithLifecycle()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    SearchScreen(
        query = query,
        language = language,
        state = state,
        onQueryChange = viewModel::onQueryChange,
        onLanguageChange = viewModel::onLanguageChange,
        onRetry = viewModel::retry,
        onResultClick = { title -> onArticleClick(language, title) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    query: String,
    language: WikiLanguage,
    state: SearchUiState,
    onQueryChange: (String) -> Unit,
    onLanguageChange: (WikiLanguage) -> Unit,
    onRetry: () -> Unit,
    onResultClick: (String) -> Unit,
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.app_name)) }) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                label = { Text(stringResource(R.string.search_hint)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            )

            LanguagePicker(selected = language, onSelect = onLanguageChange)

            Box(modifier = Modifier.fillMaxSize()) {
                when (state) {
                    SearchUiState.Idle -> CenteredMessage(stringResource(R.string.search_idle))
                    SearchUiState.Loading -> {
                        val loading = stringResource(R.string.loading)
                        CircularProgressIndicator(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .semantics { contentDescription = loading },
                        )
                    }
                    is SearchUiState.Empty -> CenteredMessage(stringResource(R.string.search_empty, state.query))
                    SearchUiState.Error -> ErrorMessage(onRetry = onRetry)
                    is SearchUiState.Results -> ResultsList(items = state.items, onResultClick = onResultClick)
                }
            }
        }
    }
}

@Composable
private fun LanguagePicker(selected: WikiLanguage, onSelect: (WikiLanguage) -> Unit) {
    val label = stringResource(R.string.language_label)
    LazyRow(
        modifier = Modifier.semantics { contentDescription = label },
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(WikiLanguage.entries) { language ->
            FilterChip(
                selected = language == selected,
                onClick = { onSelect(language) },
                label = { Text(language.label) },
            )
        }
    }
}

@Composable
private fun ResultsList(items: List<SearchResult>, onResultClick: (String) -> Unit) {
    Column {
        // Announced politely by TalkBack each time the result count changes.
        Text(
            text = pluralStringResource(R.plurals.results_count, items.size, items.size),
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .semantics { liveRegion = LiveRegionMode.Polite },
        )
        LazyColumn {
            items(items, key = { it.pageId }) { result ->
                ListItem(
                    headlineContent = { Text(result.title) },
                    supportingContent = {
                        Text(result.snippet, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    },
                    modifier = Modifier.clickable { onResultClick(result.title) },
                )
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun CenteredMessage(text: String) {
    Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Text(text, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun ErrorMessage(onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            stringResource(R.string.search_error),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
        )
        Button(onClick = onRetry, modifier = Modifier.padding(top = 16.dp)) {
            Text(stringResource(R.string.retry))
        }
    }
}
