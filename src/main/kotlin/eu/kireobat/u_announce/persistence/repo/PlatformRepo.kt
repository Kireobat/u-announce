package eu.kireobat.u_announce.persistence.repo

import eu.kireobat.u_announce.persistence.entity.OrganizationEntity
import eu.kireobat.u_announce.persistence.entity.PlatformEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface PlatformRepo: JpaRepository<PlatformEntity, Long> {
}