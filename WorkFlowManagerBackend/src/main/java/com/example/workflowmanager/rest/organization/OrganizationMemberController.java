package com.example.workflowmanager.rest.organization;

import com.example.workflowmanager.db.organization.OrganizationMemberRepository;
import com.example.workflowmanager.entity.organization.OrganizationMember;
import com.example.workflowmanager.entity.organization.OrganizationMemberId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
public class OrganizationMemberController
{
    private final OrganizationMemberRepository organizationMemberRepository;

    public OrganizationMemberController(
        OrganizationMemberRepository organizationMemberRepository)
    {
        this.organizationMemberRepository = organizationMemberRepository;
    }

    @GetMapping("/api/organization/{organizationId}/member/list")
    public ResponseEntity<List<OrganizationMemberRest>> getDetails(@PathVariable Long organizationId)
    {
        List<OrganizationMemberRest> members = organizationMemberRepository
            .getListByOrganization(Collections.singleton(organizationId)).stream()
            .map(OrganizationMemberRest::new)
            .sorted(Comparator.comparing(OrganizationMemberRest::getName, Comparator.naturalOrder()))
            .collect(Collectors.toList());
        return ResponseEntity.ok(members);
    }

    @PostMapping("/api/organization/member/add")
    public ResponseEntity<OrganizationMemberCreateServiceResult> addMember(@RequestBody OrganizationMemberAddRequest request)
    {
        // TODO: add service with validation
        OrganizationMemberId organizationMemberId = new OrganizationMemberId(
            request.getOrganizationId(), request.getUserId());
        organizationMemberRepository.save(new OrganizationMember(organizationMemberId));
        return ResponseEntity.ok(new OrganizationMemberCreateServiceResult(true));
    }

    public static class OrganizationMemberCreateServiceResult
    {
        private final boolean success;

        private OrganizationMemberCreateServiceResult(boolean success)
        {
            this.success = success;
        }

        public boolean isSuccess()
        {
            return success;
        }

    }

    public static class OrganizationMemberAddRequest
    {
        private Long organizationId;
        private Long userId;

        public OrganizationMemberAddRequest(Long organizationId, Long userId)
        {
            this.organizationId = organizationId;
            this.userId = userId;
        }

        public Long getOrganizationId()
        {
            return organizationId;
        }

        public void setOrganizationId(Long organizationId)
        {
            this.organizationId = organizationId;
        }

        public Long getUserId()
        {
            return userId;
        }

        public void setUserId(Long userId)
        {
            this.userId = userId;
        }

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

    }

}
