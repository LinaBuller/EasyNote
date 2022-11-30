package com.buller.mysqlite.fragments.add


import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.buller.mysqlite.MainActivity
import com.buller.mysqlite.R
import com.buller.mysqlite.databinding.FragmentAddBinding
import com.buller.mysqlite.fragments.add.bottomsheet.pickerFavoriteColor.ModBtSheetChooseColorTitleOrColorContent
import com.buller.mysqlite.fragments.add.bottomsheet.categories.ModBtSheetCategoryFragment
import com.buller.mysqlite.fragments.constans.FragmentConstants
import com.buller.mysqlite.model.Image
import com.buller.mysqlite.model.Note
import com.buller.mysqlite.fragments.add.bottomsheet.pickerImage.BottomSheetImagePicker
import com.buller.mysqlite.fragments.add.bottomsheet.pickerImage.ButtonType
import com.buller.mysqlite.model.Category
import com.buller.mysqlite.utils.*
import com.buller.mysqlite.utils.edittextnote.EditTextNoteUtil
import com.buller.mysqlite.viewmodel.NotesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class AddFragment : Fragment(),
    BottomSheetImagePicker.OnImagesSelectedListener,
    View.OnClickListener,
    ModBtSheetChooseColorTitleOrColorContent.OnColorSelectedListener {
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

    private val menuItemClickListener = object : Toolbar.OnMenuItemClickListener {
        override fun onMenuItemClick(item: MenuItem?): Boolean {
            if (item != null) {
                when (item.itemId) {
                    R.id.Share -> {
                        ShareNoteAsSimpleText.sendSimpleText(
                            binding.etTitle,
                            binding.etContent,
                            requireContext()
                        )
                        return true
                    }
                }
            }
            return false
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

    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).setToolbarMenu(
            R.menu.menu_toolbar_add_fragment,
            menuItemClickListener
        )
        Log.d(TAG, "AddFragment onResume")
    }

    private fun initImagesLiveDataObserver() {
        Log.d(TAG, "AddFragment initLiveDataObserver")
        mNoteViewModel.editedImages.observe(viewLifecycleOwner) { listImages ->
            imageAdapter.submitList(listImages)
        }

    }

    private fun initColorLiveDataObserver() = with(binding) {
        Log.d(TAG, "AddFragment initColorLiveDataObserver")
        mNoteViewModel.editedColorsFields.observe(viewLifecycleOwner) { listColors ->
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
        imageAdapter = ImageAdapter(SystemUtils.widthScreen(requireActivity()))
        Log.d(TAG, "AddFragment initImageAdapter")
        val layoutManager = StaggeredGridLayoutManager(2, 1)
        rcImageView.layoutManager = layoutManager
        rcImageView.isNestedScrollingEnabled = false
        rcImageView.adapter = imageAdapter
    }

    //insert new note to database or update note
    private fun saveNoteToDatabase() = with(binding) {
        val listImage: List<Image>? = mNoteViewModel.editedImages.value
        val listColors: List<Int>? = mNoteViewModel.editedColorsFields.value
        val listCategories: List<Category>? =
            mNoteViewModel.editedSelectCategoryFromAddFragment.value
        val title = etTitle.text.toString()
        val content = Html.toHtml(etContent.text, Html.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL)

        if (EditTextNoteUtil.inputCheck(title, content)) {
            if (currentNote.id == 0L) {
                currentNote = Note(
                    0,
                    title,
                    content,
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
                    ModBtSheetChooseColorTitleOrColorContent(
                        listOf(
                            currentNote.colorFrameTitle,
                            currentNote.colorFrameContent
                        )
                    ).show(childFragmentManager, ModBtSheetChooseColorTitleOrColorContent.TAG)
                }

                R.id.addCategory -> {
                    val dialog = ModBtSheetCategoryFragment()
                    dialog.show(childFragmentManager, ModBtSheetCategoryFragment.TAG)
                }
            }
            true
        }
    }

    private fun selectedMultiImages() {
        BottomSheetImagePicker.Builder(resources.getString(R.string.file_provider))
            .columnSize(R.dimen.imagePickerColumnSize)
            .multiSelect(1, 10)
            .cameraButton(ButtonType.Button)
            .galleryButton(ButtonType.Button)
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

    override fun onColorSelected(colorTitle: Int, colorContent: Int) {
        mNoteViewModel.selectColorFieldsNote(listOf(colorTitle, colorContent))
    }

}

