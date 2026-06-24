package com.cyna.app.data.repository

import com.cyna.app.data.dto.TwoFactorSetupDto
import com.cyna.app.data.remote.TwoFactorAPI
import com.cyna.app.domain.model.TwoFactorSetup
import com.cyna.app.domain.repository.TwoFactorRepository

internal class TwoFactorRepositoryImpl(
    private val twoFactorAPI: TwoFactorAPI
) : TwoFactorRepository {

    override suspend fun setup(): TwoFactorSetup {
        return twoFactorAPI.setup().let { dto: TwoFactorSetupDto ->
            TwoFactorSetup(
                secret = dto.secret,
                otpAuthUrl = dto.otpAuthUrl
            )
        }
    }

    override suspend fun confirm(totpCode: String) {
        twoFactorAPI.confirm(totpCode)
    }
}