package com.buller.mysqlite

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.icu.util.Calendar
import android.os.Build
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import com.easynote.domain.models.Category
import com.easynote.domain.models.ChangeMenuItem
import com.easynote.domain.models.Sort
import com.easynote.domain.models.CurrentTheme
import com.example.data.storage.ConstantsDbName

class CustomPopupMenu(
    val context: Context,
    view: View,
    val currentTheme: CurrentTheme? = null
) : PopupMenu(context, view) {

    var onChangeNotePin: ((Boolean) -> Unit)? = null
    var onChangeNoteFavorite: ((Boolean) -> Unit)? = null
    var onChangeNoteArch: (() -> Unit)? = null
    var onSharedNoteText: (() -> Unit)? = null
    var onDeleteNote: (() -> Unit)? = null
    var onPermanentDeleteNote: (() -> Unit)? = null
    var onChangeItemNoteCategory: (() -> Unit)? = null
    var onSetSort: ((Sort) -> Unit)? = null
    var onSelectActionMode: (() -> Unit)? = null
    var onChangeItemCategory: ((Category) -> Unit)? = null
    var onDeleteItemCategory: ((Category) -> Unit)? = null
    var onRestoreNote: (() -> Unit)? = null
    var onChangeTypeListNotes: ((Boolean) -> Unit)? = null
    var onActivateActionMode: (() -> Unit)? = null

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


    fun showPopupMenuNoteItemsFromListFragment(isPin: Boolean, isFavorite: Boolean) {
        val hashMap = HashMap<Int, ChangeMenuItem>()
        hashMap[0] = ChangeMenuItem(
            R.drawable.ic_pin_24,
            "Pin",
            R.drawable.ic_unpin_24,
            "Unpin"
        )
        hashMap[1] = ChangeMenuItem(
            R.drawable.ic_favorite,
            "Add to favorite",
            R.drawable.ic_favorite_sold,
            "Delete from favorite"
        )
        setCheckableMenuItem(isPin, hashMap[0]!!, 0)
        setCheckableMenuItem(isFavorite, hashMap[1]!!, 1)

        menuInflater.inflate(R.menu.menu_popup_item, menu)

        val arch = menu.findItem(R.id.arch_item)
        val shared = menu.findItem(R.id.shared_item)
        val delete = menu.findItem(R.id.delete_item)
        val category = menu.findItem(R.id.change_category_item)
        decorate(arch, shared, delete, category)

        this.setOnMenuItemClickListener { itemMenu ->
            when (itemMenu.itemId) {
                0 -> {
                    if (isPin) {
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
                    onChangeNotePin?.invoke(!isPin)
                }

                1 -> {
                    if (isFavorite) {
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
                    onChangeNoteFavorite?.invoke(!isFavorite)
                }

                R.id.change_category_item -> {
                    onChangeItemNoteCategory?.invoke()
                }

                R.id.arch_item -> {
                    onChangeNoteArch?.invoke()
                }

                R.id.shared_item -> {
                    onSharedNoteText?.invoke()
                }

                R.id.delete_item -> {
                    onDeleteNote?.invoke()
                }
            }
            return@setOnMenuItemClickListener false
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

        val action = menu.findItem(R.id.action_multiselect)
        decorate(action)

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
                        onChangeTypeListNotes?.invoke(false)
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
                        onChangeTypeListNotes?.invoke(true)
                    }
                    return@setOnMenuItemClickListener true
                }

                R.id.action_multiselect -> {
                    onSelectActionMode?.invoke()
                    return@setOnMenuItemClickListener true
                }

                else -> return@setOnMenuItemClickListener false
            }
        }
        this.showIconPopupMenu()
        this.show()
    }

    fun showPopupMenuSort() {
        this.menuInflater.inflate(R.menu.menu_filter_list_fragment, menu)

        val noSort = menu.findItem(R.id.noSort)
        val sortAZ = menu.findItem(R.id.sortAZ)
        val sortZA = menu.findItem(R.id.sortZA)
        val sortNO = menu.findItem(R.id.sort_newest_oldest)
        val sortON = menu.findItem(R.id.sort_oldest_newest)
        val sortDate = menu.findItem(R.id.filter_by_date)
        decorate(noSort, sortAZ, sortZA, sortNO, sortON, sortDate)

        this.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.noSort -> {
                    onSetSort?.invoke(Sort())
                    Toast.makeText(context, R.string.no_sort, Toast.LENGTH_SHORT)
                        .show()
                    return@setOnMenuItemClickListener true
                }

                R.id.sortAZ -> {
                    onSetSort?.invoke(
                        Sort(
                            sortColumn = ConstantsDbName.NOTE_TITLE,
                            sortOrder = 0
                        )
                    )
                    Toast.makeText(context, R.string.sort_a_z, Toast.LENGTH_SHORT)
                        .show()
                    return@setOnMenuItemClickListener true
                }

                R.id.sortZA -> {
                    onSetSort?.invoke(Sort(sortColumn = ConstantsDbName.NOTE_TITLE))
                    Toast.makeText(context, R.string.sort_z_a, Toast.LENGTH_SHORT)
                        .show()
                    return@setOnMenuItemClickListener true
                }

                R.id.sort_newest_oldest -> {
                    onSetSort?.invoke(Sort(sortColumn = ConstantsDbName.NOTE_LAST_CHANGED_TIME))
                    Toast.makeText(context, R.string.sort_new_old, Toast.LENGTH_SHORT)
                        .show()
                    return@setOnMenuItemClickListener true
                }

                R.id.sort_oldest_newest -> {
                    onSetSort?.invoke(
                        Sort(
                            sortColumn = ConstantsDbName.NOTE_LAST_CHANGED_TIME,
                            sortOrder = 0
                        )
                    )
                    Toast.makeText(context, R.string.sort_old_new, Toast.LENGTH_SHORT)
                        .show()
                    return@setOnMenuItemClickListener true
                }

                R.id.filter_by_date -> {

                    //todo: set search for create date
                    val c: Calendar = Calendar.getInstance();
                    val mYear = c.get(Calendar.YEAR);
                    val mMonth = c.get(Calendar.MONTH);
                    val mDay = c.get(Calendar.DAY_OF_MONTH);
                    val dpd = DatePickerDialog(
                        context,
                        { view, year, monthOfYear, dayOfMonth -> // Display Selected date in textbox
                            Toast.makeText(
                                context,
                                "${R.string.sort_special_date} $year, ${monthOfYear + 1}, $dayOfMonth",
                                Toast.LENGTH_SHORT
                            ).show()
                            onSetSort?.invoke(
                                Sort(
                                    date = arrayOf(
                                        monthOfYear + 1,
                                        dayOfMonth,
                                        year
                                    )
                                )
                            )
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

    fun showPopupMenuCategoryItem(selectedCategory: Category) {
        menuInflater.inflate(
            R.menu.menu_popup_item_category_fragment,
            menu
        )

        val edit = menu.findItem(R.id.edit_title_category)
        val delete = menu.findItem(R.id.delete_category_item)
        decorate(edit, delete)

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

    fun showPopupMenuArchive() {
        menuInflater.inflate(
            R.menu.menu_archive_item_popup,
            menu
        )

        val delete = menu.findItem(R.id.archive_item_delete)
        val unarchive = menu.findItem(R.id.archive_item_return)
        decorate(delete, unarchive)

        this.setOnMenuItemClickListener { itemMenu ->
            when (itemMenu.itemId) {
                R.id.archive_item_delete -> {
                    onDeleteNote?.invoke()
                    return@setOnMenuItemClickListener true
                }

                R.id.archive_item_return -> {
                    onChangeNoteArch?.invoke()
                    return@setOnMenuItemClickListener true
                }

                else -> return@setOnMenuItemClickListener false
            }
        }
        showIconPopupMenu()
        this.show()
    }

    fun showPopupMenuBinItem() {
        menuInflater.inflate(
            R.menu.menu_bin_action_mode,
            menu
        )
        val delete = menu.findItem(R.id.action_delete)
        val restore = menu.findItem(R.id.action_restore)
        decorate(delete, restore)
        this.setOnMenuItemClickListener { itemMenu ->
            when (itemMenu.itemId) {
                R.id.action_delete -> {
                    onDeleteNote?.invoke()
                    return@setOnMenuItemClickListener true
                }

                R.id.action_restore -> {
                    onRestoreNote?.invoke()
                    return@setOnMenuItemClickListener true
                }

                else -> return@setOnMenuItemClickListener false
            }
        }
        showIconPopupMenu()
        this.show()
    }

    private fun showIconPopupMenu() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            this.setForceShowIcon(true)
        } else {
            try {
                val fieldPopupMenu = CustomPopupMenu::class.java.getDeclaredField("mPopup")
                fieldPopupMenu.isAccessible = true
                val mPopup = fieldPopupMenu.get(this)
                mPopup.javaClass.getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                    .invoke(mPopup, true)
            } catch (e: Exception) {
                Log.e("Main", "Error showing menu icons.", e)
            }
        }
    }

    fun showPopupMenuFromSelectedNote(
        isPin: Boolean,
        isFavorite: Boolean,
        fromArchive: Boolean,
        fromDelete: Boolean
    ) {
        val hashMap = HashMap<Int, ChangeMenuItem>()
        hashMap[0] = ChangeMenuItem(
            R.drawable.ic_pin_24, "Pin",
            R.drawable.ic_unpin_24, "Unpin"
        )
        hashMap[1] = ChangeMenuItem(
            R.drawable.ic_favorite, "Add to favorite",
            R.drawable.ic_favorite_sold, "Delete from favorite"
        )
        setCheckableMenuItem(isPin, hashMap[0]!!, 0)
        setCheckableMenuItem(isFavorite, hashMap[1]!!, 1)

        menuInflater.inflate(R.menu.menu_filter_add_fragment, menu)

        val inArchive = this.menu.findItem(R.id.in_arch_note)
        val outArchive = this.menu.findItem(R.id.out_arch_note)

        if (fromArchive) {
            outArchive.isVisible = true
            inArchive.isVisible = false
        } else {
            outArchive.isVisible = false
            inArchive.isVisible = true
        }

        val inDelete = this.menu.findItem(R.id.delete_note)
        val outDelete = this.menu.findItem(R.id.undelete_note)
        val foreverDelete = this.menu.findItem(R.id.permanent_delete_note)

        if (fromDelete) {
            inDelete.isVisible = false
            outDelete.isVisible = true
            foreverDelete.isVisible = true
        } else {
            inDelete.isVisible = true
            outDelete.isVisible = false
            foreverDelete.isVisible = false
        }

        val action = menu.findItem(R.id.action_mode)
        val shared = menu.findItem(R.id.shared_note)

        decorate(action,shared,inArchive,outArchive,inDelete,outDelete,foreverDelete)

        this.setOnMenuItemClickListener { itemMenu ->

//            itemMenu?.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW)
//            itemMenu?.actionView = View(context)
//            itemMenu?.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
//                override fun onMenuItemActionExpand(item: MenuItem): Boolean {
//                    return false
//                }
//
//                override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
//                    return false
//                }
//            })

            when (itemMenu.itemId) {
                0 -> {
                    if (isPin) {
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
                    onChangeNotePin?.invoke(!isPin)
                }

                1 -> {
                    if (isFavorite) {
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
                    onChangeNoteFavorite?.invoke(!isFavorite)

                }

                R.id.action_mode -> {
                    onActivateActionMode?.invoke()
                }

                R.id.in_arch_note -> {
                    onChangeNoteArch?.invoke()
                }

                R.id.out_arch_note -> {
                    onChangeNoteArch?.invoke()
                }

                R.id.shared_note -> {
                    onSharedNoteText?.invoke()
                }

                R.id.delete_note -> {
                    onDeleteNote?.invoke()
                }

                R.id.undelete_note -> {
                    onDeleteNote?.invoke()
                }

                R.id.permanent_delete_note -> {
                    onPermanentDeleteNote?.invoke()
                }
            }
            return@setOnMenuItemClickListener false
        }
        this.showIconPopupMenu()
        this.show()
    }

    private fun decorate(vararg items: MenuItem) {
        for (item in items) {
            item.iconTintList = DecoratorView.changeColorIcon(
                context,
                currentTheme?.themeId!!
            )
        }
    }
}

