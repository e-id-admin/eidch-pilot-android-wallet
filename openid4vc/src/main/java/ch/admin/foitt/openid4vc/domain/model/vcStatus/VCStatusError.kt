package ch.admin.foitt.openid4vc.domain.model.vcStatus

import java.io.IOException
import java.util.zip.ZipException

sealed interface VCStatusError {
    object NetworkError : VCStatusError
    object CertificateNotPinnedError : VCStatusError
    data class Unexpected(val throwable: Throwable) : VCStatusError
}

sealed interface FetchVCStatusError {
    object NetworkError : FetchVCStatusError
    object CertificateNotPinnedError : FetchVCStatusError
    object UnsupportedStatusListFormat : FetchVCStatusError
    data class Unexpected(val throwable: Throwable) : FetchVCStatusError
}

sealed interface HandleStatusList2021EntryError {
    object NetworkError : HandleStatusList2021EntryError
    object CertificateNotPinnedError : HandleStatusList2021EntryError
    data class Unexpected(val throwable: Throwable) : HandleStatusList2021EntryError
}

sealed interface PrepareStatusListError {
    object NetworkError : PrepareStatusListError
    object CertificateNotPinnedError : PrepareStatusListError
    data class Unexpected(val throwable: Throwable) : PrepareStatusListError
}

fun PrepareStatusListError.toHandleStatusList2021EntryError(): HandleStatusList2021EntryError = when (this) {
    PrepareStatusListError.NetworkError -> HandleStatusList2021EntryError.NetworkError
    PrepareStatusListError.CertificateNotPinnedError -> HandleStatusList2021EntryError.CertificateNotPinnedError
    is PrepareStatusListError.Unexpected -> HandleStatusList2021EntryError.Unexpected(this.throwable)
}

fun HandleStatusList2021EntryError.toFetchVCStatusError(): FetchVCStatusError = when (this) {
    HandleStatusList2021EntryError.NetworkError -> FetchVCStatusError.NetworkError
    HandleStatusList2021EntryError.CertificateNotPinnedError -> FetchVCStatusError.CertificateNotPinnedError
    is HandleStatusList2021EntryError.Unexpected -> FetchVCStatusError.Unexpected(this.throwable)
}

fun VCStatusError.toPrepareStatusListError(): PrepareStatusListError = when (this) {
    VCStatusError.NetworkError -> PrepareStatusListError.NetworkError
    VCStatusError.CertificateNotPinnedError -> PrepareStatusListError.CertificateNotPinnedError
    is VCStatusError.Unexpected -> PrepareStatusListError.Unexpected(this.throwable)
}

fun Throwable.toPrepareStatusListError() = when (this) {
    is IllegalArgumentException, is ZipException, is IOException -> PrepareStatusListError.Unexpected(this)
    else -> PrepareStatusListError.Unexpected(this)
}
