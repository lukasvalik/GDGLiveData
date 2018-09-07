package com.miroslavkacera.rxapp.ui.presentation

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.miroslavkacera.rxapp.databinding.FragmentPresentationBinding
import com.miroslavkacera.rxapp.db.HotelsDatabase
import com.miroslavkacera.rxapp.remote.HotelService
import com.miroslavkacera.rxapp.repository.HotelRepositoryImpl

class HotelsFragment : Fragment() {

    private lateinit var binding: FragmentPresentationBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPresentationBinding.inflate(inflater, container, false)
        binding.viewModel = ViewModelProviders.of(this,
                HotelsFragmentViewModel.Factory(HotelRepositoryImpl(HotelsDatabase.getInstance(context!!).hotelDao(), HotelService.API)))
                .get(HotelsFragmentViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}