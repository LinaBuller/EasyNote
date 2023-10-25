package com.example.data.storage.firebase

import com.example.data.storage.DataSource
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class FirebaseStorageDataSource(val storage: FirebaseStorage) : DataSource {
    private val st = storage.getReference(FirebaseConstants.MAIN_NODE)


    fun getRefForImage(path: String,idImage:String): StorageReference =
        st.child(path).child("images").child(idImage)

    fun getRefForDatabase(path: String, name: String): StorageReference =
        st.child(path).child("dump").child(name)


    fun download(uid:String,name: String): StorageReference {
        return st.child(uid).child("dump").child(name)
    }

    fun downloadFromUrl(url:String): StorageReference{
        return storage.getReferenceFromUrl(url)
    }
}