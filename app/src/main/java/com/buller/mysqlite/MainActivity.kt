package com.buller.mysqlite

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.preference.PreferenceManager
import com.buller.mysqlite.accounthelper.GoogleAccountConst
import com.buller.mysqlite.databinding.ActivityMainBinding
import com.buller.mysqlite.databinding.NavHeaderMainBinding
import com.buller.mysqlite.dialogs.DialogHelper
import com.buller.mysqlite.utils.theme.BaseTheme
import com.buller.mysqlite.utils.theme.DarkTheme
import com.buller.mysqlite.utils.theme.LightTheme
import com.buller.mysqlite.viewmodel.NotesViewModel
import com.dolatkia.animatedThemeManager.AppTheme
import com.dolatkia.animatedThemeManager.ThemeActivity
import com.dolatkia.animatedThemeManager.ThemeManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
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
class MainActivity : ThemeActivity() {
    lateinit var binding: ActivityMainBinding
    private lateinit var tvAccount: TextView
    private val dialogHelper = DialogHelper(this)
    val mAuth = FirebaseAuth.getInstance()
    lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var drawerLayoutMain: DrawerLayout
    lateinit var toolbarMain: Toolbar
    lateinit var mNoteViewModel: NotesViewModel
    lateinit var sharedPref: SharedPreferences
    var isFirstUsages: Boolean = true
    var isLight = true
    lateinit var editor: SharedPreferences.Editor

    companion object {
        const val TAG = "MyLog"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        loadSettings()
        super.onCreate(savedInstanceState)

        mNoteViewModel = ViewModelProvider(this)[NotesViewModel::class.java]
        binding = ActivityMainBinding.inflate(layoutInflater)

        binding.appBarLayout.toolbar.title = ""
        val headerView = binding.navView.getHeaderView(0)
        val headerBinding = NavHeaderMainBinding.bind(headerView)

        mNoteViewModel.currentTheme.observe(this) {
            isLight = it.themeId == 0
            editor.apply {
                putBoolean("PREFERRED_THEME", isLight)
                apply()
            }
        }
        headerBinding.switchTheme.isChecked = !isLight
        mNoteViewModel.changeTheme(if (isLight) 0 else 1)
        setSupportActionBar(binding.appBarLayout.toolbar)
        setupActionBar(binding.appBarLayout.toolbar)
        initSearchView()

        headerBinding.switchTheme.setOnClickListener {
            val toLight = !headerBinding.switchTheme.isChecked

            if (toLight) {
                ThemeManager.instance.changeTheme(LightTheme(), it)
                mNoteViewModel.changeTheme(0)
            } else {
                ThemeManager.instance.changeTheme(DarkTheme(), it)
                mNoteViewModel.changeTheme(1)
            }
        }
        val insetsWithKeyboardCallback = InsetsWithKeyboardCallback(this.window)
        ViewCompat.setOnApplyWindowInsetsListener(
            binding.appBarLayout.root, insetsWithKeyboardCallback
        )
        setContentView(binding.root)
    }

    override fun getStartTheme(): AppTheme {
        return if (isLight) {
            LightTheme()
        } else {
            DarkTheme()
        }
    }

    private fun isNightMode(): Boolean {
        return when (resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
                true
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                false
            }
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                false
            }
            else -> false
        }
    }

//    private fun setupActionBar(toolbar: Toolbar) = with(binding) {
//        toolbarMain = toolbar
//        val navHostFragment =
//            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
//        navController = navHostFragment.navController
//        drawerLayoutMain = binding.drawerLayout
//        navView.setupWithNavController(navController)
//        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)
//        setupWithNavController(toolbar, navController, appBarConfiguration)
//    }

    private fun setupActionBar(toolbar: Toolbar) = with(binding) {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        NavigationUI.setupActionBarWithNavController(this@MainActivity,navController,drawerLayout)
        toolbarMain = toolbar
        appBarConfiguration = AppBarConfiguration.Builder(R.id.listFragment).setOpenableLayout(drawerLayout).build()
        navView.setupWithNavController(navController)

        toolbarMain.setNavigationOnClickListener {
            navController.navigateUp(appBarConfiguration)
                    || super.onSupportNavigateUp()
        }

        setupWithNavController(toolbarMain,navController,appBarConfiguration)


        navController.addOnDestinationChangedListener{_, destination, _ ->
            if (destination.id in arrayOf(R.id.splashFragment,R.id.loginFragment)){
                toolbarMain.visibility = View.GONE
            }else{
                toolbarMain.visibility = View.VISIBLE
            }
            if (destination.id==R.id.listFragment){
            }
        }
    }

//    override fun onSupportNavigateUp(): Boolean {
//        val navController = findNavController(R.id.nav_host_fragment)
//        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
//    }

    override fun syncTheme(appTheme: AppTheme) {
        val theme = appTheme as BaseTheme
        binding.apply {
            root.setBackgroundColor(theme.backgroundColor(this@MainActivity))
            toolbarMain.setBackgroundColor(theme.backgroundDrawer(this@MainActivity))
            navView.setBackgroundColor(theme.backgroundDrawer(this@MainActivity))
            navView.itemTextColor = ColorStateList.valueOf(theme.textColor(this@MainActivity))

            val state: Array<IntArray> = arrayOf(
                intArrayOf(android.R.attr.state_checked),
                intArrayOf(android.R.attr.state_enabled),
                intArrayOf(android.R.attr.state_pressed),
                intArrayOf(android.R.attr.state_focused),
                intArrayOf(android.R.attr.state_pressed)
            )

            val colors: IntArray = intArrayOf(
                theme.akcColor(this@MainActivity),
                theme.backgroundColor(this@MainActivity),
                theme.backgroundColor(this@MainActivity),
                theme.backgroundColor(this@MainActivity),
                theme.backgroundColor(this@MainActivity)
            )

            navView.itemIconTintList = ColorStateList(state, colors)
        }
        window.statusBarColor = theme.setStatusBarColor(this)
        window.setLightStatusBars(theme.setColorTextStatusBar())
        window.navigationBarColor = theme.setStatusBarColor(this)
    }

    private fun Window.setLightStatusBars(b: Boolean) {
        WindowCompat.getInsetsController(this, decorView).isAppearanceLightStatusBars = b
    }

    @Deprecated("")
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

    private fun initSearchView() {
        if (toolbarMain.menu.findItem(R.id.searchItem) != null) {
            val searchItem = toolbarMain.menu.findItem(R.id.searchItem)
            val search = searchItem.actionView as? SearchView
            search?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if (query != null) {
                        searchDatabase(query)
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText != null) {
                        searchDatabase(newText)
                    }
                    return true
                }

            })
        }
    }

    private fun searchDatabase(query: String) {
        mNoteViewModel.setSearchText(query)
    }

//    override fun onStart() {
//        super.onStart()
//        uiUpdate(mAuth.currentUser)
//    }

    fun uiUpdate(user: FirebaseUser?) {
        tvAccount.text = if (user == null) {
            resources.getString(R.string.not_reg)
        } else {
            user.email
        }
    }

    private fun loadSettings(){
        sharedPref = getSharedPreferences("myPref", Context.MODE_PRIVATE)
        isFirstUsages = sharedPref.getBoolean("FIRST_USAGES", true)
        editor = sharedPref.edit()
        if (isFirstUsages) {
            isLight = !isNightMode()
            editor.apply {
                putBoolean("FIRST_USAGES", false)
                apply()
            }
        } else {
            isLight = sharedPref.getBoolean("PREFERRED_THEME", true)
        }
    }

}
