package com.buller.mysqlite.fragments.archive

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import com.buller.mysqlite.MainActivity
import com.buller.mysqlite.R
import androidx.appcompat.view.ActionMode
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.buller.mysqlite.CustomPopupMenu
import com.buller.mysqlite.DecoratorView
import com.buller.mysqlite.databinding.FragmentArchiveBinding
import com.buller.mysqlite.fragments.BaseFragment
import com.buller.mysqlite.fragments.constans.FragmentConstants
import com.buller.mysqlite.fragments.list.NotesAdapter
import com.buller.mysqlite.theme.BaseTheme
import com.easynote.domain.viewmodels.NotesViewModel
import com.dolatkia.animatedThemeManager.AppTheme
import com.easynote.domain.viewmodels.ArchiveFragmentViewModel
import com.easynote.domain.viewmodels.BaseViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class ArchiveFragment() : BaseFragment(), View.OnCreateContextMenuListener {
    private lateinit var binding: FragmentArchiveBinding
    private val mNoteViewModel: NotesViewModel by activityViewModels()
    private val mArchiveFragmentViewModel: ArchiveFragmentViewModel by viewModel()

    override val mBaseViewModel: BaseViewModel get() = mArchiveFragmentViewModel

    private val noteArchiveAdapter: NotesAdapter by lazy { NotesAdapter() }
    private var wrapper: Context? = null
    private var wrapperDialog: Context? = null
    private var isActionMode = false

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        if (state != null && state.getBoolean(FragmentConstants.ACTION_MODE_KEY, false)) {
            mArchiveFragmentViewModel.actionMode =
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
                menuInflater.inflate(R.menu.menu_archive_toolbar, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.archive_item_toolbar_multiselect -> {
                        mArchiveFragmentViewModel.actionMode =
                            (activity as MainActivity).startSupportActionMode(actionModeCallback)
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        state: Bundle?
    ): View {
        binding = FragmentArchiveBinding.inflate(inflater, container, false)
        mArchiveFragmentViewModel.loadNotes()

        binding.apply {
            rcViewArchiveNote.apply {
                adapter = noteArchiveAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }
            registerForContextMenu(rcViewArchiveNote)
            noteArchiveAdapter.onItemClick = { note, view, position ->
                val actionMode = mArchiveFragmentViewModel.actionMode

                if (actionMode != null) {
                    mArchiveFragmentViewModel.changeSelectedNotesFromActionMode(note)
                    noteArchiveAdapter.notifyItemChanged(position)

                } else {
                    openNote(note.id, view)
                }

            }

            noteArchiveAdapter.onItemLongClick = { view, note, _ ->
                mArchiveFragmentViewModel.getSelected(note.id)
                showPopupMenuArchiveItem(view)
            }

            noteArchiveAdapter.onItemActionMode = { holder, currentNote ->
                val selectedItems = mArchiveFragmentViewModel.selectedNotesFromActionMode.value
                if (selectedItems != null) {
                    holder.itemView.isActivated = selectedItems.contains(currentNote)
                }
            }
        }

        mArchiveFragmentViewModel.selectedNotesFromActionMode.observe(viewLifecycleOwner) { list ->
            mArchiveFragmentViewModel.actionMode?.title = getString(R.string.selected_items, list.size)
        }
        initThemeObserver()
        initNotesLiveDataObserver()
        onBackPressedAndBackArrow()
        return binding.root
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

    private fun openNote(noteId: Long, view: View) {
        val bundle = Bundle()
        bundle.putLong("note_id", noteId)
        view.findNavController().navigate(R.id.action_archiveFragment_to_addFragment, bundle)
    }

    private fun showPopupMenuArchiveItem(view: View) {
        val currentTheme = mNoteViewModel.currentTheme.value
        val popupMenu = CustomPopupMenu(wrapper!!, view, currentTheme)
        popupMenu.showPopupMenuArchive()

        popupMenu.onChangeNoteArch = {
            showUnarchiveDialog()
            popupMenu.dismiss()
        }

        popupMenu.onDeleteNote = {
            showDeleteDialog()
            popupMenu.dismiss()
        }
    }

    private val actionModeCallback = object : ActionMode.Callback {
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
                inflater.inflate(R.menu.menu_archive_action_mode, menu)
            }
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            return when (item!!.itemId) {
                R.id.action_delete -> {
                    if (!mArchiveFragmentViewModel.selectedNotesFromActionMode.value.isNullOrEmpty()) {
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

                R.id.action_restore_archive -> {
                    if (!mArchiveFragmentViewModel.selectedNotesFromActionMode.value.isNullOrEmpty()) {
                        showUnarchiveItemsDialog()
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
            val currentTheme = mNoteViewModel.currentTheme.value
            if (currentTheme != null) {
                DecoratorView.setThemeColorBackgroundNavigationBar(
                    requireActivity() as MainActivity,
                    currentTheme
                )
            }
            mArchiveFragmentViewModel.clearSelectedNotesFromActionMode()
            noteArchiveAdapter.notifyDataSetChanged()
            mArchiveFragmentViewModel.actionMode = null
            isActionMode = false
        }

    }

    private fun showDeleteDialog() {
        MaterialDialog(wrapperDialog!!).show {
            title(R.string.delete)
            message(R.string.message_text)
            positiveButton(R.string.yes) { dialog ->
                mArchiveFragmentViewModel.updateStatusNote(isDelete = true)
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
                mArchiveFragmentViewModel.deleteSelectionNotesFromActionMode()
                dialog.dismiss()
            }
            negativeButton(R.string.no) { dialog ->
                dialog.dismiss()
            }
        }
    }

    private fun showUnarchiveDialog() {
        MaterialDialog(wrapperDialog!!).show {
            title(R.string.return_from_archive)
            message(R.string.massage_return_from_archive)
            positiveButton(R.string.yes) { dialog ->
                mArchiveFragmentViewModel.updateStatusNote(isArchive = false)
                dialog.dismiss()
            }
            negativeButton(R.string.no) { dialog ->
                dialog.dismiss()
            }
        }
    }

    private fun showUnarchiveItemsDialog() {
        MaterialDialog(wrapperDialog!!).show {
            title(R.string.return_from_archive)
            message(R.string.massage_return_from_archive_items)
            positiveButton(R.string.yes) { dialog ->
                mArchiveFragmentViewModel.unarchiveSelectedNotesFromActionMode()
                dialog.dismiss()
            }
            negativeButton(R.string.no) { dialog ->
                dialog.dismiss()
            }
        }
    }

    private fun initNotesLiveDataObserver() = with(binding) {
        mArchiveFragmentViewModel.readAllNotes.observe(viewLifecycleOwner) { listNotes ->
            if (listNotes.isEmpty()) {
                backgroundArchiveIcon.visibility = View.VISIBLE
            } else {
                backgroundArchiveIcon.visibility = View.INVISIBLE
            }
            noteArchiveAdapter.submitList(listNotes)
        }
    }

    private fun initThemeObserver() {
        mNoteViewModel.currentTheme.observe(viewLifecycleOwner) { currentTheme ->
            noteArchiveAdapter.themeChanged(currentTheme)
        }
    }

    override fun syncTheme(appTheme: AppTheme) {
        val theme = appTheme as BaseTheme
        wrapper = ContextThemeWrapper(context, theme.stylePopupTheme())
        wrapperDialog = ContextThemeWrapper(requireContext(), theme.styleDialogTheme())
    }
}