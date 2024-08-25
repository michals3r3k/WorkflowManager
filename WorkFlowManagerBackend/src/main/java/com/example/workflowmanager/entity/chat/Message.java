package com.example.workflowmanager.entity.chat;

import com.example.workflowmanager.entity.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

@Entity
public class Message
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private LocalDateTime createTime;
    private String content;
    @ManyToOne(optional = false)
    private User creator;
    @ManyToOne(optional = false)
    private Chat chat;
    @OneToMany(mappedBy = "message")
    private Set<File> files;

    public Long getId()
    {
        return id;
    }

    public void setId(final Long id)
    {
        this.id = id;
    }

    public LocalDateTime getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(final LocalDateTime createTime)
    {
        this.createTime = createTime;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(final String content)
    {
        this.content = content;
    }

    public User getCreator()
    {
        return creator;
    }

    public void setCreator(final User creator)
    {
        this.creator = creator;
    }

    public Chat getChat()
    {
        return chat;
    }

    public void setChat(final Chat chat)
    {
        this.chat = chat;
    }

    public Set<File> getFiles()
    {
        return files;
    }

    public void setFiles(final Set<File> files)
    {
        this.files = files;
    }

    @Override
    public boolean equals(final Object o)
    {
        if(this == o)
        {
            return true;
        }
        if(o == null || getClass() != o.getClass())
        {
            return false;
        }
        final Message message = (Message) o;
        return Objects.equals(id, message.id);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id);
    }

}
