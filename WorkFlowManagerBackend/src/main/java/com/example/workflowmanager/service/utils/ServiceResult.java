package com.example.workflowmanager.service.utils;

import com.google.common.collect.ImmutableSet;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class ServiceResult<T>
{
    public static <T> ServiceResult<T> ok()
    {
        return new ServiceResult<>(Collections.emptySet());
    }

    public static <T> ServiceResult<T> error(T error)
    {
        return new ServiceResult<>(Collections.singleton(error));
    }

    private final Set<T> errors;

    public ServiceResult(Collection<T> errors)
    {
        this.errors = ImmutableSet.copyOf(errors);
    }

    public Set<T> getErrors()
    {
        return errors;
    }

    public boolean isSuccess()
    {
        return errors.isEmpty();
    }

}
