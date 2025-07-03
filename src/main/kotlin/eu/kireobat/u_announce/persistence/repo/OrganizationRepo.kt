package eu.kireobat.u_announce.persistence.repo

import eu.kireobat.u_announce.persistence.entity.OrganizationEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OrganizationRepo: JpaRepository<OrganizationEntity, Long> {
}