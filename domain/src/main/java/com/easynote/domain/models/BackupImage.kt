package com.easynote.domain.models

class BackupImage(
    var uid: String? = null,
    var id: String? = null,
    var foreignId: Long = 0L,
    var uriStorage: String? = null,
    var uri: String? = null,
    var position: Int = 0
)