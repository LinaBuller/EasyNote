package com.buller.mysqlite.fragments.add


import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.buller.mysqlite.R
import com.buller.mysqlite.databinding.FragmentAddBinding
import com.buller.mysqlite.dialogs.*
import com.buller.mysqlite.fragments.add.bottomsheet.pickerFavoriteColor.ModBtSheetChooseColorTitleOrColorContent
import com.buller.mysqlite.fragments.add.bottomsheet.pickerImage.BottomSheetImagePicker
import com.buller.mysqlite.fragments.constans.FragmentConstants
import com.buller.mysqlite.model.*
import com.buller.mysqlite.utils.*
import com.buller.mysqlite.utils.edittextnote.CommandReplaceText
import com.buller.mysqlite.utils.edittextnote.EditTextNoteUtil
import com.buller.mysqlite.utils.theme.BaseTheme
import com.buller.mysqlite.utils.theme.DecoratorView
import com.buller.mysqlite.viewmodel.NotesViewModel
import com.dolatkia.animatedThemeManager.AppTheme
import com.dolatkia.animatedThemeManager.ThemeFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class AddFragment : ThemeFragment(),
    BottomSheetImagePicker.OnImagesSelectedListener, DialogAddNewCategory.OnAddCategory,
    OnCloseDialogListener,
    View.OnClickListener {
    lateinit var binding: FragmentAddBinding
    private lateinit var mNoteViewModel: NotesViewModel
    private lateinit var imageAdapter: ImageAdapter
    private lateinit var currentNote: Note
    private var noteIsDeleted: Boolean = false
    private var selectedCategory = arrayListOf<Category>()
    private var existCategory = arrayListOf<Category>()
    var wrapper: Context? = null
    var isUserChangeText = true

    companion object {
        const val TAG = "MyLog"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "AddFragment onCreate")
        mNoteViewModel = ViewModelProvider(requireActivity())[NotesViewModel::class.java]
        if (arguments != null) {
            val isNewNote = requireArguments().getBoolean(FragmentConstants.NEW_NOTE_OR_UPDATE)
            noteIsDeleted = requireArguments().getBoolean(FragmentConstants.IMAGE_IS_DELETE)
            if (noteIsDeleted) {
                currentNote = requireArguments().getParcelable(FragmentConstants.UPDATE_NOTE)!!
            } else {
                currentNote = if (isNewNote) {
                    Note()
                } else {
                    requireArguments().getParcelable(FragmentConstants.UPDATE_NOTE)!!
                }
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "AddFragment onCreateView")
        binding = FragmentAddBinding.inflate(inflater, container, false)

        initImageAdapter()
        initImagesLiveDataObserver()
        initColorLiveDataObserver()


        if (currentNote.id != 0L) {
            initFieldsNote()
        }
        initListenersButtons()

        binding.apply {
            fbSave.setOnClickListener {
                saveNoteToDatabase()
            }
            initCategoryLiveDataObserver()
            val category = mNoteViewModel.readAllCategories.value
            if (category != null) {
                existCategory.clear()
                existCategory.addAll(category)
            }
            val selectCategory = mNoteViewModel.editedSelectCategoryFromAddFragment.value
            if (selectCategory != null) {
                selectedCategory.clear()
                selectedCategory.addAll(selectCategory)
            }


            imBtPopupMenuCategories.setOnClickListener {
                createPopupMenuCategory()
            }
            tvTitleCategory.setOnClickListener {
                createPopupMenuCategory()
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
            var oldText = ""
            etContent.addTextChangedListener(object : TextWatcher {
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

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (!isUserChangeText) return
                    if (s == null) return
                    val newText = s.substring(start, start + count)
                    mNoteViewModel.undo.push(
                        CommandReplaceText(start, oldText, newText)
                    )
                    mNoteViewModel.redo.clear()
                }

                override fun afterTextChanged(s: Editable?) {}
            })
        }
        return binding.root
    }

    private fun initCategoryLiveDataObserver() = with(binding) {
        mNoteViewModel.readAllCategories.observe(viewLifecycleOwner) { listCategories ->
            existCategory.clear()
            existCategory.addAll(listCategories)
        }
        mNoteViewModel.editedSelectCategoryFromAddFragment.observe(viewLifecycleOwner) { editedCategoryList ->
            selectedCategory.clear()
            selectedCategory.addAll(editedCategoryList)
        }
    }

    fun showDialogAddCategory() {
        DialogAddNewCategory().show(childFragmentManager, DialogAddNewCategory.TAG)
    }


    private fun createPopupMenuCategory() = with(binding) {
        val popupMenu = PopupMenu(wrapper, imBtPopupMenuCategories)

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

    private fun initListenersButtons() = with(binding) {
        bBold.setOnClickListener(this@AddFragment)
        bStrikeline.setOnClickListener(this@AddFragment)
        bItalic.setOnClickListener(this@AddFragment)
        bCleanText.setOnClickListener(this@AddFragment)
        bUnderline.setOnClickListener(this@AddFragment)
        bListText.setOnClickListener(this@AddFragment)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "AddFragment onAttach")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "AddFragment onPause")
    }

    private fun initFieldsNote() = with(binding) {
        etTitle.setText(currentNote.title)
        val currentText = Html
            .fromHtml(currentNote.content, Html.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH)
            .trimEnd('\n')
        etContent.setText(currentText)

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
            val noteWithImages = mNoteViewModel.noteWithImages(currentNote.id)
            val listImages: List<Image>? = noteWithImages.listOfImages
            if (listImages != null) {
                if (listImages.isNotEmpty()) {
                    rcImageView.visibility = View.VISIBLE
                    mNoteViewModel.selectEditedImagesPost(listImages)
                } else {
                    rcImageView.visibility = View.GONE
                }
            }

            val noteWithCategories = mNoteViewModel.getNoteWithCategories(currentNote.id)
            val listCategories: List<Category>? = noteWithCategories.listOfCategories
            if (listCategories != null) {
                mNoteViewModel.selectEditedCategoryPost(listCategories)
            }
        }
        if (noteIsDeleted) {
            fbSave.visibility = View.GONE
            editPanel.visibility = View.GONE

        } else {
            fbSave.visibility = View.VISIBLE
            editPanel.visibility = View.VISIBLE
        }
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
                    R.id.Share -> {
                        ShareNoteAsSimpleText.sendSimpleText(
                            binding.etTitle,
                            binding.etContent,
                            requireContext()
                        )
                        return true
                    }
                    R.id.undo -> {
                        if (mNoteViewModel.undo.isNotEmpty()) {
                            isUserChangeText = false
                            val currentCommand = mNoteViewModel.undo.pop()
                            mNoteViewModel.redo.push(currentCommand)
                            val text = binding.etContent.text.toString()
                            val newText = currentCommand.undo(text)
                            binding.etContent.text.clear()
                            binding.etContent.setText(newText)
                            isUserChangeText = true
                        }
                        return true
                    }
                    R.id.redo -> {
                        if (mNoteViewModel.redo.isNotEmpty()) {
                            isUserChangeText = false
                            val currentCommand = mNoteViewModel.redo.pop()
                            mNoteViewModel.undo.push(currentCommand)
                            val text = binding.etContent.text.toString()
                            val newText = currentCommand.redo(text)
                            binding.etContent.text.clear()
                            binding.etContent.setText(newText)
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

    override fun onResume() {
        super.onResume()
        binding.root.requestLayout()
        Log.d(TAG, "AddFragment onResume")
    }

    override fun syncTheme(appTheme: AppTheme) {
        val theme = appTheme as BaseTheme
        binding.apply {
            wrapper = ContextThemeWrapper(context, theme.stylePopupTheme())
            addFragmentLayout.setBackgroundColor(theme.backgroundColor(requireContext()))

            tvLastChange.setTextColor(theme.textColorTabUnselect(requireContext()))
            etTitle.setTextColor(theme.textColor(requireContext()))
            if (currentNote.colorFrameTitle == 0) {
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

            etContent.setTextColor(theme.textColor(requireContext()))

            if (currentNote.colorFrameContent == 0) {
                etContent.setBackgroundColor(theme.backgroundDrawer(requireContext()))
                contentCardViewAddFragment.setCardBackgroundColor(
                    theme.backgroundDrawer(
                        requireContext()
                    )
                )
            }
            etContent.setHintTextColor(theme.textColorTabUnselect(requireContext()))

            contentCardViewAddFragment.outlineAmbientShadowColor = theme.setShadow(requireContext())
            contentCardViewAddFragment.outlineSpotShadowColor = theme.setShadow(requireContext())

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

    @RequiresApi(Build.VERSION_CODES.Q)
    fun showPopupMenuToolbar(view: View) {
        val popupMenu = PopupMenu(wrapper, view)
        val currentTheme = mNoteViewModel.currentTheme.value

        val pinMenuItem = setPinMenuItem(popupMenu)
        val favoriteMenuItem = setFavoriteMenuItem(popupMenu)

        popupMenu.menuInflater.inflate(
            R.menu.menu_filter_add_fragment,
            popupMenu.menu
        )

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
                        if (currentNote.isPin) {
                            if (currentTheme != null) {
                                pinMenuItem.icon = DecoratorView.setIcon(
                                    requireContext(),
                                    currentTheme.themeId, R.drawable.ic_delete
                                )
                            }
                            item.title = "Unpin"
                            currentNote.isPin = false
                        } else {
                            if (currentTheme != null) {
                                pinMenuItem.icon =
                                    DecoratorView.setIcon(
                                        requireContext(),
                                        currentTheme.themeId,
                                        R.drawable.ic_push_pin_24
                                    )
                            }

                            item.title = "Pin"
                            currentNote.isPin = true
                        }
                    }
                    1 -> {
                        if (currentNote.isFavorite) {
                            if (currentTheme != null) {
                                favoriteMenuItem.icon = DecoratorView.setIcon(
                                    requireContext(),
                                    currentTheme.themeId,
                                    R.drawable.ic_favorite
                                )
                            }
                            item.title = "Add to favorite"

                            currentNote.isFavorite = false
                        } else {
                            if (currentTheme != null) {
                                favoriteMenuItem.icon = DecoratorView.setIcon(
                                    requireContext(),
                                    currentTheme.themeId,
                                    R.drawable.ic_favorite_sold
                                )
                            }
                            item.title = "Delete from favorite"

                            currentNote.isFavorite = true
                        }
                    }

                    R.id.encrypt_note -> {

                    }
                    R.id.arch_note -> {
                        val dialog = DialogAddToArchive()
                        dialog.show(childFragmentManager, DialogAddToArchive.TAG)
                        popupMenu.dismiss()
                    }
                    R.id.found_to_note -> {


                    }
                    R.id.delete_note -> {
                        val dialog = DialogDeleteNote()
                        dialog.show(childFragmentManager, DialogDeleteNote.TAG)
                        popupMenu.dismiss()
                    }
                }
                return false
            }
        })
        showIconPopupMenu(popupMenu)
        popupMenu.show()
    }

    private fun setPinMenuItem(popupMenu: PopupMenu): MenuItem {
        var title = ""
        var resIcon: Drawable? = null
        val currentTheme = mNoteViewModel.currentTheme.value

        if (currentNote.isPin) {
            title = "Pin"
            if (currentTheme != null) {
                resIcon = DecoratorView.setIcon(
                    requireContext(),
                    currentTheme.themeId,
                    R.drawable.ic_push_pin_24
                )!!
            }
        } else {
            title = "Unpin"
            if (currentTheme != null) {
                resIcon = DecoratorView.setIcon(
                    requireContext(),
                    currentTheme.themeId,
                    R.drawable.ic_delete
                )!!
            }
        }
        return popupMenu.menu.add(Menu.NONE, 0, 0, title).setIcon(resIcon)
    }

    private fun setFavoriteMenuItem(popupMenu: PopupMenu): MenuItem {
        var title = ""
        var resIcon: Drawable? = null
        val currentTheme = mNoteViewModel.currentTheme.value

        if (currentNote.isFavorite) {
            title = "Delete from favorite"
            if (currentTheme != null) {
                resIcon = DecoratorView.setIcon(
                    requireContext(),
                    currentTheme.themeId,
                    R.drawable.ic_favorite_sold
                )
            }
        } else {
            title = "Add to favorite"
            if (currentTheme != null) {
                resIcon = DecoratorView.setIcon(
                    requireContext(),
                    currentTheme.themeId,
                    R.drawable.ic_favorite
                )
            }
        }
        return popupMenu.menu.add(Menu.NONE, 1, 0, title).setIcon(resIcon)
    }

    private fun showIconPopupMenu(popupMenu: PopupMenu) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            popupMenu.setForceShowIcon(true)
        } else {
            try {
                val fieldPopupMenu = PopupMenu::class.java.getDeclaredField("mPopup")
                fieldPopupMenu.isAccessible = true
                val mPopup = fieldPopupMenu.get(popupMenu)
                mPopup.javaClass.getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                    .invoke(mPopup, true)
            } catch (e: Exception) {
                Log.e("Main", "Error showing menu icons.", e)
            }
        }
    }

    private fun initImagesLiveDataObserver() {
        Log.d(TAG, "AddFragment initLiveDataObserver")
        mNoteViewModel.editedImages.observe(viewLifecycleOwner) { listImages ->
            imageAdapter.submitList(listImages)
        }
    }

    private fun initColorLiveDataObserver() = with(binding) {
        Log.d(TAG, "AddFragment initColorLiveDataObserver")
        mNoteViewModel.currentColorsFields.observe(viewLifecycleOwner) { listColors ->
            DecoratorView.updateFieldsFromColors(
                listColors[0],
                listColors[1],
                titleCardViewAddFragment,
                contentCardViewAddFragment,
                etTitle,
                etContent
            )
        }
    }

    //init image adapter
    private fun initImageAdapter() = with(binding) {
        imageAdapter = ImageAdapter()
        val layoutManager = StaggeredGridLayoutManager(2, 1)
        rcImageView.layoutManager = layoutManager
        rcImageView.isNestedScrollingEnabled = false
        rcImageView.adapter = imageAdapter
    }

    //insert new note to database or update note
    private fun saveNoteToDatabase() = with(binding) {
        val listImage: List<Image>? = mNoteViewModel.editedImages.value
        val listColors: List<Int>? = mNoteViewModel.currentColorsFields.value
        val listCategories: List<Category>? =
            mNoteViewModel.editedSelectCategoryFromAddFragment.value
        val title = etTitle.text.toString()
        val content = Html.toHtml(etContent.text, Html.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL)
        val text = etContent.text.toString()

        if (EditTextNoteUtil.inputCheck(title, content)) {
            if (currentNote.id == 0L) {
                currentNote = Note(
                    0,
                    title,
                    content,
                    text = text,
                    time = CurrentTimeInFormat.getCurrentTime(),
                    colorFrameTitle = listColors?.get(0) ?: 0,
                    colorFrameContent = listColors?.get(1) ?: 0
                )
                mNoteViewModel.addOrUpdateNoteWithImages(
                    currentNote,
                    listImage,
                    listCategories
                )
                Toast.makeText(requireContext(), "Successfully added!", Toast.LENGTH_SHORT).show()
            } else {
                mNoteViewModel.addOrUpdateNoteWithImages(
                    currentNote.copy(
                        title = title,
                        content = content,
                        text = text,
                        time = CurrentTimeInFormat.getCurrentTime(),
                        colorFrameTitle = listColors?.get(0) ?: 0,
                        colorFrameContent = listColors?.get(1) ?: 0
                    ),
                    listImage, listCategories
                )
                Toast.makeText(requireContext(), "Successfully updated!", Toast.LENGTH_SHORT).show()

            }
            findNavController().popBackStack()
        } else {
            Toast.makeText(requireContext(), "Please fill one and more fields!", Toast.LENGTH_SHORT)
                .show()
        }
    }


