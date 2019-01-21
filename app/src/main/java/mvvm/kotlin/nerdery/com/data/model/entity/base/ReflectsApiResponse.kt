package mvvm.kotlin.nerdery.com.data.model.entity.base

import mvvm.kotlin.nerdery.com.data.model.api.base.ApiResponseObject

/**
 * This binds a contract between the locally stored model and an API response,
 * basically saying "This model has a tight (or loose) coupling to the following API response object"
 *
 * The idea is that locally stored models should NOT be the same model we get back from the API request,
 * this interface defines how they're related
 *
 * Recursive Generics
 * @param X The model that reflects the ApiResponseObject
 * @param T : ApiResponseObject
 */
interface ReflectsApiResponse<X, T : ApiResponseObject> {
    /**
     * Map from the specified ApiResponse and return the instance of yourself
     * @param apiResponse T
     * @return
     */
    fun reflectFrom(apiResponse: T): X
}