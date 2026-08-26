package eu.kireobat.u_announce.service

import eu.kireobat.u_announce.config.RustFSConfig
import eu.kireobat.u_announce.config.RustFSProperties
import eu.kireobat.u_announce.util.FormatUtil
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.s3.S3Client
import java.util.Optional

@Service
class RustFSService(private val organizationService: OrganizationService) {

    private val logger = LoggerFactory.getLogger(RustFSService::class.java)

    @Value($$"${rustfs.url}")
    private lateinit var serverUrl: String

    @Value($$"${rustfs.access-key}")
    private lateinit var accessKey: String

    @Value($$"${rustfs.secret-key}")
    private lateinit var secretKey: String

    private val rustFSAdminClient: S3Client by lazy {
        val rustFSProperties = RustFSProperties(serverUrl, accessKey, secretKey)

        RustFSConfig(rustFSProperties).rustFSAdminClient()
    }

    private fun createBucket(orgId: Long, userId: String): Optional<String> {
        return try {
            val organizationEntity = organizationService.getOrganization(orgId, userId)

            val bucketName = "${organizationEntity.slug}-${FormatUtil().generateBucketTimestamp()}-${FormatUtil().generateRandomUrlSafeBase64(4)}"



            logger.info("Successfully created bucket for org ($orgId) by user ($userId)")
            Optional.empty()
        } catch (e: Exception) {
            logger.error("Failed to create bucket for org ($orgId) by user ($userId)", e)
            Optional.empty()
        }
    }
}