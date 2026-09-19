package org.freekode.tp2intervals.config

import jakarta.annotation.PostConstruct
import org.freekode.tp2intervals.config.log.AppLogger
import org.freekode.tp2intervals.model.user.UserEntity
import org.freekode.tp2intervals.model.user.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class DefaultUserInitializer(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
     private val logger = AppLogger.get(this.javaClass)

    @PostConstruct
    fun init() {
        if (userRepository.findByUsername("admin") == null) {
            logger.infoL3In("Default user 'admin' not found. Creating it.")
            val admin = UserEntity(
                username = "admin",
                passwordHash = passwordEncoder.encode("admin"),
                email = "admin@tp2intervals.local"
            )
            userRepository.save(admin)
        }
    }
}
