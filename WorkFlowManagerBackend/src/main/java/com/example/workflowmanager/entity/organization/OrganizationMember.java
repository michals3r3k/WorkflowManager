package com.example.workflowmanager.entity.organization;

import com.example.workflowmanager.entity.organization.role.OrganizationMemberRole;
import com.example.workflowmanager.entity.user.User;
import jakarta.persistence.*;
import org.antlr.v4.runtime.misc.NotNull;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
public class OrganizationMember
{
    @EmbeddedId
    private OrganizationMemberId id;

    @NotNull
    private LocalDateTime invitationTime;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "invitation_status")
    private OrganizationMemberInvitationStatus invitationStatus;
    @ManyToOne()
    @JoinColumn(name = "organizationId",
        referencedColumnName = "id",
        insertable = false,
        updatable = false)
    private Organization organization;
    @ManyToOne()
    @JoinColumn(name = "userId",
        referencedColumnName = "id",
        insertable = false,
        updatable = false)
    private User user;
    @OneToMany(mappedBy = "member")
    private Set<OrganizationMemberRole> roles;

    public OrganizationMember(OrganizationMemberId id)
    {
        this.id = id;
    }

    protected OrganizationMember()
    {
        // for hibernate
    }

    public OrganizationMemberId getId()
    {
        return id;
    }

    protected void setId(OrganizationMemberId id)
    {
        this.id = id;
    }

    public OrganizationMemberInvitationStatus getInvitationStatus()
    {
        return invitationStatus;
    }

    public void setInvitationStatus(
        final OrganizationMemberInvitationStatus invitationStatus)
    {
        this.invitationStatus = invitationStatus;
    }

    public LocalDateTime getInvitationTime()
    {
        return invitationTime;
    }

    public void setInvitationTime(final LocalDateTime invitationTime)
    {
        this.invitationTime = invitationTime;
    }

    public Organization getOrganization()
    {
        return organization;
    }

    protected void setOrganization(
        Organization organization)
    {
        this.organization = organization;
    }

    public User getUser()
    {
        return user;
    }

    protected void setUser(User user)
    {
        this.user = user;
    }

    public Set<OrganizationMemberRole> getRoles()
    {
        return roles;
    }

    protected void setRoles(final Set<OrganizationMemberRole> roles)
    {
        this.roles = roles;
    }

    @Override
    public boolean equals(Object o)
    {
        if(this == o)
        {
            return true;
        }
        if(o == null || getClass() != o.getClass())
        {
            return false;
        }
        OrganizationMember that = (OrganizationMember) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode()
    {
        return id.hashCode();
    }

}
