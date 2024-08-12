package com.example.workflowmanager.rest.issue;

import com.example.workflowmanager.db.issue.IssueFieldDefinitionRepository;
import com.example.workflowmanager.entity.issue.IssueFieldDefinition;
import com.example.workflowmanager.entity.issue.IssueFieldDefinitionId;
import com.example.workflowmanager.entity.issue.IssueFieldType;
import com.example.workflowmanager.service.utils.ServiceResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@CrossOrigin
@RestController
public class IssueDefinitionController
{
    private final IssueFieldDefinitionRepository ifdRepository;

    public IssueDefinitionController(final IssueFieldDefinitionRepository ifdRepository)
    {
        this.ifdRepository = ifdRepository;
    }

    @GetMapping("/api/organization/{organizationId}/issue-definition")
    public List<IssueFieldDefinitionRest> get(@PathVariable Long organizationId)
    {
        return ifdRepository.getListByOrganizationId(Collections.singleton(organizationId)).stream()
            .sorted(Comparator.comparing(field -> field.getId().getCol()))
            .map(field -> new IssueFieldDefinitionRest(field.getName(),
                field.getId().getCol(), field.getType(), field.isRequired(), field.isClientVisible()))
            .collect(Collectors.toList());
    }

    @PostMapping("/api/organization/{organizationId}/issue-definition/create")
    public ResponseEntity<ServiceResult<?>> save(@PathVariable Long organizationId,
        @RequestBody List<IssueFieldDefinitionRest> fields)
    {
        ifdRepository.deleteAll(ifdRepository.getListByOrganizationId(Collections.singleton(organizationId)));
        final List<IssueFieldDefinition> fieldsToSave = fields.stream()
            .collect(Collectors.groupingBy(IssueFieldDefinitionRest::getColumn))
            .values().stream()
            .flatMap(colFields -> IntStream.range(0,colFields.size())
                .mapToObj(i -> getIssueFieldDefinition(organizationId, colFields.get(i), i)))
            .collect(Collectors.toList());
        ifdRepository.saveAll(fieldsToSave);
        return ResponseEntity.ok(ServiceResult.ok());
    }

    private static IssueFieldDefinition getIssueFieldDefinition(final Long organizationId,
        final IssueFieldDefinitionRest field, final int row)
    {
        final IssueFieldDefinitionId id = new IssueFieldDefinitionId(
            organizationId, (short) row, field.getColumn());
        return new IssueFieldDefinition(id, field.getName(), field.getType(),
            field.isRequired(), field.isClientVisible());
    }

    public static class IssueFieldDefinitionRest
    {
        private String name;
        private Byte column;
        private IssueFieldType type;
        private boolean required;
        private boolean clientVisible;

        protected IssueFieldDefinitionRest(final String name, final Byte column,
            final IssueFieldType type, final boolean required,
            final boolean clientVisible)
        {
            this.name = name;
            this.column = column;
            this.type = type;
            this.required = required;
            this.clientVisible = clientVisible;
        }

        public IssueFieldDefinitionRest()
        {
            // for Spring
        }

        public String getName()
        {
            return name;
        }

        public void setName(final String name)
        {
            this.name = name;
        }

        public Byte getColumn()
        {
            return column;
        }

        public void setColumn(final Byte column)
        {
            this.column = column;
        }

        public IssueFieldType getType()
        {
            return type;
        }

        public void setType(final IssueFieldType type)
        {
            this.type = type;
        }

        public boolean isRequired()
        {
            return required;
        }

        public void setRequired(final boolean required)
        {
            this.required = required;
        }

        public boolean isClientVisible()
        {
            return clientVisible;
        }

        public void setClientVisible(final boolean clientVisible)
        {
            this.clientVisible = clientVisible;
        }

    }

}
