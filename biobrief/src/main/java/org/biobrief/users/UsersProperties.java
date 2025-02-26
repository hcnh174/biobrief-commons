package org.biobrief.users;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import lombok.Data;

@Validated
@ConfigurationProperties("users") @Data
public class UsersProperties
{
//	@Valid @NotNull private String filename;
//	@Valid @NotNull private String adminId;
//	@Valid @NotNull private String adminUsername;
//	@Valid @NotNull private String adminPassword;
}
