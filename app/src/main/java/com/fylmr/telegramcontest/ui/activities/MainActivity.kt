package com.fylmr.telegramcontest.ui.activities

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.fylmr.telegramcontest.ui.fragments.navigation.NavigationFragment
import com.fylmr.telegramcontest.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_holder)

        startFragment(NavigationFragment(), false)
    }

    fun startFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        val transaction = supportFragmentManager.beginTransaction()

        if (addToBackStack)
            transaction.addToBackStack("$fragment")

        transaction.replace(R.id.fl_base, fragment)
            .commit()
    }
}
