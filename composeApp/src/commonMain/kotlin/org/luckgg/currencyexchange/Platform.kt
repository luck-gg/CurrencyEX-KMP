package org.luckgg.currencyexchange

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform