package au.com.starships.service

import au.com.starships.rest.RestResponse
import au.com.starships.model.Ship
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RestApiService {
    @GET("api/starships")
    fun fetchShips(): Observable<RestResponse<Ship>>

    @GET("api/starships/")
    fun fetchPage(@Query("page") page: Int): Observable<RestResponse<Ship>>
}