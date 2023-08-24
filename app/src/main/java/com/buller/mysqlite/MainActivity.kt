package com.buller.mysqlite

import android.content.res.ColorStateList
import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.buller.mysqlite.databinding.ActivityMainBinding
import com.buller.mysqlite.databinding.NavHeaderMainBinding
import com.easynote.domain.viewmodels.NotesViewModel
import com.dolatkia.animatedThemeManager.AppTheme
import com.dolatkia.animatedThemeManager.ThemeActivity
import com.dolatkia.animatedThemeManager.ThemeManager
import com.buller.mysqlite.theme.BaseTheme
import com.buller.mysqlite.theme.DarkTheme
import com.buller.mysqlite.theme.LightTheme
import com.google.firebase.auth.FirebaseUser
import org.koin.androidx.viewmodel.ext.android.viewModel




//категории заметок
//фильтр заметок по категориям
//фильстр заметок по времени(от самого позднего до раннего и наоборот, для какой то спец даты)
//чек листы(необязательно)
//любимые цвета заметок
//долгое нажатие  и там отметки
//закрепить заметку
//добавить в избранное
//корзина заметок
//поиск не только по титулу, но и по содержанию
//изменение представления колонки(2,3) или список
//градиент цветов в поле контента

//добавление видео и аудио(необязательно)
//пароль на вход
//шифрование
//облачная база данных

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

    private val mNoteViewModel by viewModel<NotesViewModel>()

    lateinit var binding: ActivityMainBinding
    private lateinit var tvAccount: TextView
//    val mAuth = FirebaseAuth.getInstance()
    lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var drawerLayoutMain: DrawerLayout
    lateinit var toolbarMain: Toolbar
    var isLight = true

    override fun onCreate(savedInstanceState: Bundle?) {
        loadSettings()
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        binding.appBarLayout.toolbar.title = ""
        val headerView = binding.navView.getHeaderView(0)
        val headerBinding = NavHeaderMainBinding.bind(headerView)


        mNoteViewModel.currentTheme.observe(this) {
//            isLight = it.themeId == 0
//            editor.apply {
//                putBoolean("PREFERRED_THEME", isLight)
//                apply()
//            }
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

    private fun setupActionBar(toolbar: Toolbar) = with(binding) {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        NavigationUI.setupActionBarWithNavController(this@MainActivity, navController, drawerLayout)
        toolbarMain = toolbar
        appBarConfiguration =
            AppBarConfiguration.Builder(R.id.listFragment).setOpenableLayout(drawerLayout).build()
        navView.setupWithNavController(navController)

        toolbarMain.setNavigationOnClickListener {
            navController.navigateUp(appBarConfiguration)
                    || super.onSupportNavigateUp()
        }

        setupWithNavController(toolbarMain, navController, appBarConfiguration)


        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id in arrayOf(R.id.splashFragment, R.id.loginFragment)) {
                toolbarMain.visibility = View.GONE
            } else {
                toolbarMain.visibility = View.VISIBLE
            }
        }
    }

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

//    @Deprecated("")
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        if (requestCode == GoogleAccountConst.GOOGLE_SIGN_REQUEST_CODE) {
//            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
//            try {
//                val account = task.getResult(ApiException::class.java)
//                if (account != null) {
//                    dialogHelper.accountHelper.signInFirebaseWithGoogle(account.idToken!!)
//                }
//            } catch (e: ApiException) {
//                Toast.makeText(this, "Api error:${e.message}", Toast.LENGTH_LONG).show()
//            }
//        }
//        super.onActivityResult(requestCode, resultCode, data)
//    }

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
       //mNoteViewModel.setSearchText(query)
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

    private fun loadSettings() {
        mNoteViewModel.getIsFirstUsagesSharedPref()
        if(mNoteViewModel.isFirstUsages){
            isLight = !isNightMode()
            mNoteViewModel.setIsFirstUsagesSharPref(false)
        }else{
            mNoteViewModel.getPreferredThemeSharedPref()
            isLight = mNoteViewModel.preferredTheme
        }
    }

}
