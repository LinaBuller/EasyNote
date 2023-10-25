package com.example.data.storage

interface EntityMapper<I,O> {
    fun mapToDomain(entity:O):I
    fun mapToStorage(model:I):O
}