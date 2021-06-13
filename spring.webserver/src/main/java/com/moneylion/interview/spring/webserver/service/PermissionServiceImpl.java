package com.moneylion.interview.spring.webserver.service;

import com.moneylion.interview.spring.webserver.model.Permission;
import com.moneylion.interview.spring.webserver.repository.PermissionRepository;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@Slf4j(topic = "PERMISSION_SERVICE_IMPLEMENTATION")
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private PermissionRepository permissionRepository;

    /**
     * update permission in database if needed
     * Otherwise, create permission in database
     * @param permission
     * @return
     */
    @Override
    public boolean updatePermission(Permission permission) {
        // query
        Query query = new Query();
        query.addCriteria(Criteria.where("email").is(permission.getEmail())
                .andOperator(Criteria.where("featureName").is(permission.getFeatureName())));

        // update rule
        Update update = new Update();
        update.set("enable", permission.isEnable());

        UpdateResult foundPermission = mongoTemplate.upsert(query, update, Permission.class);
        return foundPermission.getUpsertedId() != null || foundPermission.getModifiedCount() > 0;
    }

    /**
     * find record in database based on email and feature name
     * @param featureName
     * @param email
     * @return permission object
     */
    @Override
    public Permission findFirstByEmailAndFeatureName(String featureName, String email) {

        Optional<Permission> permission = permissionRepository.findFirstByEmailAndFeatureName(featureName, email);
        if (!permission.isPresent()) {
            throw new NoSuchElementException(String.format("Feature name: %s and email: %s not found ", featureName, email));
        }

        return permission.get();

    }

    /**
     * validate string is in email format
     * @param email email string
     * @return true if is email. Otherwise, false
     */
    public boolean validateEmail(String email) {
        String regex = "^[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        return email != null && !email.isEmpty() && Pattern.matches(regex, email);
    }

    /**
     * validate string has string only or combination of string and number
     * @param string string to validate
     * @return true if string is string only or combination of string and number. Otherwise, false
     */
    public boolean validateAlphanumeric(String string) {
        return string != null && !string.isEmpty() && Pattern.matches("(?!^\\d+$)^.+$", string);
    }

    /**
     *  validate get request parameters
     * @param email
     * @param featureName
     */
    public void validateParam(String email, String featureName) {
        boolean isValidEmail = validateEmail(email);
        boolean isValidFeatureName = validateAlphanumeric(featureName);

        if (!isValidEmail || !isValidFeatureName) {
            String error = !isValidEmail ? "email parameter is not in the correct format" : "featureName needs to contain only string or combination of string with numbers.";
            error = !isValidEmail && !isValidFeatureName ? "email and featureName are not in the correct format" : error;
            log.error("Validation failed for get parameters: " + error);
            throw new IllegalArgumentException(error);

        }

    }


}
