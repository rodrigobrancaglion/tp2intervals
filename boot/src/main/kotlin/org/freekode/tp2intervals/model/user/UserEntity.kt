package org.freekode.tp2intervals.model.user

import jakarta.persistence.*

@Table(name = "users")
@Entity
data class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,

    @Column(nullable = false, unique = true)
    val username: String,

    @Column(name = "password_hash")
    var passwordHash: String? = null,

    @Column(unique = true)
    var email: String? = null
) {
    constructor() : this(null, "", null, null)
}
