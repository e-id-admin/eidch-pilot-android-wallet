package ch.admin.foitt.pilotwallet.platform.credential.domain.usecase.implementation

import ch.admin.foitt.pilotwallet.platform.credential.domain.usecase.VerifyJwtTimestamps
import ch.admin.foitt.pilotwallet.platform.credential.domain.usecase.implementation.mock.JwtTimestamps
import ch.admin.foitt.pilotwallet.util.assertOk
import com.nimbusds.jose.util.Base64URL
import com.nimbusds.jwt.SignedJWT
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class VerifyJwtTimestampsTest {

    private lateinit var verifyJwtTimestamps: VerifyJwtTimestamps

    @Before
    fun setUp() {
        verifyJwtTimestamps = VerifyJwtTimestampsImpl()
    }

    @Test
    fun `valid timestamps returns true`() = runTest {
        val signedJwt = createSignedJwt(JwtTimestamps.VALID_JWT)
        val result = verifyJwtTimestamps(signedJwt).assertOk()

        assertTrue(result)
    }

    @Test
    fun `invalid exp timestamp returns false`() = runTest {
        val signedJwt = createSignedJwt(JwtTimestamps.EXP_INVALID_JWT)
        val result = verifyJwtTimestamps(signedJwt).assertOk()

        assertFalse(result)
    }

    @Test
    fun `invalid iat timestamp returns false`() = runTest {
        val signedJwt = createSignedJwt(JwtTimestamps.IAT_INVALID_JWT)
        val result = verifyJwtTimestamps(signedJwt).assertOk()

        assertFalse(result)
    }

    @Test
    fun `invalid nbf timestamp returns false`() = runTest {
        val signedJwt = createSignedJwt(JwtTimestamps.NBF_INVALID_JWT)
        val result = verifyJwtTimestamps(signedJwt).assertOk()

        assertFalse(result)
    }

    @Test
    fun `missing exp returns true`() = runTest {
        val signedJwt = createSignedJwt(JwtTimestamps.EXP_MISSING_JWT)
        val result = verifyJwtTimestamps(signedJwt).assertOk()

        assertTrue(result)
    }

    @Test
    fun `missing iat returns true`() = runTest {
        val signedJwt = createSignedJwt(JwtTimestamps.IAT_MISSING_JWT)
        val result = verifyJwtTimestamps(signedJwt).assertOk()

        assertTrue(result)
    }

    @Test
    fun `missing nbf returns true`() = runTest {
        val signedJwt = createSignedJwt(JwtTimestamps.NBF_MISSING_JWT)
        val result = verifyJwtTimestamps(signedJwt).assertOk()

        assertTrue(result)
    }

    @Test
    fun `no timestamps provided returns true`() = runTest {
        val signedJwt = createSignedJwt(JwtTimestamps.JWT_WITHOUT_TIMESTAMPS)
        val result = verifyJwtTimestamps(signedJwt).assertOk()

        assertTrue(result)
    }

    private fun createSignedJwt(jwt: String): SignedJWT {
        val parts = jwt.split(".").map {
            Base64URL.from(it)
        }
        return SignedJWT(parts[0], parts[1], parts[2])
    }
}
