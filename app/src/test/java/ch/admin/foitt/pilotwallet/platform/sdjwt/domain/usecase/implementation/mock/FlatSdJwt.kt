package ch.admin.foitt.pilotwallet.platform.sdjwt.domain.usecase.implementation.mock

internal object FlatSdJwt {
    /*
{
   "_sd":[
      "YRLf606clwt4-hjyGze49ySFi6VCmwb9n5hwb4VUJSY",
      "QhuvIMQd5LyX8gOR3weVzSY0yGZGGHdVXY0E-NhhUfw"
   ],
   "_sd_alg":"sha-256"
}
     */

    const val JSON = """{"test_key_1":"test_value_1", "test_key_2":"test_value_2"}"""

    const val JWT =
        "eyJhbGciOiJFUzUxMiIsInR5cCI6IkpXVCJ9.eyJfc2QiOlsiWVJMZjYwNmNsd3Q0LWhqeUd6ZTQ5eVNGaTZWQ213YjluNWh3YjRWVUpTWSIsIlFodXZJTVFkNUx5WDhnT1Izd2VWelNZMHlHWkdHSGRWWFkwRS1OaGhVZnciXSwiX3NkX2FsZyI6InNoYS0yNTYiLCJpYXQiOjE2OTc4MDY2NzF9.APiUhTXMW6pro6Y_-aLQA120nUWK9liwf7FVCsLjiW7uKYHmjCDG3V2KGEwsjyTMjXmNWEwsamw7af-DfaCzjrOyABbG7KRfhLewJOK4UeviVbM7o8a4g0OmwzbXEFXjBVC75nY067BLvid_p6FwTxDIt9acmJtE1zW6u-HuXMFiTxPE"
}
