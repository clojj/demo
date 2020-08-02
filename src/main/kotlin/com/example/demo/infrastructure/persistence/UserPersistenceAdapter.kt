package com.example.demo.infrastructure.persistence

import arrow.core.Either
import arrow.core.computations.either
import arrow.fx.coroutines.evalOn
import com.example.demo.domain.User
import com.example.demo.domain.UserPersistencePort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactive.awaitFirst
import org.springframework.stereotype.Component

@Component
class UserPersistenceAdapter(private val userRepository: UserRepository) : UserPersistencePort {

    override suspend fun create(user: User): Either<Throwable, Long> =
            either {
                val userData = evalOn(Dispatchers.IO) {!save(user)}
                userData.id ?: throw IllegalArgumentException("id was not generated")
            }

    private suspend fun save(user: User): Either<Throwable, UserData> =
            Either.catch {
                userRepository.save(UserData(null, user.email, user.username)).awaitFirst()
            }

}