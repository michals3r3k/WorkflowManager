package com.example.workflowmanager.rest.organization;

import com.example.workflowmanager.entity.organization.role.Permission;
import com.example.workflowmanager.entity.organization.role.PermissionSection;
import com.example.workflowmanager.entity.user.User;

import java.util.List;

public class OrganizationRoleDetailsRest
{
    private boolean addToNewMembers;
    private List<PermissionSectionRest> permissionSections;
    private List<OrganizationMemberRest> members;

    public OrganizationRoleDetailsRest()
    {
        // for Spring
    }

    OrganizationRoleDetailsRest(boolean addToNewMembers,
        List<PermissionSectionRest> permissionSections,
        List<OrganizationMemberRest> members)
    {
        this.addToNewMembers = addToNewMembers;
        this.permissionSections = permissionSections;
        this.members = members;
    }

    public boolean isAddToNewMembers()
    {
        return addToNewMembers;
    }

    public void setAddToNewMembers(final boolean addToNewMembers)
    {
        this.addToNewMembers = addToNewMembers;
    }

    public List<PermissionSectionRest> getPermissionSections()
    {
        return permissionSections;
    }

    public void setPermissionSections(
        final List<PermissionSectionRest> permissionSections)
    {
        this.permissionSections = permissionSections;
    }

    public List<OrganizationMemberRest> getMembers()
    {
        return members;
    }

    public void setMembers(
        final List<OrganizationMemberRest> members)
    {
        this.members = members;
    }

    public static class OrganizationMemberRest
    {
        private Long userId;
        private String email;
        private boolean selected;

        public OrganizationMemberRest()
        {
            // for Spring
        }

        OrganizationMemberRest(User user, boolean selected)
        {
            this.userId = user.getId();
            this.email = user.getEmail();
            this.selected = selected;
        }

        public Long getUserId()
        {
            return userId;
        }

        public void setUserId(final Long userId)
        {
            this.userId = userId;
        }

        public String getEmail()
        {
            return email;
        }

        public void setEmail(final String email)
        {
            this.email = email;
        }

        public boolean isSelected()
        {
            return selected;
        }

        public void setSelected(final boolean selected)
        {
            this.selected = selected;
        }

    }

    public static class PermissionSectionRest
    {
        private String sectionName;
        private List<PermissionRest> permissions;

        public PermissionSectionRest()
        {
            // for Spring
        }

        PermissionSectionRest(final PermissionSection section,
            final List<PermissionRest> permissions)
        {
            this.sectionName = section.name();
            this.permissions = permissions;
        }

        public String getSectionName()
        {
            return sectionName;
        }

        public void setSectionName(final String sectionName)
        {
            this.sectionName = sectionName;
        }

        public List<PermissionRest> getPermissions()
        {
            return permissions;
        }

        public void setPermissions(
            final List<PermissionRest> permissions)
        {
            this.permissions = permissions;
        }

    }

    public static class PermissionRest
    {
        private String permission;
        private boolean selected;

        PermissionRest(Permission permission, boolean selected)
        {
            this.permission = permission.name();
            this.selected = selected;
        }

        public String getPermission()
        {
            return permission;
        }

        public void setPermission(final String permission)
        {
            this.permission = permission;
        }

        public boolean isSelected()
        {
            return selected;
        }

        public void setSelected(final boolean selected)
        {
            this.selected = selected;
        }

    }

}
