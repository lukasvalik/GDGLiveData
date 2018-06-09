package com.lukasvalik.gdglivedata.deprecated

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.lukasvalik.gdglivedata.extensions.Function2
import com.lukasvalik.gdglivedata.extensions.zip

class oldactivity : AppCompatActivity() {

    val appleCount = MutableLiveData<Int>()
    val bananaCount = MutableLiveData<Int>()
    var fruitCount = MutableLiveData<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        binding?.let {
            binding.setLifecycleOwner(this)
            binding.fruitCount = fruitCount
        }
        */
        appleCount.value = 2
        bananaCount.value = 3
    }

    override fun onResume() {
        super.onResume()

        fruitCount.zip(appleCount, bananaCount, this, object : Function2<Int, Int, Int> {
            override fun apply(t1: Int?, t2: Int?): Int {
                return when{
                    t1 == null && t2 == null -> 0
                    t1 == null -> t2!!
                    t2 == null -> t1
                    else -> t1 + t2
                }
            }
        }).observe(this, Observer { Log.d("FRUIT", it.toString()) })
        //fruitCount.value = 5

        appleCount.observe(this, Observer { Log.d("APPLES", it.toString()) })
    }

    /*class ViewModel {
        val appleCount = MutableLiveData<Int>()
        val bananaCount = MutableLiveData<Int>()
        val fruitCount : MutableLiveData<Int> = MediatorLiveData<Int>()
        val text = MediatorLiveData<String>()

        init {
            appleCount.value = 2
            bananaCount.value = 3
            text.value = "Shit"

            fruitCount.zip(appleCount, bananaCount, object : Function2<Int, Int, Int>{
                override fun apply(t1: Int?, t2: Int?): Int {
                    return when {
                        t1 == null && t2 == null -> 0
                        t1 == null -> t2!!
                        t2 == null -> t1
                        else -> t1 + t2
                    }
                }
            }).observeInBackground(Observer { Log.d("FRUIT", it.toString()) })
        }
    }*/
}