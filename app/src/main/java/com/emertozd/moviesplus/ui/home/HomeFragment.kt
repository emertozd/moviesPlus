package com.emertozd.moviesplus.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.emertozd.moviesplus.R
import com.emertozd.moviesplus.databinding.FragmentHomeBinding
import com.emertozd.moviesplus.ui.home.HomeViewModel.Event.*
import com.emertozd.moviesplus.util.observeInLifecycle
import com.skydoves.bindables.BindingFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.onEach


@AndroidEntryPoint
class HomeFragment : BindingFragment<FragmentHomeBinding>(R.layout.fragment_home) {

    private val vm: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        return binding {
            lifecycleOwner = this@HomeFragment
            viewModel = vm
            fragmentHomeRvPopular.setHasFixedSize(true)
        }.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm.eventsFlow.onEach {
            when (it) {
                ShowNoDataWarning -> {
                    vm.toast = getString(R.string.error_no_more_data_found)
                }
            }
        }.observeInLifecycle(viewLifecycleOwner)
    }
}