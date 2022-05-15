package com.buller.mysqlite

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import com.buller.mysqlite.constans.ContentConstants
import com.buller.mysqlite.databinding.DialogModalBottomSheetLobsterpickerBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.larswerkman.lobsterpicker.OnColorListener


class ModBtSheetChooseColorTitleOrColorContent(var contextActivity: EditActivity) :
    BottomSheetDialogFragment() {
    private lateinit var binding: DialogModalBottomSheetLobsterpickerBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =
            inflater.inflate(R.layout.dialog_modal_bottom_sheet_lobsterpicker, container, false)
        binding = DialogModalBottomSheetLobsterpickerBinding.bind(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        var colorTitle = 0
        var colorContent = 0

        lobsterpicker.addDecorator(opacityslider)

        lobsterpicker.addOnColorListener(object : OnColorListener {
            override fun onColorChanged(@ColorInt color: Int) {

                if (binding.rb1.isChecked) {
                    colorTitle = color
                    binding.ivColorTitle.background.mutate()
                    binding.ivColorTitle.background.setTint(colorTitle)
                } else if (binding.rb2.isChecked) {
                    colorContent = color
                    binding.ivColorBackground.background.mutate()
                    binding.ivColorBackground.background.setTint(colorContent)
                }
            }

            override fun onColorSelected(@ColorInt color: Int) {
                return
            }
        })
        saveColor.setOnClickListener {
            contextActivity.setIntentChangeContentColorBackgroundOrTitleColorBackground(colorTitle,colorContent)
            dismiss()
        }
    }

    companion object {
        const val TAG = "com.buller.mysqlite.ModalBottomSheetLobsterpicker"
    }
}