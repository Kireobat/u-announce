package eu.kireobat.u_announce.persistence.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table

@Entity
@Table(name = "platform")
data class PlatformEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "platformIdSeq")
    @SequenceGenerator(name = "platformIdSeq", sequenceName = "platform_id_seq", allocationSize = 1)
    var id: Long = 0L,

    var slug: String = "",

    var displayName: String = "",

    var className: String = ""
)