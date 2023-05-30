package com.buller.mysqlite.utils

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.icu.util.Calendar
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import com.buller.mysqlite.R
import com.buller.mysqlite.data.ConstantsDbName
import com.buller.mysqlite.model.Category
import com.buller.mysqlite.model.ChangeMenuItem
import com.buller.mysqlite.model.Note
import com.buller.mysqlite.utils.theme.CurrentTheme
import com.buller.mysqlite.utils.theme.DecoratorView
import com.buller.mysqlite.viewmodel.NotesViewModel

class CustomPopupMenu(
    val context: Context,
    view: View,
    val currentTheme: CurrentTheme? = null
) : PopupMenu(context, view) {

    var onChangeItemNote: ((Note) -> Unit)? = null
    var onChangeItemToolbar: ((Boolean) -> Unit)? = null

    var onChangeItemNoteDelete: ((Note) -> Unit)? = null
    var onChangeItemNoteCategory: ((Note) -> Unit)? = null
    var onChangeItemNoteArchive: ((Note) -> Unit)? = null
    var onItemCrypt: ((Note) -> Unit)? = null
    var onItemPas: (() -> Unit)? = null

    var onChangeItemCategory: ((Category) -> Unit)? = null
    var onDeleteItemCategory: ((Category) -> Unit)? = null

    private fun setCheckableMenuItem(
        isSelected: Boolean,
        checkable: ChangeMenuItem,
        itemId: Int
    ): MenuItem {
        var title = ""
        var resIcon: Drawable? = null
        if (!isSelected) {
            title = checkable.titleChecked
            if (currentTheme != null) {
                resIcon = DecoratorView.setIcon(
                    context,
                    currentTheme.themeId,
                    checkable.resIconChecked
                )!!
            }
        } else {
            title = checkable.titleUnchecked
            if (currentTheme != null) {
                resIcon = DecoratorView.setIcon(
                    context,
                    currentTheme.themeId,
                    checkable.resIconUnchecked
                )!!
            }
        }
        return menu.add(Menu.NONE, itemId, 0, title).setIcon(resIcon)
    }

    private fun showIconPopupMenu() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            this.setForceShowIcon(true)
        } else {
            try {
                val fieldPopupMenu = android.widget.PopupMenu::class.java.getDeclaredField("mPopup")
                fieldPopupMenu.isAccessible = true
                val mPopup = fieldPopupMenu.get(this)
                mPopup.javaClass.getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                    .invoke(mPopup, true)
            } catch (e: Exception) {
                Log.e("Main", "Error showing menu icons.", e)
            }
        }
    }

    /** fun showPopupMenuNoteItem(selectedNote: Note) {
    val hashMap = HashMap<Int, ChangeMenuItem>()
    hashMap[0] = ChangeMenuItem(
    R.drawable.ic_push_pin_24,
    "Pin",
    R.drawable.ic_delete,
    "Unpin"
    )
    hashMap[1] = ChangeMenuItem(
    R.drawable.ic_favorite,
    "Add to favorite",
    R.drawable.ic_favorite_sold,
    "Delete from favorite"
    )
    setCheckableMenuItem(selectedNote.isPin, hashMap[0]!!, 0)

    setCheckableMenuItem(selectedNote.isFavorite, hashMap[1]!!, 1)

    menuInflater.inflate(R.menu.menu_popup_item, menu)
    this.setOnMenuItemClickListener { itemMenu ->
    when (itemMenu.itemId) {
    0 -> {
    if (selectedNote.isPin) {
    val title = hashMap[0]!!.titleChecked
    val icon = hashMap[0]!!.resIconChecked
    if (currentTheme != null) {
    itemMenu.icon =
    DecoratorView.setIcon(
    context,
    currentTheme.themeId,
    icon
    )
    }
    itemMenu.title = title
    } else {
    val title = hashMap[0]!!.titleUnchecked
    val icon = hashMap[0]!!.resIconUnchecked
    if (currentTheme != null) {
    itemMenu.icon = DecoratorView.setIcon(
    context,
    currentTheme.themeId, icon
    )
    }
    itemMenu.title = title
    }
    selectedNote.isPin = !selectedNote.isPin
    onChangeItem?.invoke(selectedNote)
    return@setOnMenuItemClickListener true
    }
    1 -> {
    if (selectedNote.isFavorite) {
    val title = hashMap[1]!!.titleChecked
    val icon = hashMap[1]!!.resIconChecked
    if (currentTheme != null) {
    itemMenu.icon =
    DecoratorView.setIcon(
    context,
    currentTheme.themeId,
    icon
    )
    }
    itemMenu.title = title
    } else {
    val title = hashMap[1]!!.titleUnchecked
    val icon = hashMap[1]!!.resIconUnchecked
    if (currentTheme != null) {
    itemMenu.icon = DecoratorView.setIcon(
    context,
    currentTheme.themeId, icon
    )
    }
    itemMenu.title = title
    }
    selectedNote.isFavorite = !selectedNote.isFavorite
    onChangeItem?.invoke(selectedNote)
    return@setOnMenuItemClickListener true
    }
    R.id.crypt_item -> {
    return@setOnMenuItemClickListener true
    }
    R.id.change_category_item -> {
    return@setOnMenuItemClickListener true
    }
    R.id.shared_item -> {
    return@setOnMenuItemClickListener true
    }
    R.id.delete_item -> {
    selectedNote.isDeleted = true
    onChangeItem?.invoke(selectedNote)
    return@setOnMenuItemClickListener true
    }
    else -> return@setOnMenuItemClickListener false
    }
    }
    this.showIconPopupMenu()
    this.show()
    }
     */

    fun showPopupMenuNoteItem(selectedNote: Note, isOpened: Boolean) {
        val hashMap = HashMap<Int, ChangeMenuItem>()
        hashMap[0] = ChangeMenuItem(
            R.drawable.ic_push_pin_24,
            "Pin",
            R.drawable.ic_delete,
            "Unpin"
        )
        hashMap[1] = ChangeMenuItem(
            R.drawable.ic_favorite,
            "Add to favorite",
            R.drawable.ic_favorite_sold,
            "Delete from favorite"
        )

        hashMap[2] = ChangeMenuItem(
            R.drawable.ic_arch,
            "Add to archive",
            R.drawable.ic_archive_24,
            "Delete from archive"
        )
        setCheckableMenuItem(selectedNote.isPin, hashMap[0]!!, 0)

        setCheckableMenuItem(selectedNote.isFavorite, hashMap[1]!!, 1)

        setCheckableMenuItem(selectedNote.isArchive, hashMap[2]!!, 2)
        if (isOpened) {

            menuInflater.inflate(R.menu.menu_filter_add_fragment, menu)

            this.setOnMenuItemClickListener { itemMenu ->

                itemMenu?.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW)
                itemMenu?.actionView = View(context)
                itemMenu?.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
                    override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                        return false
                    }

                    override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                        return false
                    }
                })

                when (itemMenu.itemId) {
                    0 -> {
                        if (selectedNote.isPin) {
                            val title = hashMap[0]!!.titleChecked
                            val icon = hashMap[0]!!.resIconChecked
                            if (currentTheme != null) {
                                itemMenu.icon =
                                    DecoratorView.setIcon(
                                        context,
                                        currentTheme.themeId,
                                        icon
                                    )
                            }
                            itemMenu.title = title
                        } else {
                            val title = hashMap[0]!!.titleUnchecked
                            val icon = hashMap[0]!!.resIconUnchecked
                            if (currentTheme != null) {
                                itemMenu.icon = DecoratorView.setIcon(
                                    context,
                                    currentTheme.themeId, icon
                                )
                            }
                            itemMenu.title = title
                        }
                        val changedSelectedNote = selectedNote.copy(isPin = !selectedNote.isPin)
                        onChangeItemNote?.invoke(changedSelectedNote)
                    }

                    1 -> {
                        if (selectedNote.isFavorite) {
                            val title = hashMap[1]!!.titleChecked
                            val icon = hashMap[1]!!.resIconChecked
                            if (currentTheme != null) {
                                itemMenu.icon =
                                    DecoratorView.setIcon(
                                        context,
                                        currentTheme.themeId,
                                        icon
                                    )
                            }
                            itemMenu.title = title
                        } else {
                            val title = hashMap[1]!!.titleUnchecked
                            val icon = hashMap[1]!!.resIconUnchecked
                            if (currentTheme != null) {
                                itemMenu.icon = DecoratorView.setIcon(
                                    context,
                                    currentTheme.themeId, icon
                                )
                            }
                            itemMenu.title = title
                        }
                        val changedSelectedNote =
                            selectedNote.copy(isFavorite = !selectedNote.isFavorite)
                        onChangeItemNote?.invoke(changedSelectedNote)

                    }

                    2 -> {
                        if (selectedNote.isArchive) {
                            val title = hashMap[2]!!.titleChecked
                            val icon = hashMap[2]!!.resIconChecked
                            if (currentTheme != null) {
                                itemMenu.icon =
                                    DecoratorView.setIcon(context, currentTheme.themeId, icon)
                            }
                            itemMenu.title = title
                        } else {
                            val title = hashMap[2]!!.titleUnchecked
                            val icon = hashMap[2]!!.resIconUnchecked
                            if (currentTheme != null) {
                                itemMenu.icon = DecoratorView.setIcon(
                                    context,
                                    currentTheme.themeId, icon
                                )
                            }
                            itemMenu.title = title
                        }
                        //val changedSelectedNote = selectedNote.copy(isArchive = !selectedNote.isArchive)
                        onChangeItemNoteArchive?.invoke(selectedNote)
                    }

                    R.id.encrypt_note -> {
                        onItemCrypt?.invoke(selectedNote)
                    }

                    R.id.arch_note -> {
                        onChangeItemNoteArchive?.invoke(selectedNote)
                    }

                    R.id.shared_note -> {
                        ShareNoteAsSimpleText.sendSimpleText(selectedNote, context)
                    }

                    R.id.delete_note -> {
                        onChangeItemNoteDelete?.invoke(selectedNote)
                    }
                }
                return@setOnMenuItemClickListener false
            }
        } else {
            menuInflater.inflate(R.menu.menu_popup_item, menu)

            this.setOnMenuItemClickListener { itemMenu ->
                when (itemMenu.itemId) {
                    0 -> {
                        if (selectedNote.isPin) {
                            val title = hashMap[0]!!.titleChecked
                            val icon = hashMap[0]!!.resIconChecked
                            if (currentTheme != null) {
                                itemMenu.icon =
                                    DecoratorView.setIcon(
                                        context,
                                        currentTheme.themeId,
                                        icon
                                    )
                            }
                            itemMenu.title = title
                        } else {
                            val title = hashMap[0]!!.titleUnchecked
                            val icon = hashMap[0]!!.resIconUnchecked
                            if (currentTheme != null) {
                                itemMenu.icon = DecoratorView.setIcon(
                                    context,
                                    currentTheme.themeId, icon
                                )
                            }
                            itemMenu.title = title
                        }
                        val changedSelectedNote = selectedNote.copy(isPin = !selectedNote.isPin)
                        onChangeItemNote?.invoke(changedSelectedNote)
                    }

                    1 -> {
                        if (selectedNote.isFavorite) {
                            val title = hashMap[1]!!.titleChecked
                            val icon = hashMap[1]!!.resIconChecked
                            if (currentTheme != null) {
                                itemMenu.icon =
                                    DecoratorView.setIcon(
                                        context,
                                        currentTheme.themeId,
                                        icon
                                    )
                            }
                            itemMenu.title = title
                        } else {
                            val title = hashMap[1]!!.titleUnchecked
                            val icon = hashMap[1]!!.resIconUnchecked
                            if (currentTheme != null) {
                                itemMenu.icon = DecoratorView.setIcon(
                                    context,
                                    currentTheme.themeId, icon
                                )
                            }
                            itemMenu.title = title
                        }
                        val changedSelectedNote =
                            selectedNote.copy(isFavorite = !selectedNote.isFavorite)
                        onChangeItemNote?.invoke(changedSelectedNote)

                    }

                    R.id.crypt_item -> {
                        onItemCrypt?.invoke(selectedNote)
                    }

                    R.id.change_category_item -> {
                        onChangeItemNoteCategory?.invoke(selectedNote)
                    }

                    R.id.arch_item -> {
                        onChangeItemNoteArchive?.invoke(selectedNote)
                    }

                    R.id.shared_item -> {
                        ShareNoteAsSimpleText.sendSimpleText(selectedNote, context)
                    }

                    R.id.delete_item -> {
                        onChangeItemNoteDelete?.invoke(selectedNote)
                    }
                }
                return@setOnMenuItemClickListener false
            }

        }

        this.showIconPopupMenu()
        this.show()
    }

    fun showPopupMenuToolbar(isSelect: Boolean) {
        val changeItem = ChangeMenuItem(
            R.drawable.ic_view_list,
            "Switch to View list",
            R.drawable.ic_grid_view_24,
            "Switch to Grid list"
        )
        setCheckableMenuItem(isSelect, changeItem, 0)
        this.menuInflater.inflate(
            R.menu.menu_toolbar_context_menu_list_fragment,
            menu
        )
        this.setOnMenuItemClickListener { itemMenu ->
            when (itemMenu!!.itemId) {
                0 -> {
                    if (isSelect) {
                        val title = changeItem.titleChecked
                        val icon = changeItem.resIconChecked
                        if (currentTheme != null) {
                            itemMenu.icon = DecoratorView.setIcon(
                                context,
                                currentTheme.themeId, icon
                            )
                        }
                        itemMenu.title = title
                        onChangeItemToolbar?.invoke(false)
                    } else {
                        val title = changeItem.titleUnchecked
                        val icon = changeItem.resIconUnchecked
                        if (currentTheme != null) {
                            itemMenu.icon = DecoratorView.setIcon(
                                context,
                                currentTheme.themeId, icon
                            )
                        }
                        itemMenu.title = title
                        onChangeItemToolbar?.invoke(true)
                    }
                    return@setOnMenuItemClickListener true
                }

                R.id.action_multiselect -> {
                    onItemPas?.invoke()
                    return@setOnMenuItemClickListener true
                }

                else -> return@setOnMenuItemClickListener false
            }
        }
        this.showIconPopupMenu()
        this.show()
    }

    fun showPopupMenuSort(mNoteViewModel: NotesViewModel) {
        menuInflater.inflate(
            R.menu.menu_filter_list_fragment,
            menu
        )
        this.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.noSort -> {
                    mNoteViewModel.resetSort()
                    Toast.makeText(context, "No sort", Toast.LENGTH_SHORT)
                        .show()
                    return@setOnMenuItemClickListener true
                }

                R.id.sortAZ -> {
                    mNoteViewModel.setSort(
                        sortColumn = ConstantsDbName.NOTE_TITLE,
                        sortOrder = 0
                    )
                    Toast.makeText(context, "Sort A - Z", Toast.LENGTH_SHORT)
                        .show()
                    return@setOnMenuItemClickListener true
                }

                R.id.sortZA -> {
                    mNoteViewModel.setSort(sortColumn = ConstantsDbName.NOTE_TITLE)
                    Toast.makeText(context, "Sort Z - A", Toast.LENGTH_SHORT)
                        .show()
                    return@setOnMenuItemClickListener true
                }

                R.id.sort_newest_oldest -> {
                    mNoteViewModel.setSort(sortColumn = ConstantsDbName.NOTE_TIME)
                    Toast.makeText(context, "Sort New - Old", Toast.LENGTH_SHORT)
                        .show()
                    return@setOnMenuItemClickListener true
                }

                R.id.sort_oldest_newest -> {
                    mNoteViewModel.setSort(
                        sortColumn = ConstantsDbName.NOTE_TIME,
                        sortOrder = 0
                    )
                    Toast.makeText(context, "Sort Old - New", Toast.LENGTH_SHORT)
                        .show()
                    return@setOnMenuItemClickListener true
                }

                R.id.filter_by_date -> {
                    val c: Calendar = Calendar.getInstance();
                    val mYear = c.get(Calendar.YEAR);
                    val mMonth = c.get(Calendar.MONTH);
                    val mDay = c.get(Calendar.DAY_OF_MONTH);
                    val dpd = DatePickerDialog(
                        context,
                        { view, year, monthOfYear, dayOfMonth -> // Display Selected date in textbox
                            //isSelectedDate = true
                            //readDbFromSelectData(year, monthOfYear + 1, dayOfMonth)
                        }, mYear, mMonth, mDay
                    )
                    dpd.show()
                    return@setOnMenuItemClickListener true
                }

                else -> return@setOnMenuItemClickListener false
            }
        }
        this.showIconPopupMenu()
        this.show()
    }

    fun showPopupMenuArchive(selectedNote: Note) {
        menuInflater.inflate(
            R.menu.menu_archive_item_popup,
            menu
        )
        this.setOnMenuItemClickListener { itemMenu ->
            when (itemMenu.itemId) {
                R.id.archive_item_delete -> {
                    onChangeItemNoteDelete?.invoke(selectedNote)
                    return@setOnMenuItemClickListener true
                }

                R.id.archive_item_return -> {
                    onChangeItemNoteArchive?.invoke(selectedNote)
                    return@setOnMenuItemClickListener true
                }

                else -> return@setOnMenuItemClickListener false
            }
        }
        showIconPopupMenu()
        this.show()
    }

    fun showPopupMenuBinItem(currentNote: Note) {
        menuInflater.inflate(
            R.menu.menu_filter_list_fragment,
            menu
        )
    }

    fun showPopupMenuCategoryItem(selectedCategory: Category) {
        menuInflater.inflate(
            R.menu.menu_popup_item_category_fragment,
            menu
        )
        this.setOnMenuItemClickListener { itemMenu ->
            when (itemMenu.itemId) {
                R.id.edit_title_category -> {
                    onChangeItemCategory?.invoke(selectedCategory)
                    return@setOnMenuItemClickListener true
                }

                R.id.delete_category_item -> {
                    onDeleteItemCategory?.invoke(selectedCategory)
                    return@setOnMenuItemClickListener true
                }

                else -> return@setOnMenuItemClickListener false
            }
        }
        showIconPopupMenu()
        this.show()
    }
}