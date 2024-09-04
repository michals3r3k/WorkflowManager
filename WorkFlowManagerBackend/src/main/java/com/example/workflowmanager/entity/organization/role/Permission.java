package com.example.workflowmanager.entity.organization.role;

public enum Permission
{
    ORGANIZATION_R(PermissionSection.ORGANIZATION),
    ORGANIZATION_MEMBER_R(PermissionSection.MEMBER),
    ORGANIZATION_MEMBER_U(PermissionSection.MEMBER),
    ROLE_R(PermissionSection.ROLE),
    ROLE_U(PermissionSection.ROLE),
    PROJECT_C(PermissionSection.PROJECT),
    PROJECT_R(PermissionSection.PROJECT),
    ORDER_R(PermissionSection.ORDER),
    ORDER_U(PermissionSection.ORDER),
    ORDER_SETTINGS_U(PermissionSection.ORDER);

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
