package org.freekode.tp2intervals.security

import org.freekode.tp2intervals.model.user.UserEntity
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class CustomUserDetails(
    val id: Int,
    private val username: String,
    private val passwordHash: String?
) : UserDetails {

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> = mutableListOf()

    override fun getPassword(): String = passwordHash ?: ""

    override fun getUsername(): String = username

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true

    companion object {
        fun build(user: UserEntity): CustomUserDetails {
            return CustomUserDetails(
                id = user.id!!,
                username = user.username,
                passwordHash = user.passwordHash
            )
        }
    }
}
