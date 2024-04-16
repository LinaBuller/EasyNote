package com.easynote.domain.utils

import androidx.sqlite.db.SimpleSQLiteQuery
import com.easynote.domain.ConstantsDbName

object BuilderQuery {
    //sortColumn: String = "n.note_id",
    fun buildQuery(
        sortColumn: String = "n.is_pin",
        sortOrder: Int = 1,
        idCategory: Set<Long> = setOf(-1L),
        searchText: String = "",
        searchDate: String = ""
    ): SimpleSQLiteQuery {

        val args: ArrayList<Any> = ArrayList()

        var queryString = ""
        queryString += "SELECT * FROM ${ConstantsDbName.NOTE_TABLE_NAME} AS n"

        val conditions: ArrayList<String> = ArrayList()

        if (idCategory.size > 1) {
            //queryString += " INNER JOIN storagenotewithcategoriescrossref AS nc ON n.note_id = nc.note_id "
            queryString += " INNER JOIN storagenotewithcategoriescrossref AS nc USING (note_id) "
            idCategory.forEach { id ->
                if (id != -1L) {
                    conditions.add(" nc.category_id = ? ")
                    args.add(id)
                }
            }
        }

        if (searchText.isNotEmpty()) {
            conditions.add("(${ConstantsDbName.NOTE_TITLE}" + " LIKE ?" + " OR " + ConstantsDbName.NOTE_TEXT + " LIKE ?" + ")")
            val search = "%$searchText%"
            args.add(search)
            args.add(search)
        }
        if (searchDate.isNotEmpty()) {
            conditions.add("(n.${ConstantsDbName.NOTE_TIME}" + " LIKE ?)")
            val search = "%$searchDate%"
            args.add(search)
        }

//        if (conditions.isNotEmpty()) {
//            queryString += " WHERE " + conditions.joinToString(" AND ")
//
//        }

        if (conditions.isNotEmpty()) {
            queryString += if (idCategory.size > 1) {
                " WHERE " + conditions.joinToString(" OR ")
            } else {
                " WHERE " + conditions.joinToString(" AND ")
            }
        }
        if (idCategory.size > 1) {
            queryString += " GROUP BY n.note_id"
        }
        queryString += " ORDER BY "
        queryString += sortColumn

        when (sortOrder) {
            1 -> queryString += " ASC, n.note_last_changed_time"
            0 -> queryString += " DESC, n.note_last_changed_time"
        }

        queryString += ";"

        return if (args.isEmpty()) {
            SimpleSQLiteQuery(queryString)
        } else {
            SimpleSQLiteQuery(queryString, args.toArray())
        }
    }

    //fun buildQuery(
//        sortColumn: String = "n.is_pin",
//        sortOrder: Int = 1,
//        idCategory: Set<Long> = setOf(-1L),
//        searchText: String = "",
//        searchDate: String = ""
//    ): SimpleSQLiteQuery {


    fun buildQueryArchive(
        sortColumn: String = "n.is_pin",
        sortOrder: Int = 1,
        idCategory: Set<Long> = setOf(-1L),
        searchText: String = ""
    ): SimpleSQLiteQuery {

        val args: ArrayList<Any> = ArrayList()

        var queryString = ""
        queryString += "SELECT * FROM ${ConstantsDbName.NOTE_TABLE_NAME} AS n"

        val conditions: ArrayList<String> = ArrayList()

        if (idCategory.size > 1) {
            //queryString += " INNER JOIN storagenotewithcategoriescrossref AS nc ON n.note_id = nc.note_id "
            queryString += " INNER JOIN storagenotewithcategoriescrossref AS nc USING (note_id) "
            idCategory.forEach { id ->
                if (id != -1L) {
                    conditions.add(" nc.category_id = ? ")
                    args.add(id)
                }
            }
        }

        if (searchText.isNotEmpty()) {
            conditions.add("(${ConstantsDbName.NOTE_TITLE}" + " LIKE ?" + " OR " + ConstantsDbName.NOTE_TEXT + " LIKE ?" + ")")
            val search = "%$searchText%"
            args.add(search)
            args.add(search)
        }

        conditions.add("${ConstantsDbName.NOTE_IS_ARCHIVE} = 1")

        if (conditions.isNotEmpty()) {
            queryString += " WHERE " + conditions.joinToString(" AND ")
        }

        queryString += " ORDER BY "
        queryString += sortColumn

        when (sortOrder) {
            1 -> queryString += " ASC, n.note_last_changed_time DESC"
            0 -> queryString += " DESC, n.note_last_changed_time DESC"
        }

        queryString += ";"

        return if (args.isEmpty()) {
            SimpleSQLiteQuery(queryString)
        } else {
            SimpleSQLiteQuery(queryString, args.toArray())
        }
    }


    fun buildQueryRecycleBin(
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
            queryString += " INNER JOIN storagenotewithcategoriescrossref AS nc ON n.note_id = nc.note_id "
            conditions.add(" nc.category_id = ? ")
            args.add(idCategory)
        }

        if (searchText.isNotEmpty()) {
            conditions.add("(${ConstantsDbName.NOTE_TITLE}" + " LIKE ?" + " OR " + ConstantsDbName.NOTE_TEXT + " LIKE ?" + ")")
            val search = "%$searchText%"
            args.add(search)
            args.add(search)
        }

        conditions.add("${ConstantsDbName.NOTE_IS_DELETED} = 1")

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