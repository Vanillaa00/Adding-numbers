package com.example.addingnumbers.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.addingnumbers.R
import com.example.addingnumbers.databinding.FragmentGameFinishedBinding
import com.example.addingnumbers.domain.entity.GameResult

class GameFinishedFragment : Fragment() {

    private lateinit var gameResult: GameResult

    private var _binding: FragmentGameFinishedBinding? = null
    private val binding: FragmentGameFinishedBinding
        get() = _binding ?: throw RuntimeException("FragmentGameFinishedBinding = null")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseArgs()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameFinishedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBackPressed()
        bindViews(gameResult)
    }

    private fun onBackPressed() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                tryAgain()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun parseArgs() {
        requireArguments().getParcelable<GameResult>(KEY_GAME_RESULT)?.let {
            gameResult = it
        }
    }

    private fun tryAgain(){
        requireActivity().supportFragmentManager.popBackStack(
            GameFragment.NAME,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )
    }

    private fun bindViews(gameResult: GameResult) {
        with(binding) {
            buttonTryAgain.setOnClickListener {
                tryAgain()
            }
            emojiResult.setImageResource(getSmileResId())
            tvQuantityOfRightAnswers.text = String.format(
                getString(R.string.quantity_of_answers_u_need),
                gameResult.gameSettings.minCountOfRightAnswers.toString()
            )
            tvScore.text = String.format(
                getString(R.string.score),
                gameResult.countOfRightAnswers.toString()
            )
            tvQuantityOfPercents.text = String.format(
                getString(R.string.percent_of_right_answers_u_need),
                gameResult.gameSettings.minPercentOfRightAnswers.toString()
            )
            tvScorePercent.text = String.format(
                getString(R.string.score_in_percent),
                getPercentOfRightAnswers().toString()
            )
        }
    }

    private fun getPercentOfRightAnswers() = with(gameResult){
        if (countOfRightAnswers == 0) {
            0
        } else {
            ((countOfRightAnswers / countOfQuestions.toDouble()) * 100).toInt()
        }
    }

    private fun getSmileResId(): Int {
        return if (gameResult.winner) {
            R.drawable.ic_smile
        } else {
            R.drawable.ic_sad
        }
    }

    companion object {

        private const val KEY_GAME_RESULT = "game_result"

        fun newInstance(gameResult: GameResult): GameFinishedFragment {
            return GameFinishedFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_GAME_RESULT, gameResult)
                }
            }
        }
    }
}