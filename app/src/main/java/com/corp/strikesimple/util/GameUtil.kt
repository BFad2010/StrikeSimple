package com.corp.strikesimple.util

import com.corp.data.model.Frame
import java.util.UUID
import javax.inject.Inject

class GameUtil @Inject constructor() {
    fun generateGameId() = UUID.randomUUID().toString()

    companion object {
        fun List<Frame>.onTenthFrame(): Boolean {
            val players = this.map { it.player }
            val frameCount = players.map { player ->
                this.filter { it.player == player }.size
            }
            return frameCount.any { it == 10 }
        }
    }
}