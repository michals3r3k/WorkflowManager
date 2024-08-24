package com.example.workflowmanager.ws.chat;

import com.example.workflowmanager.entity.chat.File;

public class FileResponse
{
    private final Long id;
    private final String name;

    FileResponse(final File file)
    {
        this.id = file.getId();
        this.name = file.getName();
    }

    public Long getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

}
