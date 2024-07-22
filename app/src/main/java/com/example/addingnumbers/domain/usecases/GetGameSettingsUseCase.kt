package com.example.addingnumbers.domain.usecases

import com.example.addingnumbers.domain.entity.GameSettings
import com.example.addingnumbers.domain.entity.Level
import com.example.addingnumbers.domain.repository.GameRepository

class GetGameSettingsUseCase(private val repository: GameRepository) {

    operator fun invoke(level: Level): GameSettings {
        return repository.getGameSettings(level)
    }
}