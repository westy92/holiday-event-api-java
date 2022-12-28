# The Official Holiday and Event API for Java and Kotlin

[![Build Status](https://github.com/westy92/holiday-event-api-java/actions/workflows/github-actions.yml/badge.svg)](https://github.com/westy92/holiday-event-api-java/actions)
[![Code Coverage](https://codecov.io/gh/westy92/holiday-event-api-java/branch/main/graph/badge.svg)](https://codecov.io/gh/westy92/holiday-event-api-java)
[![Funding Status](https://img.shields.io/github/sponsors/westy92)](https://github.com/sponsors/westy92)

Industry-leading Holiday and Event API for Java and Kotlin. Over 5,000 holidays and thousands of descriptions. Trusted by the World’s leading companies. Built by developers for developers since 2011.

## Supported Java Versions

Latest version of the the Holiday and Event API supports all currently-supported Java [releases](https://endoflife.date/java) and is also fully compatible with Kotlin.

## Authentication

Access to the Holiday and Event API requires an API Key. You can get for one for FREE [here](https://apilayer.com/marketplace/checkiday-api#pricing), no credit card required! Note that free plans are limited. To access more data and have more requests, a paid plan is required.

## Installation

```kotlin
implementation("com.westy92.holiday-event-api:holiday-event-api:1.0.0")
```

## Example

```kotlin
try {
    // Get a FREE API key from https://apilayer.com/marketplace/checkiday-api#pricing
    val client = Client("<your API key>")

    // Get Events for a given Date
    val events = client.getEvents(
        // These parameters are the defaults but can be specified:
        // date = "today",
        // timezone = "America/Chicago",
        // adult = false,
    )

    val event = events.events[0]
    println("Today is ${event.name}! Find more information at: ${event.url}.")
    println("Rate limit remaining: ${events.rateLimit.remainingMonth}/${events.rateLimit.limitMonth} (month).")

    // Get Event Information
    val eventInfo = client.getEventInfo(
        id = event.id,
        // These parameters can be specified to calculate the range of eventInfo.Event.Occurrences
        // start = 2020,
        // end = 2030,
    )

    println("The Event's hashtags are ${eventInfo.event.hashtags}.")

    // Search for Events
    val query = "zucchini"
    val search = client.search(
        query = query,
        // These parameters are the defaults but can be specified:
        // adult = false,
    )

    println("Found ${search.events.size} events, including '${search.events[0].name}', that match the query '${query}'.")
} catch (e: Exception) {
    println(e)
}
```
