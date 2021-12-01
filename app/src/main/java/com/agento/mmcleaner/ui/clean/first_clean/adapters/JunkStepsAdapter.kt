package com.agento.mmcleaner.ui.clean.first_clean.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.agento.mmcleaner.R
import com.agento.mmcleaner.scan_util.model.JunkGroup
import com.agento.mmcleaner.ui.clean.second_clean.adapters.OnChangeProgramCheckedListener
import com.agento.mmcleaner.util.UtilPhoneInfo

class JunkStepsAdapter(
    private var mList: List<JunkGroup>,
    private val listener: OnChangeStepCheckedListener
) :
    RecyclerView.Adapter<JunkStepsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_first_process, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val groupData = mList[position]

        if (groupData.mChildren.isEmpty()) {
            holder.groupSize.visibility = View.INVISIBLE
            holder.groupCheckbox.visibility = View.GONE
            holder.groupMinus.visibility = View.VISIBLE
        } else {
            holder.groupSize.visibility = View.VISIBLE
            holder.groupCheckbox.visibility = View.VISIBLE
            holder.groupMinus.visibility = View.GONE
            holder.groupSize.text = UtilPhoneInfo.toNormalFormat(groupData.mSize.toDouble())
            val adapter = JunkStepsProgramsAdapter(
                groupData.mChildren,
                object : OnChangeProgramCheckedListener {
                    override fun onChange(positionProgram: Int) {
                        listener.onChange(positionProgram, position)
                    }

                })
            holder.listProgram.layoutManager = LinearLayoutManager(holder.itemView.context)
            holder.listProgram.adapter = adapter

            if (groupData.isOpen) {
                holder.openContainer.visibility = View.VISIBLE
                holder.groupArrow.setImageResource(R.drawable.ic_arrow_up)
                holder.groupName.setTextColor(holder.itemView.resources.getColor(R.color.primary))
                holder.groupImage.setColorFilter(
                    ContextCompat.getColor(holder.itemView.context, R.color.primary)
                )
            } else {
                holder.openContainer.visibility = View.GONE
                holder.groupArrow.setImageResource(R.drawable.ic_arrow_down)
                holder.groupName.setTextColor(holder.itemView.resources.getColor(R.color.color_333A44))
                holder.groupImage.setColorFilter(
                    ContextCompat.getColor(holder.itemView.context, R.color.color_8E9AAB)
                )
            }

            holder.itemView.setOnClickListener {
                mList[position].isOpen = !groupData.isOpen
                notifyItemChanged(position)
            }
        }
        holder.groupName.text = when (groupData.mType) {
            JunkGroup.GROUP_APK -> holder.itemView.context.getString(R.string.apk_files)
            JunkGroup.GROUP_TEMPORARY_FILES -> holder.itemView.context.getString(R.string.temporary_files)
            JunkGroup.GROUP_ADVERTISING -> holder.itemView.context.getString(R.string.advertising_rub)
            JunkGroup.GROUP_CACHE -> holder.itemView.context.getString(R.string.app_cache)
            else -> ""
        }

        holder.groupImage.setImageResource(
            when (groupData.mType) {
                JunkGroup.GROUP_APK -> R.drawable.ic_apk_files
                JunkGroup.GROUP_TEMPORARY_FILES -> R.drawable.ic_temporary_files
                JunkGroup.GROUP_ADVERTISING -> R.drawable.ic_adver_rubbish
                JunkGroup.GROUP_CACHE -> R.drawable.ic_app_cache
                else -> R.drawable.ic_app_cache
            }
        )

        holder.groupCheckbox.isChecked = groupData.isCheck
        holder.groupCheckbox.setOnClickListener {
            listener.onChangeStep(position)
        }
    }

    fun setData(list: List<JunkGroup>) {
        mList = list
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val groupImage: ImageView = itemView.findViewById(R.id.group_image)
        val groupArrow: ImageView = itemView.findViewById(R.id.group_arrow)
        val groupMinus: ImageView = itemView.findViewById(R.id.group_minus)
        val groupName: TextView = itemView.findViewById(R.id.group_name)
        val groupSize: TextView = itemView.findViewById(R.id.group_size)
        val openContainer: LinearLayout = itemView.findViewById(R.id.open_container)
        val listProgram: RecyclerView = itemView.findViewById(R.id.list_program)
        val groupCheckbox: AppCompatCheckBox = itemView.findViewById(R.id.checkbox)
    }
}