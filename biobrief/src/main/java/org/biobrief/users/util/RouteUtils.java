package org.biobrief.users.util;

import java.util.List;

import org.biobrief.users.entities.Route;
import org.biobrief.util.StringHelper;

public class RouteUtils
{
	public static boolean isIgnored(Route route, String includePaths, String excludePaths)
	{
		if (StringHelper.hasContent(includePaths) && startsWith(route, includePaths))
			return false;
		if (StringHelper.hasContent(excludePaths) && startsWith(route, excludePaths))
			return true;
		//System.out.println("default rule. is not ignored: "+route.getUrl());
		return false;
	}
	
	public static boolean startsWith(Route route, String paths)
	{
		if (!StringHelper.hasContent(paths))
			return false;
		String url=StringHelper.remove(route.getUrl(), "#/");
		for (String path : splitPaths(paths))
		{
			if (url.startsWith(path))
			{
				//System.out.println("found a match: "+path+" matches "+url);
				return true;
			}
		}
		return false;
	}

	private static List<String> splitPaths(String value)
	{
		return StringHelper.split(value, ",");
	}
}
