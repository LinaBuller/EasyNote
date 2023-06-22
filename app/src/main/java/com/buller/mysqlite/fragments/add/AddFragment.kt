package com.buller.mysqlite.fragments.add


import android.content.Context
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ActionMode
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.buller.mysqlite.MainActivity
import com.buller.mysqlite.R
import com.buller.mysqlite.databinding.FragmentAddBinding
import com.buller.mysqlite.dialogs.*
import com.buller.mysqlite.fragments.add.bottomsheet.pickerFavoriteColor.ModBtSheetChooseColorTitleOrColorContent
import com.buller.mysqlite.fragments.add.bottomsheet.pickerImage.BottomSheetImagePicker
import com.buller.mysqlite.fragments.add.multiadapter.ImageItem
import com.buller.mysqlite.fragments.add.multiadapter.MultiItemAdapter
import com.buller.mysqlite.fragments.add.multiadapter.TextItem
import com.buller.mysqlite.fragments.categories.ItemMoveCallback
import com.buller.mysqlite.fragments.constans.FragmentConstants
import com.buller.mysqlite.model.*
import com.buller.mysqlite.utils.*
import com.buller.mysqlite.utils.theme.BaseTheme
import com.buller.mysqlite.utils.theme.DecoratorView
import com.buller.mysqlite.viewmodel.NotesViewModel
import com.dolatkia.animatedThemeManager.AppTheme
import com.dolatkia.animatedThemeManager.ThemeFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class AddFragment : ThemeFragment(),
    BottomSheetImagePicker.OnImagesSelectedListener,
    OnCloseDialogListener,
    View.OnClickListener, OnUserChangeText {
    lateinit var binding: FragmentAddBinding
    private val mNoteViewModel: NotesViewModel by activityViewModels()
    private val itemsAdapter: MultiItemAdapter by lazy { MultiItemAdapter(this) }
    private var selectedCategory = arrayListOf<Category>()
    private var existCategory = arrayListOf<Category>()
    var wrapper: Context? = null
    var isUserChangeText = true
    var isActionMode = false
    val callback: ItemMoveCallback by lazy { ItemMoveCallback(itemsAdapter) }
    val touchHelper: ItemTouchHelper by lazy { ItemTouchHelper(callback) }

    companion object {
        const val TAG = "MyLog"
    }

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        if (state != null && state.getBoolean(FragmentConstants.ACTION_MODE_KEY, false)) {
            mNoteViewModel.actionMode =
                (activity as MainActivity).startSupportActionMode(actionModeCallback)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(FragmentConstants.ACTION_MODE_KEY, isActionMode)
        super.onSaveInstanceState(outState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddBinding.inflate(inflater, container, false)


        initItemsAdapter()

        mNoteViewModel.selectedNote.observe(viewLifecycleOwner) { selectedNote ->
            if (selectedNote != null) {
                initFieldsNote(selectedNote)
            }
        }

        initColorLiveDataObserver()
        initListenersButtons()
        initCategories()
        initEditNoteLayout()
        onBackPressedAndBackArrow()

        mNoteViewModel.selectedItemsNoteFromActionMode.observe(viewLifecycleOwner) { list ->
            mNoteViewModel.actionMode?.title = getString(R.string.selected_items, list.size)
        }
        return binding.root
    }

    private fun initItemsAdapter() {
        binding.rcItemsNote.apply {
            layoutManager = LinearLayoutManager(requireContext())
            initThemeObserver()
            adapter = itemsAdapter
        }

        itemsAdapter.onTextChanged = { command ->
            mNoteViewModel.undo.push(command)
            mNoteViewModel.redo.clear()
        }

        itemsAdapter.onItemClick = { item, view, position ->
            val actionMode = mNoteViewModel.actionMode
            if (actionMode != null) {
                mNoteViewModel.changeSelectedItemsNote(item)
                itemsAdapter.notifyItemChanged(position)
            }
        }
        mNoteViewModel.currentItemsFromNote.observe(viewLifecycleOwner) { listItems ->
            if (listItems != null) {
                itemsAdapter.submitListItems(listItems)
            }
        }
    }

    private fun initCategories() = with(binding) {
        initCategoryLiveDataObserver()
        val category = mNoteViewModel.readAllCategories.value
        if (category != null) {
            existCategory.clear()
            existCategory.addAll(category)
        }
        val selectCategory = mNoteViewModel.editedSelectCategoryFromDialogMoveCategory.value
        if (selectCategory != null) {
            selectedCategory.clear()
            selectedCategory.addAll(selectCategory)
        }

        imBtPopupMenuCategories.setOnClickListener {
            createPopupMenuCategory(imBtPopupMenuCategories)
        }
        tvTitleCategory.setOnClickListener {
            createPopupMenuCategory(imBtPopupMenuCategories)
        }
    }

    private fun initEditNoteLayout() = with(binding) {
        fbSave.setOnClickListener {
            saveNoteToDatabase()
        }
        //change animation and visibility
        btEditText.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                cardVChangesStyleText.visibility = View.VISIBLE
                editTextPanel.slideAnimation(SlideDirection.RIGHT, SlideType.SHOW, 300)
            } else {
                editTextPanel.slideAnimation(SlideDirection.LEFT, SlideType.HIDE, 300)
                cardVChangesStyleText.visibility = View.GONE
            }
        }

        btAddTextItem.setOnClickListener {
            mNoteViewModel.createNewItemTextFromNote()
        }
        btAddPhoto.setOnClickListener {
            selectedMultiImages()
        }
        btChangeColorBackground.setOnClickListener {
            ModBtSheetChooseColorTitleOrColorContent().show(
                childFragmentManager,
                ModBtSheetChooseColorTitleOrColorContent.TAG
            )
            mNoteViewModel.updateEditedFieldColor()
        }
    }

    private fun initThemeObserver() {
        mNoteViewModel.currentTheme.observe(viewLifecycleOwner) { currentTheme ->
            itemsAdapter.themeChanged(currentTheme)
        }
    }

    private fun initCategoryLiveDataObserver() = with(binding) {
        mNoteViewModel.readAllCategories.observe(viewLifecycleOwner) { listCategories ->
            existCategory.clear()
            existCategory.addAll(listCategories)
        }
        mNoteViewModel.editedSelectCategoryFromDialogMoveCategory.observe(viewLifecycleOwner) { editedCategoryList ->
            selectedCategory.clear()
            if (editedCategoryList != null) {
                selectedCategory.addAll(editedCategoryList)
            }
        }
    }

    private fun initListenersButtons() = with(binding) {
        bBold.setOnClickListener(this@AddFragment)
        bStrikeline.setOnClickListener(this@AddFragment)
        bItalic.setOnClickListener(this@AddFragment)
        bCleanText.setOnClickListener(this@AddFragment)
        bUnderline.setOnClickListener(this@AddFragment)
        bListText.setOnClickListener(this@AddFragment)
    }

    private fun initFieldsNote(currentNote: Note) = with(binding) {
        if (currentNote.id != 0L) {
            etTitle.setText(currentNote.title)
//            val currentText = Html
//                .fromHtml(currentNote.content, Html.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH)
//                .trimEnd('\n')
//            etContent.setText(currentText)

            mNoteViewModel.selectColorFieldsNote(
                listOf(
                    currentNote.colorFrameTitle,
                    currentNote.colorFrameContent
                )
            )
            val currentTime = currentNote.time
            if (currentTime.isNotEmpty()) {
                val timeLastChangeText = "Text changed: $currentTime"
                tvLastChange.text = timeLastChangeText
            } else {
                tvLastChange.visibility = View.INVISIBLE
            }

            lifecycleScope.launch(Dispatchers.IO) {
                mNoteViewModel.unionLiveData(currentNote.id)
                val noteWithCategories = mNoteViewModel.getNoteWithCategories(currentNote.id)
                val listCategories: List<Category>? = noteWithCategories.listOfCategories
                if (listCategories != null) {
                    mNoteViewModel.selectEditedCategoryPost(listCategories)
                }
            }

        } else {
            tvLastChange.visibility = View.INVISIBLE
            mNoteViewModel.setItemFromCurrentListItemsForNote(TextItem(position = 0))
        }
    }

    private fun initColorLiveDataObserver() = with(binding) {
        mNoteViewModel.currentColorsFields.observe(viewLifecycleOwner) { listColors ->
            DecoratorView.updateFieldsFromColors(
                listColors[0],
                listColors[1],
                titleCardViewAddFragment,
                addFragmentLayout,
                etTitle
            )
        }
    }

    private fun onBackPressedAndBackArrow() {
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showSaveDialog()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            onBackPressedCallback
        )

        val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            showSaveDialog()
        }
    }

    fun showSaveDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Do you want save this changes?")
        builder.setPositiveButton("Yes") { dialog, _ ->
            saveNoteToDatabase()
            dialog.dismiss()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
            findNavController().popBackStack()
        }
        builder.show()
    }

    fun showDialogAddCategory() {
        DialogAddNewCategory().show(childFragmentManager, DialogAddNewCategory.TAG)
    }

    private fun createPopupMenuCategory(view: View) = with(binding) {
        val popupMenu = PopupMenu(wrapper, view)
        popupMenu.menu.add("Add new category")
        val checkedId = arrayListOf<Int>()
        selectedCategory.forEach {
            checkedId.add(it.idCategory.toInt())
        }
        for ((index, categoryItem) in existCategory) {
            val text: CharSequence = categoryItem
            val categoryId = existCategory[index.toInt() - 1].idCategory.toInt()
            popupMenu.menu.add(
                Menu.NONE,
                categoryId,
                index.toInt(),
                text
            ).setCheckable(true).setChecked(categoryId in checkedId)
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
                        showDialogAddCategory()
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
                existCategory.forEach {
                    if (idCategory == it.idCategory.toInt()) {
                        listSelected.add(it)
                    }
                }
            }
            mNoteViewModel.selectEditedCategoryPost(listSelected)
        }
        popupMenu.show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            @RequiresApi(Build.VERSION_CODES.Q)
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_toolbar_add_fragment, menu)
                val menuPopupButton = menu.findItem(R.id.menu_add_note).actionView as ImageButton
                val lockButton = menu.findItem(R.id.edit_note).actionView as CheckBox
                menuPopupButton.background = ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.ic_filters_menu_list_fragment,
                    null
                )
                lockButton.buttonDrawable =
                    ResourcesCompat.getDrawable(resources, R.drawable.ic_edit, null)

                lockButton.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        Toast.makeText(requireContext(), "Lock Note", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Unlock Note", Toast.LENGTH_SHORT).show()
                    }
                }

                menuPopupButton.setOnClickListener {
                    val viewButton: ImageButton = requireActivity().findViewById(R.id.menu_add_note)
                    showPopupMenuToolbar(viewButton)
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.found_to_note -> {
                        //found text from current note
                        return true
                    }

                    R.id.undo -> {
                        if (mNoteViewModel.undo.isNotEmpty()) {
                            isUserChangeText = false
                            mNoteViewModel.undoTextFromItem()
                            isUserChangeText = true
                        }
                        return true
                    }

                    R.id.redo -> {
                        if (mNoteViewModel.redo.isNotEmpty()) {
                            isUserChangeText = false
                            mNoteViewModel.redoTextFromItem()
                            isUserChangeText = true
                        }
                        return true
                    }

                    R.id.action_mode -> {
                        mNoteViewModel.actionMode =
                            (activity as MainActivity).startSupportActionMode(actionModeCallback)
                        itemsAdapter.mViewModel = mNoteViewModel
                        itemsAdapter.notifyDataSetChanged()
                        return true
                    }
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        binding.root.requestLayout()
        Log.d(TAG, "AddFragment onResume")
    }

    override fun syncTheme(appTheme: AppTheme) {
        val theme = appTheme as BaseTheme
        val currentNote = mNoteViewModel.selectedNote.value
        binding.apply {
            wrapper = ContextThemeWrapper(requireContext(), theme.stylePopupTheme())


            tvLastChange.setTextColor(theme.textColorTabUnselect(requireContext()))
            etTitle.setTextColor(theme.textColor(requireContext()))

            if (currentNote!!.colorFrameTitle == 0) {
                etTitle.setBackgroundColor(theme.backgroundDrawer(requireContext()))
                titleCardViewAddFragment.setCardBackgroundColor(
                    theme.backgroundDrawer(
                        requireContext()
                    )
                )
            }

            etTitle.setHintTextColor(theme.textColorTabUnselect(requireContext()))


            titleCardViewAddFragment.outlineAmbientShadowColor = theme.setShadow(requireContext())
            titleCardViewAddFragment.outlineSpotShadowColor = theme.setShadow(requireContext())

            if (currentNote.colorFrameContent == 0) {
                addFragmentLayout.setBackgroundColor(theme.backgroundDrawer(requireContext()))
            }

            cardVChangesStyleText.backgroundTintList =
                ColorStateList.valueOf(theme.backgroundDrawer(requireContext()))

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

    fun showPopupMenuToolbar(view: View) {
        val currentTheme = mNoteViewModel.currentTheme.value
        val popupMenu = CustomPopupMenu(wrapper!!, view, currentTheme)
        val currentNote = mNoteViewModel.selectedNote.value
        popupMenu.showPopupMenuNoteItem(currentNote!!, true)

        popupMenu.onChangeItemNote = {
            mNoteViewModel.updateNote(it)
        }
        popupMenu.onItemCrypt = {

        }
        popupMenu.onChangeItemNoteArchive = {
            DialogIsArchive().show(childFragmentManager, DialogIsArchive.TAG)
            popupMenu.dismiss()
        }
        popupMenu.onChangeItemNoteDelete = {
            DialogDeleteNote().show(childFragmentManager, DialogDeleteNote.TAG)
            popupMenu.dismiss()
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
        mNoteViewModel.saveData(title)
        val currentNote = mNoteViewModel.selectedNote.value
        if (currentNote!!.id == 0L) {
            Toast.makeText(requireContext(), "Successfully added!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Successfully updated!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun selectedMultiImages() {
        BottomSheetImagePicker.Builder(resources.getString(R.string.file_provider))
            .columnSize(R.dimen.imagePickerColumnSize)
            .multiSelect(1, 10)
            .cameraButton(BottomSheetImagePicker.ButtonType.Button)
            .galleryButton(BottomSheetImagePicker.ButtonType.Button)
            .multiSelectTitles(
                R.plurals.imagePickerMulti,
                R.plurals.imagePickerMultiMore,
                R.string.imagePickerMultiLimit
            )
            .peekHeight(R.dimen.imagePickerPeekHeight)
            .show(childFragmentManager)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "AddFragment onDestroy")
        mNoteViewModel.clearEditImages()
        mNoteViewModel.cleanSelectedColors()
        mNoteViewModel.cleanSelectedCategories()
        mNoteViewModel.clearUndoRedo()
        mNoteViewModel.clearListItems()
    }

    override fun onImagesSelected(uris: List<Uri>, tag: String?) = with(binding) {
        if (uris.isNotEmpty()) {
            val listImages = arrayListOf<Image>()
            uris.forEach {
                listImages.add(Image(0, 0, it.toString()))
            }
            val currentItemSize = mNoteViewModel.currentItemsFromNote.value?.size
            if (currentItemSize != null) {
                val imageItem = ImageItem(position = currentItemSize, listImageItems = listImages)
                mNoteViewModel.setItemFromCurrentListItemsForNote(imageItem)
            }
        }
    }

    override fun onClick(view: View?) = with(binding) {
//        if (view != null) {
//            etContent.text = EditTextNoteUtil.editText(etContent, view)
//            etContent.setSelection(etContent.selectionStart, etContent.selectionEnd)
//        }
    }

    enum class SlideDirection {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }

    enum class SlideType {
        SHOW,
        HIDE
    }

    fun View.slideAnimation(direction: SlideDirection, type: SlideType, duration: Long = 250) {
        val fromX: Float
        val toX: Float
        val fromY: Float
        val toY: Float
        val array = IntArray(2)
        getLocationInWindow(array)
        if ((type == SlideType.HIDE && (direction == SlideDirection.RIGHT || direction == SlideDirection.DOWN)) ||
            (type == SlideType.SHOW && (direction == SlideDirection.LEFT || direction == SlideDirection.UP))
        ) {
            val displayMetrics = DisplayMetrics()
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            val deviceWidth = displayMetrics.widthPixels
            val deviceHeight = displayMetrics.heightPixels
            array[0] = deviceWidth
            array[1] = deviceHeight
        }
        when (direction) {
            SlideDirection.UP -> {
                fromX = 0f
                toX = 0f
                fromY = if (type == SlideType.HIDE) 0f else (array[1] + height).toFloat()
                toY = if (type == SlideType.HIDE) -1f * (array[1] + height) else 0f
            }

            SlideDirection.DOWN -> {
                fromX = 0f
                toX = 0f
                fromY = if (type == SlideType.HIDE) 0f else -1f * (array[1] + height)
                toY = if (type == SlideType.HIDE) 1f * (array[1] + height) else 0f
            }

            SlideDirection.LEFT -> {
                fromX = if (type == SlideType.HIDE) 0f else 1f * (array[0] + width)
                toX = if (type == SlideType.HIDE) -1f * (array[0] + width) else 0f
                fromY = 0f
                toY = 0f
            }

            SlideDirection.RIGHT -> {
                fromX = if (type == SlideType.HIDE) 0f else -1f * (array[0] + width)
                toX = if (type == SlideType.HIDE) 1f * (array[0] + width) else 0f
                fromY = 0f
                toY = 0f
            }
        }
        val animate = TranslateAnimation(
            fromX,
            toX,
            fromY,
            toY
        )
        animate.duration = duration
        animate.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                if (type == SlideType.HIDE) {
                    visibility = View.INVISIBLE
                }
            }

            override fun onAnimationStart(animation: Animation?) {
                visibility = View.VISIBLE
            }

        })
        startAnimation(animate)
    }

    override fun onCloseDialog(isDelete: Boolean, isArchive: Boolean) {
        mNoteViewModel.updateStatusNote(isDelete, isArchive)
        findNavController().popBackStack()
    }

    override fun setUserChange(): Boolean {
        return isUserChangeText
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
            touchHelper.attachToRecyclerView(binding.rcItemsNote)

            isActionMode = true
            binding.apply {
                linear.visibility = View.GONE
                titleCardViewAddFragment.visibility = View.GONE
                editPanel.visibility = View.GONE
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
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle("Do you want delete selected items?")
                    builder.setPositiveButton("Yes") { dialog, _ ->
                        mNoteViewModel.deleteOrUpdateSelectionItemsNote()
                        dialog.dismiss()
                        mode?.finish()
                    }
                    builder.setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                    builder.show()
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
                linear.visibility = View.VISIBLE
                titleCardViewAddFragment.visibility = View.VISIBLE
                editPanel.visibility = View.VISIBLE
            }

            touchHelper.attachToRecyclerView(null)
            mNoteViewModel.clearSelectedItemsNote()
            mNoteViewModel.actionMode = null
            itemsAdapter.notifyDataSetChanged()
            isActionMode = false
        }
    }
}

