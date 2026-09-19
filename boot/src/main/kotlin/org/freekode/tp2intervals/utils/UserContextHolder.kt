package org.freekode.tp2intervals.utils

object UserContextHolder {
    private val currentUser = ThreadLocal<String>()

    var username: String
        get() = currentUser.get() ?: "admin"
        set(value) {
            currentUser.set(value)
        }

    fun clear() {
        currentUser.remove()
    }
}
