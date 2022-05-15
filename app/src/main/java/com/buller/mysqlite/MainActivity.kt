package com.buller.mysqlite

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.buller.mysqlite.accounthelper.GoogleAccountConst
import com.buller.mysqlite.constans.ContentConstants
import com.buller.mysqlite.databinding.ActivityMainBinding
import com.buller.mysqlite.db.MyDbManager
import com.buller.mysqlite.dialogs.DialogHelper
import com.buller.mysqlite.permissions.PermissionUtils
import com.buller.mysqlite.utils.TypefaceUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
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


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var tvAccount: TextView
    private lateinit var permissionUtil: PermissionUtils
    private val myDbManager = MyDbManager(this)
    private val noteAdapter = NotesAdapter(ArrayList(), this)
    private var job: Job? = null
    private val dialogHelper = DialogHelper(this)
    val mAuth = FirebaseAuth.getInstance()
    private var isPermission = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        permissionUtil = PermissionUtils(this)
        permissionUtil.checkPermissions()
        TypefaceUtil.overrideFont(applicationContext,"SERIF","font/Roboto-Regular.ttf")
        initView()
        initNotesAdapter()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 100) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                isPermission = true
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
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

    override fun onResume() {
        super.onResume()
        myDbManager.openDb()
        fillNotesAdapter("")
    }

    override fun onDestroy() {
        super.onDestroy()
        myDbManager.closeDb()
    }

    fun onClickAdd(view: View) = with(binding) {
            val i = Intent(this@MainActivity, EditActivity::class.java)
            i.putExtra(ContentConstants.EDIT_CHOOSE,false)
            startActivity(i)
    }

    private fun initNotesAdapter() = with(binding) {
        rcView.layoutManager = LinearLayoutManager(this@MainActivity)
        //удаление свайпом
        val swapHelper = getSwapMg()
        swapHelper.attachToRecyclerView(rcView)

        rcView.adapter = noteAdapter
    }

    private fun fillNotesAdapter(text: String) {
        job?.cancel()
        job = CoroutineScope(Dispatchers.Main).launch {
            val list = myDbManager.readDb(text)
            noteAdapter.updateAdapter(list)
        }
    }

    private fun getSwapMg(): ItemTouchHelper {
        return ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                noteAdapter.removeItem(viewHolder.adapterPosition, myDbManager)
            }
        })
    }

    //search note text
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        initSearchView(menu!!.findItem(R.id.app_bar_search).actionView as SearchView)
        return super.onCreateOptionsMenu(menu)
    }

    private fun initSearchView(searchView:SearchView) = with(binding) {

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                TODO("Not yet implemented")
            }
            override fun onQueryTextChange(text: String?): Boolean {
                fillNotesAdapter(text!!)
                return true
            }
        })
    }


    //filters menu and search
    override fun onOptionsItemSelected(item: MenuItem): Boolean = with(binding) {
        if (item.itemId == R.id.nav_filters) {
            Toast.makeText(this@MainActivity, "filters", Toast.LENGTH_SHORT).show()
        } else if(item.itemId == R.id.category){
            val i = Intent(this@MainActivity, CategoryActivity::class.java)
            startActivity(i)

        }
        return super.onOptionsItemSelected(item)
    }

    private fun initView() = with(binding) {
        val toolbarMain = toolbar.toolbarView
        toolbarMain.title = ""
        setSupportActionBar(toolbarMain)
        val toggle = ActionBarDrawerToggle(
            this@MainActivity,
            drawerLayout,
            toolbar.toolbarView,
            R.string.open,
            R.string.close
        )

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        binding.navView.setNavigationItemSelectedListener(this@MainActivity)
        tvAccount = binding.navView.getHeaderView(0).findViewById(R.id.tvLogInEmail)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.ac_sign_up -> {
                dialogHelper.createSignDialog(ContentConstants.SIGN_UP_STATE)
                return true
            }
            R.id.ac_sign_in -> {
                dialogHelper.createSignDialog(ContentConstants.SIGN_IN_STATE)
                return true
            }
            R.id.ac_sign_out -> {
                uiUpdate(null)
                mAuth.signOut()
                dialogHelper.accountHelper.signOutGoogleAccount()
                return true
            }
            R.id.rec_bin -> {
                Toast.makeText(this@MainActivity, "Bin", Toast.LENGTH_LONG).show()
                return true
            }
            R.id.settings -> {
                Toast.makeText(this@MainActivity, "Settings", Toast.LENGTH_LONG).show()
                return true
            }
            R.id.dev -> {
                Toast.makeText(this@MainActivity, "Dev", Toast.LENGTH_LONG).show()
                return true
            }
            R.id.rate -> {
                Toast.makeText(this@MainActivity, "Rate 5 stars", Toast.LENGTH_LONG).show()
                return true
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun uiUpdate(user: FirebaseUser?) {
        tvAccount.text = if (user == null) {
            resources.getString(R.string.not_reg)
        } else {
            user.email
        }
    }

    override fun onStart() {
        super.onStart()
        uiUpdate(mAuth.currentUser)
    }
}