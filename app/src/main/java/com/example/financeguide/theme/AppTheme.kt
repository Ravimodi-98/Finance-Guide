package com.example.financeguide.theme

enum class AppTheme {
    FINANCE_PRO,
    ONE_PIECE,
    SPIDER_MAN;

    companion object {
        fun fromString(valStr: String): AppTheme {
            return when (valStr.lowercase()) {
                "one_piece", "one piece", "pirate" -> ONE_PIECE
                "spider_man", "spiderman", "hero" -> SPIDER_MAN
                else -> FINANCE_PRO
            }
        }
    }
}
