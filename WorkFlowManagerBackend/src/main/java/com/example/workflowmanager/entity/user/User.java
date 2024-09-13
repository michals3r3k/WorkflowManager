package com.example.workflowmanager.entity.user;

import io.micrometer.common.util.StringUtils;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "app_user", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"email"})
})
public class User
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String password;
    private String firstName;
    private String secondName;
    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] imgContent;

    public Long getId()
    {
        return id;
    }

    protected void setId(final Long id)
    {
        this.id = id;
    }


    public String getEmail()
    {
        return email;
    }

    public void setEmail(final String email)
    {
        this.email = email;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(final String password)
    {
        this.password = password;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(final String firstName)
    {
        this.firstName = firstName;
    }

    public String getSecondName()
    {
        return secondName;
    }

    public void setSecondName(final String secondName)
    {
        this.secondName = secondName;
    }

    public byte[] getImgContent()
    {
        return imgContent;
    }

    public void setImgContent(final byte[] imgContent)
    {
        this.imgContent = imgContent;
    }

    public String getFullName()
    {
        if(StringUtils.isBlank(firstName) && StringUtils.isBlank(secondName))
        {
            return email;
        }
        if(StringUtils.isBlank(firstName))
        {
            return secondName;
        }
        if(StringUtils.isBlank(secondName))
        {
            return firstName;
        }
        return firstName + " " + secondName;
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
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id);
    }

}
