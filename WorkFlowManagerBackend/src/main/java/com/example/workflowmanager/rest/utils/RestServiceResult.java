package com.example.workflowmanager.rest.utils;

import com.example.workflowmanager.service.utils.ServiceResult;

import java.util.List;
import java.util.stream.Collectors;

public class RestServiceResult
{
    public static <E extends Enum<E>> RestServiceResult fromEnum(ServiceResult<E> result)
    {
        final List<String> errors = result.getErrors().stream()
            .map(Enum::name)
            .collect(Collectors.toList());
        return new RestServiceResult(result.isSuccess(), errors);
    }

    private final boolean success;
    private final List<String> errors;

    public RestServiceResult(boolean success, List<String> errors)
    {
        this.success = success;
        this.errors = errors;
    }

    public boolean isSuccess()
    {
        return success;
    }

    public List<String> getErrors()
    {
        return errors;
    }

}
