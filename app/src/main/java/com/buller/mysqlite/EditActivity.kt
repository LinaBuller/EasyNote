package com.buller.mysqlite

import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.*
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.buller.mysqlite.constans.ContentConstants
import com.buller.mysqlite.databinding.ActivityEditBinding
import com.buller.mysqlite.db.ImageItem
import com.buller.mysqlite.db.MyDbManager
import com.buller.mysqlite.utils.CreateNewImageFile
import com.buller.mysqlite.utils.CurrentTimeInFormat
import com.buller.mysqlite.utils.SpanDefinition
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class EditActivity : AppCompatActivity(), OnDataDeleteImagePass {
    lateinit var binding: ActivityEditBinding
    var editLauncher: ActivityResultLauncher<Intent>? = null
    var editLauncherImage:ActivityResultLauncher<Intent>? = null
    private var tempArrayImageUri: MutableList<ImageItem> = mutableListOf()
    private val myDbManager = MyDbManager(this)
    var intentTitle: String? = null
    var intentContext: String? = null
    var firstTableId = 0
    var isEditState = false
    var colorTitleSave = 0
    var colorContentSave = 0
    var imageId = 0
    var noteID = 0L

    private val imageAdapter = ImageAdapter(tempArrayImageUri, this@EditActivity)
    private var job: Job? = null
    lateinit var currentPhotoUri: Uri

    val idCategoryChecked = ArrayList<Int>()
    val listItemCategoryTitle = mutableListOf<ItemCategoryTitle>()

    var noteCategoryList =ArrayList<NoteCategory>()
    private val categoryEditListAdapter = CategoryAdapterEditActivity(noteCategoryList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myDbManager.openDb()
        initImageAdapter()
        initResultLauncher()
        initCategoryAdapter()
        initResultCategories()
        bottomNavigationInit()
        getIntents()
    }

    override fun onResume() {
        super.onResume()
        myDbManager.openDb()
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
        colorTitleSave = colorTitle
        colorContentSave = colorContent

        if (colorTitle != 0) {
            binding.etTitle.background.mutate()
            binding.etTitle.background.setTint(colorTitleSave)
        } else {
            binding.etTitle.setBackgroundResource(R.drawable.rounded_border_rcview_item)
        }

        if (colorContent != 0) {
            binding.etContent.background.mutate()
            binding.etContent.background.setTint(colorContentSave)
        } else {
            binding.etContent.setBackgroundResource(R.drawable.rounded_border_rcview_item)

        }
    }

    private fun initResultCategories() = with(binding) {
        editLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { it ->
                if (it.resultCode == RESULT_OK) {
                    val intent = it.data
                    val idCategory = intent?.getIntegerArrayListExtra(ContentConstants.ID_CATEGORY)
                    val nameCategory =
                        intent?.getStringArrayListExtra(ContentConstants.NAME_CATEGORY)

                    if (idCategory != null) {
                        resViewCategory.visibility = View.VISIBLE
                        idCategoryChecked.clear()
                        listItemCategoryTitle.clear()
                        idCategoryChecked.addAll(idCategory)
                        nameCategory?.forEach {
                            listItemCategoryTitle.add(ItemCategoryTitle(it))
                            //categoryEditListAdapter.notifyItemInserted(listItemCategoryTitle.size - 1)
                        }
                        categoryEditListAdapter.notifyDataSetChanged()
                    }
                }
            }
    }

    private fun initCategoryAdapter() = with(binding) {
        resViewCategory.layoutManager =
            LinearLayoutManager(this@EditActivity, LinearLayoutManager.HORIZONTAL, false)
        resViewCategory.adapter = categoryEditListAdapter
    }

    private fun initImageAdapter() = with(binding) {
        rcViewImages.layoutManager = GridAutofitLayoutManager(this@EditActivity, 200)
        val dividerItemDecoration = DividerItemDecoration(
            rcViewImages.context,
            (rcViewImages.layoutManager as GridAutofitLayoutManager).orientation
        )
        rcViewImages.addItemDecoration(dividerItemDecoration)
        rcViewImages.adapter = imageAdapter
    }

    private fun initResultLauncher() = with(binding) {
        editLauncherImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                rcViewImages.visibility = View.VISIBLE
                val uri: Uri = if (it.data?.data != null) {
                    contentResolver.takePersistableUriPermission(
                        it.data!!.data!!,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                    it.data!!.data!!
                } else {
                    currentPhotoUri
                }
                tempArrayImageUri.add(ImageItem(uri.toString()))
                imageAdapter.notifyItemInserted(tempArrayImageUri.size - 1)
            }
        }
    }

    fun onClickSave(view: View) {
        saveItemNoteToBD()
        finish()
    }

    fun saveItemNoteToBD() = with(binding) {
        var myTitle = etTitle.text.toString()
        val myContent = Html.toHtml(etContent.text, Html.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL)
        if (myTitle == "") {
            myTitle = resources.getString(R.string.no_title)
        }

        if (isEditState) {

            myDbManager.updateItemDb(
                myTitle,
                myContent,
                firstTableId,
                CurrentTimeInFormat.getCurrentTime(),
                colorTitleSave,
                colorContentSave
            )

            tempArrayImageUri.forEach { tempImage ->
                if (tempImage.isNew()) {
                    myDbManager.insertImagesDb(tempImage.uri, firstTableId)
                }
            }

            if (noteCategoryList.isNotEmpty()) {
                val listOldSelectedCategories =
                    myDbManager.readJoinTableConnectionWithTableCategory(firstTableId)
                if (!listOldSelectedCategories.containsAll(noteCategoryList)) {
                    myDbManager.removeCategoriesForIdNote(firstTableId)
                    val listUnion = listOldSelectedCategories.union(idCategoryChecked)
                    listUnion.forEach {
                        myDbManager.insertDBConnectionNotesAndCategory(
                            firstTableId.toLong(),
                            it.toLong()
                        )
                    }
                }
            }
        } else {
            noteID = myDbManager.insertDb(
                myTitle,
                myContent,
                CurrentTimeInFormat.getCurrentTime(),
                colorTitleSave,
                colorContentSave
            )
            tempArrayImageUri.forEach {
                myDbManager.insertImagesDb(it.uri, noteID.toInt())
            }
            if (noteCategoryList.isNotEmpty()) {
                noteCategoryList.forEach {
                    myDbManager.insertDBConnectionNotesAndCategory(noteID, it.id.toLong())
                }
            }
        }
    }

    //получить инфо после повторного открытия заметки (надо переделать uri)
    private fun getIntents() = with(binding) {
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
                etTitle.setText(intentTitle)

                if (intentContext == null) {
                    etContent.visibility = View.GONE
                }

                etContent.setText(
                    Html.fromHtml(
                        intentContext,
                        Html.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH
                    ).trimEnd('\n')
                )
                readImageDB()
                noteCategoryList = myDbManager.readJoinTableConnectionWithTableCategory(firstTableId)
                if (noteCategoryList.isNotEmpty()) {
                    resViewCategory.visibility = View.VISIBLE
                    categoryEditListAdapter.notifyDataSetChanged()
                }else{
                    resViewCategory.visibility = View.GONE
                }
            }
        }
    }

    fun openCategorySelect() {
        val intent = Intent(this@EditActivity, CategoryActivitySelect::class.java)
        intent.putIntegerArrayListExtra(
            ContentConstants.ID_CATEGORY_TO_CATEGORY_ACTIVITY_SELECT,
            idCategoryChecked
        )
        editLauncher?.launch(intent)
    }

    private fun readImageDB() = with(binding) {
        job?.cancel()
        job = CoroutineScope(Dispatchers.Main).launch {
            val readImageUri = myDbManager.readDbImageForForeignId(firstTableId)
            tempArrayImageUri.addAll(readImageUri)
            if (tempArrayImageUri.isNotEmpty()) {
                rcViewImages.visibility = View.VISIBLE
                imageAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun bottomNavigationInit() = with(binding) {
        botNView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.addSomth -> {
                    val modBtShChooseImage =
                        ModBtSheetChooseAddImageGalleryOrCamera(this@EditActivity)
                    modBtShChooseImage.show(
                        supportFragmentManager,
                        ModBtSheetChooseAddImageGalleryOrCamera.TAG
                    )
                }
                R.id.edit_color -> {
                    val modBtSheChooseColor =
                        ModBtSheetChooseColorTitleOrColorContent(this@EditActivity)
                    modBtSheChooseColor.show(
                        supportFragmentManager,
                        ModBtSheetChooseColorTitleOrColorContent.TAG
                    )
                }
                R.id.other_action -> {
                    val modBtShChooseExport = ModBtSheetChooseExport(this@EditActivity)
                    modBtShChooseExport.show(supportFragmentManager, ModBtSheetChooseExport.TAG)
                    //Toast.makeText(this@EditActivity, "Item3", Toast.LENGTH_SHORT).show()
                }
            }
            true
        }
    }

    fun onClickEditText(view: View) = with(binding) {
        val content = etContent.text
        val selectStartContent: Int = etContent.selectionStart
        val selectEndContent: Int = etContent.selectionEnd
        val sb = SpannableStringBuilder(content)

        val firstSpanDef = SpanDefinition()
        val secondSpanDef = SpanDefinition()

        when (view.id) {
            R.id.bBold -> {
                var array =
                    sb.getSpans(selectStartContent, selectEndContent, StyleSpan::class.java)
                array = array.filter { b -> b.style == Typeface.BOLD }.toTypedArray()
                createSpanDefinitions(
                    array as Array<Any>,
                    sb,
                    selectStartContent,
                    selectEndContent,
                    firstSpanDef,
                    secondSpanDef
                )


                if (firstSpanDef.create) {
                    sb.setSpan(
                        StyleSpan(Typeface.BOLD),
                        firstSpanDef.start,
                        firstSpanDef.end,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                if (secondSpanDef.create) {
                    sb.setSpan(
                        StyleSpan(Typeface.BOLD),
                        secondSpanDef.start,
                        secondSpanDef.end,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
            R.id.bItalic -> {
                var array =
                    sb.getSpans(selectStartContent, selectEndContent, StyleSpan::class.java)
                array = array.filter { b -> b.style == Typeface.ITALIC }.toTypedArray()
                createSpanDefinitions(
                    array as Array<Any>,
                    sb,
                    selectStartContent,
                    selectEndContent,
                    firstSpanDef,
                    secondSpanDef
                )


                if (firstSpanDef.create) {
                    sb.setSpan(
                        StyleSpan(Typeface.ITALIC),
                        firstSpanDef.start,
                        firstSpanDef.end,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                if (secondSpanDef.create) {
                    sb.setSpan(
                        StyleSpan(Typeface.ITALIC),
                        secondSpanDef.start,
                        secondSpanDef.end,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }


            }
            R.id.bUnderline -> {
                val array =
                    sb.getSpans(selectStartContent, selectEndContent, UnderlineSpan::class.java)
                createSpanDefinitions(
                    array as Array<Any>,
                    sb,
                    selectStartContent,
                    selectEndContent,
                    firstSpanDef,
                    secondSpanDef
                )
                if (firstSpanDef.create) {
                    sb.setSpan(
                        UnderlineSpan(),
                        firstSpanDef.start,
                        firstSpanDef.end,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                if (secondSpanDef.create) {
                    sb.setSpan(
                        UnderlineSpan(),
                        secondSpanDef.start,
                        secondSpanDef.end,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
            R.id.bListText -> {
                val findChar = sb.lastIndexOfAny("\n".toCharArray(), selectStartContent, false)
                val listNewLine: MutableList<Int> = ArrayList<Int>()
                listNewLine.add(0, findChar + 1)
                var countDot = 0
                var currentPosition = selectStartContent
                while (true) {
                    val nextNewLine = sb.indexOfAny("\n".toCharArray(), currentPosition, false)
                    if (nextNewLine == -1 || nextNewLine > selectEndContent) {
                        break
                    }
                    currentPosition = nextNewLine + 1
                    listNewLine.add(nextNewLine + 1)
                }


                for (i in listNewLine.size until listNewLine.size) {
                    if (i >= sb.length) {
                        break
                    }
                    if (sb[i] == "⦁"[0]) {
                        countDot++
                    }
                }
//                listNewLine.forEach { dot ->
//                    if(dot>=sb.length){
//                        return
//                    }
//                    if (sb[dot] == "⦁"[0]) {
//                        countDot++
//                    }
//                }

                if (countDot == listNewLine.size) {
                    for (index in listNewLine.indices.reversed()) {
                        sb.insert(listNewLine[index], "")
                    }
                } else {
                    for (index in listNewLine.indices.reversed()) {
                        val position = listNewLine[index]
                        if (position >= sb.length || sb[position] != "⦁"[0]) {
                            sb.insert(position, "⦁ ")
                        }
                    }
                }
            }
            R.id.bStrikeline -> {
                val array =
                    sb.getSpans(
                        selectStartContent,
                        selectEndContent,
                        StrikethroughSpan::class.java
                    )
                createSpanDefinitions(
                    array as Array<Any>,
                    sb,
                    selectStartContent,
                    selectEndContent,
                    firstSpanDef,
                    secondSpanDef
                )
                if (firstSpanDef.create) {
                    sb.setSpan(
                        StrikethroughSpan(),
                        firstSpanDef.start,
                        firstSpanDef.end,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                if (secondSpanDef.create) {
                    sb.setSpan(
                        StrikethroughSpan(),
                        secondSpanDef.start,
                        secondSpanDef.end,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
            R.id.bCleanText -> {
                val arrayAllSpans = sb.getSpans(0, sb.length, Any::class.java)
                if (arrayAllSpans.isNotEmpty()) {
                    arrayAllSpans.forEach { ss ->
                        sb.removeSpan(ss)
                    }
                }
            }
        }
        etContent.text = sb
        etContent.setSelection(selectStartContent, selectEndContent)
    }

    private fun createSpanDefinitions(
        arraySpansAllText: Array<Any>,
        spannableStrBuilder: SpannableStringBuilder,
        selectStartContent: Int,
        selectEndContent: Int,
        firstSpan: SpanDefinition,
        secondSpan: SpanDefinition
    ) {
        firstSpan.create = true
        firstSpan.start = selectStartContent
        firstSpan.end = selectEndContent

        arraySpansAllText.forEach { b ->
            val startSpan = spannableStrBuilder.getSpanStart(b)
            val endSpan = spannableStrBuilder.getSpanEnd(b)
            spannableStrBuilder.removeSpan(b)

            if (selectStartContent in startSpan..endSpan && selectEndContent in startSpan..endSpan) {
                if (selectStartContent != startSpan) {
                    firstSpan.create = true
                    firstSpan.start = startSpan
                    firstSpan.end = selectStartContent
                } else {
                    firstSpan.create = false
                }

                if (selectEndContent != endSpan) {
                    secondSpan.create = true
                    secondSpan.start = selectEndContent
                    secondSpan.end = endSpan
                } else {
                    secondSpan.create = false
                }

            } else {
                firstSpan.create = true
                secondSpan.create = false
                if (startSpan < firstSpan.start) {
                    firstSpan.start = startSpan

                }
                if (firstSpan.end < endSpan) {
                    firstSpan.end = endSpan
                }
            }
        }
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
                    myDbManager.removeImagesDbforForeignId(it.id)
                    Toast.makeText(this, "hello ${it.id}", Toast.LENGTH_SHORT).show()
                    tempArrayImageUri.removeAt(index)
                    imageAdapter.notifyItemRemoved(index)
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

