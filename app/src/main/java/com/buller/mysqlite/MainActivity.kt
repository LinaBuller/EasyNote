package com.buller.mysqlite

import android.app.DatePickerDialog
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.MenuProvider
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.buller.mysqlite.accounthelper.GoogleAccountConst
import com.buller.mysqlite.databinding.ActivityMainBinding
import com.buller.mysqlite.dialogs.DialogHelper
import com.buller.mysqlite.utils.TypefaceUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.util.*

//облачная база данных
//категории заметок
//фильтр заметок по категориям
//фильстр заметок по времени(от самого позднего до раннего и наоборот, для какой то спец даты)
//чек листы
//любимые цвета заметок
//долгое нажатие  и там отметки
//закрепить заметку
//добавить в избранное
//корзина заметок
//поиск не только по титулу, но и по содержанию
//изменение представления колонки(2,3) или список
//градиент цветов в поле контента
//добавление видео и аудио
//пароль на вход
//шифрование


//Renovation
//Сделано:
// Создание новой заметки (без категорий)
// Редактирование заметки (без категорий)
// Удаление заметки
// Картинки к заметкам
// Редактирование стиля текста

//Сделать:

// Категории к заметкам
// Изменение цвета полей заметки
// Шаринг файла заметки
//NavigationView.OnNavigationItemSelectedListener
class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private lateinit var tvAccount: TextView
    private val dialogHelper = DialogHelper(this)
    val mAuth = FirebaseAuth.getInstance()
    lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var drawerLayoutMain: DrawerLayout
    lateinit var toolbarMain: Toolbar

    companion object {
        const val TAG = "MyLog"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        TypefaceUtil.overrideFont(applicationContext, "SERIF", "font/Roboto-Regular.ttf")
        //getIntentsForNewMainActivityToSelectedFromDate()
        setupActionBar( binding.toolbar)
    }

    fun setupActionBar(toolbar: Toolbar) = with(binding) {
        toolbarMain = toolbar
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        drawerLayoutMain = binding.drawerLayout
        navView.setupWithNavController(navController)
        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)
        setupWithNavController(toolbar, navController, appBarConfiguration)
    }

    fun setToolbarMenu(menuId:Int,listener: Toolbar.OnMenuItemClickListener){
        toolbarMain.menu.clear()
        toolbarMain.inflateMenu(menuId)
        toolbarMain.setOnMenuItemClickListener(listener)
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "MainActivity onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "MainActivity onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "MainActivity onStop")
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "MainActivity onStart")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "MainActivity onDestroy")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(TAG, "MainActivity onRestart")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == GoogleAccountConst.GOOGLE_SIGN_REQUEST_CODE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    dialogHelper.accountHelper.signInFirebaseWithGoogle(account.idToken!!)
                }
            } catch (e: ApiException) {
                Toast.makeText(this, "Api error:${e.message}", Toast.LENGTH_LONG).show()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

//    override fun onResume() {
//        super.onResume()
//        //fillNotesAdapter("")
//    }

//    override fun onStart() {
//        super.onStart()
//        uiUpdate(mAuth.currentUser)
//    }

//    private fun getIntentsForNewMainActivityToSelectedFromDate(): ArrayList<Note>? {
//        val intent = intent
//        isSelectedDate = intent.getBooleanExtra(ContentConstants.SELECT_NOTE_FROM_DATE, false)
//        return intent.getParcelableArrayListExtra(ContentConstants.LIST_SELECTED_NOTE_FROM_DATE)
//    }

//    override fun onNavigationItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.ac_sign_up -> {
//                dialogHelper.createSignDialog(ContentConstants.SIGN_UP_STATE)
//                return true
//            }
//            R.id.ac_sign_in -> {
//                dialogHelper.createSignDialog(ContentConstants.SIGN_IN_STATE)
//                return true
//            }
//            R.id.ac_sign_out -> {
//                uiUpdate(null)
//                mAuth.signOut()
//                dialogHelper.accountHelper.signOutGoogleAccount()
//                return true
//            }
//            R.id.menu_rec_bin -> {
//                Toast.makeText(this@MainActivity, "Bin", Toast.LENGTH_LONG).show()
//                return true
//            }
//            R.id.menu_settings -> {
//                Toast.makeText(this@MainActivity, "Settings", Toast.LENGTH_LONG).show()
//                return true
//            }
//            R.id.menu_dev -> {
//                Toast.makeText(this@MainActivity, "Dev", Toast.LENGTH_LONG).show()
//                return true
//            }
//            R.id.rate -> {
//                Toast.makeText(this@MainActivity, "Rate 5 stars", Toast.LENGTH_LONG).show()
//                return true
//            }
//        }
//        binding.drawerLayout.closeDrawer(GravityCompat.START)
//        return true
//    }

    fun uiUpdate(user: FirebaseUser?) {
        tvAccount.text = if (user == null) {
            resources.getString(R.string.not_reg)
        } else {
            user.email
        }
    }

    private fun initSearchView(searchView: SearchView) = with(binding) {

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                TODO("Not yet implemented")
            }

            override fun onQueryTextChange(text: String?): Boolean {
                //fillNotesAdapter(text!!)
                return true
            }
        })
    }

}
//    private fun fillNotesAdapter(text: String) {
//        val categoryId = findNoteCategoryID()
//
//            var list =ArrayList<NotesModel>()
//            if (categoryId!=null){
//                val lisCt =notesViewModel.readCategories(categoryId)
//                lisCt.forEach {
//                    val item = notesViewModel.readDbSelectCategoryFromNote(it)
//                    list.add(item)
//                }
//            } else {
//                if (isSelectedDate) {
//                    list = getIntentsForNewMainActivityToSelectedFromDate()!!
//                } else
//                    list = notesViewModel.readNotesFromSearchText(text)
//            }
//            noteAdapter.submitList(list)
//        }
//    }

//    private fun findNoteCategoryID(): Int? {
//        val intent = intent
//        val id = intent?.getIntExtra(ContentConstants.ID_CATEGORY_FROM_SELECTED, 0)
//        return if (id == 0) {
//            null
//        } else {
//            id
//        }
//    }
