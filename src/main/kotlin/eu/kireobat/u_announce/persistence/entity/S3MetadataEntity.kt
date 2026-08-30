package eu.kireobat.u_announce.persistence.entity

import eu.kireobat.u_announce.common.Constants
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import java.time.ZonedDateTime

@Entity
@Table(name = "s3_metadata")
data class S3MetadataEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "s3MetadataIdSeq")
    @SequenceGenerator(name = "s3MetadataIdSeq", sequenceName = "s3_metadata_id_seq", allocationSize = 1)
    var id: Long = 0L,

    var mediaKey: String = "",

    var displayFilename: String = "",

    var originalFilename: String = "",

    var publishTime: ZonedDateTime = ZonedDateTime.now(),

    var expiryTime: ZonedDateTime = ZonedDateTime.now().plusDays(Constants().EXPIRY_TIME),

    var organizationId: Long = 0L,

    var keycloakCreatedByUserId: String = "",

    var createdTime: ZonedDateTime = ZonedDateTime.now()
)
