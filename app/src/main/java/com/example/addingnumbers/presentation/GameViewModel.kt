package com.example.addingnumbers.presentation

import android.app.Application
import android.content.Context
import android.os.CountDownTimer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.addingnumbers.R
import com.example.addingnumbers.data.GameRepositoryImpl
import com.example.addingnumbers.domain.entity.GameResult
import com.example.addingnumbers.domain.entity.GameSettings
import com.example.addingnumbers.domain.entity.Level
import com.example.addingnumbers.domain.entity.Question
import com.example.addingnumbers.domain.usecases.GenerateQuestionUseCase
import com.example.addingnumbers.domain.usecases.GetGameSettingsUseCase

class GameViewModel(
    private val application: Application,
    private val level: Level
) : ViewModel() {

    private lateinit var settings: GameSettings
    private val repository = GameRepositoryImpl
    private val generateQuestionUseCase = GenerateQuestionUseCase(repository)
    private val getGameSettingsUseCase = GetGameSettingsUseCase(repository)

    private var timer:CountDownTimer? = null

    private val _formattedTime = MutableLiveData<String>()
    val formattedTime: LiveData<String>
        get() = _formattedTime

    private val _question = MutableLiveData<Question>()
    val question : LiveData<Question>
        get() = _question

    private var countOfRightAnswers = 0
    private var countOfQuestions = 0

    private val _percentOfRightAnswers = MutableLiveData<Int>()
    val percentOfRightAnswers : LiveData<Int>
        get() = _percentOfRightAnswers

    private val _progressAnswers = MutableLiveData<String>()
    val progressAnswers : LiveData<String>
        get() = _progressAnswers

    private val _enoughCount = MutableLiveData<Boolean>()
    val enoughCount: LiveData<Boolean>
        get() = _enoughCount

    private val _enoughPercent = MutableLiveData<Boolean>()
    val enoughPercent: LiveData<Boolean>
        get() = _enoughPercent

    private val _gameResult = MutableLiveData<GameResult>()
    val gameResult : LiveData<GameResult>
        get() = _gameResult

    private val _minPercent = MutableLiveData<Int>()
    val minPercent : LiveData<Int>
        get() = _minPercent

    init {
        startGame()
    }

    private fun startGame() {
        getGameSettings()
        startTimer()
        generateQuestion()
        updateProgress()
    }

    private fun generateQuestion() {
        _question.value = generateQuestionUseCase(settings.maxSumValue)
    }

    fun chooseAnswer(number: Int) {
        checkAnswer(number)
        countOfQuestions++
        updateProgress()
        generateQuestion()
    }

    private fun updateProgress(){
        val percents = calculateProgressInPercents()
        _percentOfRightAnswers.value = percents
        _progressAnswers.value = String.format(
            application.resources.getString(R.string.right_answers),
            countOfRightAnswers.toString(),
            settings.minCountOfRightAnswers.toString()
        )
        _enoughCount.value = countOfRightAnswers >= settings.minCountOfRightAnswers
        _enoughPercent.value = percents >= settings.minPercentOfRightAnswers
    }

    private fun calculateProgressInPercents(): Int{
        if (countOfQuestions == 0) {
            return 0
        }
        return ((countOfRightAnswers / countOfQuestions.toDouble()) * 100).toInt()
    }

    private fun checkAnswer(number: Int) {
        val rightAnswer = question.value?.rightAnswer
        if (rightAnswer == number) {
            countOfRightAnswers++
        }
    }

    private fun getGameSettings() {
        settings = getGameSettingsUseCase(level)
        _minPercent.value = settings.minPercentOfRightAnswers
    }

    private fun startTimer() {
        timer = object : CountDownTimer(
            settings.gameTimeInSeconds * MILLIS_IN_SECONDS,
            MILLIS_IN_SECONDS
        ) {
            override fun onTick(millisUntilFinished: Long) {
                _formattedTime.value = formatTime(millisUntilFinished)
            }

            override fun onFinish() {
                finishGame()
            }
        }
        timer?.start()
    }

    private fun finishGame() {
        _gameResult.value = GameResult(
            enoughCount.value == true && enoughPercent.value == true,
            countOfRightAnswers,
            countOfQuestions,
            settings
        )
    }

    private fun formatTime(millisUntilFinished: Long): String {
        val seconds = millisUntilFinished / MILLIS_IN_SECONDS
        val minutes = seconds / SECONDS_IN_MINUTES
        val leftSeconds = seconds - (minutes * SECONDS_IN_MINUTES)
        return String.format("%02d:%02d", minutes, leftSeconds)
    }

    override fun onCleared() {
        super.onCleared()
        timer?.cancel()
    }

    companion object {

        private const val SECONDS_IN_MINUTES = 60

        private const val MILLIS_IN_SECONDS = 1000L
    }
}