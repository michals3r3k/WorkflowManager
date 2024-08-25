package com.example.workflowmanager.service.utils;

import java.util.function.Function;

public abstract class ObjectUtils
{
    private ObjectUtils()
    {
        // instance creation not allowed
    }

    public static <T, R> T accessNullable(R value, Function<R, T> mapper)
    {
        if(value == null)
        {
            return null;
        }
        return mapper.apply(value);
    }

}
