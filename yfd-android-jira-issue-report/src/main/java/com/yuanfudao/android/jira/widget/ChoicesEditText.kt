package com.yuanfudao.android.jira.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import com.yuanfudao.android.jira.R

/**
 * Created by meng on 2018/5/10.
 */
class ChoicesEditText : AutoCompleteTextView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    var choices: List<String> = listOf()
        set(value) {
            field = value
            setupChoices()
        }

    private fun setupChoices() {
        setAdapter(ArrayAdapter<String>(context, R.layout.jira_spinner_dropdown_item, choices))
        setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                showDropDown()
                true
            } else {
                false
            }
        }
    }
}
