package com.buller.mysqlite.utils

import androidx.sqlite.db.SimpleSQLiteQuery
import com.buller.mysqlite.data.ConstantsDbName

object BuilderQuery {
//sortColumn: String = "n.note_id",
    fun buildQuery(
        sortColumn: String = "n.is_pin",
        sortOrder: Int = 1,
        idCategory: Long = -1L,
        searchText: String = ""
    ): SimpleSQLiteQuery {

        val args: ArrayList<Any> = ArrayList()

        var queryString = ""
        queryString += "SELECT * FROM ${ConstantsDbName.NOTE_TABLE_NAME} AS n"

        val conditions: ArrayList<String> = ArrayList()

        if (idCategory != -1L) {
            queryString += " INNER JOIN notewithcategoriescrossref AS nc ON n.note_id = nc.note_id "
            conditions.add(" nc.category_id = ? ")
            args.add(idCategory)
        }

        if (searchText.isNotEmpty()) {
            conditions.add("(${ConstantsDbName.NOTE_TITLE}" + " LIKE ?" + " OR " + ConstantsDbName.NOTE_TEXT + " LIKE ?" + ")")
            val search = "%$searchText%"
            args.add(search)
            args.add(search)
        }

        if (conditions.isNotEmpty()) {
            queryString += " WHERE " + conditions.joinToString(" AND ")
        }

        queryString += " ORDER BY "
        queryString += sortColumn

        when (sortOrder) {
            1 -> queryString += " ASC"
            0 -> queryString += " DESC"
        }

        queryString += ";"

        return if (args.isEmpty()) {
            SimpleSQLiteQuery(queryString)
        } else {
            SimpleSQLiteQuery(queryString, args.toArray())
        }
    }
}