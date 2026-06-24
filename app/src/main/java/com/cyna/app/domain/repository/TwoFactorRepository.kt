package com.cyna.app.domain.repository

import com.cyna.app.domain.model.TwoFactorSetup

interface TwoFactorRepository {
    suspend fun setup(): TwoFactorSetup
    suspend fun confirm(totpCode: String)
}