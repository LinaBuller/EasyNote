package com.easynote.domain.viewmodels

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.easynote.domain.utils.BackupManagerLocalDatabase
import com.easynote.domain.ConstantsDbName
import com.easynote.domain.models.BackupDatabase
import com.easynote.domain.models.Image
import com.easynote.domain.models.BackupImage
import com.easynote.domain.models.NetworkResult
import com.easynote.domain.models.UserCredential
import com.easynote.domain.usecase.GetPathLocalDatabaseUseCase
import com.easynote.domain.usecase.firebase.GetBackupFromStorageFirebaseUseCase
import com.easynote.domain.usecase.firebase.GetBackupImageFromStorageFirebaseUseCase
import com.easynote.domain.usecase.firebase.GetBackupImageUriFromRealtimeFirebaseUseCase
import com.easynote.domain.usecase.firebase.GetBackupUriFromRealtimeDBFirebaseUseCase
import com.easynote.domain.usecase.firebase.GetCurrentUserUseCase
import com.easynote.domain.usecase.firebase.GetUidUserFirebaseUseCase
import com.easynote.domain.usecase.firebase.LogoutFirebaseUseCase
import com.easynote.domain.usecase.firebase.SendEmailRecoveryFirebaseUseCase
import com.easynote.domain.usecase.firebase.SendEmailVerificationFirebaseUseCase
import com.easynote.domain.usecase.firebase.SetBackupToRealtimeDBFirebaseUseCase
import com.easynote.domain.usecase.firebase.SetBackupToStorageFirebaseUseCase
import com.easynote.domain.usecase.firebase.SetImageFromRealtimeFirebaseUseCase
import com.easynote.domain.usecase.firebase.SetImageFromStorageFirebaseUseCase
import com.easynote.domain.usecase.firebase.SignInWithEmailFirebaseUseCase
import com.easynote.domain.usecase.firebase.SignInWithGoogleFirebaseUseCase
import com.easynote.domain.usecase.firebase.SignUpWithEmailFirebaseUseCase
import com.easynote.domain.usecase.itemsNote.GetAllImagesForBackupUseCase
import com.easynote.domain.utils.ImageManager
import com.example.domain.R
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File


class FirebaseViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val signInWithEmailFirebaseUseCase: SignInWithEmailFirebaseUseCase,
    private val signUpWithEmailFirebaseUseCase: SignUpWithEmailFirebaseUseCase,
    private val sendEmailVerificationFirebaseUseCase: SendEmailVerificationFirebaseUseCase,
    private val sendEmailRecoveryFirebaseUseCase: SendEmailRecoveryFirebaseUseCase,
    private val logoutFirebaseUseCase: LogoutFirebaseUseCase,
    private val signInWithGoogleFirebaseUseCase: SignInWithGoogleFirebaseUseCase,
    private val getPathLocalDatabaseUseCase: GetPathLocalDatabaseUseCase,
    private val setBackupToFirebaseUseCase: SetBackupToStorageFirebaseUseCase,
    private val setBackupToRealtimeDBFirebaseUseCase: SetBackupToRealtimeDBFirebaseUseCase,
    private val getBackupUriFromRealtimeDBFirebaseUseCase: GetBackupUriFromRealtimeDBFirebaseUseCase,
    private val getBackupFromStorageFirebaseUseCase: GetBackupFromStorageFirebaseUseCase,
    private val setImageFromStorageFirebaseUseCase: SetImageFromStorageFirebaseUseCase,
    private val getUidUserFirebaseUseCase: GetUidUserFirebaseUseCase,
    private val getAllImagesForBackupUseCase: GetAllImagesForBackupUseCase,
    private val setImageFromRealtimeFirebaseUseCase: SetImageFromRealtimeFirebaseUseCase,
    private val getBackupImageUriFromRealtimeFirebaseUseCase: GetBackupImageUriFromRealtimeFirebaseUseCase,
    private val getBackupImageFromStorageFirebaseUseCase: GetBackupImageFromStorageFirebaseUseCase,
    val backupManager: BackupManagerLocalDatabase,
    val imageManager: ImageManager
) : BaseViewModel() {


    fun getUidUser(): String? {
        return try {
            getUidUserFirebaseUseCase.execute()
        } catch (e: Exception) {
            null
        }

    }

    fun backupDatabase() {
        viewModelScope.launch {
            if (backupAllImages()) {
                val backup: File? = backupManager.backupDatabase(ConstantsDbName.DATABASE_NAME)
                if (backup != null) {
                    setBackupFileToFirebaseStorage(backup)
                } else {
                    setMessage(R.string.error_not_found_database)
                    Log.d("msg", "Local database file not found")
                }
            } else {
                Log.d("msg", "An error occurred while copying media files")
                postVisibleProgressBar(false)
                setMessage(R.string.error_copy_images)
            }
        }
    }

    private fun setBackupFileToFirebaseStorage(backupFile: File) {
        viewModelScope.launch {
            val uid = getUidUser()
            if (uid != null) {
                setBackupToFirebaseUseCase.execute(backupFile, uid).collect { response ->
                    when (response) {
                        is NetworkResult.Success<*> -> {
                            Log.d("msg", "Local database file recorded in remote storage")
                            response.data?.let {
                                setBackupToRealtimeDBFirebase(it)
                                postVisibleProgressBar(false)
                            }
                        }

                        is NetworkResult.Error -> {
                            response.message?.let {
                                Log.d(
                                    "msg",
                                    "Local database file not recorded to a remote storage, error: $it"
                                )
                                postVisibleProgressBar(false)
                            }
                        }

                        is NetworkResult.Loading -> {
                            Log.d("msg", "Local database file recording to a remote storage")
                            postVisibleProgressBar(true)
                        }
                    }
                }
            } else {
                setMessage(R.string.error_not_auth)
                Log.d("msg", "Not auth")
                postVisibleProgressBar(false)
            }
        }
    }

    private fun setBackupToRealtimeDBFirebase(database: BackupDatabase) {
        viewModelScope.launch {
            setBackupToRealtimeDBFirebaseUseCase.execute(database).collect { response ->
                when (response) {
                    is NetworkResult.Success<*> -> {
                        response.data?.let {
                            Log.d("msg", "Save local database file to realtime database completed")
                            postVisibleProgressBar(false)
                        }
                    }

                    is NetworkResult.Error -> {
                        response.message?.let {
                            Log.d(
                                "msg",
                                "Save local database file to realtime database not completed, error: $it"
                            )
                            postVisibleProgressBar(false)
                        }
                    }

                    is NetworkResult.Loading -> {
                        setVisibleProgressBar(true)
                        Log.d("msg", "Loading local database file to realtime database")
                    }
                }
            }
        }
    }

    fun restoreBackup() {
        viewModelScope.launch {
            if (readAllImages()) {
                viewModelScope.launch {
                    val uid = getUidUser()
                    if (uid != null) {
                        getBackupUriFromRealtimeDBFirebaseUseCase.execute(uid).collect { response ->
                            when (response) {
                                is NetworkResult.Success<*> -> {
                                    response.data?.let { database ->
                                        Log.d("msg", "Read backup from realtime database completed")
                                        getBackupFromStorageFirebase(database)
                                        postVisibleProgressBar(false)
                                    }
                                }

                                is NetworkResult.Error -> {
                                    response.message?.let {
                                        Log.d(
                                            "msg",
                                            "Read backup to realtime database not completed, error: $it"
                                        )
                                        postVisibleProgressBar(false)
                                    }
                                }

                                is NetworkResult.Loading -> {
                                    setVisibleProgressBar(true)
                                    Log.d("msg", "Reading backup to realtime database")
                                }
                            }
                        }
                    }
                }
            } else {
                postVisibleProgressBar(false)
            }
        }
    }

    private fun getBackupFromStorageFirebase(backupDatabase: BackupDatabase) {
        viewModelScope.launch(Dispatchers.IO) {
            getBackupFromStorageFirebaseUseCase.execute(backupDatabase).collect { response ->
                when (response) {
                    is NetworkResult.Success<*> -> {
                        response.data?.let { stream ->
                            val dbFile = backupManager.createDBTempFile()
                            val tempFile = backupManager.loadTempFile(stream, dbFile)
                            if (tempFile != null) {
                                Log.d("msg", "Temp file written")
                                restoreFileDB(tempFile)
                            } else {
                                Log.d("msg", "Temp file not written")
                            }
                            postVisibleProgressBar(false)
                        }
                    }

                    is NetworkResult.Error -> {
                        response.message?.let {
                            Log.d("msg", "File backup downloaded unsuccessfully $it")
                            postVisibleProgressBar(false)
                        }
                    }

                    is NetworkResult.Loading -> {
                        postVisibleProgressBar(true)
                        Log.d("msg", "File backup downloading")
                    }
                }
            }
        }

    }

    private fun restoreFileDB(tempFile: File) {
        val pathLocalDatabase = getPathLocalDatabaseUseCase.execute()
        if (pathLocalDatabase != null) {
            backupManager.restoreTempFileToDatabase(pathLocalDatabase, tempFile)
            Log.d("msg", "File backup restore successfully")
            postVisibleProgressBar(false)
            backupManager.restartApp()
        }
    }

    private var _user = MutableLiveData<FirebaseUser?>(getCurrentUser())
    val user: LiveData<FirebaseUser?> = _user
    fun setUser(currentUser: FirebaseUser?) {
        _user.postValue(currentUser)
    }

    private fun getCurrentUser() = getCurrentUserUseCase.execute()

    fun onSignInWithEmail(userCredential: UserCredential) {
        viewModelScope.launch {
            signInWithEmailFirebaseUseCase.execute(userCredential).collect { response ->
                when (response) {
                    is NetworkResult.Success<*> -> {
                        response.data?.let { user ->
                            setUser(user)
                            setVisibleProgressBar(false)
                            setMessage(R.string.sign_in_successful)
                        }
                    }

                    is NetworkResult.Error -> {
                        response.message?.let {
                            setVisibleProgressBar(false)
                            setMessage(R.string.sign_in_failure)
                        }
                    }

                    is NetworkResult.Loading -> {
                        setVisibleProgressBar(true)
                    }
                }
            }
        }
    }

    fun onSignUpWithEmail(userCredential: UserCredential) {
        viewModelScope.launch {
            signUpWithEmailFirebaseUseCase.execute(userCredential).collect { response ->
                when (response) {
                    is NetworkResult.Success<*> -> {
                        response.data?.let { user ->
                            sendEmailVerification(user)
                            setUser(user)
                            setVisibleProgressBar(false)
                        }
                    }

                    is NetworkResult.Error -> {
                        response.message?.let {
                            setVisibleProgressBar(false)
                            setMessage(R.string.sign_up_failure)
                        }
                    }

                    is NetworkResult.Loading -> {
                        setVisibleProgressBar(true)
                    }
                }
            }
        }
    }

    private fun sendEmailVerification(user: FirebaseUser) {
        viewModelScope.launch {
            sendEmailVerificationFirebaseUseCase.execute(user).collect { response ->
                when (response) {
                    is NetworkResult.Success<*> -> {
                        response.data?.let {
                            setVisibleProgressBar(false)
                            setMessage(R.string.sign_up_successful)
                        }
                    }

                    is NetworkResult.Error -> {
                        response.message?.let {
                            setVisibleProgressBar(false)
                            setMessage(R.string.sign_up_failure)
                        }
                    }

                    is NetworkResult.Loading -> {
                        setVisibleProgressBar(true)
                    }
                }
            }
        }
    }

    fun sendEmailRecovery(email: String) {
        viewModelScope.launch {
            sendEmailRecoveryFirebaseUseCase.execute(email).collect { response ->
                when (response) {
                    is NetworkResult.Success<*> -> {
                        response.data?.let {
                            setMessage(R.string.send_reset_email_successful)
                            setVisibleProgressBar(false)
                        }
                    }

                    is NetworkResult.Error -> {
                        response.message?.let {
                            setVisibleProgressBar(false)
                            setMessage(R.string.send_reset_email_failure)
                        }
                    }

                    is NetworkResult.Loading -> {
                        setVisibleProgressBar(true)
                    }
                }
            }
        }
    }

    fun logoutFirebase() {
        viewModelScope.launch {
            logoutFirebaseUseCase.execute().collect { response ->
                when (response) {
                    is NetworkResult.Success<*> -> {
                        response.data?.let {
                            setUser(null)
                            postVisibleProgressBar(false)
                            setMessage(R.string.log_out_successful)
                        }
                    }

                    is NetworkResult.Error -> {}

                    is NetworkResult.Loading -> {
                        setVisibleProgressBar(true)
                    }
                }
            }
        }
    }

    fun onSignInWithGoogle(task: Task<GoogleSignInAccount>) {
        viewModelScope.launch {
            signInWithGoogleFirebaseUseCase.execute(task).collect { response ->
                when (response) {
                    is NetworkResult.Success<*> -> {
                        response.data?.let { user ->
                            setUser(user)
                            setVisibleProgressBar(false)
                            setMessage(R.string.sign_in_successful)
                        }
                    }

                    is NetworkResult.Error -> {
                        response.message?.let {
                            setVisibleProgressBar(false)
                            setMessage(R.string.sign_in_failure)
                        }
                    }

                    is NetworkResult.Loading -> {
                        setVisibleProgressBar(true)
                    }
                }
            }
        }
    }

    private suspend fun setImageFromStorage(bitMap: Bitmap, image: Image): Boolean {
        val isDone = viewModelScope.async(Dispatchers.IO) {
            val prepareImage = prepareImageToByteArray(bitMap)
            Log.d("msg", "Image file ${image.id} prepare")
            val uid = getUidUser()
            var isDoneDownload = false
            if (uid != null) {
                setImageFromStorageFirebaseUseCase.execute(prepareImage, uid, image.id)
                    .collect { response ->
                        when (response) {
                            is NetworkResult.Success<*> -> {
                                response.data?.let { uri ->
                                    val backupImage = BackupImage(
                                        uid = uid,
                                        id = image.id,
                                        uri = image.uri,
                                        foreignId = image.foreignId,
                                        uriStorage = uri.toString(),
                                        position = image.position
                                    )
                                    isDoneDownload = setImageFromRealtimeDBFirebase(backupImage)
                                    Log.d("msg", "Image file ${image.id} upload successfully")
                                }
                            }

                            is NetworkResult.Error -> {
                                response.message?.let {
                                    setVisibleProgressBar(false)
                                    Log.d("msg", "Image file ${image.id} not upload successfully")
                                }
                            }

                            is NetworkResult.Loading -> {
                                Log.d("msg", "Image file ${image.id} uploading")
                            }
                        }
                    }
            }
            isDoneDownload
        }
        return isDone.await()
    }

    private suspend fun setImageFromRealtimeDBFirebase(backupImage: BackupImage): Boolean {
        val isDone = viewModelScope.async(Dispatchers.IO) {
            var isDownload = false
            setImageFromRealtimeFirebaseUseCase.execute(backupImage).collect { response ->
                when (response) {
                    is NetworkResult.Success<*> -> {
                        response.data?.let { isUpload ->
                            isDownload = isUpload
                            postVisibleProgressBar(false)
                            Log.d("msg", "Image file upload realtime database successfully")
                        }
                    }

                    is NetworkResult.Error -> {
                        response.message?.let {
                            postVisibleProgressBar(false)
                            Log.d("msg", "Image file  not upload to realtime database")
                        }
                    }

                    is NetworkResult.Loading -> {
                        Log.d("msg", "Image file uploading to realtime database")
                    }
                }
            }
            isDownload
        }
        return isDone.await()
    }

    private fun prepareImageToByteArray(bitMap: Bitmap): ByteArray {
        ByteArrayOutputStream().use {
            bitMap.compress(Bitmap.CompressFormat.JPEG, 85, it)
            return it.toByteArray()
        }
    }

    private suspend fun backupAllImages(): Boolean {
        val isDone = viewModelScope.async(Dispatchers.IO) {
            postVisibleProgressBar(true)
            val listImage = getAllImagesForBackupUseCase.execute()
            Log.d("msg", "${listImage.size} images will coping into remote database")

            val listIsDone = arrayListOf<Boolean>()

            listImage.forEach { image ->

                val bitmap = imageManager.uriToBitmap(image)
                if (bitmap != null) {
                    Log.d("msg", "bitmap != null")
                    val isDone = setImageFromStorage(bitmap, image)
                    if (isDone) listIsDone.add(isDone)
                } else {
                    Log.d("msg", "bitmap == null")
                    postVisibleProgressBar(false)
                }
            }
            listIsDone.size == listImage.size
        }
        return isDone.await()
    }

    private suspend fun readAllImages(): Boolean {
        val isDone = viewModelScope.async(Dispatchers.IO) {
            val uid = getUidUser()
            if (uid != null) {

                var isDoneDownload = false
                getBackupImageUriFromRealtimeFirebaseUseCase.execute(uid).collect { response ->

                    when (response) {
                        is NetworkResult.Success<*> -> {
                            response.data?.let { images ->
                                Log.d("msg", "Read backup from realtime database completed")
                                isDoneDownload = getBackupImagesFromStorageFirebase(images)
                            }
                        }

                        is NetworkResult.Error -> {
                            response.message?.let {
                                Log.d(
                                    "msg",
                                    "Read backup to realtime database not completed, error: $it"
                                )
                                isDoneDownload = true
                                postVisibleProgressBar(false)
                            }
                        }

                        is NetworkResult.Loading -> {
                            Log.d("msg", "Loading backup to realtime database")
                            postVisibleProgressBar(true)
                        }
                    }

                }
                isDoneDownload
            } else {
                false
            }

        }
        return isDone.await()
    }

    private suspend fun getBackupImagesFromStorageFirebase(images: List<BackupImage>): Boolean {
        val isDone = viewModelScope.async(Dispatchers.IO) {
            images.forEach { image ->
                val idImage = image.id
                getBackupImageFromStorageFirebaseUseCase.execute(image).collect { response ->

                    when (response) {
                        is NetworkResult.Success<*> -> {
                            response.data?.let { stream ->
                                val dbFile = idImage?.let { imageManager.createImageTempFile(it) }
                                val tempFile = dbFile?.let { imageManager.loadTempFile(stream, it) }
                                if (tempFile != null) {
                                    Log.d("msg", "Temp file $idImage written")

                                } else {
                                    Log.d("msg", "Temp file $idImage not written")
                                }
                            }
                        }

                        is NetworkResult.Error -> {
                            response.message?.let {
                                Log.d("msg", "Error backup $idImage from storage")
                            }
                        }

                        is NetworkResult.Loading -> {
                            Log.d("msg", "Loading backup $idImage from storage")
                        }
                    }
                }

                Log.d("msg", "File $idImage successfully")
            }
            Log.d("msg", "File backup  all images restore successfully")
            postVisibleProgressBar(false)
            true
        }
        return isDone.await()
    }

}