//    //select option add photo from camera or gallery, change colors fields note, import note
//    private fun initBottomNavigation() = with(binding) {
//        Log.d(TAG, "AddFragment initBottomNavigation")
//        val menuItem: MenuItem = botNView.menu.findItem(R.id.changeText);
//        val checkBox: CheckBox = menuItem.actionView!!.findViewById(R.id.changeText) as CheckBox
//
//        botNView.setOnItemSelectedListener {
//            when (it.itemId) {
//                R.id.changeText -> {
//                    val ch: CheckBox = requireActivity().findViewById(R.id.changeText) as CheckBox
//                    ch.setOnCheckedChangeListener { buttonView, isChecked ->
//                        if (isChecked) {
//                            cardVChangesStyleText.visibility = View.VISIBLE
//                        } else {
//                            cardVChangesStyleText.visibility = View.GONE
//                        }
//                    }
//                }
//                R.id.addPhoto -> {
//                    selectedMultiImages()
//                }
//                R.id.editBackgroundColor -> {
//                    ModBtSheetChooseColorTitleOrColorContent().show(
//                        childFragmentManager,
//                        ModBtSheetChooseColorTitleOrColorContent.TAG
//                    )
//                    mNoteViewModel.updateEditedFieldColor()
//                }
//
//                R.id.addCategory -> {
//                    ModBtSheetCategoryFragment().show(
//                        childFragmentManager,
//                        ModBtSheetCategoryFragment.TAG
//                    )
//                }
//            }
//            true
//        }
//    }

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
        imageAdapter.clear()
        mNoteViewModel.clearEditImages()
        mNoteViewModel.cleanSelectedColors()
        mNoteViewModel.cleanSelectedCategories()
        mNoteViewModel.clearUndoRedo()
    }

    override fun onImagesSelected(uris: List<Uri>, tag: String?) = with(binding) {
        if (uris.isNotEmpty()) {
            rcImageView.visibility = View.VISIBLE
            addSelectedImagesToViewModel(uris)
        } else {
            rcImageView.visibility = View.GONE
        }
    }

    private fun addSelectedImagesToViewModel(uris: List<Uri>) {
        val newImageList = arrayListOf<Image>()
        if (mNoteViewModel.editedImages.value != null) {
            newImageList.addAll(mNoteViewModel.editedImages.value!!)
        }
        uris.forEach { newUri ->
            newImageList.add(Image(0, 0, newUri.toString()))
        }
        mNoteViewModel.selectEditedImages(newImageList)
    }

    override fun onClick(view: View?) = with(binding) {
        if (view != null) {
            etContent.text = EditTextNoteUtil.editText(etContent, view)
            etContent.setSelection(etContent.selectionStart, etContent.selectionEnd)
        }
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

    override fun onAddCategory(titleCategory: String) {
        if (titleCategory != "") {
            mNoteViewModel.addCategory(Category(titleCategory = titleCategory))
            Toast.makeText(
                requireContext(),
                "You add $titleCategory in categories",
                Toast.LENGTH_SHORT
            ).show()
            //добавить в текущую заметку тоже
        } else {
            Toast.makeText(requireContext(), "We need more letters", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onCloseDialog(isDelete: Boolean, isArchive: Boolean) {
        if (isDelete) currentNote.isDeleted = isDelete
        if (isArchive) currentNote.isArchive = isArchive
        findNavController().popBackStack()
    }
}

