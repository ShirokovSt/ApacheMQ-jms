package com.example.project4.repository

interface PersonRepository {

    fun addPerson(name: String, lastName: String) : Boolean
}