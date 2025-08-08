package eu.kireobat.u_announce.service

import com.fasterxml.jackson.databind.ObjectMapper
import eu.kireobat.u_announce.config.MinioConfig
import eu.kireobat.u_announce.config.MinioProperties
import io.minio.admin.MinioAdminClient
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class MinioPolicyService(
    private val objectMapper: ObjectMapper
) {

    private val logger = LoggerFactory.getLogger(MinioPolicyService::class.java.name)

    @Value($$"${minio.endpoint}")
    private lateinit var endpoint: String
    @Value($$"${minio.access-key}")
    private lateinit var accessKey: String
    @Value($$"${minio.secret-key}")
    private lateinit var secretKey: String


    private val minioAdminClient: MinioAdminClient by lazy {
        val minioProperties = MinioProperties(endpoint, accessKey, secretKey)

        MinioConfig(minioProperties).minioAdminClient()
    }

    fun createOrganizationPolicy(orgSlug: String): Boolean {
        return try {
            val policyName = "org-$orgSlug"
            val policyJson = createOrgPolicyJson(orgSlug)

            minioAdminClient.addCannedPolicy(policyName, policyJson)

            logger.info("Successfully created MinIO policy: $policyName")
            true
        } catch (e: Exception) {
            logger.error("Failed to create MinIO policy for org: $orgSlug", e)
            false
        }
    }

    fun deleteOrganizationPolicy(orgSlug: String): Boolean {
        return try {
            val policyName = "org-$orgSlug"
            minioAdminClient.removeCannedPolicy(policyName)

            logger.info("Successfully deleted MinIO policy: $policyName")
            true
        } catch (e: Exception) {
            logger.error("Failed to delete MinIO policy for org: $orgSlug", e)
            false
        }
    }

    private fun createOrgPolicyJson(orgSlug: String): String {
        val policy = mapOf(
            "Version" to "2012-10-17",
            "Statement" to listOf(
                mapOf(
                    "Effect" to "Allow",
                    "Action" to listOf(
                        "s3:GetObject",
                        "s3:PutObject",
                        "s3:DeleteObject",
                        "s3:ListBucket"
                    ),
                    "Resource" to listOf(
                        "arn:aws:s3:::organizations/org-$orgSlug/*",
                        "arn:aws:s3:::organizations"
                    ),
                    "Condition" to mapOf(
                        "StringLike" to mapOf(
                            "s3:prefix" to listOf("org-$orgSlug/*")
                        )
                    )
                )
            )
        )

        return objectMapper.writeValueAsString(policy)
    }
}