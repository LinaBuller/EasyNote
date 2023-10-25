package com.example.data.storage.firebase

import com.example.data.storage.DataSource
import com.example.data.storage.firebase.models.FirebaseDump
import com.example.data.storage.firebase.models.FirebaseImage
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase

class FirebaseDatabaseDataSource(private val database: FirebaseDatabase) : DataSource {

    private val db = database.getReference(FirebaseConstants.MAIN_NODE)

    fun insertDump(firebaseDump: FirebaseDump): Task<Void> {
        return db.child(firebaseDump.uid!!).child("dump").setValue(firebaseDump)
    }

    fun readDump(uid: String): Task<DataSnapshot> {
        return db.child(uid).child("dump").get()
    }

    fun insertImage(image: FirebaseImage): Task<Void> {
        return db.child(image.uid!!).child("images").child(image.id.toString()).setValue(image)
    }

    fun readImage(uid: String): Task<DataSnapshot>{
        return db.child(uid).child("images").get()
    }
}