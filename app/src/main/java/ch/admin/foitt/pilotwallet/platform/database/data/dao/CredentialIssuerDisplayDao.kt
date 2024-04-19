package ch.admin.foitt.pilotwallet.platform.database.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialIssuerDisplay
import ch.admin.foitt.pilotwallet.platform.database.domain.model.DisplayLanguage

@Dao
interface CredentialIssuerDisplayDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(credentialIssuerDisplays: Collection<CredentialIssuerDisplay>)

    /**
     * @return a LocalizedIssuer with [credentialId] and a locale starting with [language]. If no locale starting with [language] is found,
     * a LocalizedIssuer with [credentialId] and a locale == [DisplayLanguage.FALLBACK] is returned
     *
     * @param language is a ISO-639 string, e.g. "fr" or "de"
     */
    @Query(
        "SELECT * FROM CredentialIssuerDisplay WHERE credentialId = :credentialId AND " +
            "(locale LIKE :language || '%' " +
            "OR (locale = '" + DisplayLanguage.FALLBACK + "' AND NOT EXISTS " +
            "(SELECT * FROM CredentialIssuerDisplay WHERE credentialId LIKE :credentialId AND locale LIKE :language || '%' ) " +
            "))"
    )
    fun getByCredentialIdAndLanguage(credentialId: Long, language: String): CredentialIssuerDisplay?

    @Query("SELECT * FROM CredentialIssuerDisplay WHERE credentialId = :credentialId")
    fun getByCredentialId(credentialId: Long): List<CredentialIssuerDisplay>
}
