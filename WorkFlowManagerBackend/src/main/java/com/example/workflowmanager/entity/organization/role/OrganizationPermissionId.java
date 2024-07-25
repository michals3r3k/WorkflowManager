package com.example.workflowmanager.entity.organization.role;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.PrimaryKeyJoinColumns;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class OrganizationPermissionId implements Serializable
{
    @Enumerated(EnumType.STRING)
    private Permission permission;
    private OrganizationRoleId roleId;

    public OrganizationPermissionId(Permission permission,
        OrganizationRoleId roleId)
    {
        this.permission = permission;
        this.roleId = roleId;
    }

    protected OrganizationPermissionId()
    {
        // for hibernate
    }

    public Permission getPermission()
    {
        return permission;
    }

    protected void setPermission(
        Permission permission)
    {
        this.permission = permission;
    }

    public OrganizationRoleId getRoleId()
    {
        return roleId;
    }

    protected void setRoleId(
        OrganizationRoleId roleId)
    {
        this.roleId = roleId;
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
        OrganizationPermissionId that = (OrganizationPermissionId) o;
        return permission == that.permission && Objects.equals(roleId, that.roleId);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(permission, roleId);
    }

}
