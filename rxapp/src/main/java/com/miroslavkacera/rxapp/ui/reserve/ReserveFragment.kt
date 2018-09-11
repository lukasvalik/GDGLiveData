package com.miroslavkacera.rxapp.ui.reserve

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.miroslavkacera.rxapp.databinding.FragmentReserveBinding
import com.miroslavkacera.rxapp.repository.HotelRepository
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class ReserveFragment : Fragment() {

    @Inject
    lateinit var repository: HotelRepository

    private lateinit var navController: NavController

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentReserveBinding.inflate(layoutInflater, container, false)
        val viewModel = ViewModelProviders.of(this,
                ReserveFragmentViewModel.Factory(repository, arguments?.getString("hotelName")!!))
                .get(ReserveFragmentViewModel::class.java)

        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                // Respond to the action bar's Up/Home button
                return navController.navigateUp()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}