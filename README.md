# FaithFlow

Help congregation get an update of church activities, special events like seminar or workshop, news and article

## Overview

FaithFlow is a Compose Multiplatform mobile application that helps church congregations stay informed about church activities, special events, news, and announcements. Built with Kotlin Multiplatform, it provides a native experience on both Android and iOS platforms.

## Features

### Event Calendar
Browse upcoming seminars, workshops, and church activities with date, time, and category information. Filter events by ministry or audience to find what matters most to you.

### News Feed
Read church announcements, pastoral updates, and community highlights. Stay connected with the latest happenings in your congregation.

### Event Details
View full details for any event including location, description, speaker information, and registration details. Get directions to event locations with integrated map support.

### Category Filtering
Filter events and news by ministry or audience (e.g., Youth, Women's Ministry, Outreach). Save preferred categories for quick access.

### Admin Configuration
Church administrators can configure their FaithFlow instance by setting the Google Sheet URL, validating connectivity, and updating church profile data.

## Technology Stack

- **Framework**: Compose Multiplatform
- **Language**: Kotlin
- **Backend**: Google Sheets (CSV export)
- **Architecture**: MVVM with Repository pattern
- **Dependency Injection**: Koin
- **Database**: Room (for offline caching)
- **Networking**: Ktor Client
- **Navigation**: Type-safe Navigation with Serializable routes

## Project Structure

```
composeApp/src/
├── commonMain/
│   └── kotlin/com/faithflow/
│       ├── domain/          # Business models and repository interfaces
│       │   ├── model/       # Domain models (Event, NewsItem, ChurchProfile)
│       │   ├── repository/  # Repository interfaces
│       │   └── usecase/     # Business logic use cases
│       ├── data/            # Data layer implementation
│       │   ├── local/       # Room database, entities, DAOs
│       │   ├── remote/      # Google Sheets service
│       │   └── repository/  # Repository implementations
│       ├── presentation/    # UI layer
│       │   ├── screen/      # Composable screens
│       │   ├── viewmodel/   # ViewModels with state management
│       │   ├── navigation/  # Type-safe navigation
│       │   ├── theme/       # Material3 theme
│       │   └── components/  # Reusable UI components
│       └── di/              # Dependency injection modules
├── androidMain/             # Android-specific code
└── iosMain/                 # iOS-specific code
```

## Architecture Patterns

### THIN ViewModels
ViewModels observe repository state flows and transform them to UI state. Business logic lives in use cases, not ViewModels.

### Repository State Management
Repositories hold MutableStateFlow for shared state across multiple screens and ViewModels. This ensures consistent data across the app.

### Type-Safe Navigation
Navigation uses @Serializable route classes instead of string-based routes, providing compile-time safety.

### Offline-First
Data is cached in Room database, allowing the app to work offline and sync when connectivity is restored.

## Building the Project

### Prerequisites
- JDK 17 or higher
- Android Studio with Compose Multiplatform plugin
- Xcode (for iOS builds)

### Build Commands
```bash
# Build Android
./gradlew :composeApp:assembleDebug

# Build iOS
./gradlew :composeApp:iosSimulatorArm64Test

# Run Android
./gradlew :composeApp:installDebug

# Run on iOS Simulator
./gradlew :composeApp:iosDeployIPhone15Debug
```

## Configuration

FaithFlow uses Google Sheets as a backend. On first launch, administrators need to:

1. Launch the app
2. Enter a Google Sheets URL (must be publicly accessible)
3. The app will automatically fetch data from the following tabs:
   - Events (event calendar data)
   - News (news feed articles)
   - Profile (church profile information)

### Google Sheets Format

Your Google Sheet should have three tabs:

**Events Tab** (columns: id, title, date, category, location, description, topic, bibleVerse, picCouncilMember, onDutyCouncilMembers, isFeatured)

**News Tab** (columns: id, headline, publishDate, author, body, category, scriptureReference, isUrgent, photoURL, relatedEventId)

**Profile Tab** (columns: name, logoURL, welcomeMessage, address, phone, website, email, mission, serviceTimes, socialFacebook)

## License

Copyright 2026 Mismaiti. All rights reserved.
