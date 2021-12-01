package com.agento.mmcleaner.ui.clean.first_clean.adapters

interface OnChangeStepCheckedListener {
    fun onChange(positionProgram: Int, positionStep: Int)
    fun onChangeStep(positionStep: Int)
}