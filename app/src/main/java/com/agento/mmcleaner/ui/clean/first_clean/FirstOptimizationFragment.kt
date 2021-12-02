package com.agento.mmcleaner.ui.clean.first_clean

import android.animation.Animator
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.agento.mmcleaner.MyApplication
import com.agento.mmcleaner.R
import com.agento.mmcleaner.scan_util.OverallScanTask
import com.agento.mmcleaner.scan_util.SysCacheScanTask
import com.agento.mmcleaner.scan_util.callback.IScanCallback
import com.agento.mmcleaner.scan_util.model.JunkGroup
import com.agento.mmcleaner.scan_util.model.JunkInfo
import com.agento.mmcleaner.ui.BaseFragment
import com.agento.mmcleaner.ui.clean.first_clean.adapters.JunkStepsAdapter
import com.agento.mmcleaner.ui.clean.first_clean.adapters.OnChangeStepCheckedListener
import com.agento.mmcleaner.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*


class FirstOptimizationFragment : BaseFragment(R.layout.fragment_first_optimization) {

    lateinit var loader: ImageView
    lateinit var bg_optim: ImageView
    lateinit var bg_optim_green: ImageView
    lateinit var titleText: TextView
    private lateinit var unncessary: TextView
    private lateinit var unncessaryType: TextView
    lateinit var clearBtn: AppCompatButton
    lateinit var list: RecyclerView
    lateinit var thisView: View
    lateinit var adapter: JunkStepsAdapter
    lateinit var loaderAnimation: Animation
    var processes: MutableList<JunkGroup> = mutableListOf()
    var isCacheScanning = false
    var isAdvertisingScanning = false
    var isTemporaryScanning = false
    var isApkScanning = false
    var allSize = 0.0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MyApplication.get().setCurrentScreen(5)
        thisView = view
        //allSize = requireArguments().getDouble("unncessary")
        initViews()
    }

    private fun initViews() {
        bg_optim = thisView.findViewById(R.id.bg_optim)
        bg_optim_green = thisView.findViewById(R.id.bg_optim_green)
        loader = thisView.findViewById(R.id.loader_optimization)
        list = thisView.findViewById(R.id.process_recycler)
        titleText = thisView.findViewById(R.id.title_text)
        clearBtn = thisView.findViewById(R.id.clear_btn)
        unncessary = thisView.findViewById(R.id.unncessary)
        unncessaryType = thisView.findViewById(R.id.unncessary_type)


        clearBtn.setOnClickListener { deleteAllItems() }

        startAnimation()
        initList()

        if (!UtilPermissions.isPermissionDenied(requireActivity() as AppCompatActivity, false)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                imitateList()
            } else {
                scanCacheApp()
                scanBadFiles()
            }
        } else {
            imitateList()
        }
    }

    private fun imitateList() {
        val randomInstallApps = UStats.getRandomJunkApps(requireContext())
        MainScope().launch {
            delay(700)
            generateEmptyAPK()
            delay(900)
            generateEmptyTemporary()
            delay(1500)
            generateEmptyCache(randomInstallApps as ArrayList<JunkInfo>)
            delay(1200)
            generateAdvertising(randomInstallApps)
            checkScanning()
        }
        MainScope().launch {
            for (i in 0..25) {
                if (!titleText.text.equals("Done...")) {
                    titleText.text = getRandomString(35)
                    delay(150)
                }
            }
        }
    }

    private fun calculateUncessary() {
        allSize = 0.0
        for (process in processes) {
            for (info in process.mChildren) {
                allSize += info.mSize
            }
        }

        val unncessaryCount = UtilPhoneInfo.toNormalFormat(allSize, "#.#")
        unncessary.text = unncessaryCount.substring(0, unncessaryCount.indexOf(" "))
        unncessaryType.text = unncessaryCount.substring(unncessaryCount.indexOf(" ") + 1)
    }

    private fun generateEmptyCache(apps: ArrayList<JunkInfo>) {
        val cacheGroup =
            JunkGroup()
        cacheGroup.mType = JunkGroup.GROUP_CACHE
        cacheGroup.mChildren = apps
        for (info in cacheGroup.mChildren) {
            cacheGroup.mSize += info.mSize
        }
        processes.add(cacheGroup)
        adapter.setData(processes)
        isCacheScanning = true
        calculateUncessary()
    }

    private fun generateEmptyTemporary() {
        val cacheGroup =
            JunkGroup()
        cacheGroup.mType = JunkGroup.GROUP_TEMPORARY_FILES
        cacheGroup.mChildren = generateRandomFiles(".temp")
        for (info in cacheGroup.mChildren) {
            cacheGroup.mSize += info.mSize
        }
        processes.add(cacheGroup)
        adapter.setData(processes)
        isTemporaryScanning = true
        calculateUncessary()
    }

    private fun generateEmptyAPK() {
        val cacheGroup =
            JunkGroup()
        cacheGroup.mType = JunkGroup.GROUP_APK
        cacheGroup.mChildren = generateRandomFiles(".apk")
        for (info in cacheGroup.mChildren) {
            cacheGroup.mSize += info.mSize
        }
        processes.add(cacheGroup)
        adapter.setData(processes)
        isApkScanning = true
        calculateUncessary()
    }

    private fun generateRandomFiles(type: String): ArrayList<JunkInfo> {
        val randomFiles = ArrayList<JunkInfo>()

        for (i in 0 until 3) {
            var string = ""
            string += getRandomString((2..3).random())
            string += "-"
            string += getRandomString((2..3).random())
            string += type

            val junkInfo = JunkInfo()
            junkInfo.name = string
            val x = 3072L
            val y = 61440L
            val r = Random()
            val number = x + (r.nextDouble() * (y - x)).toLong()
            junkInfo.mSize = number
            randomFiles.add(junkInfo)
        }

        return randomFiles
    }

    fun getRandomString(length: Int): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

    private fun initList() {
        list.layoutManager = LinearLayoutManager(requireContext())
        adapter = JunkStepsAdapter(processes, object : OnChangeStepCheckedListener {
            override fun onChange(positionProgram: Int, positionStep: Int) {
                processes[positionStep].mChildren[positionProgram].isCheck =
                    !processes[positionStep].mChildren[positionProgram].isCheck

                var countChecked = 0
                for (i in 0 until processes[positionStep].mChildren.size) {
                    if (processes[positionStep].mChildren[i].isCheck)
                        countChecked++

                }
                if (countChecked == 0) {
                    processes[positionStep].isCheck = false

                }
                list.post { adapter.setData(processes) }
            }

            override fun onChangeStep(positionStep: Int) {
                processes[positionStep].isCheck = !processes[positionStep].isCheck

                for (i in 0 until processes[positionStep].mChildren.size)
                    processes[positionStep].mChildren[i].isCheck =
                        processes[positionStep].isCheck

                list.post { adapter.setData(processes) }
            }

        })
        list.adapter = adapter
    }

    private fun startAnimation() {
        loaderAnimation =
            AnimationUtils.loadAnimation(context, R.anim.animation_loader)
        loader.animation = loaderAnimation
    }

    private fun stopAnimation() {
        loader.clearAnimation()
        loaderAnimation.cancel()
        loaderAnimation.reset()
    }

    private fun checkScanning() {
        if (isAdvertisingScanning && isApkScanning && isCacheScanning && isTemporaryScanning) {
            clearBtn.visibility = View.VISIBLE
            stopAnimation()
            titleText.text = getString(R.string.done)
            loader.visibility = View.INVISIBLE
        }
    }

    private fun scanCacheApp() {
        val sysCacheScanTask = SysCacheScanTask(object :
            IScanCallback {
            override fun onBegin() {

            }

            override fun onProgress(info: JunkInfo?) {

            }

            override fun onFinish(children: ArrayList<JunkInfo>) {
                MainScope().launch {
                    val cacheGroup =
                        JunkGroup()
                    cacheGroup.mType = JunkGroup.GROUP_CACHE
                    cacheGroup.mChildren.addAll(
                        UStats.filter(
                            children[0].mChildren,
                            requireContext()
                        )
                    )
                    cacheGroup.mChildren.sort()
                    cacheGroup.mChildren.reverse()
                    for (info in cacheGroup.mChildren) {
                        cacheGroup.mSize += info.mSize
                    }
                    processes.add(cacheGroup)
                    adapter.setData(processes)
                    generateAdvertising(cacheGroup.mChildren)
                    isCacheScanning = true
                    checkScanning()
                    calculateUncessary()
                }

            }
        })
        sysCacheScanTask.execute()
    }

    private fun generateAdvertising(apps: ArrayList<JunkInfo>) {
        var advertisingList = mutableListOf<JunkInfo>()
        for (i in 0..(0..apps.size / 2).random()) {
            val program = apps.random()
            program.mSize = program.mSize / 4
            advertisingList.add(program)
        }

        advertisingList = ArrayList(HashSet(advertisingList))

        val cacheGroup = JunkGroup()
        cacheGroup.mType = JunkGroup.GROUP_ADVERTISING
        cacheGroup.mChildren.addAll(advertisingList)
        cacheGroup.mChildren.sort()
        cacheGroup.mChildren.reverse()
        for (info in advertisingList) {
            cacheGroup.mSize += info.mSize
        }
        processes.add(cacheGroup)
        adapter.setData(processes)

        isAdvertisingScanning = true
        calculateUncessary()
    }

    private fun scanBadFiles() {
        val overallScanTask = OverallScanTask(object :
            IScanCallback {
            override fun onBegin() {
            }

            override fun onProgress(info: JunkInfo?) {
            }

            override fun onFinish(children: ArrayList<JunkInfo>) {

                MainScope().launch {
                    var map: MutableMap<Int, JunkInfo> = mutableMapOf()
                    map[JunkGroup.GROUP_APK] = JunkInfo()
                    map[JunkGroup.GROUP_TEMPORARY_FILES] = JunkInfo()

                    for (info in children) {
                        val path: String = info.mChildren.get(0).mPath
                        var groupFlag = 0
                        isApkScanning = true
                        isTemporaryScanning = true
                        if (path.endsWith(".apk")) {
                            groupFlag = JunkGroup.GROUP_APK
                        } else if (path.endsWith(".log") || path.endsWith(".tmp") || path.endsWith(".temp")) {
                            groupFlag = JunkGroup.GROUP_TEMPORARY_FILES
                        }

                        map[groupFlag] = info

                        checkScanning()
                    }

                    for ((key, value) in map) {
                        val cacheGroup =
                            JunkGroup()
                        cacheGroup.mType = key
                        cacheGroup.mChildren.addAll(
                            UStats.filter(
                                value.mChildren,
                                requireContext()
                            )
                        )
                        cacheGroup.mSize = value.mSize
                        processes.add(cacheGroup)
                        adapter.setData(processes)
                    }
                    calculateUncessary()
                }
            }
        })
        overallScanTask.execute()
    }

    private fun deleteItem(rowView: View, position: Int) {
        val anim = AnimationUtils.loadAnimation(
            requireContext(),
            android.R.anim.slide_out_right
        )
        anim.duration = 500
        rowView.startAnimation(anim)
        Handler().postDelayed(Runnable {
            processes.removeAt(position) //Remove the current content from the array
            adapter.notifyDataSetChanged() //Refresh list
        }, anim.duration)
    }

    var mStopHandler = false

    private fun deleteAllItems() {
        titleText.text = getString(R.string.cleaning)
        MainScope().launch(context = Dispatchers.Main) {
            if (!UtilPermissions.isPermissionDenied(
                    requireActivity() as AppCompatActivity,
                    false
                ) && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q
            ) {
                clearJunk()
            }
            for (i in 0 until processes.size) {

                try {
                    val v: View = list.findViewHolderForAdapterPosition(
                        (list.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                    )!!.itemView
                    deleteItem(
                        v,
                        (list.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                    )
                    calculateUncessary()
                    if (i != (processes.size - 1))
                        delay(1050)
                } catch (e: Exception) {
                }

            }
            optimizationComplete()
        }
    }

    private fun clearJunk() {
        val allProcess: MutableList<JunkGroup> = mutableListOf()
        allProcess.addAll(processes)
        val clearThread = Thread {

            for (i in 0 until allProcess.size) {
                if (allProcess[i].isCheck) {
                    if (allProcess[i].mType == JunkGroup.GROUP_APK) {
                        val junks =
                            allProcess[i].mChildren.filter { junkInfo -> junkInfo.isCheck } as ArrayList<JunkInfo>

                        CleanUtil().freeAllAppsCache(junks)
                    }
                    if (allProcess[i].mType == JunkGroup.GROUP_TEMPORARY_FILES || allProcess[i].mType == JunkGroup.GROUP_APK) {
                        val junks =
                            allProcess[i].mChildren.filter { junkInfo -> junkInfo.isCheck } as ArrayList<JunkInfo>

                        CleanUtil().freeJunkInfos(junks)
                    }
                }
            }
        }
        clearThread.start()
    }

    private fun optimizationComplete() {
        bg_optim_green.animate().alpha(1f).setDuration(1200).setListener(object : Animator.AnimatorListener{
            override fun onAnimationStart(p0: Animator?) {
                
            }

            override fun onAnimationEnd(p0: Animator?) {
                startAds();
                SingletonClassApp.getInstance().start_ads = 1;
            }

            override fun onAnimationCancel(p0: Animator?) {
                
            }

            override fun onAnimationRepeat(p0: Animator?) {
                
            }

        }).start()
        bg_optim.animate().alpha(0f).setDuration(800).start()
        //bg_optim.setImageResource(R.drawable.green_bg)

//        val intent = Intent(requireContext(), FirstOptimizationEndActivity::class.java)
//        intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
//        startActivity(intent)
//        requireActivity().finish()
    }

}