package ch.admin.foitt.pilotwallet.platform.pinValidation.domain.usecase.implementation

import ch.admin.foitt.pilotwallet.platform.pinInput.domain.model.PinConstraints
import ch.admin.foitt.pilotwallet.platform.pinInput.domain.model.PinValidationState
import ch.admin.foitt.pilotwallet.platform.pinInput.domain.usecase.implementation.ValidatePinImpl
import org.junit.Assert
import org.junit.Test

class ValidatePinImplTest {

    @Test
    fun `pin should fullfill default pin constraints`() {
        val usecase = ValidatePinImpl(constraints = PinConstraints())
        Assert.assertEquals(PinValidationState.INCOMPLETE, usecase.invoke("123"))
        Assert.assertEquals(PinValidationState.INCOMPLETE, usecase.invoke(""))
        Assert.assertEquals(PinValidationState.INCOMPLETE, usecase.invoke("12345"))

        Assert.assertEquals(PinValidationState.VALID, usecase.invoke("123456"))
        Assert.assertEquals(PinValidationState.VALID, usecase.invoke("000000"))

        Assert.assertEquals(PinValidationState.INVALID, usecase.invoke("aaa"))
        Assert.assertEquals(PinValidationState.INVALID, usecase.invoke("1234567"))
        Assert.assertEquals(PinValidationState.INVALID, usecase.invoke("123a2"))
        Assert.assertEquals(PinValidationState.INVALID, usecase.invoke(" "))
        Assert.assertEquals(PinValidationState.INVALID, usecase.invoke("\n"))
    }
}
