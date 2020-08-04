package com.example.demo.domain

import arrow.core.Either
import arrow.core.computations.either
import arrow.fx.coroutines.evalOn
import kotlinx.coroutines.Dispatchers

interface UserPersistencePort {
    suspend fun create(user: User): Either<Throwable, Long>
}

interface UserRepo {
    suspend fun create(user: User): Either<Throwable, User>
}

suspend fun <R> R.createUser(user: User): Either<Throwable, Long> where R : UserRepo =
        either {
            val newUser = evalOn(Dispatchers.IO) {!create(user)}
            newUser.id
        }