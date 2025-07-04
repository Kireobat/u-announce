package eu.kireobat.u_announce.persistence.repo

import eu.kireobat.u_announce.persistence.entity.PlatformEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PlatformRepo: JpaRepository<PlatformEntity, Long> {
}