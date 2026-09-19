package org.freekode.tp2intervals.model.user

import org.springframework.data.repository.CrudRepository

interface UserRepository : CrudRepository<UserEntity, Int> {
    fun findByUsername(username: String): UserEntity?
    fun findByEmail(email: String): UserEntity?
}
