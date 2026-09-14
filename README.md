# NIT3213 Final Assignment — s8014554Assignment2

An Android application that authenticates against the nit3213api and displays a
list of artworks, with a details screen for each one.

## Screens

| Screen | Description |
|---|---|
| Login | Student ID and first name, validated and sent to `POST /footscray/auth`. Displays an error message on failure. |
| Dashboard | RecyclerView of the entities returned by `GET /dashboard/{keypass}`, showing a summary of each artwork. |
| Details | All fields of the selected artwork, including the description. |

## Architecture

The app follows an MVVM structure with a repository layer:

```
ui/               Fragments, ViewModels and UI state (one ViewModel per screen)
data/model        Moshi data classes matching the API's JSON
data/remote       Retrofit ApiService interface
data/repository   Repositories that the ViewModels depend on
di/               Hilt module providing the networking stack
```

- **Dependency injection:** Hilt. `NetworkModule` provides the OkHttp client,
  Moshi, Retrofit and `ApiService` as singletons; repositories and ViewModels
  receive their dependencies through `@Inject` constructors.
- **Asynchronicity:** Kotlin coroutines. Network calls run in `viewModelScope`
  and results are exposed as a `StateFlow` of a sealed UI state, which each
  fragment collects with `repeatOnLifecycle`.
- **Navigation:** a single activity (`FragmentHostActivity`) hosting a
  navigation graph with the three fragment destinations.

## Requirements

- Android Studio (AGP 9.4, Gradle 9.6)
- JDK 17
- Minimum SDK 27, target SDK 37

## Build and run

1. Clone the repository: git clone https://github.com/halfpelican/NIT3213_FinalAssessment.git
2. Open the project in Android Studio and let Gradle sync.
3. Run the `app` configuration on an emulator or device (API 27 or higher).
4. Log in with your student ID as the username and your first name as the
   password (case-sensitive).

Note: the API is hosted on Render's free tier and sleeps when idle, so the first
login after a period of inactivity can take up to a minute. Network timeouts are
set to 60 seconds for this reason.

## Tests

Run the unit tests from Android Studio, or: ./gradlew test

`LoginViewModelTest` covers input validation, a successful login and the 400
credentials failure. `DashboardViewModelTest` covers the success path, the 404
failure path, and the guard that prevents a refetch when entities are already
loaded. Both use MockK for the repository and `kotlinx-coroutines-test` to
control coroutine execution.

## Libraries

| Library | Purpose |
|---|---|
| Retrofit + Moshi | HTTP client and JSON parsing |
| OkHttp logging interceptor | Request/response logging during development |
| Hilt | Dependency injection |
| AndroidX Navigation | Fragment navigation |
| Lifecycle (ViewModel, runtime) | Coroutine scopes and state holders |
| MockK, kotlinx-coroutines-test | Unit testing |
