package com.illusion.checkfirm.settings

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.messaging.FirebaseMessaging
import com.illusion.checkfirm.R
import com.illusion.checkfirm.database.BookmarkDB
import com.illusion.checkfirm.database.BookmarkDBHelper
import java.util.ArrayList

class InfoCatcherActivity : AppCompatActivity(), CompoundButton.OnCheckedChangeListener {

    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var mEditor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_catcher)

        sharedPrefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
        mEditor = sharedPrefs.edit()
        val one = sharedPrefs.getBoolean("one", true)
        val catcher = sharedPrefs.getBoolean("catcher", false)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = ""
        setSupportActionBar(toolbar)
        val mAppBar = findViewById<AppBarLayout>(R.id.appbar)
        val height = (resources.displayMetrics.heightPixels * 0.3976)
        val lp = mAppBar.layoutParams
        lp.height = height.toInt()
        if (one) {
            mAppBar.setExpanded(true)
        } else {
            mAppBar.setExpanded(false)
        }

        val title = findViewById<TextView>(R.id.title)
        val expandedTitle = findViewById<TextView>(R.id.expanded_title)
        mAppBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, _ ->
            val percentage = (appBarLayout.y / appBarLayout.totalScrollRange)
            expandedTitle.alpha = 1 - (percentage * 2 * -1)
            title.alpha = percentage * -1
        })

        val switchText = findViewById<TextView>(R.id.switch_text)
        val catcherSwitch = findViewById<SwitchMaterial>(R.id.catcher_switch)
        if (catcher) {
            catcherSwitch.isChecked = true
            switchText.text = getString(R.string.switch_on)
        } else {
            catcherSwitch.isChecked = false
            switchText.text = getString(R.string.switch_off)
        }

        catcherSwitch.setOnCheckedChangeListener(this)

        val catcherLayout = findViewById<ConstraintLayout>(R.id.catcher_layout)
        catcherLayout.setOnClickListener {
            catcherSwitch.toggle()
        }

        val bookmarkList = ArrayList<BookmarkDB>()
        val bookmarkChipGroup = findViewById<ChipGroup>(R.id.chipGroup)
        val bookmarkHelper = BookmarkDBHelper(this)
        bookmarkList.addAll(bookmarkHelper.allBookmarkDB)
        if (bookmarkList.isEmpty()) {
            bookmarkChipGroup.visibility = View.GONE
        } else {
            bookmarkChipGroup.visibility = View.VISIBLE
        }

        val model = findViewById<TextInputEditText>(R.id.model)
        val csc = findViewById<TextInputEditText>(R.id.csc)

        for (i in bookmarkList.indices) {
            val bookmarkChip = Chip(this)
            bookmarkChip.text = bookmarkList[i].name
            bookmarkChip.isCheckable = false
            bookmarkChip.setOnClickListener {
                model.setText(bookmarkList[i].model)
                csc.setText(bookmarkList[i].csc)
            }
            bookmarkChipGroup.addView(bookmarkChip)
        }

        val sharedModel = sharedPrefs.getString("catcher_model", "SM-") as String
        if (sharedModel.isBlank()) {
            model.setText(getString(R.string.default_string))
        } else {
            model.setText(sharedModel)
        }
        model.setSelection(model.text!!.length)

        val sharedCSC = sharedPrefs.getString("catcher_csc", "") as String
        csc.setText(sharedCSC)

        val saveButton = findViewById<MaterialButton>(R.id.save)
        saveButton.setOnClickListener {
            if (sharedModel.isNotBlank() && sharedCSC.isNotBlank()) {
                FirebaseMessaging.getInstance().unsubscribeFromTopic(sharedModel+sharedCSC)
            }
            val modelText = model.text!!.trim().toString().toUpperCase()
            val cscText = csc.text!!.trim().toString().toUpperCase()

            if (modelText.isBlank() || cscText.isBlank()) {
                Toast.makeText(this, getString(R.string.info_catcher_error), Toast.LENGTH_SHORT).show()
            } else {
                mEditor.putString("catcher_model", modelText)
                mEditor.putString("catcher_csc", cscText)
                mEditor.apply()
                FirebaseMessaging.getInstance().subscribeToTopic(modelText+cscText)
                Toast.makeText(this, getString(R.string.welcome_search_saved), Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    override fun onCheckedChanged(p0: CompoundButton, isChecked: Boolean) {
        when {
            p0.id == R.id.catcher_switch -> {
                val model = sharedPrefs.getString("catcher_model", "CheckFirm") as String
                val csc = sharedPrefs.getString("catcher_csc", "Catcher") as String
                if (isChecked) {
                    mEditor.putBoolean("catcher", true)
                    if (model.isNotBlank() && csc.isNotBlank()) {
                        FirebaseMessaging.getInstance().subscribeToTopic(model+csc)
                    }
                } else {
                    mEditor.putBoolean("catcher", false)
                    if (model.isNotBlank() && csc.isNotBlank()) {
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(model+csc)
                    }
                }
                mEditor.apply()
            }
        }
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}