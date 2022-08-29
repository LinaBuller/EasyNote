package com.buller.mysqlite.fragments.add.bottomsheet

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import com.buller.mysqlite.R
import com.buller.mysqlite.databinding.DialogModalBottomSheetLobsterpickerBinding

import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.larswerkman.lobsterpicker.OnColorListener
import com.larswerkman.lobsterpicker.adapters.BitmapColorAdapter


class ModBtSheetChooseColorTitleOrColorContent(private val listCurrentColorTitleFromAddList: List<Int>) :
    BottomSheetDialogFragment() {
    private lateinit var binding: DialogModalBottomSheetLobsterpickerBinding
    private var onColorSelectedListener: OnColorSelectedListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnColorSelectedListener) {
            onColorSelectedListener = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DialogModalBottomSheetLobsterpickerBinding.inflate(inflater, container, false).also {
                (parentFragment as? OnColorSelectedListener)?.let {
                    onColorSelectedListener = it
                }
            }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        var colorTitle = 0
        var colorContent = 0

        val currentColorTitleFromAddList = listCurrentColorTitleFromAddList[0]
        val currentColorContentFromAddList = listCurrentColorTitleFromAddList[1]
        if (currentColorTitleFromAddList != 0) {
            binding.ivColorTitle.background.mutate()
            binding.ivColorTitle.background.setTint(currentColorTitleFromAddList)
        }
        if (currentColorContentFromAddList != 0) {
            binding.ivColorContent.background.mutate()
            binding.ivColorContent.background.setTint(currentColorContentFromAddList)
        }

        lobsterpicker.addDecorator(opacityslider)
        lobsterpicker.addOnColorListener(object : OnColorListener {
            override fun onColorChanged(@ColorInt color: Int) {

                if (binding.rb1.isChecked) {
                    colorTitle = color
                    binding.ivColorTitle.background.mutate()
                    binding.ivColorTitle.background.setTint(colorTitle)
                } else if (binding.rb2.isChecked) {
                    colorContent = color
                    binding.ivColorContent.background.mutate()
                    binding.ivColorContent.background.setTint(colorContent)
                }
            }

            override fun onColorSelected(@ColorInt color: Int) {
                return
            }
        })
        saveColor.setOnClickListener {
            onColorSelectedListener?.onColorSelected(colorTitle, colorContent)
            dismiss()
        }
    }

    interface OnColorSelectedListener {
        fun onColorSelected(colorTitle: Int, colorContent: Int)
    }

    companion object {
        const val TAG = "ModalBottomSheetLobsterpicker"
    }
}