package com.moneylion.interview.spring.webserver.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "permissions")
public class Permission {

    private String featureName;
    private String email;
    private boolean enable;

     public Permission(@JsonProperty("featureName") String featureName,@JsonProperty("email") String email, @JsonProperty("enable") boolean enable) {
         this.featureName = featureName;
         this.email = email;
         this.enable = enable;
     }
}
