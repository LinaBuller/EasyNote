package com.buller.mysqlite.fragments.add.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.buller.mysqlite.R
import com.buller.mysqlite.databinding.DialogModBottomSheetChooseExportBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ModBtSheetChooseExport() : BottomSheetDialogFragment() {
    private lateinit var binding: DialogModBottomSheetChooseExportBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.dialog_mod_bottom_sheet_choose_export, container, false)
        binding = DialogModBottomSheetChooseExportBinding.bind(view)
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        btExportSimpleText.setOnClickListener {
            //ShareNoteAsSimpleText.sendSimpleText(contextActivity.binding.etTitle,contextActivity.binding.etContent,contextActivity)
            //contextActivity.saveItemNoteToBD()
            dismiss()
        }

        btExportJson.setOnClickListener {
            Toast.makeText(requireContext(), "ExportJson", Toast.LENGTH_SHORT).show()
            dismiss()
        }

        btCategories.setOnClickListener {
            //contextActivity.openCategorySelect()
            dismiss()
        }
    }

    companion object {
        const val TAG = "ModalBottomSheetExport"
    }
}