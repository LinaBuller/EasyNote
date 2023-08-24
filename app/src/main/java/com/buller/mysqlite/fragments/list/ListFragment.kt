package com.buller.mysqlite.fragments.list

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.*
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.view.ActionMode
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.list.listItemsMultiChoice
import com.buller.mysqlite.CustomPopupMenu
import com.buller.mysqlite.DecoratorView
import com.buller.mysqlite.MainActivity
import com.buller.mysqlite.R
import com.buller.mysqlite.databinding.FragmentListBinding
import com.buller.mysqlite.dialogs.DialogAddNewCategory
import com.buller.mysqlite.dialogs.DialogCategoryAdapter
import com.buller.mysqlite.dialogs.DialogIsArchive
import com.buller.mysqlite.dialogs.DialogMoveCategory
import com.buller.mysqlite.dialogs.OnCloseDialogListener
import com.buller.mysqlite.dialogs.OnUpdateSelectedCategory
import com.buller.mysqlite.fragments.constans.FragmentConstants
import com.buller.mysqlite.fragments.image.ImageFragment
import com.buller.mysqlite.theme.BaseTheme
import com.dolatkia.animatedThemeManager.AppTheme
import com.dolatkia.animatedThemeManager.ThemeFragment
import com.easynote.domain.models.Category
import com.easynote.domain.models.Note
import com.easynote.domain.utils.ShareNoteAsSimpleText
import com.easynote.domain.viewmodels.ListFragmentViewModel
import com.easynote.domain.viewmodels.NotesViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class ListFragment : ThemeFragment(), CategoryFromListFragmentAdapter.OnClickAddNewCategory,
    DialogCategoryAdapter.OnItemClickListener, OnUpdateSelectedCategory{
    private val mNoteViewModel: NotesViewModel by activityViewModels()
    private val mListFragmentViewModel: ListFragmentViewModel by viewModel()
    lateinit var binding: FragmentListBinding
    private val noteAdapter: NotesAdapter by lazy { NotesAdapter() }
    private lateinit var categoryAdapter: CategoryFromListFragmentAdapter
    private var isLineOfList: Boolean = true
    private var wrapperPopupMenu: Context? = null
    private var isActionMode = false
    private var wrapperDialog: Context? = null
    private var wrapperDialogAddCategory: Context? = null


    override fun onCreate(state: Bundle?) {
        isLineOfList = if (mNoteViewModel.isFirstUsages) {
            true
        } else {
            mListFragmentViewModel.getTypeList()
            mListFragmentViewModel.currentKindOfList.value!!
        }

        super.onCreate(state)
        if (state != null && state.getBoolean(FragmentConstants.ACTION_MODE_KEY, false)) {
            mListFragmentViewModel.actionMode =
                (activity as MainActivity).startSupportActionMode(actionModeCallback)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(FragmentConstants.ACTION_MODE_KEY, isActionMode)
        super.onSaveInstanceState(outState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        state: Bundle?
    ): View {
        binding = FragmentListBinding.inflate(inflater, container, false)
        mListFragmentViewModel.loadNotes()

        initNoteList()
        initNotesLiveDataObserver()
        initCategory()
        initCategoriesLiveDataObserver()

        binding.btAdd.setOnClickListener {
            openNewNote()
        }

        onBackPressedAndBackArrow()
        initCountSelectedNotesFromActionMode()
        initThemeObserver()
        return binding.root
    }

    private fun openNewNote() {
        val bundle = Bundle()
        bundle.putLong("note_id", 0)
        view?.findNavController()?.navigate(R.id.action_listFragment_to_addFragment, bundle)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_toolbar_list_fragment, menu)
                val sortButton = menu.findItem(R.id.sort).actionView as ImageButton?
                if (sortButton != null) {
                    sortButton.background =
                        ResourcesCompat.getDrawable(resources, R.drawable.ic_sort_24, null)
                }
                sortButton?.setOnClickListener {
                    val viewButton: ImageButton = requireActivity().findViewById(R.id.sort)
                    showPopupMenuSort(viewButton)
                }

                val locButton = menu.findItem(R.id.menu_filter).actionView as ImageButton?
                if (locButton != null) {
                    locButton.background =
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.ic_filters_menu_list_fragment,
                            null
                        )
                }
                locButton?.setOnClickListener {
                    val viewButton: ImageButton = requireActivity().findViewById(R.id.menu_filter)
                    showPopupMenuToolbarMenu(viewButton)
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initNoteList() = with(binding) {

        mListFragmentViewModel.currentKindOfList.observe(viewLifecycleOwner) {
            isLineOfList = it

            if (isLineOfList) {
                val linearLayoutManager = LinearLayoutManager(requireContext())
                linearLayoutManager.reverseLayout = true
                linearLayoutManager.stackFromEnd = true
                rcView.layoutManager = linearLayoutManager
            } else {
                val gridLayoutManager =
                    GridLayoutManager(context, 2, RecyclerView.VERTICAL, true)
                rcView.layoutManager = gridLayoutManager
            }

            rcView.adapter = noteAdapter

            mListFragmentViewModel.setTypeList(isLineOfList)
        }

        noteAdapter.onItemClick = { note, view, position ->
            val actionMode = mListFragmentViewModel.actionMode
            if (actionMode != null) {
                mListFragmentViewModel.changeSelectedNotesFromActionMode(note)
                noteAdapter.notifyItemChanged(position)
            } else {
                if (!note.isDeleted || !note.isArchive) {
                    val bundle = Bundle()
                    bundle.putLong("note_id", note.id)
                    view.findNavController()
                        .navigate(R.id.action_listFragment_to_addFragment, bundle)
                }
            }
        }

        noteAdapter.onItemLongClick = { view, item, _ ->
            mListFragmentViewModel.setSelectedNote(item)
            showPopupMenuLongClickNote(view, item.isPin, item.isFavorite)
            Toast.makeText(requireContext(), "Long click", Toast.LENGTH_SHORT).show()
        }

        noteAdapter.onItemActionMode = { holder, currentNote ->
            val selectedItems = mListFragmentViewModel.selectedNotesFromActionMode.value
            if (selectedItems != null) {
                holder.itemView.isActivated = selectedItems.contains(currentNote)
            }
        }
        mListFragmentViewModel.setCurrentKindOfList(isLineOfList)
    }

    private fun initNotesLiveDataObserver() {
        mListFragmentViewModel.readAllNotes.observe(viewLifecycleOwner) { listAllNotes ->
            val listOfCurrentNotes = arrayListOf<Note>()
            listAllNotes.forEach { note ->
                if (!note.isDeleted && !note.isArchive) {
                    listOfCurrentNotes.add(note)
                }
            }
            noteAdapter.submitList(listOfCurrentNotes)
            binding.rcView.smoothScrollToPosition(noteAdapter.itemCount)
        }
    }

    private fun initCategory() = with(binding) {
        rcCategories.apply {
            categoryAdapter =
                CategoryFromListFragmentAdapter(this@ListFragment)
            val linearLayout =
                LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            layoutManager = linearLayout
            adapter = categoryAdapter
        }

        var itemCategoryId = 0L
        var filterCategoryId = 0L
        var pin: CheckBox

        categoryAdapter.onSetPinItem = { pinAdapter ->
            pin = pinAdapter
            pin.isChecked = itemCategoryId == filterCategoryId
        }
        mListFragmentViewModel.filterCategoryId.observe(viewLifecycleOwner) { filterCategory ->
            filterCategoryId = filterCategory
        }

        categoryAdapter.onChangeThemeItem = { currentTheme, holder ->
            if (holder is CategoryFromListFragmentAdapter.AddCategoryFromListHolder) {
                DecoratorView.changeImageView(
                    currentTheme,
                    holder.imageViewAddNewCategory,
                    holder.context
                )
                DecoratorView.changeBackgroundCardView(
                    currentTheme,
                    holder.cardViewAddCategoryFromListHolder,
                    holder.context
                )
                DecoratorView.changeColorElevationCardView(
                    currentTheme,
                    holder.cardViewAddCategoryFromListHolder,
                    holder.context
                )
            } else if (holder is CategoryFromListFragmentAdapter.CategoryFromListHolder) {
                //TODO не получается поменять цвет выделенного чекбокса
                DecoratorView.changeColorElevationCardView(
                    currentTheme,
                    holder.cardView,
                    holder.context
                )
                DecoratorView.changeCheckBox(
                    currentTheme,
                    holder.pin as AppCompatCheckBox,
                    holder.context
                )
                DecoratorView.changeBackgroundCardView(
                    currentTheme,
                    holder.cardView,
                    holder.context
                )
                DecoratorView.changeText(currentTheme, holder.pin, holder.context)
            }
        }


        categoryAdapter.onClickCheckBox = { clickCategoryId ->
            itemCategoryId = clickCategoryId
            if (mListFragmentViewModel.filterCategoryId.value == clickCategoryId) {
                mListFragmentViewModel.resetFilterCategoryId()
            } else {
                mListFragmentViewModel.setFilterCategoryId(clickCategoryId)
            }
        }
    }

    private fun initCategoriesLiveDataObserver() {
        mListFragmentViewModel.existCategories.observe(viewLifecycleOwner) { listCategories ->
            categoryAdapter.submitList(listCategories)
        }
    }

    private fun showPopupMenuSort(view: View) {
        val popupMenu = CustomPopupMenu(wrapperPopupMenu!!, view)
        popupMenu.showPopupMenuSort()
        popupMenu.onSetSort = { sort ->
            //todo: constant for default sort
            if (sort.sortColumn == "n.is_pin" && sort.sortOrder == 1) {
                mListFragmentViewModel.resetSort()
            } else {
                mListFragmentViewModel.setSort(sort.sortColumn, sort.sortOrder)
            }
        }
    }

    private fun showPopupMenuToolbarMenu(view: View) {
        val currentTheme = mNoteViewModel.currentTheme.value

        val popupMenu = CustomPopupMenu(wrapperPopupMenu!!, view, currentTheme)
        popupMenu.showPopupMenuToolbar(isLineOfList)
        popupMenu.onChangeTypeListNotes = {
            mListFragmentViewModel.setCurrentKindOfList(it)
        }
        popupMenu.onSelectActionMode = {
            mListFragmentViewModel.actionMode =
                (activity as MainActivity).startSupportActionMode(actionModeCallback)
        }
    }

    private fun showPopupMenuLongClickNote(view: View, isPin: Boolean, isFavorite: Boolean) {
        val currentTheme = mNoteViewModel.currentTheme.value
        val popupMenu = CustomPopupMenu(wrapperPopupMenu!!, view, currentTheme)
        popupMenu.showPopupMenuNoteItemsFromListFragment(isPin, isFavorite)

        popupMenu.onChangeNotePin = { newIsPin ->
            mListFragmentViewModel.changeNotePin(newIsPin)
        }

        popupMenu.onChangeNoteFavorite = { newIsFavorite ->
            mListFragmentViewModel.changeNoteFavorite(newIsFavorite)
        }

        popupMenu.onChangeNoteArch = {
            showArchiveDialog()
            popupMenu.dismiss()
        }

        popupMenu.onSharedNoteText = {
            val select = mListFragmentViewModel.selectedNote.value
            if (select != null) {
                //todo: add ALl exist text(many items text in note)
                ShareNoteAsSimpleText.sendSimpleText(select, requireContext())
            }
        }

        popupMenu.onDeleteNote = {
            showDeleteDialog()
            popupMenu.dismiss()
        }

        popupMenu.onChangeItemNoteCategory = {
            val existCategory = mListFragmentViewModel.existCategories.value
            val currentCategory = mListFragmentViewModel.currentCategories.value
            DialogMoveCategory(existCategory, currentCategory, this).show(
                childFragmentManager,
                DialogMoveCategory.TAG
            )
            popupMenu.dismiss()
        }
    }

    private fun showDeleteDialog() {
        MaterialDialog(wrapperDialog!!).show {
            title(R.string.delete)
            message(R.string.message_text)
            positiveButton(R.string.yes) { dialog ->
                mListFragmentViewModel.updateStatusNote(isDelete = true)
                dialog.dismiss()
            }
            negativeButton(R.string.no) { dialog ->
                dialog.dismiss()
            }
        }
    }

    private fun showArchiveDialog() {
        MaterialDialog(wrapperDialog!!).show {
            title(R.string.archive)
            message(R.string.add_this_note_to_the_archive)
            positiveButton(R.string.yes) { dialog ->
                mListFragmentViewModel.updateStatusNote(isArchive = true)
                dialog.dismiss()
            }
            negativeButton(R.string.no) { dialog ->
                dialog.dismiss()
            }
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
            binding.apply {
                btAdd.visibility = View.GONE
                rcCategories.visibility = View.GONE
            }

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
                    MaterialDialog(wrapperDialog!!).show {
                        title(R.string.delete)
                        message(R.string.dialog_delete_selected_items)
                        positiveButton(R.string.yes) { dialog ->
                            mListFragmentViewModel.deleteOrUpdateSelectionNotesFromActionMode()
                            dialog.dismiss()
                            mode?.finish()
                        }
                        negativeButton(R.string.no) { dialog ->
                            dialog.dismiss()
                        }
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
            binding.apply {
                btAdd.visibility = View.VISIBLE
                rcCategories.visibility = View.VISIBLE
            }
            mListFragmentViewModel.clearSelectedNotesFromActionMode()
            noteAdapter.notifyDataSetChanged()
            mListFragmentViewModel.actionMode = null
            isActionMode = false
        }

    }

    private fun initCountSelectedNotesFromActionMode() {
        mListFragmentViewModel.selectedNotesFromActionMode.observe(viewLifecycleOwner) { list ->
            mListFragmentViewModel.actionMode?.title = getString(R.string.selected_items, list.size)
        }
    }

    override fun syncTheme(appTheme: AppTheme) {
        val theme = appTheme as BaseTheme
        wrapperPopupMenu = ContextThemeWrapper(context, theme.stylePopupTheme())
        wrapperDialog = ContextThemeWrapper(requireContext(), theme.styleDialogTheme())
        wrapperDialogAddCategory =
            ContextThemeWrapper(requireContext(), theme.styleDialogAddCategory())

        binding.apply {
            btAdd.setColorFilter(theme.backgroundDrawer(requireContext()))
            btAdd.backgroundTintList =
                ColorStateList.valueOf(theme.akcColor(requireContext()))
        }
    }

    private fun initThemeObserver() {
        mNoteViewModel.currentTheme.observe(viewLifecycleOwner) { currentTheme ->
            noteAdapter.themeChanged(currentTheme)
            categoryAdapter.themeChanged(currentTheme)
        }
    }

    override fun onClickAddNewCategory() {
        showAddCategoryDialog()
    }

    private fun showAddCategoryDialog() {
        MaterialDialog(wrapperDialogAddCategory!!).show {
            title(R.string.add_new_category)
            val customDialog = customView(
                R.layout.dialog_add_new_category,
                scrollable = false,
                horizontalPadding = true
            )
            val field = customDialog.findViewById<EditText>(R.id.et_add_category)

            if (field.requestFocus()) {
                customDialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
            }


            positiveButton(R.string.yes) { dialog ->
                val input = dialog.getCustomView().findViewById<EditText>(R.id.et_add_category)
                val newCategory = Category(titleCategory = input.text.toString())
                mListFragmentViewModel.setCategory(newCategory)
            }
            negativeButton(R.string.no) { dialog ->
                dialog.dismiss()
            }
        }
    }

    override fun onCheckBoxClick(category: Category, isChecked: Boolean) {
        mListFragmentViewModel.changeCheckboxCategory(category, isChecked)
    }

    override fun onUpdateCategoriesFromSelectedNote() {
        mListFragmentViewModel.updateCategoryFromNote()
    }

}