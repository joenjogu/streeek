package com.bizilabs.streeek.lib.domain.models

import kotlinx.datetime.LocalDateTime

data class AccountDomain(
    val id: String,
    val githubId: Int,
    val username: String,
    val email: String,
    val bio: String,
    val avatarUrl: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
