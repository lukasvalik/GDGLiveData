package com.miroslavkacera.rxapp.ui.presentation

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.miroslavkacera.rxapp.R
import com.miroslavkacera.rxapp.databinding.FragmentPresentationBinding
import com.miroslavkacera.rxapp.repository.HotelRepository
import dagger.android.support.AndroidSupportInjection
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject


class HotelsFragment : Fragment() {

    @Inject
    lateinit var repository: HotelRepository
    private lateinit var binding: FragmentPresentationBinding

    private val disposables = CompositeDisposable()

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPresentationBinding.inflate(inflater, container, false)
        val fragmentViewModel = ViewModelProviders.of(this,
                HotelsFragmentViewModel.Factory(repository))
                .get(HotelsFragmentViewModel::class.java)

        disposables.add(fragmentViewModel.buttonSignal.subscribe({ arguments ->
                Navigation.findNavController(binding.root).navigate(R.id.reserve, arguments)
        }, { error -> error.printStackTrace() }))

        lifecycle.addObserver(fragmentViewModel)
        binding.viewModel = fragmentViewModel
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()

        disposables.clear()
    }
}