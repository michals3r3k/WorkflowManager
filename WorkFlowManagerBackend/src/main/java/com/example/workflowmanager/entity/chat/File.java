package com.example.workflowmanager.entity.chat;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class File
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String contentType;
    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] data;
    @ManyToOne(optional = false)
    private Message message;

    public Long getId()
    {
        return id;
    }

    public void setId(final Long id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    public String getContentType()
    {
        return contentType;
    }

    public void setContentType(final String contentType)
    {
        this.contentType = contentType;
    }

    public byte[] getData()
    {
        return data;
    }

    public void setData(final byte[] data)
    {
        this.data = data;
    }

    public Message getMessage()
    {
        return message;
    }

    public void setMessage(final Message message)
    {
        this.message = message;
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
        final File file = (File) o;
        return Objects.equals(id, file.id);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id);
    }

}
