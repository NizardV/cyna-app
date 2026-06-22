package com.cyna.app.data.repository

import com.cyna.app.data.dto.TwoFactorSetupDto
import com.cyna.app.data.remote.TwoFactorAPI
import com.cyna.app.ui.screens.auth.security2fa.TwoFactorRepository

internal class TwoFactorRepositoryImpl(
    private val twoFactorAPI: TwoFactorAPI
) : TwoFactorRepository {

    override suspend fun setup(): TwoFactorSetupDto =
        twoFactorAPI.setup()

    override suspend fun confirm(totpCode: String) {
        twoFactorAPI.confirm(totpCode)
    }
}