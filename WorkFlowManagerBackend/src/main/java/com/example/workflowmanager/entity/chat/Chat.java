package com.example.workflowmanager.entity.chat;

import jakarta.persistence.*;

import java.util.Objects;
import java.util.Set;

@Entity
public class Chat
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToMany(mappedBy = "chat")
    private Set<Message> messages;

    public Long getId()
    {
        return id;
    }

    public void setId(final Long id)
    {
        this.id = id;
    }

    public Set<Message> getMessages()
    {
        return messages;
    }

    public void setMessages(final Set<Message> messages)
    {
        this.messages = messages;
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
        final Chat chat = (Chat) o;
        return id.equals(chat.id);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id);
    }

}
