# Wiki Reader

A small Android app for searching Wikipedia and reading article summaries, built with Kotlin and Jetpack Compose.

## Features

- Search Wikipedia as you type, with a 300 ms debounce so the API is not called on every keystroke
- Switch between Wikipedia editions: English, French, Kiswahili, Twi and Arabic
- Article screen with the summary, description and thumbnail, plus a link to the full article
- Loading, empty and error states, with retry
- Accessibility: TalkBack announces result counts through a polite live region, article titles are marked as headings, and the back arrow mirrors in right-to-left layouts

## Architecture

MVVM with unidirectional data flow:

```
Compose UI  ->  ViewModel (StateFlow<UiState>)  ->  WikiRepository  ->  WikipediaApi (Retrofit)
```

- **UI:** Jetpack Compose and Material 3. Each screen is split into a `Route`, which connects to the ViewModel, and a stateless `Screen`, which is easy to preview and test.
- **ViewModels:** expose a single `StateFlow` of a sealed `UiState`. Search combines the query, language and a retry trigger, and uses `flatMapLatest` so an in-flight request is cancelled when the user keeps typing.
- **Data:** `WikiRepository` returns `Result` types and never swallows coroutine cancellation.
- **Networking:** Retrofit, OkHttp and kotlinx.serialization. Each language edition has its own subdomain, so requests use full URLs built in `WikiUrls`.
- **DI:** Hilt.

## APIs used

- MediaWiki Action API, `list=search`, for search results
- Wikimedia REST API, `page/summary/{title}`, for article summaries

API and image requests share one OkHttp client that sends a descriptive `User-Agent`, as Wikimedia's API etiquette asks. Wikimedia's image servers reject requests that use OkHttp's default one.

## Tests

JVM unit tests with JUnit, kotlinx-coroutines-test and Turbine cover search debouncing, loading, empty, error and retry states, language switching, URL building and snippet cleanup.

```
./gradlew test
```

## Tech

Kotlin, Jetpack Compose, Material 3, Hilt, Retrofit, OkHttp, kotlinx.serialization, Coroutines and Flow, Navigation Compose, Coil, JUnit, Turbine.

## License

MIT
