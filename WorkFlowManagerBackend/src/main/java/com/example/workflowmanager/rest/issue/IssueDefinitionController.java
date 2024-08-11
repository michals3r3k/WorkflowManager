package com.example.workflowmanager.rest.issue;

import com.example.workflowmanager.entity.issue.IssueFieldType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
public class IssueDefinitionController
{
    @PostMapping("/api/organization/{organizationId}/issue/create")
    public void save(@PathVariable Long organizationId,
        @RequestBody List<IssueFieldDefinitionRest> fields)
    {
        organizationId.hashCode();
    }

    public static class IssueFieldDefinitionRest
    {
        private String name;
        private Byte col;
        private IssueFieldType type;
        private boolean required;
        private boolean clientVisible;

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

        public Byte getCol()
        {
            return col;
        }

        public void setCol(final Byte col)
        {
            this.col = col;
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
