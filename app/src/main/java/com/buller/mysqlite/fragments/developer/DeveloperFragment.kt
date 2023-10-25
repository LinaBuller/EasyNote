package com.buller.mysqlite.fragments.developer

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import com.buller.mysqlite.constans.DevelopInfoConstants
import com.buller.mysqlite.databinding.FragmentDeveloperBinding
import com.buller.mysqlite.fragments.BaseFragment
import com.buller.mysqlite.theme.BaseTheme
import com.dolatkia.animatedThemeManager.AppTheme
import com.easynote.domain.viewmodels.BaseViewModel
import com.easynote.domain.viewmodels.DeveloperFragmentViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class DeveloperFragment : BaseFragment() {
    lateinit var binding: FragmentDeveloperBinding
    private val mDeveloperFragmentViewModel: DeveloperFragmentViewModel by viewModel()
    override val mBaseViewModel: BaseViewModel get() = mDeveloperFragmentViewModel

    override fun syncTheme(appTheme: AppTheme) {
        val theme = appTheme as BaseTheme
        binding.apply {
            cardViewDevPhoto.setCardBackgroundColor(
                ColorStateList.valueOf(
                    theme.backgroundDrawer(
                        requireContext()
                    )
                )
            )
            tv1.setTextColor(ColorStateList.valueOf(theme.textColor(requireContext())))
            tv2.setTextColor(ColorStateList.valueOf(theme.textColor(requireContext())))
            tv3.setTextColor(ColorStateList.valueOf(theme.textColor(requireContext())))
            tv4.setTextColor(ColorStateList.valueOf(theme.textColor(requireContext())))
            tv5.setTextColor(ColorStateList.valueOf(theme.textColor(requireContext())))
            cardViewEmail.setCardBackgroundColor(
                ColorStateList.valueOf(
                    theme.backgroundDrawer(
                        requireContext()
                    )
                )
            )
            tvDevEmail.setTextColor(theme.akcColor(requireContext()))

            cardViewGitHub.setCardBackgroundColor(
                ColorStateList.valueOf(
                    theme.backgroundDrawer(
                        requireContext()
                    )
                )
            )
            tvGitHub.setTextColor(ColorStateList.valueOf(theme.textColor(requireContext())))
            imBtCopyEmail.backgroundTintList =
                ColorStateList.valueOf(theme.backgroundDrawer(requireContext()))
            ivGitHub.setColorFilter(theme.akcColor(requireContext()))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDeveloperBinding.inflate(inflater, container, false)
        binding.apply {
            imBtCopyEmail.setOnClickListener {
                copyTextToClipboard()
            }
            tvDevEmail.setOnClickListener {
                sendEmail(DevelopInfoConstants.DEV_EMAIL)
            }

            tvGitHub.setOnClickListener {
                openWebPage(DevelopInfoConstants.DEV_GITHUB)
            }
        }
        return binding.root
    }

    private fun copyTextToClipboard() {
        if (context != null) {
            val clipboard = requireContext().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip: ClipData = ClipData.newPlainText("E-mail", DevelopInfoConstants.DEV_EMAIL)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(
                requireContext(),
                "copy: $DevelopInfoConstants.DEV_EMAIL",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun sendEmail(email: String) {
        val selectorIntent = Intent(Intent.ACTION_SENDTO)
        selectorIntent.data = Uri.parse("mailto:")
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        emailIntent.selector = selectorIntent
        requireActivity().startActivity(Intent.createChooser(emailIntent, "Send email..."))
    }

    private fun openWebPage(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }
}