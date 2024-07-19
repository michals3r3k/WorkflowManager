package com.example.workflowmanager.rest.organization;

import com.example.workflowmanager.entity.organization.role.Permission;
import com.example.workflowmanager.entity.user.User;

import java.util.List;

public class OrganizationRoleDetailsRest
{
    private List<PermissionRest> permissions;
    private List<OrganizationMemberRest> members;

    OrganizationRoleDetailsRest(List<PermissionRest> permissions,
        List<OrganizationMemberRest> members)
    {
        this.permissions = permissions;
        this.members = members;
    }

    public List<PermissionRest> getPermissions()
    {
        return permissions;
    }

    public List<OrganizationMemberRest> getMembers()
    {
        return members;
    }

    public static class OrganizationMemberRest
    {
        private User user;
        private boolean selected;

        OrganizationMemberRest(User user, boolean selected)
        {
            this.user = user;
            this.selected = selected;
        }

        public String getEmail()
        {
            return user.getEmail();
        }

        public boolean isSelected()
        {
            return selected;
        }

    }

    public static class PermissionRest
    {
        private Permission permission;
        private boolean selected;

        PermissionRest(Permission permission, boolean selected)
        {
            this.permission = permission;
            this.selected = selected;
        }

        public String getPermission()
        {
            return permission.name();
        }

        public boolean isSelected()
        {
            return selected;
        }

    }

}
