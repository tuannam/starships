package au.com.starships.service

import android.net.Uri
import au.com.starships.rest.RestResponse
import au.com.starships.model.Ship
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.net.UnknownHostException
import javax.inject.Inject

abstract class RestApiResponse<T>(val message: String?, val response: T?)
class RestApiOKResponse<T>(result: T?): RestApiResponse<T>("OK", result)
abstract class RestApiFailedResponse<T>(message: String?): RestApiResponse<T>(message, null)
class RestApiUnknownFailedResponse<T>(message: String?): RestApiFailedResponse<T>(message)
class RestApiNetworkErrorResponse<T>(): RestApiFailedResponse<T>(null)

typealias ApiHandler<T> = (response: RestApiResponse<T>) -> Unit

interface RestApi {
    fun fetchShips(handler: ApiHandler<RestResponse<Ship>>)
    fun fetchAllShips(handler: ApiHandler<RestResponse<Ship>>)
}
class RestApiFacade: RestApi {
    private object HOLDER {
        val INSTANCE = RestApiFacade()
    }

    companion object {
        val instance: RestApi by lazy { HOLDER.INSTANCE }
    }

    @Inject
    lateinit var restService: RestApiService
    private val mCompositeDisposable = CompositeDisposable()

    override fun fetchShips(handler: ApiHandler<RestResponse<Ship>>) {
        val job = restService.fetchShips()
        addJob(job, handler)
    }

    override fun fetchAllShips(handler: ApiHandler<RestResponse<Ship>>) {
        fetchPage(1, handler)
    }

    private fun fetchPage(page: Int, handler: ApiHandler<RestResponse<Ship>>) {
        val job = restService.fetchPage(page)
        addJob(job) {
            if (it is RestApiOKResponse<*>) {
                handler(it)
                it.response?.next?.let { nextUrl ->
                    val uri = Uri.parse(nextUrl)
                    uri.getQueryParameter("page")?.toIntOrNull()?.let { page ->
                        fetchPage(page, handler)
                    }
                }
            }
        }
    }

    private fun <T> addJob(job: Observable<T>, apiHandler: ApiHandler<T>) {
        mCompositeDisposable.add(job
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response -> apiHandler(RestApiOKResponse(response)) },
                { error ->
                    if (error is UnknownHostException) {
                        apiHandler(RestApiNetworkErrorResponse())
                    } else {
                        error.printStackTrace()
                        apiHandler(RestApiUnknownFailedResponse(error.message))
                    }
                }))
    }
}