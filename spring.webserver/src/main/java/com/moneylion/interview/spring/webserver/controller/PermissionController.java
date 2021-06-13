package com.moneylion.interview.spring.webserver.controller;

import com.moneylion.interview.spring.webserver.model.Permission;
import com.moneylion.interview.spring.webserver.service.PermissionService;
import com.moneylion.interview.spring.webserver.validator.ValidJson;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@CrossOrigin(origins = "http://localhost:3000")
@RestController
@Slf4j(topic = "PERMISSION_CONTROLLER")
public class PermissionController {


    @Autowired
    private PermissionService permissionService;

    /**
     * To handler to handle POST request to /feature to add or change user access for a feature
     * Returns an empty response with HTTP Status OK (200) when the database is updated successfully,
     * otherwise returns Http Status Not Modified (304).
     * @param json request body
     * @return http response
     */
    @PostMapping(value = "/feature")
    public ResponseEntity<String> saveOrUpdatePermission(@ValidJson("json_schema.json") Permission permission
    ) {

        log.debug("postPermission");

        boolean isModified = permissionService.updatePermission(permission);
        return isModified ? new ResponseEntity<String>(HttpStatus.OK) : new ResponseEntity<String>(HttpStatus.NOT_MODIFIED);

    }

    /**
     * To handle GET request from /feature.
     * @param email
     * @param featureName
     * @return http response with response body
     */
    @GetMapping(value = "/feature")
    public @ResponseBody ResponseEntity<?> getPermission(@RequestParam String email, @RequestParam String featureName) {
        log.debug("getPermission");

        // validate parameters in correct format
        permissionService.validateParam(email, featureName);

        // find record in database
        Permission permission = permissionService.findFirstByEmailAndFeatureName(email, featureName);

        // construct response
        JSONObject json = new JSONObject();
        json.put("canAccess", permission.isEnable());
        return new ResponseEntity<JSONObject>(json, HttpStatus.OK);
    }
}
