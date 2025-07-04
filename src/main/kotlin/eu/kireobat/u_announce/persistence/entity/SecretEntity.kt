package eu.kireobat.u_announce.persistence.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import java.time.ZonedDateTime

@Entity
@Table(name = "secret")
data class SecretEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "secretSeq")
    @SequenceGenerator(name = "secretSeq", sequenceName = "secret_seq", allocationSize = 1)
    var id: Long = 0L,

    var organizationId: Long = 0L,

    var platformId: Long = 0L,

    var encryptedCredential: ByteArray = ByteArray(0),

    var keycloakCreatedByUserId: String = "",

    var createdTime: ZonedDateTime = ZonedDateTime.now()
)