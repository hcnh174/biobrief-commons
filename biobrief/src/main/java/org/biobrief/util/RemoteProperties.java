package org.biobrief.util;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import lombok.Data;

@Data @Validated
public class RemoteProperties
{
	@Valid @NotNull private String username;
	@Valid @NotNull private String password;
	@Valid @NotNull private String host;
	@Valid @NotNull private Integer port=22;
}
