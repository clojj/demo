package com.example.demo.infrastructure.persistence

import com.example.demo.domain.User
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : ReactiveCrudRepository<UserData?, Int?>

@Table("registrations")
class UserData(
        @Id
        @Column("id")
        var id: Long?,

        @Column("email")
        var email: String,

        @Column("username")
        var username: String
)

fun UserData.toUser() = User(this.id ?: 0, this.email, this.username)