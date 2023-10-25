package com.buller.mysqlite.fragments.recycle

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.view.ActionMode
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.buller.mysqlite.CustomPopupMenu
import com.buller.mysqlite.DecoratorView
import com.buller.mysqlite.MainActivity
import com.buller.mysqlite.R
import com.buller.mysqlite.databinding.FragmentRecycleBinBinding
import com.buller.mysqlite.fragments.BaseFragment
import com.buller.mysqlite.fragments.constans.FragmentConstants
import com.buller.mysqlite.fragments.list.NotesAdapter
import com.buller.mysqlite.theme.BaseTheme
import com.easynote.domain.viewmodels.NotesViewModel
import com.dolatkia.animatedThemeManager.AppTheme
import com.easynote.domain.viewmodels.BaseViewModel
import com.easynote.domain.viewmodels.RecycleBinFragmentViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class RecycleBinFragment : BaseFragment() {
    private lateinit var binding: FragmentRecycleBinBinding
    private val mNoteViewModel: NotesViewModel by activityViewModels()
    private val mRecycleBinFragmentVM: RecycleBinFragmentViewModel by viewModel()
    override val mBaseViewModel: BaseViewModel get() = mRecycleBinFragmentVM
    private val noteDeleteAdapter: NotesAdapter by lazy { NotesAdapter() }
    private var wrapper: Context? = null
    private var wrapperDialog: Context? = null
    private var isActionMode = false

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        if (state != null && state.getBoolean(FragmentConstants.ACTION_MODE_KEY, false)) {
            mRecycleBinFragmentVM.actionMode =
                (activity as MainActivity).startSupportActionMode(actionModeCallback)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(FragmentConstants.ACTION_MODE_KEY, isActionMode)
        super.onSaveInstanceState(outState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_toolbar_recyclerbin_fragment, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.bin_item_toolbar_multiselect -> {
                        mRecycleBinFragmentVM.actionMode =
                            (activity as MainActivity).startSupportActionMode(actionModeCallback)
                        return true
                    }

                    else -> return false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        super.onViewCreated(view, savedInstanceState)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecycleBinBinding.inflate(inflater, container, false)
        mRecycleBinFragmentVM.loadNotes()

        binding.apply {
            rcViewDeletedNote.apply {
                adapter = noteDeleteAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }
            noteDeleteAdapter.onItemClick = { note, view, position ->
                val actionMode = mRecycleBinFragmentVM.actionMode
                if (actionMode != null) {
                    mRecycleBinFragmentVM.changeSelectedNotesFromActionMode(note)
                    noteDeleteAdapter.notifyItemChanged(position)
                } else {
                    openNote(note.id, view)
                }
            }
            noteDeleteAdapter.onItemLongClick = { view, note, i ->
                mRecycleBinFragmentVM.getSelected(note.id)
                showPopupMenuBinItem(view)
            }

            noteDeleteAdapter.onItemActionMode = { holder, currentNote ->
                val selectedItems = mRecycleBinFragmentVM.selectedNotesFromActionMode.value
                if (selectedItems != null) {
                    holder.itemView.isActivated = selectedItems.contains(currentNote)
                }
            }
        }

        initThemeObserver()
        initNotesLiveDataObserver()

        mRecycleBinFragmentVM.selectedNotesFromActionMode.observe(viewLifecycleOwner) { list ->
            mRecycleBinFragmentVM.actionMode?.title = getString(R.string.selected_items, list.size)
        }

        onBackPressedAndBackArrow()
        return binding.root
    }

    private fun openNote(noteId: Long, view: View) {
        val bundle = Bundle()
        bundle.putLong("note_id", noteId)
        view.findNavController().navigate(R.id.action_recycleBinFragment_to_addFragment, bundle)
    }

    private fun onBackPressedAndBackArrow() {
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                super.isEnabled = true
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            onBackPressedCallback
        )

        val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            (requireActivity() as MainActivity).binding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    private fun showPopupMenuBinItem(view: View) {
        val currentTheme = mNoteViewModel.currentTheme.value
        val popupMenu = CustomPopupMenu(wrapper!!, view, currentTheme)
        popupMenu.showPopupMenuBinItem()

        popupMenu.onRestoreNote = {
            showRestoreDialog()
            popupMenu.dismiss()
        }

        popupMenu.onDeleteNote = {
            showDeleteDialog()
            popupMenu.dismiss()
        }
    }


    val actionModeCallback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            val currentTheme = mNoteViewModel.currentTheme.value
            if (currentTheme != null) {
                DecoratorView.setColorBackgroundFromActionModeToolbar(
                    requireActivity() as MainActivity,
                    currentTheme
                )
            }
            isActionMode = true
            if (mode != null) {
                val inflater: MenuInflater = mode.menuInflater
                inflater.inflate(R.menu.menu_bin_action_mode, menu)
            }
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            return when (item!!.itemId) {
                R.id.action_delete -> {
                    if (!mRecycleBinFragmentVM.selectedNotesFromActionMode.value.isNullOrEmpty()) {
                        showDeleteItemsDialog()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            R.string.not_selected_items,
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    true
                }

                R.id.action_restore -> {
                    if (!mRecycleBinFragmentVM.selectedNotesFromActionMode.value.isNullOrEmpty()) {
                        showRestoreItemsDialog()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            R.string.not_selected_items,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    true
                }

                else -> false
            }
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            mRecycleBinFragmentVM.clearSelectedNotesFromActionMode()
            noteDeleteAdapter.notifyDataSetChanged()
            mRecycleBinFragmentVM.actionMode = null
            isActionMode = false
        }

    }

    private fun showDeleteDialog() {
        MaterialDialog(wrapperDialog!!).show {
            title(R.string.delete)
            message(R.string.message_permanent_delete)
            positiveButton(R.string.yes) { dialog ->
                mRecycleBinFragmentVM.deleteSelectedNote()
                dialog.dismiss()
            }
            negativeButton(R.string.no) { dialog ->
                dialog.dismiss()
            }
        }
    }

    private fun showDeleteItemsDialog() {
        MaterialDialog(wrapperDialog!!).show {
            title(R.string.delete)
            message(R.string.dialog_delete_selected_items)
            positiveButton(R.string.yes) { dialog ->
                mRecycleBinFragmentVM.deleteSelectionNotesFromActionMode()
                dialog.dismiss()
            }
            negativeButton(R.string.no) { dialog ->
                dialog.dismiss()
            }
        }
    }

    private fun showRestoreDialog() {
        MaterialDialog(wrapperDialog!!).show {
            title(R.string.title_restore)
            message(R.string.message_restore_note)
            positiveButton(R.string.yes) { dialog ->
                mRecycleBinFragmentVM.updateStatusNote(isDelete = false)
                dialog.dismiss()
            }
            negativeButton(R.string.no) { dialog ->
                dialog.dismiss()
            }
        }
    }

    private fun showRestoreItemsDialog() {
        MaterialDialog(wrapperDialog!!).show {
            title(R.string.title_restore)
            message(R.string.message_restore_items)
            positiveButton(R.string.yes) { dialog ->
                mRecycleBinFragmentVM.restoreSelectedNotesFromActionMode()
                dialog.dismiss()
            }
            negativeButton(R.string.no) { dialog ->
                dialog.dismiss()
            }
        }
    }


    override fun syncTheme(appTheme: AppTheme) {
        val theme = appTheme as BaseTheme
        wrapper = ContextThemeWrapper(context, theme.stylePopupTheme())
        wrapperDialog = ContextThemeWrapper(requireContext(), theme.styleDialogTheme())
    }

    private fun initThemeObserver() {
        mNoteViewModel.currentTheme.observe(viewLifecycleOwner) { currentTheme ->
            noteDeleteAdapter.themeChanged(currentTheme)
        }
    }

    private fun initNotesLiveDataObserver() = with(binding) {
        mRecycleBinFragmentVM.readAllNotes.observe(viewLifecycleOwner) { listNotes ->
            if (listNotes.isEmpty()) {
                backgroundBinIcon.visibility = View.VISIBLE
            } else {
                backgroundBinIcon.visibility = View.GONE
            }
            noteDeleteAdapter.submitList(listNotes)
        }
    }
}