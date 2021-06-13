package com.moneylion.interview.spring.webserver.service;

import com.moneylion.interview.spring.webserver.model.Permission;


public interface PermissionService {

    boolean updatePermission(Permission permission);

    // expect only have 1 record for the same featue name and email if present
    Permission findFirstByEmailAndFeatureName(String name, String email);

    // validate email format for param
    boolean validateEmail(String email);

    // validate featureName format
    boolean validateAlphanumeric(String string);

    // validate GET REQUEST PARAMETERS
    void validateParam(String email, String string);
}
