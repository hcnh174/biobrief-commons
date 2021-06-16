package org.biobrief.users;

import org.biobrief.util.ExportedEnum;

public interface UserConstants
{
	@ExportedEnum public enum Role
	{
		SUPERUSER,
		ROLE_NONE,
		ROLE_ADMIN,
		ROLE_DATAROOM,
		ROLE_RESEARCH,
		ROLE_EXPERT_PANEL,
		ROLE_GUT_MICROBIOTA,
		ROLE_NASH
	}
	
	// ENUMS_START
	// ENUMS_END
}