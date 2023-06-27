package com.easynote.domain.models


data class Image(

    var id: Long = 0L,

    var foreignId:  Long = 0L,

    var uri: String = "",

) {

    constructor(foreignId:  Long, uri: String) : this(id = 0)
    constructor(uri: String) : this(id = 0, foreignId = 0)
    constructor() : this(0,0,"")
}