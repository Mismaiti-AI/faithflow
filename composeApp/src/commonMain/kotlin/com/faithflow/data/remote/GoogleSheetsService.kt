package com.faithflow.data.remote

import com.faithflow.domain.model.ChurchProfile
import com.faithflow.domain.model.Event
import com.faithflow.domain.model.NewsItem
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class GoogleSheetsService(
    private val httpClient: HttpClient
) {
    suspend fun fetchEvents(sheetUrl: String): List<Event> {
        val csvUrl = convertToExportUrl(sheetUrl, findTabName("Events"))
        val csvText = httpClient.get(csvUrl).bodyAsText()

        if (!isValidCsv(csvText)) {
            throw Exception("Invalid CSV response - might be an error page")
        }

        return parseEventsCsv(csvText)
    }

    suspend fun fetchNewsItems(sheetUrl: String): List<NewsItem> {
        val csvUrl = convertToExportUrl(sheetUrl, findTabName("News"))
        val csvText = httpClient.get(csvUrl).bodyAsText()

        if (!isValidCsv(csvText)) {
            throw Exception("Invalid CSV response - might be an error page")
        }

        return parseNewsCsv(csvText)
    }

    suspend fun fetchChurchProfile(sheetUrl: String): ChurchProfile? {
        val csvUrl = convertToExportUrl(sheetUrl, findTabName("Profile"))
        val csvText = httpClient.get(csvUrl).bodyAsText()

        if (!isValidCsv(csvText)) {
            return null
        }

        return parseProfileCsv(csvText)
    }

    private fun isValidCsv(text: String): Boolean {
        val trimmed = text.trim()
        return !trimmed.startsWith("<!DOCTYPE") &&
                !trimmed.startsWith("<html") &&
                trimmed.isNotEmpty()
    }

    private fun convertToExportUrl(sheetUrl: String, tabName: String = ""): String {
        // Extract spreadsheet ID
        val idPattern = Regex("/d/([a-zA-Z0-9-_]+)")
        val matchResult = idPattern.find(sheetUrl)
        val spreadsheetId = matchResult?.groupValues?.get(1)
            ?: throw IllegalArgumentException("Invalid Google Sheets URL")

        // If tab name provided, try to find its GID (for now use default)
        return "https://docs.google.com/spreadsheets/d/$spreadsheetId/export?format=csv"
    }

    private fun findTabName(tabName: String): String = tabName

    private fun parseEventsCsv(csv: String): List<Event> {
        val lines = csv.lines().filter { it.isNotBlank() }
        if (lines.isEmpty()) return emptyList()

        val headers = lines.first().split(",").map { it.trim().lowercase() }

        return lines.drop(1).mapNotNull { line ->
            try {
                val values = parseCsvLine(line)
                val data = headers.zip(values).toMap()

                Event(
                    id = data["id"] ?: generateId(),
                    title = data["title"] ?: "",
                    date = parseDate(data["date"]),
                    category = data["category"] ?: "",
                    location = data["location"] ?: "",
                    description = data["description"] ?: "",
                    topic = data["topic"] ?: "",
                    bibleVerse = data["bibleverse"] ?: data["bible_verse"] ?: "",
                    picCouncilMember = data["piccouncilmember"] ?: data["pic_council_member"] ?: "",
                    onDutyCouncilMembers = parseList(data["ondutycouncilmembers"] ?: data["on_duty_council_members"] ?: ""),
                    isFeatured = parseBoolean(data["isfeatured"] ?: data["is_featured"])
                )
            } catch (e: Exception) {
                println("Error parsing event row: $line - ${e.message}")
                null
            }
        }
    }

    private fun parseNewsCsv(csv: String): List<NewsItem> {
        val lines = csv.lines().filter { it.isNotBlank() }
        if (lines.isEmpty()) return emptyList()

        val headers = lines.first().split(",").map { it.trim().lowercase() }

        return lines.drop(1).mapNotNull { line ->
            try {
                val values = parseCsvLine(line)
                val data = headers.zip(values).toMap()

                NewsItem(
                    id = data["id"] ?: generateId(),
                    headline = data["headline"] ?: "",
                    publishDate = parseDate(data["publishdate"] ?: data["publish_date"]),
                    author = data["author"] ?: "",
                    body = data["body"] ?: "",
                    category = data["category"] ?: "",
                    scriptureReference = data["scripturereference"] ?: data["scripture_reference"] ?: "",
                    isUrgent = parseBoolean(data["isurgent"] ?: data["is_urgent"]),
                    photoURL = data["photourl"] ?: data["photo_url"] ?: "",
                    relatedEventId = data["relatedeventid"] ?: data["related_event_id"] ?: ""
                )
            } catch (e: Exception) {
                println("Error parsing news row: $line - ${e.message}")
                null
            }
        }
    }

    private fun parseProfileCsv(csv: String): ChurchProfile? {
        val lines = csv.lines().filter { it.isNotBlank() }
        if (lines.size < 2) return null

        val headers = lines.first().split(",").map { it.trim().lowercase() }
        val values = parseCsvLine(lines[1])
        val data = headers.zip(values).toMap()

        return try {
            ChurchProfile(
                name = data["name"] ?: "",
                logoURL = data["logourl"] ?: data["logo_url"] ?: "",
                welcomeMessage = data["welcomemessage"] ?: data["welcome_message"] ?: "",
                address = data["address"] ?: "",
                phone = data["phone"] ?: "",
                website = data["website"] ?: "",
                email = data["email"] ?: "",
                mission = data["mission"] ?: "",
                serviceTimes = data["servicetimes"] ?: data["service_times"] ?: "",
                socialFacebook = data["socialfacebook"] ?: data["social_facebook"] ?: ""
            )
        } catch (e: Exception) {
            println("Error parsing church profile: ${e.message}")
            null
        }
    }

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

    private fun parseDate(dateString: String?): Instant {
        if (dateString.isNullOrBlank()) return Instant.DISTANT_PAST

        // Try multiple date formats
        val formats = listOf(
            dateString, // ISO format: 2024-10-20T10:00:00Z
            "${dateString}T00:00:00Z", // Date only: 2024-10-20
            dateString.replace("/", "-") + "T00:00:00Z" // Slash format: 10/20/2024
        )

        for (format in formats) {
            try {
                return Instant.parse(format)
            } catch (e: Exception) {
                continue
            }
        }

        // If all fail, try manual parsing for common formats
        return try {
            parseDateManually(dateString)
        } catch (e: Exception) {
            println("Could not parse date: $dateString")
            Instant.DISTANT_PAST
        }
    }

    private fun parseDateManually(dateString: String): Instant {
        // Handle formats like: MM/DD/YYYY, DD/MM/YYYY, YYYY-MM-DD
        val parts = dateString.split("/", "-")
        if (parts.size != 3) throw IllegalArgumentException("Invalid date format")

        val (first, second, third) = parts.map { it.toIntOrNull() ?: throw IllegalArgumentException("Invalid date parts") }

        // Determine format
        val (year, month, day) = when {
            first > 1000 -> Triple(first, second, third) // YYYY-MM-DD
            third > 1000 -> Triple(third, first, second) // MM/DD/YYYY or DD/MM/YYYY
            else -> throw IllegalArgumentException("Cannot determine year")
        }

        // Create ISO string
        val isoString = String.format("%04d-%02d-%02dT00:00:00Z", year, month, day)
        return Instant.parse(isoString)
    }

    private fun parseList(value: String): List<String> {
        return value.split(",", ";").map { it.trim() }.filter { it.isNotEmpty() }
    }

    private fun parseBoolean(value: String?): Boolean {
        return value?.lowercase() in listOf("true", "1", "yes", "y")
    }

    private fun generateId(): String {
        return "gen_${Instant.now().toEpochMilliseconds()}_${(0..9999).random()}"
    }
}
