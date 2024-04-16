package com.buller.mysqlite.fragments.add


import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.text.style.UnderlineSpan
import android.view.*
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.view.ActionMode
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.buller.mysqlite.BaseItemAdapterCallback
import com.buller.mysqlite.BaseViewHolder
import com.buller.mysqlite.CustomPopupMenu
import com.buller.mysqlite.CustomUnderlineSpan
import com.buller.mysqlite.DecoratorView
import com.buller.mysqlite.EditTextNoteUtil
import com.buller.mysqlite.MainActivity
import com.buller.mysqlite.R
import com.buller.mysqlite.databinding.FragmentAddBinding
import com.buller.mysqlite.fragments.BaseFragment
import com.buller.mysqlite.fragments.add.bottomsheet.pickerFavoriteColor.ModBtSheetChooseColor
import com.buller.mysqlite.fragments.add.bottomsheet.pickerImage.OnImageSelectListener
import com.buller.mysqlite.fragments.add.bottomsheet.pickerImage.SelectMediaBottomSheetFragment
import com.buller.mysqlite.fragments.categories.ItemMoveCallback
import com.buller.mysqlite.fragments.constans.FragmentConstants
import com.buller.mysqlite.theme.BaseTheme
import com.bumptech.glide.Glide
import com.dolatkia.animatedThemeManager.AppTheme
import com.easynote.domain.models.BackgroundColor
import com.easynote.domain.models.Category
import com.easynote.domain.models.ColorWithHSL
import com.easynote.domain.models.Image
import com.easynote.domain.models.ImageItem
import com.easynote.domain.models.MultiItem
import com.easynote.domain.models.Note
import com.easynote.domain.models.TextItem
import com.easynote.domain.utils.ImageManager
import com.easynote.domain.utils.ShareNoteAsSimpleText
import com.easynote.domain.utils.SystemUtils
import com.easynote.domain.utils.edittextnote.CommandReplaceText
import com.easynote.domain.viewmodels.AddFragmentViewModel
import com.easynote.domain.viewmodels.BaseViewModel
import com.easynote.domain.viewmodels.NotesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.UUID

