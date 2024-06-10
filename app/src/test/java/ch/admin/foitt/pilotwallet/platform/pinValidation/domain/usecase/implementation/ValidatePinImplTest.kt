package ch.admin.foitt.pilotwallet.platform.pinValidation.domain.usecase.implementation

import ch.admin.foitt.pilotwallet.platform.pinInput.domain.model.PinConstraints
import ch.admin.foitt.pilotwallet.platform.pinInput.domain.model.PinValidationState
import ch.admin.foitt.pilotwallet.platform.pinInput.domain.usecase.implementation.ValidatePinImpl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ValidatePinImplTest {

    @Test
    fun `pin should fullfill default pin constraints`() {
        val usecase = ValidatePinImpl(constraints = PinConstraints())
        assertEquals(PinValidationState.INCOMPLETE, usecase.invoke("123"))
        assertEquals(PinValidationState.INCOMPLETE, usecase.invoke(""))
        assertEquals(PinValidationState.INCOMPLETE, usecase.invoke("12345"))

        assertEquals(PinValidationState.VALID, usecase.invoke("123456"))
        assertEquals(PinValidationState.VALID, usecase.invoke("000000"))

        assertEquals(PinValidationState.INVALID, usecase.invoke("aaa"))
        assertEquals(PinValidationState.INVALID, usecase.invoke("1234567"))
        assertEquals(PinValidationState.INVALID, usecase.invoke("123a2"))
        assertEquals(PinValidationState.INVALID, usecase.invoke(" "))
        assertEquals(PinValidationState.INVALID, usecase.invoke("\n"))
    }
}
