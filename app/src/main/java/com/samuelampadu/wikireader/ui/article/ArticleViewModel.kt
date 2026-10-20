package com.samuelampadu.wikireader.ui.article

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samuelampadu.wikireader.domain.ArticleSummary
import com.samuelampadu.wikireader.domain.WikiLanguage
import com.samuelampadu.wikireader.domain.WikiRepository
import com.samuelampadu.wikireader.ui.navigation.ArticleArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ArticleUiState {
    data object Loading : ArticleUiState
    data class Success(val article: ArticleSummary) : ArticleUiState
    data object Error : ArticleUiState
}

@HiltViewModel
class ArticleViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: WikiRepository,
) : ViewModel() {

    private val title: String = checkNotNull(savedStateHandle[ArticleArgs.TITLE])
    private val language: WikiLanguage = WikiLanguage.fromCode(savedStateHandle[ArticleArgs.LANG])

    private val _uiState = MutableStateFlow<ArticleUiState>(ArticleUiState.Loading)
    val uiState: StateFlow<ArticleUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = ArticleUiState.Loading
            _uiState.value = repository.summary(title, language).fold(
                onSuccess = { ArticleUiState.Success(it) },
                onFailure = { ArticleUiState.Error },
            )
        }
    }
}
