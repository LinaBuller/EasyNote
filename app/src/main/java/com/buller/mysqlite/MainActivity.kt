package com.buller.mysqlite

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.View
import android.view.Window
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.buller.mysqlite.accounthelper.AccountHelper
import com.buller.mysqlite.databinding.ActivityMainBinding
import com.buller.mysqlite.databinding.NavHeaderMainBinding
import com.buller.mysqlite.dialogs.DialogHelper
import com.buller.mysqlite.theme.BaseTheme
import com.buller.mysqlite.theme.DarkTheme
import com.buller.mysqlite.theme.LightTheme
import com.dolatkia.animatedThemeManager.AppTheme
import com.dolatkia.animatedThemeManager.ThemeManager
import com.easynote.domain.models.UserCredential
import com.easynote.domain.viewmodels.BaseViewModel
import com.easynote.domain.viewmodels.FirebaseViewModel
import com.easynote.domain.viewmodels.NotesViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import org.koin.androidx.viewmodel.ext.android.viewModel



/**категории заметок
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
// Шаринг файла заметки **/

class MainActivity : BaseActivity(), OnPassUserToViewModel {

    private val mNoteViewModel by viewModel<NotesViewModel>()
    private val mFirebaseViewModel by viewModel<FirebaseViewModel>()
    override val mBaseViewModel: BaseViewModel get() = mFirebaseViewModel
    private val dialogAuthHelper: DialogHelper = DialogHelper(this)

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var toolbarMain: Toolbar
    lateinit var binding: ActivityMainBinding
    private var isLight = true
    private lateinit var wrapperDialog: Context


    override fun onCreate(savedInstanceState: Bundle?) {
        loadSettings()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.appBarLayout.toolbar.title = ""
        mNoteViewModel.currentTheme.observe(this) {
//            isLight = it.themeId == 0
//            editor.apply {
//                putBoolean("PREFERRED_THEME", isLight)
//                apply()
//            }
        }

        setSupportActionBar(binding.appBarLayout.toolbar)
        setupActionBar(binding.appBarLayout.toolbar)

        initHeader()
        initSearchView()
        initObservers()
        onSaveDatabase()
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

    override fun initObservers() {
        super.initObservers()
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
        wrapperDialog = ContextThemeWrapper(this, theme.styleDialogAddCategory())
        window.statusBarColor = theme.setStatusBarColor(this)
        window.setLightStatusBars(theme.setColorTextStatusBar())
        window.navigationBarColor = theme.setStatusBarColor(this)
    }

    private fun Window.setLightStatusBars(b: Boolean) {
        WindowCompat.getInsetsController(this, decorView).isAppearanceLightStatusBars = b
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
        //mNoteViewModel.setSearchText(query)
    }

    private fun loadSettings() {
        mNoteViewModel.getIsFirstUsagesSharedPref()
        if (mNoteViewModel.isFirstUsages) {
            isLight = !isNightMode()
            mNoteViewModel.setIsFirstUsagesSharPref(false)
        } else {
            mNoteViewModel.getPreferredThemeSharedPref()
            isLight = mNoteViewModel.preferredTheme
        }
    }

    private fun initHeader() {
        val primaryHeaderView = binding.navView.getHeaderView(0)
        val headerMainBinding = NavHeaderMainBinding.bind(primaryHeaderView)
        switchTheme(headerMainBinding)
        authorizationUser(headerMainBinding)
    }

    private fun switchTheme(headerBinding: NavHeaderMainBinding) {
        headerBinding.switchTheme.isChecked = !isLight
        mNoteViewModel.changeTheme(if (isLight) 0 else 1)
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
    }

    private fun authorizationUser(headerBinding: NavHeaderMainBinding) {

        mFirebaseViewModel.user.observe(this) { user ->
            if (user == null) {
                headerBinding.tvTextEmail.text = resources.getText(R.string.not_reg)
                headerBinding.btSignIn.visibility = View.VISIBLE
                headerBinding.btSignUp.visibility = View.VISIBLE
                headerBinding.btSignOut.visibility = View.GONE
            } else {
                headerBinding.tvTextEmail.text = user.email
                headerBinding.btSignOut.visibility = View.VISIBLE
                headerBinding.btSignIn.visibility = View.GONE
                headerBinding.btSignUp.visibility = View.GONE
            }
        }

        headerBinding.apply {
            btSignIn.setOnClickListener {
                dialogAuthHelper.createSignInDialog(wrapperDialog)
            }

            btSignUp.setOnClickListener {
                dialogAuthHelper.createSignUpDialog(wrapperDialog)
            }
            btSignOut.setOnClickListener {
                mFirebaseViewModel.logoutFirebase()
            }
        }
    }

    override fun onSignUpWithEmailToViewModel(userCredential: UserCredential) {
        mFirebaseViewModel.onSignUpWithEmail(userCredential)
        binding.drawerLayout.openDrawer(GravityCompat.START)
    }

    override fun onSignInWithEmailToViewModel(userCredential: UserCredential) {
        mFirebaseViewModel.onSignInWithEmail(userCredential)
    }

    override fun onSignInWithGoogleToViewModel() {
        val accountHelper = AccountHelper(this)
        val signInClient = accountHelper.getSignInClient()
        resultLauncher.launch(signInClient.signInIntent)
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {

                val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
                mFirebaseViewModel.onSignInWithGoogle(task)
            }
        }

    override fun onSendRecoveryPassword(email: String) {
        mFirebaseViewModel.sendEmailRecovery(email)
        binding.drawerLayout.closeDrawer(GravityCompat.START)
    }

    private fun onSaveDatabase() {
        binding.navView.menu.findItem(R.id.save_database).setOnMenuItemClickListener {
            mFirebaseViewModel.backupDatabase()
            return@setOnMenuItemClickListener true
        }

        binding.navView.menu.findItem(R.id.load_database).setOnMenuItemClickListener {
            showRetrieveDialog()
            return@setOnMenuItemClickListener true
        }
    }

    private fun showRetrieveDialog() {
        MaterialDialog(wrapperDialog).show {
            title(R.string.attention)
            message(R.string.retrieve_database)
            positiveButton { dialog ->
                mFirebaseViewModel.restoreBackup()
                dialog.dismiss()
            }
            negativeButton { dialog ->
                dialog.dismiss()
            }
        }
    }
}
