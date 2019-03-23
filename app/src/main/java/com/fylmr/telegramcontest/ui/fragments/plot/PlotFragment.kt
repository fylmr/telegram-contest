package com.fylmr.telegramcontest.ui.fragments.plot

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fylmr.telegramcontest.R
import kotlinx.android.synthetic.main.fragment_plot.*

class PlotFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_plot, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val values = intArrayOf(4, 18, 42, 15, 19, 30, 15, 33, 26, 19, 40, 2, 36, 10, 18, 20, 4)
        plot_view.values = values
        plot_view.visibleValues = 5
        plot_navigation.values = values
        plot_navigation.visibleValues = 5

        plot_view.onDragListener = {
            plot_navigation.drag(it)
        }
        plot_view.onScaleListener = {
            plot_navigation.onScale(it)
        }

        plot_navigation.onDragListener = {
            plot_view.drag(it)
        }
        plot_navigation.onScaleListener = {
            plot_view.onScale(it)
        }
    }

}