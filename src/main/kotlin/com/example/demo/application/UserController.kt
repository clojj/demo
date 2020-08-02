package com.example.demo.application

import arrow.core.Either
import com.example.demo.domain.User
import com.example.demo.domain.UserRepo
import com.example.demo.domain.createUser
import com.example.demo.infrastructure.persistence.UserData
import com.example.demo.infrastructure.persistence.UserPersistenceAdapter
import com.example.demo.infrastructure.persistence.UserRepository
import com.example.demo.infrastructure.persistence.toUser
import com.fasterxml.jackson.annotation.JsonRootName
import kotlinx.coroutines.reactive.awaitFirst
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(private val userRepository: UserRepository) {

    @PostMapping("/api/users")
    suspend fun register(@RequestBody registration: RegistrationDto): ResponseEntity<Response> {

        val registerUseCase = object : UserRepo {
            override suspend fun create(user: User): Either<Throwable, User> = Either.catch {
                userRepository.save(UserData(null, user.email, user.username)).awaitFirst().toUser()
            }
        }
        val either = registerUseCase.createUser(User(0, registration.email, registration.username))
//        val either = userPersistenceAdapter.create(User(0, registration.email, registration.username))

        return either.fold({
            ResponseEntity.status(400).body(UserError(it.message ?: it.toString()))
        }) {
            ResponseEntity.status(200).body(UserResponse(UserResponseDto(it)))
        }
    }
}

@JsonRootName("user")
data class RegistrationDto(
        val email: String,
        val username: String
)

sealed class Response

data class UserError(val error: String): Response()

data class UserResponse(val user: UserResponseDto): Response() {
    companion object {
        fun fromDomain(domain: User) = UserResponse(UserResponseDto.fromDomain(domain))
    }
}

data class UserResponseDto(
        val id: Long?
) {
    companion object {
        fun fromDomain(domain: User) = with(domain) {
            UserResponseDto(id = id)
        }
    }
}

