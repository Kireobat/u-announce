package eu.kireobat.u_announce.persistence.repo

import eu.kireobat.u_announce.persistence.entity.IntegrationStatusEntity
import eu.kireobat.u_announce.persistence.entity.PlatformEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface IntegrationStatusRepo: JpaRepository<IntegrationStatusEntity, Long> {
    fun findByOrganizationIdAndPlatformIdAndActiveTrue(organizationId: Long, platformId: Long): Optional<IntegrationStatusEntity>
    fun findByOrganizationIdAndPlatformId(organizationId: Long, platformId: Long): Optional<IntegrationStatusEntity>
    fun deleteAllByOrganizationId(organizationId: Long)
}