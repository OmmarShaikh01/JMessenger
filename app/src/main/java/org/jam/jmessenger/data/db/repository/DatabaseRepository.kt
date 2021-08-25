package org.jam.jmessenger.data.db.repository

import org.jam.jmessenger.data.db.entity.User
import org.jam.jmessenger.data.db.remote.FirebaseDatabaseSource

class DatabaseRepository {
    private val firebaseDatabaseService = FirebaseDatabaseSource()

    // Create Functions ----------------------------------------------------------------------------
    fun createNewUser(user: User) {
        firebaseDatabaseService.createNewUser(user)
    }

    // Update Functions ----------------------------------------------------------------------------
    // Select Functions ----------------------------------------------------------------------------
    // Delete Functions ----------------------------------------------------------------------------
}