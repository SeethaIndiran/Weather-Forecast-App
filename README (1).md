
## Weather Forecast App

## Overview


The Weather  Forecast App is a native Android application built using Kotlin that provides real-time weather updates, forecasts, and temperature conversions between Celsius and Fahrenheit. Users can search for locations using Google Places API and view weather details for today, tomorrow, and a 7-day forecast with graphical representations.
## Features



* Fetch real-time weather data using OpenWeather API

* Display current, hourly, and daily weather forecasts

* Search for locations using Google Places API

* Temperature conversion between Celsius and Fahrenheit

* Graphical representation of temperature trends using MPAndroid Charts

* User preferences stored using SharedPreferences

* Offline support using Room Database

* Modern UI with Material Design and ConstraintLayout

* Weather animations using Lottie

* Background updates with WorkManager
## Tech Stack

* Language: Kotlin

* Architecture: MVVM (Model-View-ViewModel)

* Dependency Injection: Dagger Hilt

* Networking: Retrofit with Coroutines

* Data Storage: Room Database, SharedPreferences

* UI Components: Jetpack Compose, ConstraintLayout, Material Design

* Asynchronous Handling: Kotlin Coroutines and Flow

* Testing: JUnit, Mockito, Espresso


## Project Structure

- data  
  - local (Room Database for offline caching)  
  - remote (Retrofit API calls)  
  - repository (Repository pattern for data handling)  
- di (Dagger Hilt modules)  
- ui  
  - home (Main weather UI)  
  - settings (Settings fragment for unit conversion)  
  - details (Detailed weather information with graphs)  
- utils (Helper functions, extensions, and constants)  
- viewmodel (ViewModels for UI logic)  
## API Reference

The app fetches weather data from OpenWeather API. To use this app, you need an API key.

1. Steps to Get API Key

2. Sign up at OpenWeather

3. Get the API key from the dashboard

4. Add the key in local.properties file:

  WEATHER_API_KEY=your_api_key_here

  Modify gradle.properties to include the API key:


## Installation

Clone the repository:

1. git clone https://github.com/yourusername/weather-app.git

2. Open the project in Android Studio

3. Sync Gradle and build the project

4. Run the app on an emulator or a physical device
    
## Running Tests

The app includes unit tests and UI tests.

Unit Testing: ViewModel, Repository, and Database tested with JUnit and Mockito.

UI Testing: Fragment navigation and user interactions tested using Espresso.

Run tests with:

./gradlew testDebugUnitTest
./gradlew connectedAndroidTest

## Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository

2. Create a feature branch

3. Commit your changes

4. Push to the branch

5. Open a Pull Request


## Contact

For questions or feedback, contact:

Email: seethaindhiran@gmail.com

GitHub: SeethaIndiran