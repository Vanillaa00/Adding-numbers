package com.example.addingnumbers.domain.repository

import com.example.addingnumbers.domain.entity.GameSettings
import com.example.addingnumbers.domain.entity.Level
import com.example.addingnumbers.domain.entity.Question

interface GameRepository {

    fun generateQuestion(maxSumValue: Int, countOfOptions: Int): Question

    fun getGameSettings(level: Level): GameSettings
}