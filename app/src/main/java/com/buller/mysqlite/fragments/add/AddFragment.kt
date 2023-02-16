package com.buller.mysqlite.fragments.add


import android.app.DatePickerDialog
import android.content.Context
import android.content.res.ColorStateList
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.buller.mysqlite.MainActivity
import com.buller.mysqlite.R
import com.buller.mysqlite.data.ConstantsDbName
import com.buller.mysqlite.databinding.FragmentAddBinding
import com.buller.mysqlite.fragments.add.bottomsheet.categories.ModBtSheetCategoryFragment
import com.buller.mysqlite.fragments.add.bottomsheet.pickerFavoriteColor.ModBtSheetChooseColorTitleOrColorContent
import com.buller.mysqlite.fragments.add.bottomsheet.pickerImage.BottomSheetImagePicker
import com.buller.mysqlite.fragments.constans.FragmentConstants
import com.buller.mysqlite.model.Category
import com.buller.mysqlite.model.Image
import com.buller.mysqlite.model.Note
import com.buller.mysqlite.utils.*
import com.buller.mysqlite.utils.edittextnote.EditTextNoteUtil
import com.buller.mysqlite.utils.theme.BaseTheme
import com.buller.mysqlite.viewmodel.NotesViewModel
import com.dolatkia.animatedThemeManager.AppTheme
import com.dolatkia.animatedThemeManager.ThemeFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class AddFragment : ThemeFragment(),
    BottomSheetImagePicker.OnImagesSelectedListener,
    View.OnClickListener {
    lateinit var binding: FragmentAddBinding
    private lateinit var mNoteViewModel: NotesViewModel
    private lateinit var imageAdapter: ImageAdapter
    private lateinit var currentNote: Note
    private lateinit var categoryAdapter: SelectedCategoryAdapter
    private var noteIsDeleted: Boolean = false

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
        initThemeObserver()
        initImagesLiveDataObserver()
        initCategoryAdapter()
        initCategoryLiveDataObserver()
        initColorLiveDataObserver()
        if (currentNote.id != 0L) {
            initFieldsNote()
        }
        initBottomNavigation()
        initListenersButtons()
        binding.apply {
            fbSave.setOnClickListener {
                saveNoteToDatabase()
            }
            btEditTextPanel.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    editTextPanel.slideAnimation(SlideDirection.RIGHT, SlideType.SHOW, 300)
//                    editTextPanel.visibility = View.VISIBLE
                } else {
                    editTextPanel.slideAnimation(SlideDirection.LEFT, SlideType.HIDE, 300)
//                    editTextPanel.visibility = View.GONE
                }
            }

        }
        return binding.root
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

    private fun initFieldsNote() = with(binding) {
        etTitle.setText(currentNote.title)
        etContent.setText(
            Html.fromHtml(
                currentNote.content,
                Html.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH
            ).trimEnd('\n')
        )

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
            val noteWithCategories = mNoteViewModel.noteWithCategories(currentNote.id)
            val listCategories: List<Category>? = noteWithCategories.listOfCategories
            if (listCategories != null) {
                if (listCategories.isNotEmpty()) {
                    rcViewCategory.visibility = View.VISIBLE
                    mNoteViewModel.selectEditedCategoryPost(listCategories)
                } else {
                    rcViewCategory.visibility = View.GONE
                }
            }
        }
        if (noteIsDeleted) {
            fbSave.visibility = View.GONE
            editPanel.visibility = View.GONE
            botNView.visibility = View.GONE
        } else {
            fbSave.visibility = View.VISIBLE
            editPanel.visibility = View.VISIBLE
            botNView.visibility = View.VISIBLE
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_toolbar_add_fragment, menu)
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
            addFragmentLayout.setBackgroundColor(theme.backgroundColor(requireContext()))

            tvLastChange.setTextColor(theme.textColorTabUnselect(requireContext()))
            etTitle.setTextColor(theme.textColor(requireContext()))
            etTitle.setBackgroundColor(theme.backgroundDrawer(requireContext()))
            etTitle.setHintTextColor(theme.textColorTabUnselect(requireContext()))
            titleCardViewAddFragment.setCardBackgroundColor(
                theme.backgroundDrawer(
                    requireContext()
                )
            )

            titleCardViewAddFragment.outlineAmbientShadowColor = theme.setShadow(requireContext())
            titleCardViewAddFragment.outlineSpotShadowColor = theme.setShadow(requireContext())

            etContent.setTextColor(theme.textColor(requireContext()))
            etContent.setBackgroundColor(theme.backgroundDrawer(requireContext()))
            etContent.setHintTextColor(theme.textColorTabUnselect(requireContext()))
            contentCardViewAddFragment.setCardBackgroundColor(
                theme.backgroundDrawer(
                    requireContext()
                )
            )
            contentCardViewAddFragment.outlineAmbientShadowColor = theme.setShadow(requireContext())
            contentCardViewAddFragment.outlineSpotShadowColor = theme.setShadow(requireContext())

            cardVChangesStyleText.backgroundTintList =
                ColorStateList.valueOf(theme.backgroundDrawer(requireContext()))

            btEditTextPanel.backgroundTintList =
                ColorStateList.valueOf(theme.backgroundDrawer(requireContext()))

            btEditTextPanel.buttonTintList = ColorStateList(
                arrayOf(intArrayOf(0, 1)),
                intArrayOf(
                    theme.textColorTabUnselect(requireContext()),
                    theme.akcColor(requireContext())
                )
            )

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

            botNView.setBackgroundColor(theme.backgroundDrawer(requireContext()))
            botNView.itemIconTintList = ColorStateList(
                arrayOf(intArrayOf(android.R.attr.state_selected), intArrayOf()),
                intArrayOf(theme.akcColor(requireContext()),theme.textColorTabUnselect(requireContext()))
            )
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
            EditTextNoteUtil.updateFieldsFromColors(
                listColors[0],
                listColors[1],
                titleCardViewAddFragment, contentCardViewAddFragment, null, null,
                requireContext()
            )
        }
    }

    //init image adapter
    private fun initImageAdapter() = with(binding) {
        imageAdapter = ImageAdapter()
        Log.d(TAG, "AddFragment initImageAdapter")
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
                mNoteViewModel.addOrUpdateNoteWithImages(currentNote, listImage, listCategories)
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

    //select option add photo from camera or gallery, change colors fields note, import note
    private fun initBottomNavigation() = with(binding) {
        Log.d(TAG, "AddFragment initBottomNavigation")
        botNView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.addPhoto -> {
                    selectedMultiImages()
                }
                R.id.editBackgroundColor -> {
                    ModBtSheetChooseColorTitleOrColorContent().show(
                        childFragmentManager,
                        ModBtSheetChooseColorTitleOrColorContent.TAG
                    )
                    mNoteViewModel.updateEditedFieldColor()
                }

                R.id.addCategory -> {
                    ModBtSheetCategoryFragment().show(
                        childFragmentManager,
                        ModBtSheetCategoryFragment.TAG
                    )
                }
            }
            true
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
        imageAdapter.clear()
        mNoteViewModel.clearEditImages()
        mNoteViewModel.cleanSelectedColors()
        mNoteViewModel.cleanSelectedCategories()
    }

    override fun onImagesSelected(uris: List<Uri>, tag: String?) = with(binding) {
        if (uris.isNotEmpty()) {
            rcImageView.visibility = View.VISIBLE
            addSelectedImagesToViewModel(uris)
        } else {
            rcImageView.visibility = View.GONE
        }
    }

    override fun onClick(view: View?) = with(binding) {
        if (view != null) {
            etContent.text = EditTextNoteUtil.editText(etContent, view)
            etContent.setSelection(etContent.selectionStart, etContent.selectionEnd)
        }
    }

    private fun initCategoryAdapter() = with(binding) {
        categoryAdapter = SelectedCategoryAdapter()
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        rcViewCategory.layoutManager = layoutManager
        rcViewCategory.adapter = categoryAdapter
    }

    private fun initCategoryLiveDataObserver() {
        mNoteViewModel.editedSelectCategoryFromAddFragment.observe(viewLifecycleOwner) { listSelectedCategories ->
            categoryAdapter.submitList(listSelectedCategories)
        }
    }

    private fun initThemeObserver(){
        mNoteViewModel.currentTheme.observe(viewLifecycleOwner){currentTheme->
            categoryAdapter.themeChanged(currentTheme)
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
}

