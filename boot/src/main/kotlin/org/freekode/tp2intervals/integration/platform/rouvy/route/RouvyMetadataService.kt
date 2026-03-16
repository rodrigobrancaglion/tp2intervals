package org.freekode.tp2intervals.integration.platform.rouvy.route

import org.freekode.tp2intervals.integration.platform.rouvy.RouvyAuthClient
import org.freekode.tp2intervals.integration.platform.rouvy.RouvyFirestoreClient
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

/**
 * Service responsible for fetching Rouvy route metadata from Firebase Firestore.
 *
 * NOTE: This implementation is incomplete pending Rouvy opening their public API.
 * The Firebase refreshToken flow is not supported outside the Rouvy web app context.
 * The idToken approach works but requires manual renewal every hour.
 *
 * Firebase project: virtualtraining-prod
 * Firestore path: /users/{uid}/rideResults/{activityId}
 * Relevant field: routeName (stringValue)
 */
@Service
class RouvyMetadataService(
    private val firestoreClient: RouvyFirestoreClient,
    private val authClient: RouvyAuthClient,
    @Value("\${app.rouvy.firebase.api-key:}") private val apiKey: String,
    @Value("\${app.rouvy.firebase.refresh-token:}") private val refreshToken: String,
    @Value("\${app.rouvy.firebase.project-id:virtualtraining-prod}") private val projectId: String,
    @Value("\${app.rouvy.firebase.uid:}") private val uid: String
) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    /**
     * Attempts to renew the Firebase JWT using the refresh token.
     * NOTE: This currently fails because Rouvy restricts token refresh to their web app origin.
     */
    private fun getFreshIdToken(): String? {
        return try {
            val cleanRefreshToken = refreshToken.trim()
            val formBody = "grant_type=refresh_token&refresh_token=$cleanRefreshToken"
            val response = authClient.refreshToken(apiKey, formBody)
            val newToken = response["id_token"] as? String ?: response["access_token"] as? String
            if (newToken != null) "Bearer $newToken" else null
        } catch (e: Exception) {
            log.error("Failed to refresh Rouvy token: ${e.message}")
            null
        }
    }

    /**
     * Fetches the real route name from Rouvy Firestore.
     * Returns null if the API is unavailable or the token is expired.
     *
     * @param fileName the .fit filename, e.g. "260319164706-96942331.fit.gz"
     * @return route name e.g. "Challenge Sanremo | Italy", or null
     */
    fun getRouteName(fileName: String): String? {
        if (!fileName.contains("-")) return null

        val authHeader = getFreshIdToken() ?: return null
        val rouvyActivityId = fileName.removeSuffix(".gz").removeSuffix(".fit")
        log.info("Fetching Rouvy route for activityId: $rouvyActivityId")

        return try {
            val response = firestoreClient.getActivity(
                projectId = projectId,
                uid = uid,
                activityId = rouvyActivityId,
                token = authHeader
            )

            // Firestore returns: { fields: { routeName: { stringValue: "..." } } }
            val fields = response["fields"] as? Map<*, *>
            val routeNameData = fields?.get("routeName") as? Map<*, *>
            val routeName = routeNameData?.get("stringValue") as? String

            if (!routeName.isNullOrBlank()) {
                log.info("Rouvy route found: $routeName")
                routeName
            } else {
                log.warn("Route name empty for activityId: $rouvyActivityId")
                null
            }
        } catch (e: Exception) {
            log.warn("Rouvy route not found for activityId: $rouvyActivityId — ${e.message}")
            null
        }
    }
}
