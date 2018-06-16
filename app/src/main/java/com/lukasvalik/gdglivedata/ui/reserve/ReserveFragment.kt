package com.lukasvalik.gdglivedata.ui.reserve

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lukasvalik.gdglivedata.databinding.FragmentReserveBinding
import com.lukasvalik.gdglivedata.extensions.observeInBackground

class ReserveFragment : Fragment() {

    private lateinit var binding: FragmentReserveBinding
    private lateinit var viewModel: ReserveVM
    private val requestObserver : Observer<Boolean> = Observer {}

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentReserveBinding.inflate(inflater, container, false)
        binding.setLifecycleOwner(this)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ReserveVM::class.java)
        binding.vm = viewModel

        viewModel.isSessionActive().observe(this, Observer { Log.d("SESSION", if (it == false) "closed" else "active") } )
        viewModel.checkBoxChecked.observe(this, Observer { onCheckBoxClicked(it) } )
        viewModel.getChainRequestResult().observe(this, requestObserver)
    }

    /**
     * change observer, reset session and start loading again
     */
    private fun onCheckBoxClicked(checked: Boolean?) {
        if (checked != false) {
            viewModel.getChainRequestResult().removeObserver(requestObserver)
            viewModel.getChainRequestResult().observeInBackground(requestObserver)
        } else {
            // we do not remove previous observer since we remove observerInBackground right after finishing task
            viewModel.getChainRequestResult().observe(this, requestObserver)
        }
        viewModel.startLoading()
    }
}