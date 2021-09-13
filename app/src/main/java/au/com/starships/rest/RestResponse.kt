package au.com.starships.rest

data class RestResponse<T>(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<T>
)

