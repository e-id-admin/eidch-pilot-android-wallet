package ch.admin.foitt.pilotwallet.platform.database.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialClaimDisplay
import ch.admin.foitt.pilotwallet.platform.database.domain.model.DisplayLanguage

@Dao
interface CredentialClaimDisplayDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(credentialClaimDisplays: Collection<CredentialClaimDisplay>)

    /**
     * @return a LocalizedDisplayClaim for [claimId] and a locale starting with [language]. If no locale starting with [language] is found,
     * a LocalizedDisplayClaim with [claimId] and a locale == [DisplayLanguage.FALLBACK] is returned
     *
     * @param language is a ISO-639 string, e.g. "fr" or "de"
     */
    @Query(
        "SELECT * FROM CredentialClaimDisplay WHERE claimId = :claimId AND " +
            "(locale LIKE :language || '%' " +
            "OR (locale = '" + DisplayLanguage.FALLBACK + "' AND NOT EXISTS " +
            "(SELECT * FROM CredentialClaimDisplay WHERE claimId LIKE :claimId AND locale LIKE :language || '%' ) " +
            "))"
    )
    fun getByClaimIdAndLanguage(claimId: Long, language: String): CredentialClaimDisplay?

    @Query("SELECT * FROM CredentialClaimDisplay WHERE claimId = :claimId")
    fun getByClaimId(claimId: Long): List<CredentialClaimDisplay>
}
