package org.luckgg.currencyexchange.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse(
    val meta: MetaData,
    val data: Map<String, Currency>,
)

@Serializable
data class MetaData(
    @SerialName("last_updated_at")
    val lastUpdated: String,
)

@Serializable
data class Currency(
    val code: String,
    val value: Double,
)
