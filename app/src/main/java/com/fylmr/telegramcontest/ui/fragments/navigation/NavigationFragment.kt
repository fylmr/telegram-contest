package com.fylmr.telegramcontest.ui.fragments.navigation

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fylmr.telegramcontest.R
import com.fylmr.telegramcontest.ui.activities.MainActivity
import com.fylmr.telegramcontest.ui.fragments.plot.PlotFragment
import kotlinx.android.synthetic.main.fragment_navigation.*

class NavigationFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_navigation, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setListeners()
    }

    private fun setListeners() {
        btn_plot.setOnClickListener {
            openFragment(PlotFragment())
        }
    }

    private fun openFragment(fragment: Fragment) {
        (activity as MainActivity).startFragment(fragment)
    }

}