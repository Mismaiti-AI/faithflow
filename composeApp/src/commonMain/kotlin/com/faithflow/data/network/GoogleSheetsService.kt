package com.faithflow.data.network

import com.faithflow.domain.model.ChurchProfile
import com.faithflow.domain.model.Event
import com.faithflow.domain.model.NewsItem
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toInstant

/**
 * Service for fetching and parsing data from Google Sheets CSV exports
 * Handles multiple date formats and defensive data parsing
 */
@OptIn(ExperimentalTime::class)
class GoogleSheetsService(
    private val httpClient: HttpClient
) {

    /**
     * Fetch events from Google Sheets CSV
     */
    suspend fun fetchEvents(csvUrl: String): List<Event> {
        return try {
            val csv = httpClient.get(csvUrl).bodyAsText()

            // Check if response is HTML error page instead of CSV
            if (csv.trim().startsWith("<!DOCTYPE") || csv.trim().startsWith("<html")) {
                return emptyList()
            }

            parseCsvToEvents(csv)
        } catch (e: Exception) {
            println("Error fetching events: ${e.message}")
            emptyList()
        }
    }

    /**
     * Fetch news items from Google Sheets CSV
     */
    suspend fun fetchNews(csvUrl: String): List<NewsItem> {
        return try {
            val csv = httpClient.get(csvUrl).bodyAsText()

            if (csv.trim().startsWith("<!DOCTYPE") || csv.trim().startsWith("<html")) {
                return emptyList()
            }

            parseCsvToNews(csv)
        } catch (e: Exception) {
            println("Error fetching news: ${e.message}")
            emptyList()
        }
    }

    /**
     * Fetch church profile from Google Sheets CSV
     */
    suspend fun fetchChurchProfile(csvUrl: String): ChurchProfile? {
        return try {
            val csv = httpClient.get(csvUrl).bodyAsText()

            if (csv.trim().startsWith("<!DOCTYPE") || csv.trim().startsWith("<html")) {
                return null
            }

            parseCsvToChurchProfile(csv)
        } catch (e: Exception) {
            println("Error fetching church profile: ${e.message}")
            null
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // CSV PARSING - Events
    // ═══════════════════════════════════════════════════════════════

    private fun parseCsvToEvents(csv: String): List<Event> {
        val lines = csv.lines().filter { it.isNotBlank() }
        if (lines.isEmpty()) return emptyList()

        val headers = lines.first().split(",").map { it.trim().lowercase() }

        return lines.drop(1).mapNotNull { line ->
            try {
                val values = parseCsvLine(line)
                val data = headers.zip(values).toMap()

                Event(
                    id = data["id"] ?: return@mapNotNull null,
                    title = data["title"] ?: "",
                    date = parseDate(data["date"]),
                    category = data["category"] ?: "",
                    location = data["location"] ?: "",
                    description = data["description"] ?: "",
                    topic = data["topic"] ?: "",
                    bibleVerse = data["bibleverse"] ?: "",
                    picCouncilMember = data["piccouncilmember"] ?: "",
                    onDutyCouncilMembers = data["ondutycouncilmembers"]?.split(";")?.map { it.trim() } ?: emptyList(),
                    isFeatured = data["isfeatured"]?.lowercase() in listOf("true", "yes", "1")
                )
            } catch (e: Exception) {
                println("Error parsing event row: $line - ${e.message}")
                null
            }
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // CSV PARSING - News
    // ═══════════════════════════════════════════════════════════════

    private fun parseCsvToNews(csv: String): List<NewsItem> {
        val lines = csv.lines().filter { it.isNotBlank() }
        if (lines.isEmpty()) return emptyList()

        val headers = lines.first().split(",").map { it.trim().lowercase() }

        return lines.drop(1).mapNotNull { line ->
            try {
                val values = parseCsvLine(line)
                val data = headers.zip(values).toMap()

                NewsItem(
                    id = data["id"] ?: return@mapNotNull null,
                    headline = data["headline"] ?: "",
                    publishDate = parseDate(data["publishdate"]),
                    author = data["author"] ?: "",
                    body = data["body"] ?: "",
                    category = data["category"] ?: "",
                    scriptureReference = data["scripturereference"] ?: "",
                    isUrgent = data["isurgent"]?.lowercase() in listOf("true", "yes", "1"),
                    photoURL = data["photourl"] ?: "",
                    relatedEventId = data["relatedeventid"] ?: ""
                )
            } catch (e: Exception) {
                println("Error parsing news row: $line - ${e.message}")
                null
            }
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // CSV PARSING - Church Profile
    // ═══════════════════════════════════════════════════════════════

    private fun parseCsvToChurchProfile(csv: String): ChurchProfile? {
        val lines = csv.lines().filter { it.isNotBlank() }
        if (lines.size < 2) return null

        val headers = lines.first().split(",").map { it.trim().lowercase() }
        val values = parseCsvLine(lines[1])
        val data = headers.zip(values).toMap()

        return try {
            ChurchProfile(
                name = data["name"] ?: "",
                logoURL = data["logourl"] ?: "",
                welcomeMessage = data["welcomemessage"] ?: "",
                address = data["address"] ?: "",
                phone = data["phone"] ?: "",
                website = data["website"] ?: "",
                email = data["email"] ?: "",
                mission = data["mission"] ?: "",
                serviceTimes = data["servicetimes"] ?: "",
                socialFacebook = data["socialfacebook"] ?: ""
            )
        } catch (e: Exception) {
            println("Error parsing church profile: ${e.message}")
            null
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // CSV LINE PARSING (Handles quoted fields with commas)
    // ═══════════════════════════════════════════════════════════════

    private fun parseCsvLine(line: String): List<String> {
        val result = mutableListOf<String>()
        val current = StringBuilder()
        var inQuotes = false

        for (char in line) {
            when {
                char == '"' -> inQuotes = !inQuotes
                char == ',' && !inQuotes -> {
                    result.add(current.toString().trim())
                    current.clear()
                }
                else -> current.append(char)
            }
        }
        result.add(current.toString().trim())

        return result
    }

    // ═══════════════════════════════════════════════════════════════
    // DATE PARSING (Multi-format support)
    // ═══════════════════════════════════════════════════════════════

    private fun parseDate(dateString: String?): Instant {
        if (dateString.isNullOrBlank()) return Clock.System.now()

        // Try multiple date formats
        val formats = listOf(
            "yyyy-MM-dd",           // ISO date (2024-10-20)
            "yyyy/MM/dd",           // Slash format (2024/10/20)
            "dd/MM/yyyy",           // European (20/10/2024)
            "MM/dd/yyyy",           // US format (10/20/2024)
            "yyyy-MM-dd'T'HH:mm:ss" // ISO datetime
        )

        for (format in formats) {
            try {
                return when (format) {
                    "yyyy-MM-dd", "yyyy/MM/dd" -> {
                        val parts = dateString.split("-", "/")
                        if (parts.size == 3) {
                            val year = parts[0].toInt()
                            val month = parts[1].toInt()
                            val day = parts[2].toInt()
                            LocalDate(year, month, day)
                                .atStartOfDayIn(TimeZone.currentSystemDefault())
                        } else {
                            continue
                        }
                    }
                    "dd/MM/yyyy" -> {
                        val parts = dateString.split("/")
                        if (parts.size == 3) {
                            val day = parts[0].toInt()
                            val month = parts[1].toInt()
                            val year = parts[2].toInt()
                            LocalDate(year, month, day)
                                .atStartOfDayIn(TimeZone.currentSystemDefault())
                        } else {
                            continue
                        }
                    }
                    "MM/dd/yyyy" -> {
                        val parts = dateString.split("/")
                        if (parts.size == 3) {
                            val month = parts[0].toInt()
                            val day = parts[1].toInt()
                            val year = parts[2].toInt()
                            LocalDate(year, month, day)
                                .atStartOfDayIn(TimeZone.currentSystemDefault())
                        } else {
                            continue
                        }
                    }
                    else -> {
                        try {
                            LocalDateTime.parse(dateString)
                                .toInstant(TimeZone.currentSystemDefault())
                        } catch (e: Exception) {
                            continue
                        }
                    }
                }
            } catch (e: Exception) {
                continue
            }
        }

        // Fallback to current time if all parsing fails
        println("Failed to parse date: $dateString, using current time")
        return Clock.System.now()
    }
}
