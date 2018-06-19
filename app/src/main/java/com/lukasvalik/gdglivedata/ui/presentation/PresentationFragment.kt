package com.lukasvalik.gdglivedata.ui.presentation

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.lukasvalik.gdglivedata.App
import com.lukasvalik.gdglivedata.R
import com.lukasvalik.gdglivedata.databinding.FragmentPresentationBinding
import com.lukasvalik.gdglivedata.reObserve

class PresentationFragment : Fragment() {

    private lateinit var binding: FragmentPresentationBinding
    private lateinit var viewModel: PresentationVM

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentPresentationBinding.inflate(inflater, container, false)
        binding.setLifecycleOwner(this)
        binding.button.setOnClickListener({ Navigation.findNavController(binding.root).navigate(R.id.reserve)})
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val factory = PresentationVM.Factory(App.instance.hotelRepository)
        viewModel = ViewModelProviders.of(this, factory).get(PresentationVM::class.java)
        binding.vm = viewModel

        // A ViewModel must never reference a view, Lifecycle, or any class that may hold a reference to the activity context.
        // TODO test addObserver while do not keep activities - it will count down twice as much
        lifecycle.reObserve(viewModel.timer)
    }
}