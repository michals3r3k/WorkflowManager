package com.example.workflowmanager.rest.organization;

import com.example.workflowmanager.db.organization.OrganizationMemberRepository;
import com.example.workflowmanager.entity.organization.OrganizationMember;
import com.example.workflowmanager.entity.organization.OrganizationMemberId;
import com.example.workflowmanager.entity.organization.OrganizationMemberInvitationStatus;
import com.example.workflowmanager.service.organization.member.OrganizationMemberDeleteService;
import com.example.workflowmanager.service.organization.member.OrganizationMemberInvitationService;
import com.example.workflowmanager.service.organization.member.OrganizationMemberInvitationService.OrganizationMemberInvitationError;
import com.example.workflowmanager.service.utils.ServiceResult;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
public class OrganizationMemberController
{
    private final OrganizationMemberRepository organizationMemberRepository;
    private final OrganizationMemberInvitationService invitationService;
    private final OrganizationMemberDeleteService deleteService;

    public OrganizationMemberController(
        final OrganizationMemberRepository organizationMemberRepository,
        final OrganizationMemberInvitationService invitationService,
        final OrganizationMemberDeleteService deleteService)
    {
        this.organizationMemberRepository = organizationMemberRepository;
        this.invitationService = invitationService;
        this.deleteService = deleteService;
    }

    @GetMapping("/api/organization/{organizationId}/member/list")
    @Transactional
    @PreAuthorize("hasAuthority('ORGANIZATION_MEMBER_R')")
    public ResponseEntity<List<OrganizationMemberRest>> getDetails(@PathVariable Long organizationId)
    {
        List<OrganizationMemberRest> members = organizationMemberRepository
            .getListByOrganization(Collections.singleton(organizationId),
                EnumSet.allOf(OrganizationMemberInvitationStatus.class)).stream()
            .map(OrganizationMemberRest::new)
            .sorted(Comparator.comparing(OrganizationMemberRest::getName, Comparator.naturalOrder()))
            .collect(Collectors.toList());
        return ResponseEntity.ok(members);
    }

    @PostMapping("/api/organization/{organizationId}/member/delete")
    @Transactional
    public ResponseEntity<ServiceResult<?>> delete(@PathVariable Long organizationId,
        @RequestBody Long userId)
    {
        return ResponseEntity.ok(deleteService.delete(new OrganizationMemberId(organizationId, userId)));
    }

    @PostMapping("/api/organization/{organizationId}/member/add")
    @Transactional
    @PreAuthorize("hasAuthority('ORGANIZATION_MEMBER_C')")
    public ResponseEntity<ServiceResult<OrganizationMemberInvitationError>> addMember(
        @PathVariable Long organizationId, @RequestBody Long userId)
    {
        final LocalDateTime now = LocalDateTime.now();
        return ResponseEntity.ok(invitationService.invite(new OrganizationMemberId(organizationId, userId), now));
    }

    public static class OrganizationMemberRest
    {
        private final OrganizationMember member;

        private OrganizationMemberRest(OrganizationMember member)
        {
            this.member = member;
        }

        public Long getUserId()
        {
            return member.getId().getUserId();
        }

        public String getName()
        {
            return member.getUser().getEmail();
        }

        public OrganizationMemberInvitationStatus getInvitationStatus()
        {
            return member.getInvitationStatus();
        }

    }

}
