package com.example.workflowmanager.db.organization;

import com.example.workflowmanager.entity.organization.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long>
{
    @Query("select o from Organization o join o.user u where u.id in (?1)")
    List<Organization> getListByUserIds(Collection<Long> userIds);

    @Query("select o from Organization o join fetch o.user u where o.id = ?1")
    Organization getByIdWithUser(Long organizationId);

    @Query("select o from Organization o where o.name like (?1)")
    Set<Organization> getListByNameLike(String nameSearch);

}
