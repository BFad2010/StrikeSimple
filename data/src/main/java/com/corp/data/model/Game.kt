package com.corp.data.model

data class Frame(
    val id: String,
    val number: String,
    val player: String,
    val pins: List<Int>,
    val bonusThrow: Int?,
    val secondBonusThrow: Int?,
)

data class Game(
    val id: String,
    val players: List<String>,
    val frames: List<Frame>,
    val bonusThrow: Int?,
    val isComplete: Boolean,
)

data class Final(
    val id: String,
    val winner: String,
    val score: Int,
)