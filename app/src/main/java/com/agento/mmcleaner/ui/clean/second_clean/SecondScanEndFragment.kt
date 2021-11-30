package com.agento.mmcleaner.ui.clean.second_clean

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.agento.mmcleaner.R
import com.agento.mmcleaner.scan_util.model.JunkInfo
import com.agento.mmcleaner.ui.clean.second_clean.adapters.OnChangeProgramCheckedListener
import com.agento.mmcleaner.ui.clean.second_clean.adapters.RunProgramsAdapter
import com.agento.mmcleaner.ui.clean.third_clean.ThirdOptimizationFragment
import com.agento.mmcleaner.util.UStats
import com.agento.mmcleaner.util.Util
import com.agento.mmcleaner.util.UtilPhoneInfo

class SecondScanEndFragment : Fragment(R.layout.fragment_second_scan_end) {

    lateinit var thisView: View
    lateinit var countApp: TextView
    lateinit var clearBtn: AppCompatButton
    lateinit var runAppsList: RecyclerView
    var usage = mutableListOf<JunkInfo>()
    lateinit var adapter: RunProgramsAdapter
    companion object{
        var procentUse = 0
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        thisView = view
        initViews()
    }

    private fun initViews(){
        countApp = thisView.findViewById(R.id.count_app)
        clearBtn = thisView.findViewById(R.id.clear_btn)
        runAppsList = thisView.findViewById(R.id.run_apps)
        usage = UStats.getUsageStatsList(requireContext(), true)
        val memData:TextView = thisView.findViewById(R.id.mem_data)
        if(usage.size>0) {
            val usageStat = JunkInfo()
            usage.add(0, usageStat)
        }

        val totalRam = UtilPhoneInfo.getTotalRAM()
        val totalRamDigit = totalRam.substring(0, totalRam.indexOf(" ")).toLong()
        memData.text = "${(totalRamDigit.toDouble() *procentUse.toDouble())/100.0} GB / ${UtilPhoneInfo.getTotalRAM()}"

        countApp.text = usage.size.toString()

        initList()

        clearBtn.setOnClickListener {
            SecondOptimizationFragment.usage = usage.filter { junkInfo -> junkInfo.isCheck } as MutableList<JunkInfo>
            openNextStep()
        }
    }

    private fun initList() {
        runAppsList.layoutManager = LinearLayoutManager(requireContext())
        for(i in 1 until usage.size){
            if(UtilPhoneInfo.toNormalFormat(usage[i].mSize.toDouble()).equals("-0 KB"))
                usage.removeAt(i)
        }
        adapter = RunProgramsAdapter(usage, requireActivity() as AppCompatActivity, object :
            OnChangeProgramCheckedListener {
            override fun onChange(positionProgram: Int) {
                if (positionProgram != 0) {
                    usage[positionProgram].isCheck = !usage[positionProgram].isCheck
                    var countChecked = 0
                    for (i in 1 until usage.size) {
                        if(usage[i].isCheck)
                            countChecked++

                    }
                    if(countChecked == 0)
                        usage[0].isCheck = false
                }else{
                    usage[0].isCheck = !usage[0].isCheck
                    for(i in 1 until usage.size){
                        usage[i].isCheck = usage[0].isCheck

                    }
                }
                adapter.setData(usage)
            }
        })
        runAppsList.adapter = adapter
    }

    private fun openNextStep(){
        val controller = NavHostFragment.findNavController(this@SecondScanEndFragment)
        controller.navigate(R.id.fragment_second_optimization, null, Util.generateNavOptions())
    }
}