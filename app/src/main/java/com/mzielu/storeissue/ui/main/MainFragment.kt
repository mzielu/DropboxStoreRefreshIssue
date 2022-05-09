package com.mzielu.storeissue.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mzielu.storeissue.databinding.MainFragmentBinding

//default starting file generated by Android Studio
class MainFragment : Fragment() {
    private val mainViewModel: MainViewModel by viewModels()
    private var _binding: MainFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MainFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.refreshBtn.setOnClickListener { mainViewModel.refresh() }
        return root
    }

    override fun onResume() {
        super.onResume()
        mainViewModel.start()
    }

    override fun onPause() {
        super.onPause()
        mainViewModel.stop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = MainFragment()
    }
}