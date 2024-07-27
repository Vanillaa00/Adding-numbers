package com.example.addingnumbers.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.addingnumbers.R
import com.example.addingnumbers.databinding.FragmentGameFinishedBinding
import com.example.addingnumbers.domain.entity.GameResult

class GameFinishedFragment : Fragment() {

    val args by navArgs<GameFinishedFragmentArgs>()

    private var _binding: FragmentGameFinishedBinding? = null
    private val binding: FragmentGameFinishedBinding
        get() = _binding ?: throw RuntimeException("FragmentGameFinishedBinding = null")

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
        bindViews()
    }

    private fun onBackPressed() {
        tryAgain()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun tryAgain(){
        findNavController().popBackStack()
    }

    private fun bindViews() {
        with(binding) {
            buttonTryAgain.setOnClickListener {
                tryAgain()
            }
            emojiResult.setImageResource(getSmileResId())
            tvQuantityOfRightAnswers.text = String.format(
                getString(R.string.quantity_of_answers_u_need),
                args.gameResult.gameSettings.minCountOfRightAnswers.toString()
            )
            tvScore.text = String.format(
                getString(R.string.score),
                args.gameResult.countOfRightAnswers.toString()
            )
            tvQuantityOfPercents.text = String.format(
                getString(R.string.percent_of_right_answers_u_need),
                args.gameResult.gameSettings.minPercentOfRightAnswers.toString()
            )
            tvScorePercent.text = String.format(
                getString(R.string.score_in_percent),
                getPercentOfRightAnswers().toString()
            )
        }
    }

    private fun getPercentOfRightAnswers() = with(args.gameResult){
        if (countOfRightAnswers == 0) {
            0
        } else {
            ((countOfRightAnswers / countOfQuestions.toDouble()) * 100).toInt()
        }
    }

    private fun getSmileResId(): Int {
        return if (args.gameResult.winner) {
            R.drawable.ic_smile
        } else {
            R.drawable.ic_sad
        }
    }
}