Weather App

Overview

This Weather App provides real-time weather updates, forecasts, and temperature trends for different locations. Users can search for places using the Google Places API and view current, hourly, and weekly weather details.

Features

Real-Time Weather Data: Fetches weather details such as temperature, humidity, wind speed, and more.

7-Day Forecast: Displays a weekly weather forecast.

Temperature Conversion: Switch between Celsius and Fahrenheit.

Graphical Representation: Uses MPAndroid Charts to display temperature trends.

Location-Based Weather: Retrieves weather based on the user's current location.

Search for Locations: Uses Google Places API for searching cities.

Modern UI: Well-designed UI with a CollapsingToolbarLayout and smooth animations.

Technologies Used

Kotlin: Primary programming language.

MVVM Architecture: Ensures clean separation of concerns.

Dagger Hilt: Dependency injection for better code management.

Coroutines & Flow: Efficiently handle asynchronous operations.

Retrofit: Fetch weather data from OpenWeather API.

LiveData & ViewModel: Manage UI-related data lifecycle-aware.

Room Database: Caching weather data locally for offline access.

MPAndroidChart: Visual representation of temperature trends.

Google Places API: Location search functionality.

Project Structure

WeatherApp/
│── data/
│   ├── model/          # Data models for weather response
│   ├── api/            # Retrofit API service
│   ├── repository/     # Repository handling data logic
│── ui/
│   ├── viewmodel/      # ViewModels for UI interaction
│   ├── adapter/        # RecyclerView Adapters
│   ├── fragment/       # UI Fragments
│── di/                 # Dependency Injection (Dagger Hilt)
│── utils/              # Utility functions
│── MainActivity.kt     # Entry point of the app

Setup & Installation

Clone the repository:

git clone https://github.com/yourusername/weather-app.git

Open the project in Android Studio.

Add your OpenWeather API Key and Google Places API Key in local.properties:

OPENWEATHER_API_KEY=your_api_key_here
GOOGLE_PLACES_API_KEY=your_api_key_here

Sync the project and build it.

Run the app on an emulator or a physical device.

Testing

Unit Testing: JUnit, Mockito

UI Testing: Espresso

ViewModel & Repository Tests

MockWebServer for Retrofit API testing

Screenshots



![image alt](https://github.com/SeethaIndiran/Weather-Forecast-App/blob/d3b6b7d62678340ca3f420abc93918b3ba13cbe8/1%5B1%5D.png)
![image alt](https://github.com/SeethaIndiran/Weather-Forecast-App/blob/2445c90040e00005f01b6ce4b08140cad5aee5ae/2%5B1%5D.png)
![image alt](https://github.com/SeethaIndiran/Weather-Forecast-App/blob/58d34960caf40cc39c06a3be027a9aa134fe0425/3%5B1%5D.png)
