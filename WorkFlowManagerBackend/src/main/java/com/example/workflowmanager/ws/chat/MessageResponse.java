package com.example.workflowmanager.ws.chat;

import com.example.workflowmanager.entity.chat.Message;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class MessageResponse
{
    private static final DateTimeFormatter DTF = DateTimeFormatter.ISO_DATE_TIME;

    private final Long creatorId;
    private final String creatorName;
    private final String createTime;
    private final String content;
    private final List<FileResponse> files;

    public MessageResponse(final Message message)
    {
        this.creatorId = message.getCreator().getId();
        this.creatorName = message.getCreator().getEmail();
        this.createTime = message.getCreateTime().format(DTF);
        this.content = message.getContent();
        this.files = message.getFiles().stream()
            .map(FileResponse::new)
            .collect(Collectors.toList());
    }

    public Long getCreatorId()
    {
        return creatorId;
    }

    public String getCreatorName()
    {
        return creatorName;
    }

    public String getCreateTime()
    {
        return createTime;
    }

    public String getContent()
    {
        return content;
    }

    public List<FileResponse> getFiles()
    {
        return files;
    }

}
