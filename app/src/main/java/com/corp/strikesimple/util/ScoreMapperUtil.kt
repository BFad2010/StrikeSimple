package com.corp.strikesimple.util

import com.corp.data.model.Frame

class ScoreMapperUtil {
    companion object {
        private const val strike = "X"
        private const val spare = "/"

        fun Frame.isStrike(): Boolean = pins.firstOrNull()?.toInt() == 10

        fun Frame.isSpare(): Boolean {
            val first = pins.firstOrNull()?.toInt() ?: 0
            val second = pins.getOrNull(1)?.toInt() ?: 0
            return first + second == 10 && !isStrike()
        }

        fun Frame.formatThrows(): Triple<String, String, String> {
            val first = pins.firstOrNull()?.toInt() ?: 0
            val second = pins.getOrNull(1)?.toInt() ?: 0
            val isStrike = first == 10
            val isSpare = first + second == 10 && !isStrike

            val firstThrow = when {
                isStrike -> ""
                first == 0 -> "-"
                else -> first.toString()
            }

            val secondThrow = when {
                isStrike -> "X"
                isSpare -> "/"
                second == 0 -> "-"
                else -> second.toString()
            }

            val thirdThrow = if (number == "10") {
                if (bonusThrow == 10) strike else bonusThrow?.toString() ?: "-"
            } else ""
            return Triple(firstThrow, secondThrow, thirdThrow)
        }


        fun List<Frame>.determineScores(): List<String> {
            val frames = this
            var cumulativeScore = 0
            return mapIndexed { index, frame ->
                val score = when (frame.determineScore()) {
                    strike -> {
                        if (index == 9) {
                            cumulativeScore += 10 + (frame.pins.getOrNull(1)
                                ?: 0) + (frame.bonusThrow ?: 0)
                            cumulativeScore.toString()
                        } else if (index + 1 < size) {
                            val nextThrow = frames[index + 1].pins.first()
                            if (nextThrow == 10 && index + 2 < size) {
                                cumulativeScore += 10 + nextThrow + frames[index + 2].pins.first()
                            } else {
                                cumulativeScore += 10 + nextThrow + frames[index + 1].pins.last()
                            }
                            cumulativeScore.toString()
                        } else ""
                    }

                    spare -> {
                        if (index == 9) {
                            cumulativeScore += 10 + (frame.bonusThrow ?: 0)
                            cumulativeScore.toString()
                        } else if (index + 1 < size) {
                            cumulativeScore += 10 + frames[index + 1].pins.first()
                            cumulativeScore.toString()
                        } else ""
                    }

                    else -> {
                        cumulativeScore += frame.pins.sum()
                        cumulativeScore.toString()
                    }
                }
                score
            }
        }

        fun List<Frame>.determineTotalScore(): Int {
            val scores = determineScores().filter { it.isNotEmpty() }
            return scores.lastOrNull()?.toIntOrNull() ?: 0
        }


        private fun Frame.determineScore(): String {
            val first = pins.firstOrNull()?.toInt() ?: 0
            val second = pins.getOrNull(1)?.toInt() ?: 0
            return when {
                first == 10 -> strike
                first + second == 10 -> spare
                else -> (first + second).toString()
            }
        }
    }
}