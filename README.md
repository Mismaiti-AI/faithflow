# FaithFlow

A Compose Multiplatform mobile app for helping congregations stay connected with church activities, special events, news, and announcements.

## About

FaithFlow helps your congregation get updates about church activities, special events like seminars or workshops, news, and articles. Built with Kotlin Multiplatform, it runs on both Android and iOS with a shared codebase.

## Features

- **Event Calendar**: Browse upcoming seminars, workshops, and church activities with date, time, and category
- **News Feed**: Read church announcements, pastoral updates, and community highlights
- **Event Details**: View full details for any event including location, description, speaker, and registration info
- **Category Filtering**: Filter events and news by ministry or audience (Youth, Women's Ministry, Outreach, etc.)
- **Admin Configuration**: Configure your Google Sheets URL to manage content

## Backend

This app uses **Google Sheets** as a simple Content Management System (CMS). Church administrators can update content directly in Google Sheets, and the app fetches the data via published CSV URLs.

### Setup Instructions

1. Create a Google Sheet with tabs for:
   - Events (columns: id, title, date, category, location, description, topic, bibleVerse, picCouncilMember, onDutyCouncilMembers, isFeatured)
   - News (columns: id, headline, publishDate, author, body, category, scriptureReference, isUrgent, photoURL, relatedEventId)
   - ChurchProfile (columns: name, logoURL, welcomeMessage, address, phone, website, email, mission, serviceTimes, socialFacebook)

2. Publish each tab to the web as CSV:
   - Go to File → Share → Publish to web
   - Select each tab and choose "Comma-separated values (.csv)"
   - Copy the generated URL

3. Enter the Google Sheets URL in the app's Setup screen on first launch

## Tech Stack

- **Kotlin Multiplatform**: Share code between Android and iOS
- **Compose Multiplatform**: Modern declarative UI framework
- **Ktor**: HTTP client for fetching CSV data
- **Koin**: Dependency injection
- **Material3**: Modern Material Design components
- **Type-Safe Navigation**: Using Kotlin Serialization for routes

## Architecture

- **Clean Architecture**: Separation of concerns with domain, data, and presentation layers
- **Repository Pattern**: Single source of truth for data
- **StateFlow**: Reactive state management
- **THIN ViewModels**: ViewModels observe repository state rather than copying it

## Project Structure

```
composeApp/src/
├── commonMain/           # Shared code for all platforms
│   └── kotlin/com/faithflow/
│       ├── domain/       # Business logic and models
│       ├── data/         # Repository implementations and services
│       ├── presentation/ # UI screens, ViewModels, and components
│       ├── di/           # Dependency injection modules
│       └── App.kt        # Main app entry point
├── androidMain/          # Android-specific code
└── iosMain/              # iOS-specific code
```

## Building

### Android
```bash
./gradlew :composeApp:assembleDebug
```

### iOS
Open `iosApp/iosApp.xcodeproj` in Xcode and build

## Color Scheme

- **Primary**: Deep Purple (#4A235A) - Elegant church theme
- **Secondary**: Orange (#FF9800) - Vibrant accent for calls-to-action
- **Background**: Ivory white (#FFFBFE) - Warm and welcoming

## Requirements

- Android: API 24+ (Android 7.0)
- iOS: iOS 15.0+
- Kotlin: 2.1.0
- Compose Multiplatform: 1.7.1

## License

Created with Mismaiti Backend Code Generator
