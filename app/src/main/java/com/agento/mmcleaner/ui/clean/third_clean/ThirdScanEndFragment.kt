package com.agento.mmcleaner.ui.clean.third_clean

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.agento.mmcleaner.MyApplication
import com.agento.mmcleaner.R
import com.agento.mmcleaner.scan_util.model.JunkInfo
import com.agento.mmcleaner.ui.clean.second_clean.adapters.OnChangeProgramCheckedListener
import com.agento.mmcleaner.ui.clean.second_clean.adapters.RunProgramsAdapter
import com.agento.mmcleaner.util.UStats
import com.agento.mmcleaner.util.Util
import com.agento.mmcleaner.util.UtilPhoneInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class ThirdScanEndFragment : Fragment(R.layout.fragment_third_scan_end) {

    lateinit var thisView: View
    lateinit var countApp: TextView
    lateinit var clearBtn: AppCompatButton
    lateinit var runAppsList: RecyclerView
    var usage = mutableListOf<JunkInfo>()
    lateinit var adapter: RunProgramsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MyApplication.get().setCurrentScreen(12)
        thisView = view
        initViews()
    }

    private fun initViews() {
        runAppsList = thisView.findViewById(R.id.run_apps)
        countApp = thisView.findViewById(R.id.count_app)
        clearBtn = thisView.findViewById(R.id.clear_btn)
        runAppsList = thisView.findViewById(R.id.run_apps)
        usage = UStats.getUsageStatsList(requireContext(), true)
        if (usage.size > 0) {
            val usageStat = JunkInfo()
            usage.add(0, usageStat)
        }

        countApp.text = usage.size.toString()
        initList()

        clearBtn.setOnClickListener {
            ThirdOptimizationFragment.usage =
                usage.filter { junkInfo -> junkInfo.isCheck } as MutableList<JunkInfo>
            openNextStep()
        }
    }

    private fun initList() {
        runAppsList.layoutManager = LinearLayoutManager(requireContext())
        for (i in 1 until usage.size) {
            if (UtilPhoneInfo.toNormalFormat(usage[i].mSize.toDouble()).equals("-0 KB"))
                usage.removeAt(i)
        }
        adapter = RunProgramsAdapter(usage, requireActivity() as AppCompatActivity, object :
            OnChangeProgramCheckedListener {
            override fun onChange(positionProgram: Int) {
                if (positionProgram != 0) {
                    usage[positionProgram].isCheck = !usage[positionProgram].isCheck
                    var countChecked = 0
                    for (i in 1 until usage.size) {
                        if (usage[i].isCheck)
                            countChecked++

                    }
                    if (countChecked == 0)
                        usage[0].isCheck = false
                } else {
                    usage[0].isCheck = !usage[0].isCheck
                    for (i in 1 until usage.size) {
                        usage[i].isCheck = usage[0].isCheck

                    }
                }
                adapter.setData(usage)
            }
        })
        runAppsList.adapter = adapter
    }

    private fun deleteItem(rowView: View, position: Int) {
        val anim = AnimationUtils.loadAnimation(
            requireContext(),
            android.R.anim.slide_out_right
        )
        anim.duration = 500
        rowView.startAnimation(anim)
        Handler().postDelayed(Runnable {
            usage.removeAt(position) //Remove the current content from the array
            adapter.notifyDataSetChanged() //Refresh list
        }, anim.duration)
    }

    private fun deleteAllItems() {
        kotlinx.coroutines.GlobalScope.launch(context = Dispatchers.Main) {
            for (i in 0 until usage.size) {
                val v: View = runAppsList.findViewHolderForAdapterPosition(
                    (runAppsList.layoutManager as LinearLayoutManager)
                        .findFirstVisibleItemPosition()
                )!!.itemView
                deleteItem(
                    v, 0
                )
                if (i != (usage.size - 1))
                    delay(1050)
            }
            openNextStep()
        }
    }

    private fun openNextStep() {
        val controller = NavHostFragment.findNavController(this@ThirdScanEndFragment)
        controller.navigate(R.id.fragment_third_optimization, null, Util.generateNavOptions())
    }
}