class AddFragment : BaseFragment(),
    View.OnClickListener, OnDragImageToAnotherImageItem, BaseItemAdapterCallback<MultiItem>,
    OnImageSelectListener {
    private lateinit var binding: FragmentAddBinding
    private val mNoteViewModel: NotesViewModel by activityViewModels()
    private val mAddFragmentViewModel: AddFragmentViewModel by viewModel()
    override val mBaseViewModel: BaseViewModel get() = mAddFragmentViewModel
    private val itemsAdapter: MultiAdapter by lazy { MultiAdapter(this) }
    private var existCategories = arrayListOf<Category>()
    private var isUserChangeText = true
    private val callback: ItemMoveCallback by lazy { ItemMoveCallback(itemsAdapter) }
    private val touchHelper: ItemTouchHelper by lazy { ItemTouchHelper(callback) }
    private var wrapperPopupmenu: Context? = null
    private var wrapperDialog: Context? = null
    private var wrapperDialogAddCategory: Context? = null
    private val imageManager: ImageManager by inject()
    private var isEditableNote: Boolean = true

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        imageDeleteCallback()
        if (state != null && state.getBoolean(FragmentConstants.ACTION_MODE_KEY, false)) {
            mAddFragmentViewModel.setActionMode(
                (activity as MainActivity).startSupportActionMode(
                    actionModeCallback
                )
            )
        }

        val noteId = requireArguments().getLong(FragmentConstants.NOTE_ID)

        mAddFragmentViewModel.setNoteId(noteId)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        val actionMode = mAddFragmentViewModel.actionMode.value
        if (actionMode != null) {
            outState.putBoolean(FragmentConstants.ACTION_MODE_KEY, true)
        } else {
            outState.putBoolean(FragmentConstants.ACTION_MODE_KEY, false)
        }

        super.onSaveInstanceState(outState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        state: Bundle?
    ): View {
        binding = FragmentAddBinding.inflate(inflater, container, false)
        initEventObservers()
        initItemsAdapter()
        initEditNoteLayout()

        mAddFragmentViewModel.currentItemsNote.observe(viewLifecycleOwner) { listItems ->
            if (listItems != null) {
                val currentItems = listItems.filterNot { item -> item.isDeleted }
                itemsAdapter.submitList(currentItems)
            }
        }

        mAddFragmentViewModel.listCurrentGradientColors.observe(viewLifecycleOwner) { listColor ->

            if (listColor.isNotEmpty()) {
                createdBackground(
                    listColor[0].colorWithHSL.color,
                    listColor[1].colorWithHSL.color
                )
            }
        }

        mAddFragmentViewModel.selectedNote.observe(viewLifecycleOwner) { selectedNote ->
            if (selectedNote != null) {
                initFieldsNote(selectedNote)
            }
        }

        mAddFragmentViewModel.existCategories.observe(viewLifecycleOwner) { list ->
            existCategories.clear()
            val sortedCategories = list.sortedBy { it.position }
            existCategories.addAll(sortedCategories)
        }

        mAddFragmentViewModel.actionMode.observe(viewLifecycleOwner) { actionMode ->
            itemsAdapter.setActionMode(actionMode)
            itemsAdapter.notifyDataSetChanged()
        }

        binding.apply {

            imBtPopupMenuCategories.setOnClickListener {
                createPopupMenuCategory(imBtPopupMenuCategories)
            }
            tvTitleCategory.setOnClickListener {
                createPopupMenuCategory(imBtPopupMenuCategories)
            }
            bBold.setOnClickListener(this@AddFragment)
            bStrikeline.setOnClickListener(this@AddFragment)
            bItalic.setOnClickListener(this@AddFragment)
            bCleanText.setOnClickListener(this@AddFragment)
            bUnderline.setOnClickListener(this@AddFragment)
            bListText.setOnClickListener(this@AddFragment)

            etTitle.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    mAddFragmentViewModel.setChangeInSelectedNote()
                }
            })
        }
        scrollingListItems()
        onBackPressedAndBackArrow()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            @RequiresApi(Build.VERSION_CODES.Q)
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_toolbar_add_fragment, menu)
                initLockButtonToolbar(menu)
                initPopupMenuButtonToolbar(menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.undo -> {
                        if (mAddFragmentViewModel.undo.isNotEmpty()) {
                            isUserChangeText = false
                            mAddFragmentViewModel.undoTextFromItem()
                            isUserChangeText = true
                        }
                        return true
                    }

                    R.id.redo -> {
                        if (mAddFragmentViewModel.redo.isNotEmpty()) {
                            isUserChangeText = false
                            mAddFragmentViewModel.redoTextFromItem()
                            isUserChangeText = true
                        }
                        return true
                    }
                }
                return true
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
        super.onViewCreated(view, savedInstanceState)
    }

    fun initLockButtonToolbar(menu: Menu) {
        val lockButton = menu.findItem(R.id.edit_note).actionView as CheckBox
        lockButton.buttonDrawable =
            ResourcesCompat.getDrawable(resources, R.drawable.selector_edit_note, null)

        val colorStateList = ColorStateList(
            arrayOf(
                intArrayOf(-android.R.attr.state_checked),
                intArrayOf(android.R.attr.state_checked)
            ), intArrayOf(
                ContextCompat.getColor(requireContext(), R.color.dark_gray),
                ContextCompat.getColor(requireContext(), R.color.red_delete_light)
            )
        )
        lockButton.buttonTintList = colorStateList
        lockButton.isChecked = !isEditableNote

        if (!isEditableNote) {
            binding.rcItemsNote.forEachChildView { it.isEnabled = false }
            binding.editPanel.forEachChildView { it.isEnabled = false }
        } else {
            binding.addFragmentLayout.forEachChildView { it.isEnabled = true }
        }

        lockButton.setOnCheckedChangeListener { button, isChecked ->
            if (isChecked) {
                binding.rcItemsNote.forEachChildView { it.isEnabled = false }
                binding.editPanel.forEachChildView { it.isEnabled = false }
                mAddFragmentViewModel.setEditable(false)
                Toast.makeText(requireContext(), "Lock Note", Toast.LENGTH_SHORT).show()
            } else {
                binding.rcItemsNote.forEachChildView { it.isEnabled = true }
                binding.editPanel.forEachChildView { it.isEnabled = true }
                mAddFragmentViewModel.setEditable(true)
                Toast.makeText(requireContext(), "Unlock Note", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun initPopupMenuButtonToolbar(menu: Menu) {
        val menuPopupButton = menu.findItem(R.id.menu_add_note).actionView as ImageButton
        menuPopupButton.background = ResourcesCompat.getDrawable(
            resources, R.drawable.ic_filters_menu_list_fragment, context?.theme
        )

        menuPopupButton.setOnClickListener {
            val viewButton: ImageButton = requireActivity().findViewById(R.id.menu_add_note)
            val currentNote = mAddFragmentViewModel.selectedNote.value
            if (currentNote != null) {
                showPopupMenuToolbar(viewButton, currentNote.isPin, currentNote.isFavorite)
            }
        }
    }

    private fun View.forEachChildView(closure: (View) -> Unit) {
        closure(this)
        val groupView = this as? ViewGroup ?: return
        val size = groupView.childCount - 1
        for (i in 0..size) {
            groupView.getChildAt(i).forEachChildView(closure)
        }
    }

    private fun onBackPressedAndBackArrow() {
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val currentNote = mAddFragmentViewModel.selectedNote.value
                if (currentNote != null) {
                    if (currentNote.isChanged) {
                        showSaveChangesDialog()
                    } else {
                        findNavController().popBackStack()
                    }
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner, onBackPressedCallback
        )

        val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar)

        toolbar.setNavigationOnClickListener {
            val currentNote = mAddFragmentViewModel.selectedNote.value
            if (currentNote != null) {
                if (currentNote.isChanged) {
                    showSaveChangesDialog()
                } else {
                    findNavController().popBackStack()
                }
            }
        }
    }

    private fun initItemsAdapter() {
        binding.rcItemsNote.apply {
           layoutManager = LinearLayoutManager(requireContext())
            initThemeObserver()
            adapter = itemsAdapter
        }
        itemsAdapter.attachCallback(this@AddFragment)

        mAddFragmentViewModel.selectedItemsNoteFromActionMode.observe(viewLifecycleOwner) { list ->
            val actionMode = mAddFragmentViewModel.actionMode.value
            if (actionMode != null) {
                actionMode!!.title = getString(R.string.selected_items, list.size)
            }
        }

        itemsAdapter.getTextWatcher = { item ->
            createTextWatcher(item)
        }

    }

    private fun initFieldsNote(currentNote: Note) = with(binding) {
        if (currentNote.id != 0L) {
            etTitle.setText(currentNote.title)
            val timeLastSave = currentNote.lastChangedTime
            if (timeLastSave.isNotEmpty()) {
                val timeLastChangeText = "Last save: $timeLastSave"
                tvLastChange.text = timeLastChangeText
            } else {
                tvLastChange.visibility = View.INVISIBLE
            }
            isEditableNote = currentNote.isEditable
        } else {
            tvLastChange.visibility = View.GONE
        }

        if (currentNote.isPin) {
            imPinAddFragment.visibility = View.VISIBLE
        } else {
            imPinAddFragment.visibility = View.INVISIBLE
        }

        if (currentNote.isFavorite) {
            imFavAddFragment.visibility = View.VISIBLE
        } else {
            imFavAddFragment.visibility = View.INVISIBLE
        }


        createdBackground(
            currentNote.gradientColorFirst,
            currentNote.gradientColorSecond
        )
        mAddFragmentViewModel.setSelectedColors(mapToBackground(currentNote))

    }

    private fun initEditNoteLayout() = with(binding) {
        fbSave.setOnClickListener {
            saveNoteToDatabase()
        }

        btEditText.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                showEditTextPanel()
            } else {
                hideEditTextPanel()
            }
        }

        btAddTextItem.setOnClickListener {
            mAddFragmentViewModel.createNewItemTextFromNote()
            val lastPosition = mAddFragmentViewModel.currentItemsNote.value?.size
            if (lastPosition != null) {
                binding.rcItemsNote.smoothSnapToPosition(lastPosition)
            }
        }

        btAddPhoto.setOnClickListener {
            selectedMultiImages()
        }

        btChangeColorBackground.setOnClickListener {
            val currentColor = mAddFragmentViewModel.listCurrentGradientColors.value!!
            val dialog = ModBtSheetChooseColor(currentColor)
            dialog.show(childFragmentManager, ModBtSheetChooseColor.TAG)
            dialog.onSaveColorsFromCurrentNote = { currList ->
                mAddFragmentViewModel.setSelectedColors(currList)
            }
        }
    }

    private fun showEditTextPanel() = with(binding) {
        editNoteGroupLayout.animate()
            .translationYBy(100f)
            .alpha(0.0f)
            .setDuration(200).withEndAction {
                editNoteGroupLayout.alpha = 0.0f
                editNoteGroupLayout.visibility = View.INVISIBLE
            }

        btEditText.animate()
            .alpha(1.0f)
            .translationX(-editNoteGroupLayout.width.toFloat())
            .setDuration(300).withEndAction {

                editNoteGroupLayout.visibility = View.GONE

                btEditText.translationX = 0f
                editTextPanel.translationY = 100f
                editTextPanel.animate()
                    .alpha(1.0f)
                    .translationY(0f)
                    .setDuration(300)
                    .withStartAction {

                        editTextPanel.visibility = View.VISIBLE
                    }
            }
    }

    private fun hideEditTextPanel() = with(binding) {
        editTextPanel.animate()
            .alpha(0.0f)
            .translationY(100f)
            .setDuration(200)
            .withEndAction {
                editTextPanel.visibility = View.GONE
            }
        btEditText.translationX = 0f
        btEditText.animate()
            .alpha(1.0f)
            .translationX(
                editNoteGroupLayout.width.toFloat()
            )
            .setDuration(300).withEndAction {
                btChangeColorBackground.alpha = 1.0f

                editNoteGroupLayout.visibility = View.VISIBLE

                btEditText.translationX = 0f


                editNoteGroupLayout.animate()
                    .translationY(0f)
                    .alpha(1.0f)
                    .setDuration(200).withStartAction {
                        editNoteGroupLayout.alpha = 1.0f
                    }
            }
    }

    private fun RecyclerView.smoothSnapToPosition(
        position: Int,
        snapMode: Int = LinearSmoothScroller.SNAP_TO_END
    ) {
        val smoothScroller = object : LinearSmoothScroller(this.context) {
            override fun getVerticalSnapPreference(): Int = snapMode
            override fun getHorizontalSnapPreference(): Int = snapMode
        }
        smoothScroller.targetPosition = position
        layoutManager?.startSmoothScroll(smoothScroller)
    }

    private fun initThemeObserver() {
        mNoteViewModel.currentTheme.observe(viewLifecycleOwner) { currentTheme ->
            itemsAdapter.setTheme(currentTheme)
        }
    }

    private fun createdBackground(firstColor: Int, secondColor: Int) {
        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            intArrayOf(
                firstColor,
                secondColor
            )
        )
        gradientDrawable.cornerRadius = 50f
        binding.addFragmentLayout.background = gradientDrawable
    }

    private fun mapToBackground(currentNote: Note): List<BackgroundColor> {
        val firstGradient = BackgroundColor(
            0,
            ColorWithHSL(
                currentNote.gradientColorFirst,
                currentNote.gradientColorFirstH,
                currentNote.gradientColorFirstS,
                currentNote.gradientColorFirstL
            ),
        )

        val secondGradient = BackgroundColor(
            0,
            ColorWithHSL(
                currentNote.gradientColorSecond,
                currentNote.gradientColorSecondH,
                currentNote.gradientColorSecondS,
                currentNote.gradientColorSecondL
            ),
        )
        val listColor = arrayListOf<BackgroundColor>()
        listColor.add(firstGradient)
        listColor.add(secondGradient)
        return listColor
    }

    private fun createPopupMenuCategory(view: View) {
        val popupMenu = PopupMenu(wrapperPopupmenu, view)
        popupMenu.menu.add("Add new category")
        val checkedId = arrayListOf<Int>()

        mAddFragmentViewModel.currentCategories.value?.forEach {
            checkedId.add(it.idCategory.toInt())
        }

        if (existCategories.isNotEmpty()) {
            for ((id, title, position) in existCategories) {
                val text: CharSequence = title
                popupMenu.menu.add(
                    Menu.NONE, id.toInt(), position, text
                ).setCheckable(true).setChecked(id.toInt() in checkedId)

//                val categoryId = existCategories[id.toInt() - 1].idCategory.toInt()
//                popupMenu.menu.add(
//                    Menu.NONE, categoryId, id.toInt(), text
//                ).setCheckable(true).setChecked(categoryId in checkedId)

            }
        }

        popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
            override fun onMenuItemClick(item: MenuItem?): Boolean {
                item?.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW)
                item?.actionView = View(requireContext())
                item?.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
                    override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                        return false
                    }

                    override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                        return false
                    }
                })
                when (item!!.itemId) {
                    0 -> {
                        showAddCategoryDialog()
                        popupMenu.dismiss()
                    }

                    else -> {
                        if (item.isChecked) {
                            item.isChecked = false
                            checkedId.remove(item.itemId)
                        } else {
                            item.isChecked = true
                            checkedId.add(item.itemId)
                        }
                    }
                }
                return false
            }
        })

        popupMenu.setOnDismissListener {
            val listSelected = arrayListOf<Category>()
            checkedId.forEach { idCategory ->
                existCategories.forEach {
                    if (idCategory == it.idCategory.toInt()) {
                        listSelected.add(it)
                    }
                }
            }
            mAddFragmentViewModel.updateCurrentCategories(listSelected)
        }
        popupMenu.show()
    }

    override fun syncTheme(appTheme: AppTheme) {
        val theme = appTheme as BaseTheme

        binding.apply {
            wrapperPopupmenu = ContextThemeWrapper(requireContext(), theme.stylePopupTheme())
            wrapperDialog = ContextThemeWrapper(requireContext(), theme.styleDialogTheme())
            wrapperDialogAddCategory =
                ContextThemeWrapper(requireContext(), theme.styleDialogAddCategory())

            tvLastChange.setTextColor(theme.textColor(requireContext()))

            tvTitleCategory.setTextColor(theme.textColor(requireContext()))

            imBtPopupMenuCategories.setColorFilter(theme.textColorTabUnselect(requireContext()))

            etTitle.setTextColor(theme.textColor(requireContext()))
            etTitle.setHintTextColor(theme.textColorTabUnselect(requireContext()))

            editPanel.backgroundTintList =
                ColorStateList.valueOf(theme.backgroundDrawer(requireContext()))

            btAddTextItem.backgroundTintList =
                ColorStateList.valueOf(theme.backgroundDrawer(requireContext()))
            btAddTextItem.setColorFilter(theme.akcColor(requireContext()))

            btEditText.backgroundTintList =
                ColorStateList.valueOf(theme.backgroundDrawer(requireContext()))
            btEditText.buttonTintList = ColorStateList.valueOf(theme.akcColor(requireContext()))

            btAddPhoto.backgroundTintList =
                ColorStateList.valueOf(theme.backgroundDrawer(requireContext()))
            btAddPhoto.setColorFilter(theme.akcColor(requireContext()))

            btChangeColorBackground.backgroundTintList =
                ColorStateList.valueOf(theme.backgroundDrawer(requireContext()))
            btChangeColorBackground.setColorFilter(theme.akcColor(requireContext()))

            bBold.backgroundTintList =
                ColorStateList.valueOf(theme.backgroundDrawer(requireContext()))
            bBold.setColorFilter(theme.akcColor(requireContext()))

            bCleanText.backgroundTintList =
                ColorStateList.valueOf(theme.backgroundDrawer(requireContext()))
            bCleanText.setColorFilter(theme.akcColor(requireContext()))

            bItalic.backgroundTintList =
                ColorStateList.valueOf(theme.backgroundDrawer(requireContext()))
            bItalic.setColorFilter(theme.akcColor(requireContext()))

            bListText.backgroundTintList =
                ColorStateList.valueOf(theme.backgroundDrawer(requireContext()))
            bListText.setColorFilter(theme.akcColor(requireContext()))

            bStrikeline.backgroundTintList =
                ColorStateList.valueOf(theme.backgroundDrawer(requireContext()))
            bStrikeline.setColorFilter(theme.akcColor(requireContext()))

            bUnderline.backgroundTintList =
                ColorStateList.valueOf(theme.backgroundDrawer(requireContext()))
            bUnderline.setColorFilter(theme.akcColor(requireContext()))

            fbSave.backgroundTintList =
                ColorStateList.valueOf(theme.backgroundDrawer(requireContext()))
            fbSave.setColorFilter(theme.akcColor(requireContext()))
        }
    }

    private fun showPopupMenuToolbar(view: View, isPin: Boolean, isFavorite: Boolean) {
        val currentTheme = mNoteViewModel.currentTheme.value
        val popupMenu = CustomPopupMenu(wrapperPopupmenu!!, view, currentTheme)
        val fromArchive = mAddFragmentViewModel.selectedNote.value!!.isArchive
        val fromDelete = mAddFragmentViewModel.selectedNote.value!!.isDeleted
        popupMenu.showPopupMenuFromSelectedNote(isPin, isFavorite, fromArchive, fromDelete)

        popupMenu.onChangeNotePin = { newIsPin ->
            mAddFragmentViewModel.changeNotePin(newIsPin)
        }

        popupMenu.onChangeNoteFavorite = { newIsFavorite ->
            mAddFragmentViewModel.changeNoteFavorite(newIsFavorite)
        }

        popupMenu.onChangeNoteArch = {
            if (fromArchive) {
                showUnarchiveDialog()
            } else {
                showArchiveDialog()
            }
            popupMenu.dismiss()
        }

        popupMenu.onDeleteNote = {
            if (fromDelete) {
                showUndeleteNoteDialog()
            } else {
                showDeleteNoteDialog()
            }

            popupMenu.dismiss()
        }

        popupMenu.onPermanentDeleteNote = {
            showPermanentDeleteDialog()
            popupMenu.dismiss()
        }

        popupMenu.onSharedNoteText = {
            val select = mAddFragmentViewModel.selectedNote.value
            if (select != null) {
                ShareNoteAsSimpleText.sendSimpleText(select, requireContext())
            }
        }

        popupMenu.onActivateActionMode = {
            mAddFragmentViewModel.setActionMode(
                (activity as MainActivity).startSupportActionMode(
                    actionModeCallback
                )
            )
            popupMenu.dismiss()
        }
    }

    private fun showSaveChangesDialog() {
        MaterialDialog(wrapperDialog!!).show {
            title(R.string.dialog_save_title)
            message(R.string.dialog_save_message)
            positiveButton(R.string.yes) { dialog ->
                saveNoteToDatabase()
                dialog.dismiss()
            }
            negativeButton(R.string.no) { dialog ->
                mAddFragmentViewModel.deleteTempImageFiles()
                dialog.dismiss()
                findNavController().popBackStack()
            }

            cancelable(false)
            cancelOnTouchOutside(false)
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
                //ToDo :add new category from current note (set checkbox)
                mAddFragmentViewModel.setCategory(newCategory)
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
                mAddFragmentViewModel.changeNoteArchive(isArchive = false)
                dialog.dismiss()
                findNavController().popBackStack()
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
                mAddFragmentViewModel.changeNoteArchive(isArchive = true)
                dialog.dismiss()
                findNavController().popBackStack()
            }
            negativeButton(R.string.no) { dialog ->
                dialog.dismiss()
            }
        }
    }

    private fun showUndeleteNoteDialog() {
        MaterialDialog(wrapperDialog!!).show {
            title(R.string.title_restore)
            message(R.string.message_restore_note)
            positiveButton(R.string.yes) { dialog ->
                mAddFragmentViewModel.changeNoteDelete(isDelete = false)
                dialog.dismiss()
                findNavController().popBackStack()
            }
            negativeButton(R.string.no) { dialog ->
                dialog.dismiss()
            }
        }
    }

    private fun showDeleteNoteDialog() {
        MaterialDialog(wrapperDialog!!).show {
            title(R.string.delete)
            message(R.string.message_text)
            positiveButton(R.string.yes) { dialog ->
                mAddFragmentViewModel.changeNoteDelete(isDelete = true)
                dialog.dismiss()
                findNavController().popBackStack()
            }
            negativeButton(R.string.no) { dialog ->
                dialog.dismiss()
            }
        }
    }

    private fun showPermanentDeleteDialog() {
        MaterialDialog(wrapperDialog!!).show {
            title(R.string.delete)
            message(R.string.message_permanent_delete)
            positiveButton(R.string.yes) { dialog ->
                mAddFragmentViewModel.deleteSelectedNote()
                dialog.dismiss()
                findNavController().popBackStack()
            }
            negativeButton(R.string.no) { dialog ->
                dialog.dismiss()
            }
        }
    }

    private fun showDeleteSelectedNoteItemsDialog() {
        MaterialDialog(wrapperDialog!!).show {
            title(R.string.delete)
            message(R.string.dialog_delete_selected_items)
            positiveButton(R.string.yes) { dialog ->
                mAddFragmentViewModel.deleteSelectionItemsNoteFromActionMode()
                dialog.dismiss()
            }
            negativeButton(R.string.no) { dialog ->
                dialog.dismiss()
            }
        }
    }

    private fun saveNoteToDatabase() = with(binding) {
        val title = etTitle.text.toString()
        mAddFragmentViewModel.saveNoteToDatabase(title)
        findNavController().popBackStack()
    }

    private fun selectedMultiImages() {
        val dialog = SelectMediaBottomSheetFragment(this@AddFragment)
        dialog.show(childFragmentManager, SelectMediaBottomSheetFragment.TAG)
    }

    override fun onImagesSelected(uris: List<Uri>) {
        if (uris.isNotEmpty()) {
            workWithImages(uris)
        }
    }

    private fun workWithImages(uris: List<Uri>) {
        lifecycleScope.launch(Dispatchers.IO) {
            val map = hashMapOf<String, String>()
            uris.forEach {
                val uuid = UUID.randomUUID().toString()
                val job = async(Dispatchers.IO) {
                    val bitmap = Glide.with(requireContext()).asBitmap().load(it).submit().get()
                    imageManager.createFile(bitmap, uuid)
                }
                map[uuid] = job.await()
            }
            mAddFragmentViewModel.setImagesToNote(map)
        }
    }

    override fun onClick(view: View?) {
        val activeView = requireActivity().window.currentFocus
        if (activeView != null && view != null) {
            (activeView as EditText).text = EditTextNoteUtil.editText(activeView, view)
            activeView.setSelection(activeView.selectionStart, activeView.selectionEnd)
        }
    }

    private val actionModeCallback = object : ActionMode.Callback {

        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            val currentTheme = mNoteViewModel.currentTheme.value

            if (currentTheme != null) {
                DecoratorView.setColorBackgroundFromActionModeToolbar(
                    requireActivity() as MainActivity, currentTheme
                )
            }

            hideKeyboard()

            touchHelper.attachToRecyclerView(binding.rcItemsNote)
            binding.apply {
                linear.visibility = View.GONE
                editPanel.visibility = View.GONE
                fbSave.visibility = View.GONE
            }

            itemsAdapter.notifyDataSetChanged()
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
                    showDeleteSelectedNoteItemsDialog()
                    true
                }

                else -> false
            }
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            val currentTheme = mNoteViewModel.currentTheme.value
            if (currentTheme != null) {
                DecoratorView.setThemeColorBackgroundNavigationBar(
                    requireActivity() as MainActivity, currentTheme
                )
            }
            binding.apply {
                linear.visibility = View.VISIBLE
                editPanel.visibility = View.VISIBLE
                fbSave.visibility = View.VISIBLE
            }

            touchHelper.attachToRecyclerView(null)
            mAddFragmentViewModel.clearSelectedItemsNoteFromActionMode()
            mAddFragmentViewModel.setActionMode(null)
            itemsAdapter.notifyDataSetChanged()
        }
    }

    fun hideKeyboard() {
        var view = requireActivity().currentFocus
        if (view == null) view = View(requireActivity())
        SystemUtils.hideSoftKeyboard(view, requireContext())
    }

    private fun imageDeleteCallback() {
        setFragmentResultListener("imageFragment") { requestKey: String, bundle: Bundle ->
            val result = bundle.getParcelable<Image>("deleteImageId")
            if (result != null) {
                mAddFragmentViewModel.deleteImageFromImageItem(result)
            }
        }
    }

    private fun scrollingListItems() = with(binding) {
        val heightScreen = SystemUtils.heightScreen(requireActivity())
        val scrollHeight = heightScreen / 6

        val layoutParamsUp = viewScrollingUp.layoutParams
        layoutParamsUp.height = scrollHeight
        viewScrollingUp.layoutParams = layoutParamsUp

        val layoutParamsDawn = viewScrollingDawn.layoutParams
        layoutParamsDawn.height = scrollHeight
        viewScrollingDawn.layoutParams = layoutParamsDawn

        viewScrollingDawn.layoutParams.height = scrollHeight

        viewScrollingUp.setOnDragListener { v, event ->
            if (event.action == DragEvent.ACTION_DRAG_LOCATION) {
                rcItemsNote.smoothScrollBy(0, -100)
            }
            return@setOnDragListener true
        }

        viewScrollingDawn.setOnDragListener { v, event ->
            if (event.action == DragEvent.ACTION_DRAG_LOCATION) {
                rcItemsNote.smoothScrollBy(0, 100)
            }
            return@setOnDragListener true
        }
    }

    override fun setImageFromTarget(image: Image, targetPosition: Int, targetImageItem: ImageItem) {
        mAddFragmentViewModel.setImageFromTarget(image, targetPosition, targetImageItem)
    }

    override fun removeSourceImage(image: Image, sourceImageItem: ImageItem) {
        mAddFragmentViewModel.removeSourceImage(image, sourceImageItem)
    }

    override fun onMultiItemClick(
        model: MultiItem,
        view: View,
        position: Int,
        holder: BaseViewHolder<MultiItem>
    ) {
        val actionMode = mAddFragmentViewModel.actionMode.value

        val selectedItems = mAddFragmentViewModel.selectedItemsNoteFromActionMode.value
        if (actionMode != null) {

            if (selectedItems != null) {
                view.isActivated = selectedItems.contains(model)
            }
            mAddFragmentViewModel.changeSelectedItemsNoteFromActionMode(model)
            itemsAdapter.notifyItemChanged(position)

        }

    }

    override fun onMultiItemLongClick(model: MultiItem, view: View): Boolean {
        TODO("Not yet implemented")
    }

    private fun createTextWatcher(textItem: TextItem): TextWatcher {
        var oldText = ""
        return object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                if (!isUserChangeText) return
                if (s == null) return
                oldText = s.substring(start, start + count)
            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                if (!isUserChangeText) return
                if (s == null) return
                val newText = s.substring(start, start + count)
                val command = CommandReplaceText(
                    textItem.itemTextId,
                    textItem.position,
                    start,
                    oldText,
                    newText
                )
                setCommandReplaceText(command)
            }

            override fun afterTextChanged(s: Editable?) {

                for (span in s!!.getSpans(0, s.length, UnderlineSpan::class.java)) {
                    if (span !is CustomUnderlineSpan) {
                        s.removeSpan(span)
                    }
                }
                val newTextWithSpan = Html.toHtml(s, Html.FROM_HTML_MODE_COMPACT)
                textItem.text = newTextWithSpan
            }
        }
    }

    fun setCommandReplaceText(command: CommandReplaceText) {
        mAddFragmentViewModel.undo.push(command)
        mAddFragmentViewModel.redo.clear()
    }


}

