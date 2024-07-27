package com.example.addingnumbers.presentation

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.addingnumbers.R
import com.example.addingnumbers.databinding.FragmentGameBinding
import com.example.addingnumbers.domain.entity.GameResult
import com.example.addingnumbers.domain.entity.Level


class GameFragment : Fragment() {

    private val args by navArgs<GameFragmentArgs>()

    private val viewModelFactory by lazy {
        GameViewModelFactory(args.level, requireActivity().application)
    }
    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[GameViewModel::class.java]
    }

    private val tvOptions by lazy {
        mutableListOf<TextView>().apply {
            add(binding.tvOption1)
            add(binding.tvOption2)
            add(binding.tvOption3)
            add(binding.tvOption4)
            add(binding.tvOption5)
            add(binding.tvOption6)
        }
    }

    private var _binding: FragmentGameBinding? = null
    private val binding: FragmentGameBinding
        get() = _binding ?: throw RuntimeException("FragmentGameBinding == null")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observers()
        listeners()
    }

    private fun listeners() {
        for (option in tvOptions) {
            option.setOnClickListener {
                viewModel.chooseAnswer(option.text.toString().toInt())
            }
        }
    }

    private fun observers() {
        viewModel.formattedTime.observe(requireActivity(), Observer {
            binding.tvTimer.text = it
        })
        viewModel.question.observe(requireActivity(), Observer {
            with(binding) {
                tvAnswer.text = it.sum.toString()
                tvFirstNumber.text = it.visibleNumber.toString()
                for (i in 0 until tvOptions.size) {
                    tvOptions[i].text = it.options[i].toString()
                }
            }
        })
        viewModel.percentOfRightAnswers.observe(viewLifecycleOwner) {
            binding.progress.setProgress(it, true)
        }
        viewModel.progressAnswers.observe(viewLifecycleOwner) {
            binding.tvQuantityOfRightAnswers.text = it
        }
        viewModel.enoughCount.observe(viewLifecycleOwner) {
            val color = getColor(it)
            binding.tvQuantityOfRightAnswers.setTextColor(color)
        }
        viewModel.enoughPercent.observe(viewLifecycleOwner) {
            val color = getColor(it)
            binding.progress.progressTintList = ColorStateList.valueOf(color)
        }
        viewModel.gameResult.observe(viewLifecycleOwner) {
            launchGameFinishedFragment(it)
        }
        viewModel.minPercent.observe(viewLifecycleOwner) {
            binding.progress.secondaryProgress = it
        }

    }

    private fun getColor(goodState: Boolean): Int {
        val colorResId = if (goodState) {
            android.R.color.holo_green_light
        } else {
            android.R.color.holo_red_light
        }
        return ContextCompat.getColor(requireContext(), colorResId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun launchGameFinishedFragment(gameResult: GameResult) {
        findNavController().navigate(
            GameFragmentDirections.actionGameFragmentToGameFinishedFragment(gameResult)
        )
    }
}