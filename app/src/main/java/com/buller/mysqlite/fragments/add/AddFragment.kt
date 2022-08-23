package com.buller.mysqlite.fragments.add


import android.content.Context
import android.graphics.Point
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.buller.mysqlite.MainActivity
import com.buller.mysqlite.R
import com.buller.mysqlite.databinding.FragmentAddBinding
import com.buller.mysqlite.fragments.constans.FragmentConstants
import com.buller.mysqlite.model.Image
import com.buller.mysqlite.model.Note
import com.buller.mysqlite.fragments.add.bottomsheet.picker.BottomSheetImagePicker
import com.buller.mysqlite.fragments.add.bottomsheet.picker.ButtonType
import com.buller.mysqlite.utils.*
import com.buller.mysqlite.viewmodel.NotesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class AddFragment : Fragment(), BottomSheetImagePicker.OnImagesSelectedListener {
    lateinit var binding: FragmentAddBinding
    private lateinit var mNoteViewModel: NotesViewModel
    private lateinit var imageAdapter: ImageAdapter
    private var isNewNote: Boolean = true
    private lateinit var currentNote: Note

    companion object {
        const val TAG = "MyLog"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "AddFragment onCreate")
        mNoteViewModel = ViewModelProvider(requireActivity())[NotesViewModel::class.java]
        if (arguments != null) {
            isNewNote = requireArguments().getBoolean(FragmentConstants.OPEN_NEW_OR_UPDATE_NOTE)
            currentNote = if (isNewNote) {
                Note()
            } else {
                requireArguments().getParcelable(FragmentConstants.UPDATE_NOTE)!!
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "AddFragment onCreateView")
        binding = FragmentAddBinding.inflate(inflater, container, false)
        imageAdapter = ImageAdapter(sizeScreen())
        (requireActivity() as MainActivity).supportActionBar?.hide()
        initImageAdapter()
        initImagesLiveDataObserver()
        if (currentNote.id != 0L) {
            initFieldsNote()
        }

        initBottomNavigation()

        binding.fbSave.setOnClickListener {
            saveNoteToDatabase()
        }

        return binding.root

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "AddFragment onAttach")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "AddFragment onPause")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "AddFragment onResume")
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "AddFragment onStart")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "AddFragment onStop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (requireActivity() as MainActivity).supportActionBar?.show()
        Log.d(TAG, "AddFragment onDestroyView")
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

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG, "AddFragment onDetach")
    }

    private fun initFieldsNote() = with(binding) {
        etTitle.setText(currentNote.title)
        etContent.setText(currentNote.content)
        lifecycleScope.launch(Dispatchers.IO) {
            val note = mNoteViewModel.noteWithImages(currentNote.id)
            val listImages = note.listOfImages
            if (listImages != null) {
                if (listImages.isNotEmpty()) {
                    rcImageView.visibility = View.VISIBLE
                    mNoteViewModel.selectEditedImagesPost(listImages)
                } else rcImageView.visibility = View.GONE
            }
        }
    }

    private fun initImagesLiveDataObserver() {
        Log.d(TAG, "AddFragment initLiveDataObserver")
        mNoteViewModel.editedImages.observe(viewLifecycleOwner) { listImages ->
            imageAdapter.submitList(listImages)
        }
    }

    //init image adapter
    private fun initImageAdapter() = with(binding) {
        Log.d(TAG, "AddFragment initImageAdapter")
        val layoutManager = StaggeredGridLayoutManager(2, 1)
        rcImageView.layoutManager = layoutManager
        rcImageView.isNestedScrollingEnabled = false
        rcImageView.adapter = imageAdapter
    }

    //insert new note to database or update note
    private fun saveNoteToDatabase() = with(binding) {
        val listImage: List<Image>? = mNoteViewModel.editedImages.value

        val title = etTitle.text.toString()
        val content = etContent.text.toString()

        if (inputCheck(title, content)) {
            if (isNewNote) {
                currentNote = Note(0, title, content, CurrentTimeInFormat.getCurrentTime())
                mNoteViewModel.addOrUpdateNoteWithImages(currentNote, listImage)
                Toast.makeText(requireContext(), "Successfully added!", Toast.LENGTH_SHORT).show()
            } else {
                mNoteViewModel.addOrUpdateNoteWithImages(
                    currentNote.copy(
                        title = title,
                        content = content,
                        time = CurrentTimeInFormat.getCurrentTime()
                    ),
                    listImage
                )
                Toast.makeText(requireContext(), "Successfully updated!", Toast.LENGTH_SHORT).show()
            }
            findNavController().popBackStack()
        } else {
            Toast.makeText(requireContext(), "Please fill one and more fields!", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun sizeScreen(): Int {
        val wm = requireActivity().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            val display = wm.defaultDisplay
            val size = Point()
            display.getSize(size)
            size.x

        } else {
            val curMetrics = wm.currentWindowMetrics
            val size = curMetrics.bounds
            size.width()
        }
    }

    //check input fields
    private fun inputCheck(title: String, content: String): Boolean {
        return !(TextUtils.isEmpty(title) && TextUtils.isEmpty(content))
    }


    //select option add photo from camera or gallery, change colors fields note, import note
    private fun initBottomNavigation() = with(binding) {
        Log.d(TAG, "AddFragment initBottomNavigation")
        botNView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.addSomth -> {
//                    val bundle = bundleOf(ARG_PARAM_PIX to ImagePicker.setOptions())
//                   findNavController().navigate(R.id.action_addFragment_to_CameraFragment, bundle)

                    selectedMultiImages()
                }
                R.id.edit_color -> {
                    findNavController().navigate(R.id.action_addFragment_to_modBtSheetChooseColorTitleOrColorContent)
                }
                R.id.other_action -> {
                    findNavController().navigate(R.id.action_addFragment_to_modBtSheetChooseExport)
                }
            }
            true
        }
    }
    private fun selectedMultiImages(){
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
        mNoteViewModel.selectEditedImages(listOf())

    }


    override fun onImagesSelected(uris: List<Uri>, tag: String?) = with(binding) {
        if (uris.isNotEmpty()) {
            rcImageView.visibility = View.VISIBLE
            addSelectedImagesToViewModel(uris)
        } else {
            rcImageView.visibility = View.GONE
        }

    }

}