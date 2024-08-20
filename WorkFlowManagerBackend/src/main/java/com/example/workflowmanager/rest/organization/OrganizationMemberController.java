package com.example.workflowmanager.rest.organization;

import com.example.workflowmanager.db.organization.OrganizationMemberRepository;
import com.example.workflowmanager.entity.organization.OrganizationMember;
import com.example.workflowmanager.entity.organization.OrganizationMemberId;
import com.example.workflowmanager.entity.organization.OrganizationMemberInvitationStatus;
import com.example.workflowmanager.service.organization.member.OrganizationMemberInvitationService;
import com.example.workflowmanager.service.organization.member.OrganizationMemberInvitationService.OrganizationMemberInvitationError;
import com.example.workflowmanager.service.utils.ServiceResult;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
public class OrganizationMemberController
{
    private final OrganizationMemberRepository organizationMemberRepository;
    private final OrganizationMemberInvitationService invitationService;

    public OrganizationMemberController(
        final OrganizationMemberRepository organizationMemberRepository,
        final OrganizationMemberInvitationService invitationService)
    {
        this.organizationMemberRepository = organizationMemberRepository;
        this.invitationService = invitationService;
    }

    @GetMapping("/api/organization/{organizationId}/member/list")
    @PreAuthorize("hasAuthority('ORGANIZATION_MEMBER_R')")
    public ResponseEntity<List<OrganizationMemberRest>> getDetails(@PathVariable Long organizationId)
    {
        List<OrganizationMemberRest> members = organizationMemberRepository
            .getListByOrganization(Collections.singleton(organizationId)).stream()
            .map(OrganizationMemberRest::new)
            .sorted(Comparator.comparing(OrganizationMemberRest::getName, Comparator.naturalOrder()))
            .collect(Collectors.toList());
        return ResponseEntity.ok(members);
    }

    @PostMapping("/api/organization/{organizationId}/member/add")
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
