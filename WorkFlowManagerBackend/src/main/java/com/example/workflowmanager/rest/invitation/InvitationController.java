package com.example.workflowmanager.rest.invitation;

import com.example.workflowmanager.db.organization.OrganizationMemberRepository;
import com.example.workflowmanager.entity.organization.OrganizationMember;
import com.example.workflowmanager.entity.organization.OrganizationMemberId;
import com.example.workflowmanager.entity.organization.OrganizationMemberInvitationStatus;
import com.example.workflowmanager.entity.user.User;
import com.example.workflowmanager.service.auth.CurrentUserService;
import com.example.workflowmanager.service.organization.member.OrganizationMemberEditService;
import com.example.workflowmanager.service.organization.member.OrganizationMemberEditService.OrganizationMemberEditError;
import com.example.workflowmanager.service.utils.ServiceResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
public class InvitationController
{
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    private final CurrentUserService cuService;
    private final OrganizationMemberRepository omRepository;
    private final OrganizationMemberEditService omeService;

    public InvitationController(final CurrentUserService cuService,
        final OrganizationMemberRepository omRepository,
        final OrganizationMemberEditService omeService)
    {
        this.cuService = cuService;
        this.omRepository = omRepository;
        this.omeService = omeService;
    }

    @GetMapping("/api/invitation/list")
    public ResponseEntity<List<InvitationRest>> getInvitationList()
    {
        final List<InvitationRest> invitations = getInvitations().stream()
            .sorted(Comparator.comparing(OrganizationMember::getInvitationTime)
                .reversed()
                .thenComparing(om -> om.getOrganization().getName()))
            .map(om -> new InvitationRest(om.getId().getOrganizationId(), om.getOrganization().getName(),
                om.getInvitationTime().format(DTF)))
            .collect(Collectors.toList());
        return ResponseEntity.ok(invitations);
    }

    @GetMapping("/api/invitation/count")
    public ResponseEntity<Integer> getInvitationCount()
    {
        return ResponseEntity.ok(getInvitations().size());
    }

    @PostMapping("/api/invitation/change-invitation-status")
    public ResponseEntity<ServiceResult<OrganizationMemberEditError>> changeInvitationStatus(
        @RequestBody InvitationRequestRest invitation)
    {
        final Long userId = getCurrentUserId();
        final OrganizationMemberId memberId = new OrganizationMemberId(invitation.getOrganizationId(), userId);
        return ResponseEntity.ok(omeService.changeInvitationStatus(memberId, invitation.getInvitationStatus()));
    }

    private List<OrganizationMember> getInvitations()
    {
        final Long userId = getCurrentUserId();
        return omRepository.getListByUserIdsWithOrganization(
            Collections.singleton(userId),
            Collections.singleton(OrganizationMemberInvitationStatus.INVITED));
    }

    private Long getCurrentUserId()
    {
        return cuService.getCurrentUser()
            .map(User::getId)
            .orElseThrow(NoSuchElementException::new);
    }

    public static class InvitationRequestRest
    {
        private Long organizationId;
        private OrganizationMemberInvitationStatus invitationStatus;

        public InvitationRequestRest()
        {
            // for Spring
        }

        public Long getOrganizationId()
        {
            return organizationId;
        }

        public void setOrganizationId(final Long organizationId)
        {
            this.organizationId = organizationId;
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

    }

    public static class InvitationRest
    {
        private Long organizationId;
        private String organizationName;
        private String invitationTime;

        public InvitationRest(final Long organizationId,
            final String organizationName,
            final String invitationTime)
        {
            this.organizationId = organizationId;
            this.organizationName = organizationName;
            this.invitationTime = invitationTime;
        }

        public Long getOrganizationId()
        {
            return organizationId;
        }

        public void setOrganizationId(final Long organizationId)
        {
            this.organizationId = organizationId;
        }

        public String getOrganizationName()
        {
            return organizationName;
        }

        public void setOrganizationName(final String organizationName)
        {
            this.organizationName = organizationName;
        }

        public String getInvitationTime()
        {
            return invitationTime;
        }

        public void setInvitationTime(final String invitationTime)
        {
            this.invitationTime = invitationTime;
        }

    }

}
