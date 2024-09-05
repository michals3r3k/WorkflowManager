package com.example.workflowmanager.entity.organization;

import com.example.workflowmanager.entity.user.User;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class Organization
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    private String description;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name="userId")
    private User user;

    public Organization(final String name, final String description, final User user)
    {
        this.name = name;
        this.description = description;
        this.user = user;
    }

    protected Organization()
    {
        // for hibernate
    }

    public Long getId()
    {
        return id;
    }

    protected void setId(final Long id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    protected void setName(final String name)
    {
        this.name = name;
    }

    public String getDescription()
    {
        return description;
    }

    protected void setDescription(final String description)
    {
        this.description = description;
    }

    public User getUser()
    {
        return user;
    }

    protected void setUser(User user)
    {
        this.user = user;
    }

    @Override
    public boolean equals(Object o)
    {
        if(this == o)
        {
            return true;
        }
        if(o == null || getClass() != o.getClass())
        {
            return false;
        }
        Organization that = (Organization) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id);
    }

}
