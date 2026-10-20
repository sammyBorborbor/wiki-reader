package com.samuelampadu.wikireader.ui.search

import app.cash.turbine.test
import com.samuelampadu.wikireader.MainDispatcherRule
import com.samuelampadu.wikireader.domain.SearchResult
import com.samuelampadu.wikireader.domain.WikiLanguage
import com.samuelampadu.wikireader.fakes.FakeWikiRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class SearchViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val accra = SearchResult(pageId = 1, title = "Accra", snippet = "Capital of Ghana")

    @Test
    fun `blank query stays idle and does not call the API`() = runTest {
        val repository = FakeWikiRepository()
        val viewModel = SearchViewModel(repository)

        viewModel.uiState.test {
            assertEquals(SearchUiState.Idle, awaitItem())
            viewModel.onQueryChange("   ")
            expectNoEvents()
        }
        assertEquals(emptyList<Pair<String, WikiLanguage>>(), repository.searches)
    }

    @Test
    fun `query shows loading then results`() = runTest {
        val repository = FakeWikiRepository(results = listOf(accra))
        val viewModel = SearchViewModel(repository)

        viewModel.uiState.test {
            assertEquals(SearchUiState.Idle, awaitItem())
            viewModel.onQueryChange("Accra")
            assertEquals(SearchUiState.Loading, awaitItem())
            assertEquals(SearchUiState.Results(listOf(accra)), awaitItem())
        }
    }

    @Test
    fun `rapid typing only searches once after the debounce`() = runTest {
        val repository = FakeWikiRepository(results = listOf(accra))
        val viewModel = SearchViewModel(repository)

        viewModel.uiState.test {
            awaitItem()
            viewModel.onQueryChange("Ac")
            viewModel.onQueryChange("Acc")
            viewModel.onQueryChange("Accra")
            assertEquals(SearchUiState.Loading, awaitItem())
            assertEquals(SearchUiState.Results(listOf(accra)), awaitItem())
        }
        assertEquals(listOf("Accra" to WikiLanguage.ENGLISH), repository.searches)
    }

    @Test
    fun `no matches shows empty state`() = runTest {
        val viewModel = SearchViewModel(FakeWikiRepository(results = emptyList()))

        viewModel.uiState.test {
            awaitItem()
            viewModel.onQueryChange("zzzz")
            assertEquals(SearchUiState.Loading, awaitItem())
            assertEquals(SearchUiState.Empty("zzzz"), awaitItem())
        }
    }

    @Test
    fun `failure shows error and retry searches again`() = runTest {
        val repository = FakeWikiRepository(results = listOf(accra), shouldFail = true)
        val viewModel = SearchViewModel(repository)

        viewModel.uiState.test {
            awaitItem()
            viewModel.onQueryChange("Accra")
            assertEquals(SearchUiState.Loading, awaitItem())
            assertEquals(SearchUiState.Error, awaitItem())

            repository.shouldFail = false
            viewModel.retry()
            assertEquals(SearchUiState.Loading, awaitItem())
            assertEquals(SearchUiState.Results(listOf(accra)), awaitItem())
        }
    }

    @Test
    fun `changing language searches the new edition`() = runTest {
        val repository = FakeWikiRepository(results = listOf(accra))
        val viewModel = SearchViewModel(repository)

        viewModel.uiState.test {
            awaitItem()
            viewModel.onQueryChange("Accra")
            awaitItem() // Loading
            awaitItem() // Results
            viewModel.onLanguageChange(WikiLanguage.TWI)
            assertEquals(SearchUiState.Loading, awaitItem())
            assertEquals(SearchUiState.Results(listOf(accra)), awaitItem())
        }
        assertEquals(WikiLanguage.TWI, repository.searches.last().second)
    }
}
