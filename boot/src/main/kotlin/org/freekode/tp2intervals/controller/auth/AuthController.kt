package org.freekode.tp2intervals.controller.auth

import org.freekode.tp2intervals.dto.ErrorResponse
import org.freekode.tp2intervals.model.user.UserEntity
import org.freekode.tp2intervals.model.user.UserRepository
import org.freekode.tp2intervals.security.CustomUserDetails
import org.freekode.tp2intervals.security.JwtUtils
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authenticationManager: AuthenticationManager,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtUtils: JwtUtils,
    private val configurationCrudRepository: org.freekode.tp2intervals.integration.provider.configuration.IConfigurationCrudRepository,
    private val scheduleRequestRepository: org.freekode.tp2intervals.integration.provider.schedule.IScheduleRequestRepository,
    private val cacheManager: org.springframework.cache.CacheManager
) {

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<Any> {
        return try {
            val authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(request.username, request.password)
            )
            SecurityContextHolder.getContext().authentication = authentication
            val jwt = jwtUtils.generateJwtToken(authentication)
            val userDetails = authentication.principal as CustomUserDetails

            ResponseEntity.ok(LoginResponse(jwt, userDetails.id, userDetails.username))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(ErrorResponse("Invalid username or password"))
        }
    }

    @PostMapping("/register")
    fun register(@RequestBody request: RegisterRequest): ResponseEntity<Any> {
        return try {
            if (userRepository.findByUsername(request.username) != null) {
                return ResponseEntity.badRequest().body(ErrorResponse("Username already exists"))
            }
            if (!request.email.isNullOrBlank() && userRepository.findByEmail(request.email) != null) {
                return ResponseEntity.badRequest().body(ErrorResponse("Email already exists"))
            }

            val user = UserEntity(
                username = request.username,
                email = request.email,
                passwordHash = passwordEncoder.encode(request.password)
            )
            userRepository.save(user)

            // Auto-login after registration
            val authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(request.username, request.password)
            )
            val jwt = jwtUtils.generateJwtToken(authentication)
            val userDetails = authentication.principal as CustomUserDetails

            ResponseEntity.ok(LoginResponse(jwt, userDetails.id, userDetails.username))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(ErrorResponse(e.message ?: "Registration failed"))
        }
    }

    @org.springframework.web.bind.annotation.DeleteMapping("/user")
    @org.springframework.transaction.annotation.Transactional
    fun deleteCurrentUser(): ResponseEntity<Any> {
        return try {
            val username = org.freekode.tp2intervals.utils.UserContextHolder.username
            val user = userRepository.findByUsername(username)
            if (user != null) {
                configurationCrudRepository.deleteByUsername(username)
                scheduleRequestRepository.deleteByUsername(username)
                cacheManager.cacheNames.forEach { cacheName ->
                    cacheManager.getCache(cacheName)?.clear()
                }
                userRepository.delete(user)
                ResponseEntity.ok(mapOf("message" to "User deleted successfully"))
            } else {
                ResponseEntity.badRequest().body(ErrorResponse("User not found"))
            }
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(ErrorResponse(e.message ?: "Failed to delete user"))
        }
    }
}

data class LoginRequest(val username: String, val password: String)
data class RegisterRequest(val username: String, val email: String? = null, val password: String)
data class LoginResponse(val token: String, val id: Int, val username: String)
