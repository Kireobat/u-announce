package eu.kireobat.u_announce.persistence.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import java.time.ZonedDateTime

@Entity
@Table(name = "organization")
data class OrganizationEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "organizationIdSeq")
    @SequenceGenerator(name = "organizationIdSeq", sequenceName = "organization_id_seq", allocationSize = 1)
    var id: Long = 0L,

    var slug: String = "",

    var displayName: String = "",

    var organizationKey: ByteArray = ByteArray(0),

    var keycloakGroupId: String = "",

    var keycloakCreatedByUserId: String = "",

    var createdTime: ZonedDateTime = ZonedDateTime.now()
)