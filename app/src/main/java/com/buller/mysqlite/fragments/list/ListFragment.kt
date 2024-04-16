package com.buller.mysqlite.fragments.list

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.view.ActionMode
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.buller.mysqlite.BaseCategoryAdapterCallback
import com.buller.mysqlite.BaseAdapterCallback
import com.buller.mysqlite.CustomPopupMenu
import com.buller.mysqlite.DecoratorView
import com.buller.mysqlite.MainActivity
import com.buller.mysqlite.R
import com.buller.mysqlite.databinding.FragmentListBinding
import com.buller.mysqlite.dialogs.DialogCategoryAdapter
import com.buller.mysqlite.dialogs.DialogMoveCategory
import com.buller.mysqlite.dialogs.OnUpdateSelectedCategory
import com.buller.mysqlite.fragments.BaseFragment
import com.buller.mysqlite.fragments.constans.FragmentConstants
import com.buller.mysqlite.theme.BaseTheme
import com.dolatkia.animatedThemeManager.AppTheme
import com.easynote.domain.models.Category
import com.easynote.domain.models.Note
import com.easynote.domain.utils.ShareNoteAsSimpleText
import com.easynote.domain.viewmodels.BaseViewModel
import com.easynote.domain.viewmodels.ListFragmentViewModel
import com.easynote.domain.viewmodels.NotesViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class ListFragment : BaseFragment(),
    DialogCategoryAdapter.OnItemClickListener, OnUpdateSelectedCategory,
    BaseAdapterCallback<Note>, BaseCategoryAdapterCallback<Category> {
    private val mNoteViewModel: NotesViewModel by activityViewModels()
    private val mListFragmentViewModel: ListFragmentViewModel by viewModel()
    override val mBaseViewModel: BaseViewModel get() = mListFragmentViewModel
    lateinit var binding: FragmentListBinding
    private val noteAdapter: NoteAdapter by lazy { NoteAdapter() }
    private val categoryAdapter: CategoryCheckBoxListFragmentAdapter by lazy { CategoryCheckBoxListFragmentAdapter() }
    private var isLineOfList: Boolean = true
    private var wrapperPopupMenu: Context? = null
    private var isActionMode = false
    private var wrapperDialog: Context? = null
    private var wrapperDialogAddCategory: Context? = null
    private var searchView: SearchView? = null
    private lateinit var searchItem: MenuItem

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_toolbar_list_fragment, menu)
                initSortMenuButton(menu)
                initContextMenuButton(menu)
                initSearchButton(menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        state: Bundle?
    ): View {
        binding = FragmentListBinding.inflate(inflater, container, false)
        initCategory()
        initNoteList()
        initNewNote()
        initCountSelectedNotesFromActionMode()
        initNoteScrollListener()
        initThemeObserver()
        onBackPressedAndBackArrow()
        return binding.root
    }

    private fun initNewNote() = with(binding) {
        btAdd.setOnClickListener {
            val bundle = Bundle()
            bundle.putLong(FragmentConstants.NOTE_ID, 0)
            view?.findNavController()?.navigate(R.id.action_listFragment_to_addFragment, bundle)
        }
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

    fun initSortMenuButton(menu: Menu) {
        val sortButton = menu.findItem(R.id.sort).actionView as ImageButton?
        sortButton?.background = ResourcesCompat.getDrawable(
            resources, R.drawable.ic_sort_24, context?.theme
        )
        sortButton?.setOnClickListener {
            val viewButton: ImageButton = requireActivity().findViewById(R.id.sort)
            showPopupMenuSort(viewButton)
        }
    }

    fun initContextMenuButton(menu: Menu) {
        val contextMenuButton = menu.findItem(R.id.menu_context).actionView as ImageButton?
        contextMenuButton?.background = ResourcesCompat.getDrawable(
            resources, R.drawable.ic_filters_menu_list_fragment, context?.theme
        )
        contextMenuButton?.setOnClickListener {
            val viewButton: ImageButton = requireActivity().findViewById(R.id.menu_context)
            showPopupMenuToolbarMenu(viewButton)
        }
    }

    fun initSearchButton(menu: Menu) {
        searchItem = menu.findItem(R.id.search_item)
        searchView = (searchItem.actionView as? SearchView)!!
        val closeButtonImage = searchView!!.findViewById<ImageView>(R.id.search_close_btn)
        closeButtonImage.setImageResource(R.drawable.ic_close_12)
        closeButtonImage.setOnClickListener {
            searchItem.collapseActionView()
        }
        expandSearchView()
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                mListFragmentViewModel.setSearchText(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                mListFragmentViewModel.setSearchText(newText)
                return true
            }
        })
    }

    private fun expandSearchView() {
        val pendingQuery = mListFragmentViewModel.searchText.value
        if (!pendingQuery.isNullOrEmpty()) {
            searchItem.expandActionView()
            searchView?.setQuery(pendingQuery, false)
            mListFragmentViewModel.setSearchText(pendingQuery)
        }
    }

    private fun initNoteList() = with(binding) {
        lifecycleScope.launch {
            mListFragmentViewModel.loadNotes()
        }
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
            noteAdapter.attachCallback(this@ListFragment)

            mListFragmentViewModel.setTypeList(isLineOfList)
        }

        /*
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
            */

        mListFragmentViewModel.setCurrentKindOfList(isLineOfList)

        mListFragmentViewModel.readAllNotes.observe(viewLifecycleOwner) { listAllNotes ->
            val listOfCurrentNotes = mutableListOf<Note>()

            listAllNotes.filter { !it.isDeleted && !it.isArchive }.let { currNote ->
                listOfCurrentNotes.addAll(currNote)
            }

            if (listOfCurrentNotes.isEmpty()) {
                backgroundListIcon.visibility = View.VISIBLE
            } else {
                backgroundListIcon.visibility = View.INVISIBLE
            }

            noteAdapter.submitList(listOfCurrentNotes)
            rcView.smoothScrollToPosition(noteAdapter.itemCount)
        }
    }

    private fun initNoteScrollListener() = with(binding) {
        rcView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0 || dy < 0 && btAdd.isShown()) btAdd.hide()
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) btAdd.show()
                super.onScrollStateChanged(recyclerView, newState)
            }
        })
    }

    private fun initCategory() = with(binding) {
        rcCategories.apply {
            val linearLayout =
                LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            layoutManager = linearLayout

            adapter = categoryAdapter
            categoryAdapter.attachCallback(this@ListFragment)
        }

        imAddCategory.setOnClickListener {
            showAddCategoryDialog()
        }

        mListFragmentViewModel.existCategories.observe(viewLifecycleOwner) { listCategories ->
            val sortedCategories = listCategories.sortedBy { it.position }
            categoryAdapter.submitList(sortedCategories)
        }

    }

    private fun showPopupMenuSort(view: View) {
        val currentTheme = mNoteViewModel.currentTheme.value
        val popupMenu = CustomPopupMenu(wrapperPopupMenu!!, view, currentTheme)
        popupMenu.showPopupMenuSort()
        popupMenu.onSetSort = { sort ->
            //todo: constant for default sort
            if (sort.sortColumn == ListFragmentConstants.DEFAULT_SORT_COLUMN
                && sort.sortOrder == ListFragmentConstants.DEFAULT_SORT_ORDER
            ) {
                if (sort.date == null) {
                    mListFragmentViewModel.resetSort()
                } else {
                    val timestamp = sort.date
                    val strDate = "${timestamp!![1]}-${timestamp[0]}-${timestamp[2]}"
                    mListFragmentViewModel.setSearchDate(strDate)
                }

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
            btAdd.backgroundTintList = ColorStateList.valueOf(theme.akcColor(requireContext()))
            imAddCategory.setColorFilter(theme.backgroundDrawer(requireContext()))
            imAddCategory.backgroundTintList =
                ColorStateList.valueOf(theme.akcColor(requireContext()))
        }
    }

    private fun initThemeObserver() {
        mNoteViewModel.currentTheme.observe(viewLifecycleOwner) { currentTheme ->
            noteAdapter.setTheme(currentTheme)
            categoryAdapter.setTheme(currentTheme)
        }
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
                binding.rcCategories.smoothScrollToPosition(categoryAdapter.itemCount)
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

    override fun onPause() {
        searchView?.setOnQueryTextListener(null)
        super.onPause()
    }

    override fun onDestroyView() {
        searchView?.setOnQueryTextListener(null)
        super.onDestroyView()
    }

    override fun onItemClick(model: Note, view: View, position: Int) {
        val actionMode = mListFragmentViewModel.actionMode

        if (actionMode != null) {

            val selectedItems = mListFragmentViewModel.selectedNotesFromActionMode.value
            if (selectedItems != null) {
                view.isActivated = selectedItems.contains(model)
            }
            mListFragmentViewModel.changeSelectedNotesFromActionMode(model)
            noteAdapter.notifyItemChanged(position)

        } else {

            if (!model.isDeleted || !model.isArchive) {
                val bundle = Bundle()
                bundle.putLong(ListFragmentConstants.OPEN_NOTE_ID, model.id)
                view.findNavController()
                    .navigate(R.id.action_listFragment_to_addFragment, bundle)
            }
        }
    }

    override fun onLongClick(model: Note, view: View): Boolean {
        mListFragmentViewModel.setSelectedNote(model)
        showPopupMenuLongClickNote(view, model.isPin, model.isFavorite)
        return true
    }

    override fun onItemCategoryClick(model: Category, view: View, position: Int) {
        val clickCategoryId = model.idCategory
        if (mListFragmentViewModel.filterCategoryId.contains(clickCategoryId)) {
            mListFragmentViewModel.removeFilterCategoryId(clickCategoryId)
            view.isActivated = false

        } else {
            mListFragmentViewModel.setFilterCategoryId(clickCategoryId)
            view.isActivated = true
        }
    }

    override fun onItemCategoryLongClick(model: Category, view: View): Boolean {
        return false
    }
}