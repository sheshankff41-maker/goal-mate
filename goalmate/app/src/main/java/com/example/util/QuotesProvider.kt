package com.example.util

data class MotivationalQuote(
    val quote: String,
    val author: String,
    val category: String = "Productivity"
)

object QuotesProvider {
    val quotes = listOf(
        MotivationalQuote(
            quote = "Setting goals is the first step in turning the invisible into the visible.",
            author = "Tony Robbins",
            category = "Vision"
        ),
        MotivationalQuote(
            quote = "Small daily improvements over time lead to stunning results.",
            author = "Robin Sharma",
            category = "Habits"
        ),
        MotivationalQuote(
            quote = "Action is the foundational key to all success.",
            author = "Pablo Picasso",
            category = "Execution"
        ),
        MotivationalQuote(
            quote = "You don't have to be great to start, but you have to start to be great.",
            author = "Zig Ziglar",
            category = "Courage"
        ),
        MotivationalQuote(
            quote = "Discipline is the bridge between goals and accomplishment.",
            author = "Jim Rohn",
            category = "Discipline"
        ),
        MotivationalQuote(
            quote = "The secret of getting ahead is getting started.",
            author = "Mark Twain",
            category = "Focus"
        ),
        MotivationalQuote(
            quote = "What you get by achieving your goals is not as important as what you become by achieving your goals.",
            author = "Zig Ziglar",
            category = "Growth"
        ),
        MotivationalQuote(
            quote = "Focus on being productive instead of busy.",
            author = "Tim Ferriss",
            category = "Productivity"
        ),
        MotivationalQuote(
            quote = "We are what we repeatedly do. Excellence, then, is not an act, but a habit.",
            author = "Will Durant",
            category = "Habits"
        ),
        MotivationalQuote(
            quote = "The future depends on what you do today.",
            author = "Mahatma Gandhi",
            category = "Daily Action"
        ),
        MotivationalQuote(
            quote = "A goal without a timeline is just a dream.",
            author = "Robert Herjavec",
            category = "Planning"
        ),
        MotivationalQuote(
            quote = "It always seems impossible until it's done.",
            author = "Nelson Mandela",
            category = "Perseverance"
        )
    )

    fun getRandomQuote(): MotivationalQuote {
        return quotes.random()
    }

    fun getDailyQuote(seedDate: String): MotivationalQuote {
        val hash = seedDate.hashCode()
        val index = kotlin.math.abs(hash) % quotes.size
        return quotes[index]
    }
}
