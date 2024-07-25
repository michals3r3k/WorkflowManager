package com.example.workflowmanager.entity.organization.role;

public enum Permission
{
    ORGANIZATION_R(PermissionSection.ORGANIZATION),
    ORGANIZATION_MEMBER_R(PermissionSection.MEMBER),
    ORGANIZATION_MEMBER_C(PermissionSection.MEMBER),
    ROLE_C(PermissionSection.ROLE),
    ROLE_R(PermissionSection.ROLE),
    ROLE_U(PermissionSection.ROLE),
    ROLE_D(PermissionSection.ROLE),
    PROJECT_C(PermissionSection.PROJECT),
    PROJECT_R(PermissionSection.PROJECT);

    private final PermissionSection section;

    Permission(PermissionSection section)
    {
        this.section = section;
    }

    public PermissionSection getSection()
    {
        return section;
    }

}
