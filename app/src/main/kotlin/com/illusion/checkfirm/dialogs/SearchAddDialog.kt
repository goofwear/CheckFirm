package com.illusion.checkfirm.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.illusion.checkfirm.R

class SearchAddDialog : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView = inflater.inflate(R.layout.dialog_search_add, container, false)

        val okButton = rootView.findViewById<MaterialButton>(R.id.ok)
        okButton.setOnClickListener {
            dismiss()
        }

        return rootView
    }

    companion object {
        fun newInstance(isOfficial: Boolean, latestFirmware: String, firmwareList: String, model: String, csc: String): SearchAddDialog {
            val f = SearchAddDialog()

            val args = Bundle()
            args.putBoolean("isOfficial", isOfficial)
            args.putString("latest", latestFirmware)
            args.putString("firmwareList", firmwareList)
            args.putString("model", model)
            args.putString("csc", csc)
            f.arguments = args

            return f
        }
    }
}