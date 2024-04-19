package ch.admin.foitt.pilotwallet.platform.credential.domain.usecase.implementation.mock

internal object JwtTimestamps {
    /*
{
   ...
   "exp":1733414266
   "iat":1733414266
   "nbf":1733414266
   ...
}
     */
    const val VALID_JWT =
        "eyJhbGciOiJIUzI1NiJ9.eyJuYmYiOiIwIiwiZXhwIjoiMjE0NzQ4MzY0NyIsImlhdCI6IjAifQ.LW2IpCm2ADimoltDJaXKeRuUcCJH1-errLdkxPuI4_E"

    const val EXP_INVALID_JWT =
        "eyJhbGciOiJIUzI1NiJ9.eyJuYmYiOiIwIiwiZXhwIjoiMCIsImlhdCI6IjAifQ.J8PtAibl-ymm3vCu9Z2uGPtX7f1wR6MW5RhapjrYiDY"

    const val IAT_INVALID_JWT =
        "eyJhbGciOiJIUzI1NiJ9.eyJuYmYiOiIwIiwiZXhwIjoiMCIsImlhdCI6IjIxNDc0ODM2NDcifQ.XsKCDsA_Al4IdShJK2p7o6eZubWIE8Rpq27HUp2vZAs"

    const val NBF_INVALID_JWT =
        "eyJhbGciOiJIUzI1NiJ9.eyJuYmYiOiIyMTQ3NDgzNjQ3IiwiZXhwIjoiMCIsImlhdCI6IjAifQ.V0ASqN3pBJgP1BO-5mMLdHNcPgJ1PYo1hPJIGgCVS6k"

    const val EXP_MISSING_JWT =
        "eyJhbGciOiJIUzI1NiJ9.eyJuYmYiOiIwIiwiaWF0IjoiMCJ9.T-nmTjjRlBd9_Z8h3NME8nl2qBHuojREojB_K7cnIjs"

    const val IAT_MISSING_JWT =
        "eyJhbGciOiJIUzI1NiJ9.eyJuYmYiOiIwIiwiZXhwIjoiMjE0NzQ4MzY0NyJ9.6Z_kHjcPnL1TiUfUDOgI3wsbv-1CdP-gtOzmX7WA6Ps"

    const val NBF_MISSING_JWT =
        "eyJhbGciOiJIUzI1NiJ9.eyJleHAiOiIyMTQ3NDgzNjQ3IiwiaWF0IjoiMCJ9.D8QAzdMwhYmurLq4_v9p-7mgAOLeO_fipwPvnm-paRs"

    /*
{
    "test": "test"
}
     */
    const val JWT_WITHOUT_TIMESTAMPS =
        "eyJhbGciOiJIUzI1NiJ9.eyJ0ZXN0IjoidGVzdCJ9.7NM6wkxPaLPEqzRtY0vHJxcAgXgUpcAm2Ihsw7-4TYc"
}
