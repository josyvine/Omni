package com.vineyard.omnicam.app.domain.usecases

import com.vineyard.omnicam.app.core.security.CryptoManager
import com.vineyard.omnicam.app.data.models.ShareToken
import com.vineyard.omnicam.app.data.repository.SettingsRepository
import com.vineyard.omnicam.app.di.FirebaseModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject

/**
 * UseCase to process, decrypt, and validate a QR code scanned by a Guest / Member.
 * 
 * Flow:
 * 1. Takes the raw encrypted AES-256 string from CameraX / ML Kit.
 * 2. Decrypts it using [CryptoManager].
 * 3. Parses the decrypted JSON to extract the Admin's Firebase configuration JSON,
 *    permitted camera IDs, permission level, and expiration timestamp.
 * 4. Checks if the token has expired.
 * 5. Persists the Admin's Firebase JSON in [SettingsRepository].
 * 6. Mounts the Admin's secondary FirebaseApp instance ("admin_cam_app") dynamically
 *    so the guest can query the Admin's Firestore database without needing Keystore SHA-1.
 */
class ProcessScannedQrUseCase @Inject constructor(
    private val cryptoManager: CryptoManager,
    private val settingsRepository: SettingsRepository,
    private val firebaseModule: FirebaseModule
) {

    suspend operator fun invoke(encryptedQrPayload: String): Result<ShareToken> = withContext(Dispatchers.IO) {
        try {
            // 1. Decrypt AES-256 payload
            val decryptedJsonString = cryptoManager.decrypt(encryptedQrPayload.trim())
            if (decryptedJsonString.isBlank()) {
                return@withContext Result.failure(IllegalArgumentException("Failed to decrypt QR code payload. Invalid key or corrupted data."))
            }

            // 2. Parse the decrypted JSON
            val rootJson = JSONObject(decryptedJsonString)

            val token = rootJson.optString("token", "")
            val adminEmail = rootJson.optString("admin", "")
            val adminUserId = rootJson.optString("adminUserId", "admin_master")
            val permission = rootJson.optString("perm", "VIEW_ONLY")
            val expiresAt = rootJson.optLong("exp", 0L)
            val firebaseConfigJson = rootJson.optString("firebaseConfig", "")

            if (token.isEmpty() || adminEmail.isEmpty()) {
                return@withContext Result.failure(IllegalArgumentException("Malformed QR Code: Missing required authentication tokens."))
            }

            // 3. Check for expiration
            val currentTime = System.currentTimeMillis()
            if (expiresAt in 1 until currentTime) {
                return@withContext Result.failure(IllegalStateException("This camera share QR code has expired."))
            }

            // 4. Extract permitted camera IDs
            val camerasJsonArray: JSONArray? = rootJson.optJSONArray("cameras")
            val permittedCameraIds = mutableListOf<String>()
            if (camerasJsonArray != null) {
                for (i in 0 until camerasJsonArray.length()) {
                    permittedCameraIds.add(camerasJsonArray.getString(i))
                }
            }

            // 5. Mount the Admin's Firebase configuration if provided in the QR code
            if (firebaseConfigJson.isNotBlank()) {
                // Save config to encrypted persistent storage
                settingsRepository.saveCustomFirebaseJson(firebaseConfigJson)

                // Dynamically initialize the Admin's named secondary FirebaseApp instance
                firebaseModule.initializeCustomFirebase(firebaseConfigJson)
            }

            // 6. Construct the validated ShareToken model with cameraIds matching the schema
            val shareToken = ShareToken(
                token = token,
                adminUserId = adminUserId,
                adminEmail = adminEmail,
                cameraIds = permittedCameraIds,
                permission = permission,
                expiresAt = expiresAt,
                createdAt = rootJson.optLong("createdAt", currentTime)
            )

            // 7. Save current guest session
            settingsRepository.saveActiveGuestShareToken(token)

            Result.success(shareToken)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}