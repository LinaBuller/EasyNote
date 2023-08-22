package com.buller.mysqlite.fragments.add


import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.View.OnDragListener
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.view.ActionMode
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.buller.mysqlite.CustomPopupMenu
import com.buller.mysqlite.DecoratorView
import com.buller.mysqlite.MainActivity
import com.buller.mysqlite.R
import com.buller.mysqlite.databinding.FragmentAddBinding
import com.buller.mysqlite.dialogs.*
import com.buller.mysqlite.fragments.add.bottomsheet.pickerFavoriteColor.ModBtSheetChooseColor
import com.buller.mysqlite.fragments.add.bottomsheet.pickerImage.BottomSheetImagePicker
import com.buller.mysqlite.fragments.add.multiadapter.MultiItemAdapter
import com.buller.mysqlite.fragments.categories.ItemMoveCallback
import com.buller.mysqlite.fragments.constans.FragmentConstants
import com.buller.mysqlite.theme.BaseTheme
import com.bumptech.glide.Glide
import com.dolatkia.animatedThemeManager.AppTheme
import com.dolatkia.animatedThemeManager.ThemeFragment
import com.easynote.domain.models.BackgroungColor
import com.easynote.domain.models.Category
import com.easynote.domain.models.ColorWithHSL
import com.easynote.domain.models.Image
import com.easynote.domain.models.ImageItem
import com.easynote.domain.models.MultiItem
import com.easynote.domain.models.Note
import com.easynote.domain.utils.ShareNoteAsSimpleText
import com.easynote.domain.viewmodels.AddFragmentViewModel
import com.easynote.domain.viewmodels.NotesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream
import java.lang.ref.WeakReference
import java.util.UUID


