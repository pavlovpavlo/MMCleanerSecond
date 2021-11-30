package com.agento.mmcleaner.ui.clean.first_clean.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.recyclerview.widget.RecyclerView
import com.agento.mmcleaner.R
import com.agento.mmcleaner.scan_util.model.JunkInfo
import com.agento.mmcleaner.ui.clean.second_clean.adapters.OnChangeProgramCheckedListener
import com.agento.mmcleaner.util.UtilPhoneInfo

class JunkStepsProgramsAdapter(private var mList: List<JunkInfo>,
                               private val listener: OnChangeProgramCheckedListener
) :
    RecyclerView.Adapter<JunkStepsProgramsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_first_process_program, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val programData = mList[position]

        holder.programName.text = programData.name

        holder.programSize.text = UtilPhoneInfo.toNormalFormat(programData.mSize.toDouble())
        holder.checkbox.isChecked = programData.isCheck

        holder.checkbox.setOnCheckedChangeListener { compoundButton, b ->
            run {
                listener.onChange(position)
            }
        }
    }

    fun setData(list: List<JunkInfo>) {
        mList = list
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val programName: TextView = itemView.findViewById(R.id.program_name)
        val programSize: TextView = itemView.findViewById(R.id.program_size)
        val checkbox: AppCompatCheckBox = itemView.findViewById(R.id.checkbox)
    }
}