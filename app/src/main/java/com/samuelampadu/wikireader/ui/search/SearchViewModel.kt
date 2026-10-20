package com.samuelampadu.wikireader.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samuelampadu.wikireader.domain.SearchResult
import com.samuelampadu.wikireader.domain.WikiLanguage
import com.samuelampadu.wikireader.domain.WikiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

sealed interface SearchUiState {
    data object Idle : SearchUiState
    data object Loading : SearchUiState
    data class Results(val items: List<SearchResult>) : SearchUiState
    data class Empty(val query: String) : SearchUiState
    data object Error : SearchUiState
}

internal const val SEARCH_DEBOUNCE_MS = 300L

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: WikiRepository,
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _language = MutableStateFlow(WikiLanguage.ENGLISH)
    val language: StateFlow<WikiLanguage> = _language.asStateFlow()

    private val retryTrigger = MutableStateFlow(0)

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<SearchUiState> = combine(
        _query.debounce(SEARCH_DEBOUNCE_MS).map { it.trim() }.distinctUntilChanged(),
        _language,
        retryTrigger,
    ) { query, language, _ -> query to language }
        // flatMapLatest cancels an in-flight search as soon as the query or language changes.
        .flatMapLatest { (query, language) ->
            if (query.isBlank()) {
                flowOf(SearchUiState.Idle)
            } else {
                flow<SearchUiState> {
                    emit(SearchUiState.Loading)
                    val result = repository.search(query, language)
                    emit(
                        result.fold(
                            onSuccess = { items ->
                                if (items.isEmpty()) SearchUiState.Empty(query) else SearchUiState.Results(items)
                            },
                            onFailure = { SearchUiState.Error },
                        )
                    )
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SearchUiState.Idle)

    fun onQueryChange(value: String) {
        _query.value = value
    }

    fun onLanguageChange(language: WikiLanguage) {
        _language.value = language
    }

    fun retry() {
        retryTrigger.value += 1
    }
}
