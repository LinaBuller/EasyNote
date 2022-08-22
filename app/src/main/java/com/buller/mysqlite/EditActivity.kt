package com.buller.mysqlite

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.buller.mysqlite.constans.ContentConstants
import com.buller.mysqlite.model.Image
import com.buller.mysqlite.utils.*
import kotlinx.coroutines.Job


class EditActivity : AppCompatActivity(), OnDataDeleteImagePass {
    //lateinit var binding: ActivityEditBinding
    var editLauncher: ActivityResultLauncher<Intent>? = null
    var editLauncherImage: ActivityResultLauncher<Intent>? = null
    private var tempArrayImageUri: MutableList<Image> = mutableListOf()
    var intentTitle: String? = null
    var intentContext: String? = null
    var firstTableId = 0
    var isEditState = false
    var colorTitleSave = 0
    var colorContentSave = 0
    var imageId = 0
    var noteID = 0L

    private var job: Job? = null
    lateinit var currentPhotoUri: Uri

    var noteCategoryList = ArrayList<NoteCategory>()
    private val categoryEditListAdapter = CategoryAdapterEditActivity(noteCategoryList)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        binding = ActivityEditBinding.inflate(layoutInflater)
//        setContentView(binding.root)
        initImageAdapter()
        initCategoryAdapter()
        initResultLauncher()
        initResultCategories()
        getIntents()
        bottomNavigationInit()
    }

    fun setIntentCameraOrGallery(type: Int) {
        if (type == ContentConstants.CAMERA) {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val photoFile = CreateNewImageFile(this).createImageFile()
            val photoURI: Uri = FileProvider.getUriForFile(
                this,
                "com.buller.mysqlite.fileprovider",
                photoFile
            )
            currentPhotoUri = photoURI
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            editLauncherImage?.launch(intent)
        } else {
            val intent = Intent().setType("image/*").setAction(Intent.ACTION_OPEN_DOCUMENT)
            editLauncherImage?.launch(intent)
        }
    }

    fun setIntentChangeContentColorBackgroundOrTitleColorBackground(
        colorTitle: Int,
        colorContent: Int
    ) {
//        colorTitleSave = colorTitle
//        colorContentSave = colorContent
//
//        if (colorTitle != 0) {
//            binding.etTitle.background.mutate()
//            binding.etTitle.background.setTint(colorTitleSave)
//        } else {
//            binding.etTitle.setBackgroundResource(R.drawable.rounded_border_rcview_item)
//        }
//
//        if (colorContent != 0) {
//            binding.etContent.background.mutate()
//            binding.etContent.background.setTint(colorContentSave)
//        } else {
//            binding.etContent.setBackgroundResource(R.drawable.rounded_border_rcview_item)
//
//        }
    }

    private fun initResultCategories() {
//        editLauncher =
//            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { it ->
//                if (it.resultCode == RESULT_OK) {
//                    val intent = it.data
//                    val idCategory =
//                        intent?.getParcelableArrayListExtra<NoteCategory>(ContentConstants.ID_CATEGORY)
//                    if (idCategory != null) {
//                        resViewCategory.visibility = View.VISIBLE
//                        val list = noteCategoryList
//                        list.clear()
//                        list.addAll(idCategory)
//                        categoryEditListAdapter.notifyDataSetChanged()
//                    }
//                }
//            }
    }

    private fun initCategoryAdapter()  {
//        resViewCategory.layoutManager =
//            LinearLayoutManager(this@EditActivity, LinearLayoutManager.HORIZONTAL, false)
//        resViewCategory.adapter = categoryEditListAdapter
    }

    private fun initImageAdapter(){
//        rcViewImages.layoutManager =
//            GridAutofitLayoutManager(
//                this@EditActivity,
//                200
//            )
//        val dividerItemDecoration = DividerItemDecoration(
//            rcViewImages.context,
//            (rcViewImages.layoutManager as GridAutofitLayoutManager).orientation
//        )
//        rcViewImages.addItemDecoration(dividerItemDecoration)
//        rcViewImages.adapter = imageAdapter
    }

    private fun initResultLauncher() {
//        editLauncherImage =
//            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//                if (it.resultCode == RESULT_OK) {
//                    rcViewImages.visibility = View.VISIBLE
//                    val uri: Uri = if (it.data?.data != null) {
//                        contentResolver.takePersistableUriPermission(
//                            it.data!!.data!!,
//                            Intent.FLAG_GRANT_READ_URI_PERMISSION
//                        )
//                        it.data!!.data!!
//                    } else {
//                        currentPhotoUri
//                    }
//                    //tempArrayImageUri.add(ImageModel(uri.toString()))
//                    imageAdapter.notifyItemInserted(tempArrayImageUri.size - 1)
//                }
//            }
    }

   fun onClickSave(view: View) {
        saveItemNoteToBD()
        finish()
    }

    fun saveItemNoteToBD(){
//        var myTitle = etTitle.text.toString()
//        val myContent = Html.toHtml(etContent.text, Html.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL)
//        if (myTitle == "") {
//            myTitle = resources.getString(R.string.no_title)
//        }
//
//        if (isEditState) {
//
//            notesViewModel.updateItemNote(
//                Note(
//                    firstTableId,
//                    myTitle,
//                    myContent,
//                    CurrentTimeInFormat.getCurrentTime(),
//                    colorTitleSave,
//                    colorContentSave
//                )
//            )

//            tempArrayImageUri.forEach { tempImage ->
//                if (tempImage.isNew()) {
//                    notesViewModel.insertImageDb(ImageModel(firstTableId,tempImage.uri))
//                }
//            }

            if (noteCategoryList.isNotEmpty()) {
//                val listOldSelectedCategories =
//                   notesViewModel.readJoinTableConnectionWithTableCategory(firstTableId)
//                if (!listOldSelectedCategories.containsAll(noteCategoryList)) {
//                    notesViewModel.removeConnectionNotesAndCategoryDbFromIdNote(firstTableId)
//                    noteCategoryList.forEach {
//                        notesViewModel.insertConnectionNotesAndCategoryDb(
//                            ConnectionNoteAndCategories(firstTableId, it.id.toInt()) )
//                    }
//                }
//            }
//        } else {

//            noteID = notesViewModel.insert(
//                Note(
//                    0,
//                myTitle,
//                myContent,
//                CurrentTimeInFormat.getCurrentTime(),
//                colorTitleSave,
//                colorContentSave)
//            )
//            tempArrayImageUri.forEach {
//                notesViewModel.insertImageDb(ImageModel(noteID.toInt(),it.uri))
//            }
//            if (noteCategoryList.isNotEmpty()) {
//                noteCategoryList.forEach {
//                    notesViewModel.insertConnectionNotesAndCategoryDb(ConnectionNoteAndCategories(noteID.toInt(), it.id.toInt()))
//                }
//            }
        }
    }


    //получить инфо после повторного открытия заметки (надо переделать uri)
    private fun getIntents() {
        val intent = intent
        if (intent != null) {

            intentTitle = intent.getStringExtra(ContentConstants.I_TITLE_KEY)
            intentContext = intent.getStringExtra(ContentConstants.I_CONTENT_KEY)
            firstTableId = intent.getIntExtra(ContentConstants.I_ID_KEY, 0)
            imageId = intent.getIntExtra(ContentConstants.I_URI_ID_KEY, 0)
            val intentColorTitle = intent.getIntExtra(ContentConstants.COLOR_TITLE_FRAME, 0)
            val intentColorContent = intent.getIntExtra(ContentConstants.COLOR_CONTENT_FRAME, 0)
            isEditState = intent.getBooleanExtra(ContentConstants.EDIT_CHOOSE, false)

            setIntentChangeContentColorBackgroundOrTitleColorBackground(
                intentColorTitle,
                intentColorContent
            )

            if (isEditState) {
//                etTitle.setText(intentTitle)
//
//                if (intentContext == null) {
//                    etContent.visibility = View.GONE
//                }

//                etContent.setText(
//                    Html.fromHtml(
//                        intentContext,
//                        Html.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH
//                    ).trimEnd('\n')
//                )
                readImageDB()
                readJoinCategory()
            }
        }
    }

    private fun readJoinCategory() {
//        val list = notesViewModel.readJoinTableConnectionWithTableCategory(firstTableId)
//        noteCategoryList.clear()
//        noteCategoryList.addAll(list)
//        if (noteCategoryList.isNotEmpty()) {
//            resViewCategory.visibility = View.VISIBLE
//            categoryEditListAdapter.notifyDataSetChanged()
//        } else {
//            resViewCategory.visibility = View.GONE
//        }
    }

    fun openCategorySelect() {
        val intent = Intent(this@EditActivity, CategoryActivitySelect::class.java)
        intent.putParcelableArrayListExtra(
            ContentConstants.ID_CATEGORY_TO_CATEGORY_ACTIVITY_SELECT,
            noteCategoryList
        )
        editLauncher?.launch(intent)
    }

    private fun readImageDB() {
//        job?.cancel()
//        job = CoroutineScope(Dispatchers.Main).launch {
//            val readImageUri = notesViewModel.readImageDbFromForeignId(firstTableId)
//            tempArrayImageUri.addAll(readImageUri)
//            if (tempArrayImageUri.isNotEmpty()) {
//                rcViewImages.visibility = View.VISIBLE
//                imageAdapter.notifyDataSetChanged()
//            }
//        }
    }

    private fun bottomNavigationInit()  {
//        botNView.setOnItemSelectedListener {
//            when (it.itemId) {
//                R.id.addSomth -> {
//                    val modBtShChooseImage =
//                        ModBtSheetChooseAddImageGalleryOrCamera(this@EditActivity)
//                    modBtShChooseImage.show(
//                        supportFragmentManager,
//                        ModBtSheetChooseAddImageGalleryOrCamera.TAG
//                    )
//                }
//                R.id.edit_color -> {
//                    val modBtSheChooseColor =
//                        ModBtSheetChooseColorTitleOrColorContent(this@EditActivity)
//                    modBtSheChooseColor.show(
//                        supportFragmentManager,
//                        ModBtSheetChooseColorTitleOrColorContent.TAG
//                    )
//                }
//                R.id.other_action -> {
//                    val modBtShChooseExport = ModBtSheetChooseExport(this@EditActivity)
//                    modBtShChooseExport.show(supportFragmentManager, ModBtSheetChooseExport.TAG)
//                }
//            }
//            true
//        }
    }

    fun onClickEditText(view: View) {
        //EditTextUtil.editText(etContent, view)
    }

    override fun onBackPressed() {
        if (this.supportFragmentManager.backStackEntryCount > 0) {
            this.supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onDataDeleteImagePass(isDelete: Boolean, uri: String) {
        run loop@{
            tempArrayImageUri.forEachIndexed { index, it ->
                if (isDelete && it.uri == uri) {
//                    notesViewModel.removeAllImagesFromForeignId(it.id)
//                    Toast.makeText(this, "hello ${it.id}", Toast.LENGTH_SHORT).show()
//                    tempArrayImageUri.removeAt(index)
//                    imageAdapter.notifyItemRemoved(index)
                    return@loop
                }
            }
        }
    }
}


//String htmlString = Html.toHtml(spannedText);

//Установить фон для едит текста textView.setBackgroundColor(getResources().getColor(R.color.tvBackground)); // второй вариант
//
// Установить ссылки автоматом
// <TextView
//    android:id="@+id/textView"
//    android:layout_width="match_parent"
//    android:autoLink="web"
//    android:linksClickable="true"
//    android:text="Мой адрес: developer.alexanderklimov.ru" />
//Атрибут autoLink позволяет комбинировать различные виды ссылок для автоматического распознавания: веб-адрес, email, номер телефона.
//колесо для выбора цвета текста  https://github.com/LarsWerkman/Lobsterpicker