class AddFragment : ThemeFragment(), BottomSheetImagePicker.OnImagesSelectedListener,
    View.OnClickListener, OnDragImageToAnotherImageItem{
    private lateinit var binding: FragmentAddBinding
    private val mNoteViewModel: NotesViewModel by activityViewModels()
    private val mAddFragmentViewModel: AddFragmentViewModel by viewModel()
    private val itemsAdapter: MultiItemAdapter by lazy { MultiItemAdapter(this) }
    private var existCategories = arrayListOf<Category>()
    private val listColorGradient: ArrayList<BackgroungColor> = arrayListOf()
    private var isActionMode = false
    private var isUserChangeText = true
    private val callback: ItemMoveCallback by lazy { ItemMoveCallback(itemsAdapter) }
    private val touchHelper: ItemTouchHelper by lazy { ItemTouchHelper(callback) }
    private var wrapperPopupmenu: Context? = null
    private var wrapperDialog: Context? = null
    private var wrapperDialogAddCategory: Context? = null


    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        imageDeleteCallback()
        if (state != null && state.getBoolean(FragmentConstants.ACTION_MODE_KEY, false)) {
            mAddFragmentViewModel.actionMode =
                (activity as MainActivity).startSupportActionMode(actionModeCallback)
        }

        val noteId = requireArguments().getLong("note_id")
        mAddFragmentViewModel.setNoteId(noteId)
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
        binding = FragmentAddBinding.inflate(inflater, container, false)

        initItemsAdapter()

        mAddFragmentViewModel.listCurrentGradientColors.observe(viewLifecycleOwner) { listColor ->
            createdBackground(
                listColor[0].colorWithHSL.color,
                listColor[1].colorWithHSL.color
            )
        }

        mAddFragmentViewModel.selectedNote.observe(viewLifecycleOwner) { selectedNote ->
            if (selectedNote != null) {
                initFieldsNote(selectedNote)

                createdBackground(
                    selectedNote.gradientColorFirst,
                    selectedNote.gradientColorSecond
                )

                listColorGradient.addAll(mapToBackground(currentNote = selectedNote))
            }
        }

        mAddFragmentViewModel.existCategories.observe(viewLifecycleOwner) {
            existCategories.clear()
            existCategories.addAll(it)
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
        }


        initEditNoteLayout()
        onBackPressedAndBackArrow()

        mAddFragmentViewModel.currentItemsFromNote.observe(viewLifecycleOwner) { listItems ->
            if (listItems != null) {

                val currentItems = listItems.filterNot { item -> item.isDeleted }
                itemsAdapter.submitListItems(currentItems)
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            @RequiresApi(Build.VERSION_CODES.Q)
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_toolbar_add_fragment, menu)
                val currentTheme = mNoteViewModel.currentTheme.value

                val menuPopupButton = menu.findItem(R.id.menu_add_note).actionView as ImageButton
                menuPopupButton.background = ResourcesCompat.getDrawable(
                    resources, R.drawable.ic_filters_menu_list_fragment, null
                )
                menuPopupButton.backgroundTintList =
                    ResourcesCompat.getColorStateList(resources, R.color.dark_gray, null)


                val lockButton = menu.findItem(R.id.edit_note).actionView as CheckBox
                lockButton.buttonDrawable =
                    ResourcesCompat.getDrawable(resources, R.drawable.selector_edit_note, null)

                val colorStateList = ColorStateList(
                    arrayOf(
                        intArrayOf(-android.R.attr.state_checked),
                        intArrayOf(android.R.attr.state_checked)
                    ), intArrayOf(
                        ContextCompat.getColor(requireContext(), R.color.dark_gray),
                        ContextCompat.getColor(requireContext(), R.color.red_delete)

                    )
                )
                lockButton.buttonTintList = colorStateList


                lockButton.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        //todo: do save state in database
                        binding.addFragmentLayout.forEachChildView { it.isEnabled = false }
                        binding.fbSave.isEnabled = true
                        Toast.makeText(requireContext(), "Lock Note", Toast.LENGTH_SHORT).show()
                    } else {

                        binding.addFragmentLayout.forEachChildView { it.isEnabled = true }
                        Toast.makeText(requireContext(), "Unlock Note", Toast.LENGTH_SHORT).show()
                    }
                }

                menuPopupButton.setOnClickListener {
                    val viewButton: ImageButton = requireActivity().findViewById(R.id.menu_add_note)
                    val currentNote = mAddFragmentViewModel.selectedNote.value
                    if (currentNote != null) {
                        showPopupMenuToolbar(viewButton, currentNote.isPin, currentNote.isFavorite)
                    }
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.found_to_note -> {
                        //todo found text from current note
                        return true
                    }

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

                    R.id.action_mode -> {
                        mAddFragmentViewModel.actionMode =
                            (activity as MainActivity).startSupportActionMode(actionModeCallback)

                        return true
                    }
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
        super.onViewCreated(view, savedInstanceState)
    }

    fun View.forEachChildView(closure: (View) -> Unit) {
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
                showSaveChangesDialog()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner, onBackPressedCallback
        )

        val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar)
        val colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
            resources.getColor(
                R.color.dark_gray,
                null
            ), BlendModeCompat.SRC_ATOP
        )
        toolbar.navigationIcon?.colorFilter = colorFilter
        toolbar.setNavigationOnClickListener {
            showSaveChangesDialog()
        }
    }

    private fun initItemsAdapter() {
        binding.rcItemsNote.apply {
            layoutManager = LinearLayoutManager(requireContext())
            initThemeObserver()
            adapter = itemsAdapter
        }

        mAddFragmentViewModel.selectedItemsNoteFromActionMode.observe(viewLifecycleOwner) { list ->
            mAddFragmentViewModel.actionMode?.title = getString(R.string.selected_items, list.size)
        }

        itemsAdapter.getIsUserChangeText = {
            isUserChangeText
        }

        itemsAdapter.setCommandReplaceText = { command ->
            mAddFragmentViewModel.undo.push(command)
            mAddFragmentViewModel.redo.clear()
        }

        itemsAdapter.getActionMode = {
            mAddFragmentViewModel.actionMode
        }

        itemsAdapter.getSelectedList = {
            mAddFragmentViewModel.selectedItemsNoteFromActionMode.value
        }

        itemsAdapter.onChangeSelectedList = { noteItem ->
            mAddFragmentViewModel.changeSelectedItemsNoteFromActionMode(noteItem)
        }
    }

    private fun initFieldsNote(currentNote: Note) = with(binding) {
        if (currentNote.id != 0L) {
            etTitle.setText(currentNote.title)
//            val currentText = Html
//                .fromHtml(currentNote.content, Html.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH)
//                .trimEnd('\n')
//            etContent.setText(currentText)

            val currentTime = currentNote.time
            if (currentTime.isNotEmpty()) {
                val timeLastChangeText = "Text changed: $currentTime"
                tvLastChange.text = timeLastChangeText
            } else {
                tvLastChange.visibility = View.INVISIBLE
            }

        } else {
            tvLastChange.visibility = View.INVISIBLE
        }
    }

    private fun initEditNoteLayout() = with(binding) {
        fbSave.setOnClickListener {
            saveNoteToDatabase()
        }
        //TODO: add animation
        btEditText.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                cardVChangesStyleText.visibility = View.VISIBLE
            } else {
                cardVChangesStyleText.visibility = View.GONE
            }
        }

        btAddTextItem.setOnClickListener {
            mAddFragmentViewModel.createNewItemTextFromNote()
        }
        btAddPhoto.setOnClickListener {
            selectedMultiImages()
        }
        btChangeColorBackground.setOnClickListener {
            val dialog = ModBtSheetChooseColor(listColorGradient)
            dialog.show(
                childFragmentManager, ModBtSheetChooseColor.TAG
            )
            dialog.onSaveColorsFromCurrentNote = {
                mAddFragmentViewModel.setSelectedColors(it)
            }
        }
    }

    private fun initThemeObserver() {
        mNoteViewModel.currentTheme.observe(viewLifecycleOwner) { currentTheme ->
            itemsAdapter.themeChanged(currentTheme)
        }
    }

    private fun createdBackground(firstColor: Int, secondColor: Int) = with(binding) {
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

    private fun mapToBackground(currentNote: Note): List<BackgroungColor> {
        val firstGradient = BackgroungColor(
            0,
            ColorWithHSL(
                currentNote.gradientColorFirst,
                currentNote.gradientColorFirstH,
                currentNote.gradientColorFirstS,
                currentNote.gradientColorFirstL
            ),
        )

        val secondGradient = BackgroungColor(
            0,
            ColorWithHSL(
                currentNote.gradientColorSecond,
                currentNote.gradientColorSecondH,
                currentNote.gradientColorSecondS,
                currentNote.gradientColorSecondL
            ),
        )
        val listColor = arrayListOf<BackgroungColor>()
        listColor.add(firstGradient)
        listColor.add(secondGradient)
        return listColor
    }

    private fun createPopupMenuCategory(view: View) = with(binding) {
        val popupMenu = PopupMenu(wrapperPopupmenu, view)
        popupMenu.menu.add("Add new category")
        val checkedId = arrayListOf<Int>()

        mAddFragmentViewModel.currentCategories.value?.forEach {
            checkedId.add(it.idCategory.toInt())
        }

        if (existCategories.isNotEmpty()) {
            for ((index, categoryItem) in existCategories) {
                val text: CharSequence = categoryItem
                val categoryId = existCategories[index.toInt() - 1].idCategory.toInt()
                popupMenu.menu.add(
                    Menu.NONE, categoryId, index.toInt(), text
                ).setCheckable(true).setChecked(categoryId in checkedId)
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
            cardVChangesStyleText.backgroundTintList =
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

    fun showPopupMenuToolbar(view: View, isPin: Boolean, isFavorite: Boolean) {
        val currentTheme = mNoteViewModel.currentTheme.value
        val popupMenu = CustomPopupMenu(wrapperPopupmenu!!, view, currentTheme)

        popupMenu.showPopupMenuFromSelectedNote(isPin, isFavorite)

        popupMenu.onChangeNotePin = { newIsPin ->

            mAddFragmentViewModel.changeNotePin(newIsPin)
        }

        popupMenu.onChangeNoteFavorite = { newIsFavorite ->
            mAddFragmentViewModel.changeNoteFavorite(newIsFavorite)
        }

        popupMenu.onChangeNoteArch = {
            showArchiveDialog()
            popupMenu.dismiss()
        }

        popupMenu.onSharedNoteText = {
            val select = mAddFragmentViewModel.selectedNote.value
            if (select != null) {
                ShareNoteAsSimpleText.sendSimpleText(select, requireContext())
            }
        }

        popupMenu.onDeleteNote = {
            showDeleteNoteDialog()
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

    private fun showArchiveDialog() {
        MaterialDialog(wrapperDialog!!).show {
            title(R.string.archive)
            message(R.string.add_this_note_to_the_archive)
            positiveButton(R.string.yes) { dialog ->
                mAddFragmentViewModel.updateStatusNote(isArchive = true)
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
                mAddFragmentViewModel.updateStatusNote(isDelete = true)
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

    //insert new note to database or update note
    private fun saveNoteToDatabase() = with(binding) {
//        val title = etTitle.text.toString()
//        val content = Html.toHtml(etContent.text, Html.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL)
//        val text = etContent.text.toString()
//        val currentNote = mNoteViewModel.selectedNote.value
//
//        if (EditTextNoteUtil.inputCheck(title, content)) {

//            if (currentNote!!.id == 0L) {
//                Toast.makeText(requireContext(), "Successfully added!", Toast.LENGTH_SHORT).show()
//            } else {
//                Toast.makeText(requireContext(), "Successfully updated!", Toast.LENGTH_SHORT).show()
//            }
//            findNavController().popBackStack()
//        } else {
//            Toast.makeText(requireContext(), "Please fill one and more fields!", Toast.LENGTH_SHORT)
//                .show()
//        }

        quickSave()
        findNavController().popBackStack()
    }

    private fun quickSave() = with(binding) {
        val title = etTitle.text.toString()
        mAddFragmentViewModel.saveData(title)
        val currentNote = mAddFragmentViewModel.selectedNote.value
        if (currentNote!!.id == 0L) {
            Toast.makeText(requireContext(), "Successfully added!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Successfully updated!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun selectedMultiImages() {
        BottomSheetImagePicker.Builder(resources.getString(R.string.file_provider))
            .columnSize(R.dimen.imagePickerColumnSize).multiSelect(1, 10)
            .cameraButton(BottomSheetImagePicker.ButtonType.Button)
            .galleryButton(BottomSheetImagePicker.ButtonType.Button).multiSelectTitles(
                R.plurals.imagePickerMulti,
                R.plurals.imagePickerMultiMore,
                R.string.imagePickerMultiLimit
            ).peekHeight(R.dimen.imagePickerPeekHeight).show(childFragmentManager)
    }

    override fun onImagesSelected(uris: List<Uri>, tag: String?) = with(binding) {
        if (uris.isNotEmpty()) {
            lifecycleScope.launch(Dispatchers.IO) {
                val listAbsolutePath = arrayListOf<String>()
                uris.forEach {
                    val job = async(Dispatchers.IO) {
                        val bitmap = Glide.with(requireContext()).asBitmap().load(it).submit().get()
                        createFile(bitmap)
                    }
                    listAbsolutePath.add(job.await())
                }
                mAddFragmentViewModel.setImagesToNote(listAbsolutePath)
            }
        }
    }

    private fun createFile(bitmap: Bitmap): String {
        val mContext = WeakReference(requireContext())
        var path = ""
        try {
            mContext.get()?.let {

                var file = File(it.filesDir, "Multimedia")
                if (!file.exists()) {
                    file.mkdir()
                }
                file = File(
                    file,
                    "img_${UUID.randomUUID()}.jpg"
                )

                val out = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
                out.flush()
                out.close()
                path = file.absolutePath
            }
            Log.i("Segregation", "Image saved.")
        } catch (e: Exception) {
            Log.i("Segregation", "Failed to save image.")
        }
        return path
    }


    override fun onClick(view: View?) = with(binding) {
//        if (view != null) {
//            etContent.text = EditTextNoteUtil.editText(etContent, view)
//            etContent.setSelection(etContent.selectionStart, etContent.selectionEnd)
//        }
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
            isActionMode = true

            binding.apply {
                linear.visibility = View.GONE
                editPanel.visibility = View.GONE
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
            }

            touchHelper.attachToRecyclerView(null)
            mAddFragmentViewModel.clearSelectedItemsNoteFromActionMode()
            mAddFragmentViewModel.actionMode = null
            itemsAdapter.notifyDataSetChanged()
            isActionMode = false
        }
    }

    fun hideKeyboard() {
        val imm =
            requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = requireActivity().currentFocus
        if (view == null) view = View(requireActivity())
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }


    private fun imageDeleteCallback() {
        setFragmentResultListener("imageFragment") { requestKey: String, bundle: Bundle ->
            val result = bundle.getParcelable<Image>("deleteImageId")
            if (result != null) {
                mAddFragmentViewModel.deleteImageFromImageItem(result)
            }
        }
    }

    override fun setImageFromTarget(image: Image, targetPosition: Int, targetImageItem: ImageItem) {
        mAddFragmentViewModel.setImageFromTarget(image, targetPosition, targetImageItem)
    }

    override fun removeSourceImage(image: Image, sourceImageItem: ImageItem) {
        mAddFragmentViewModel.removeSourceImage(image, sourceImageItem)
    }
}

