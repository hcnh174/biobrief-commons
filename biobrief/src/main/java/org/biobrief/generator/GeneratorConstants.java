package org.biobrief.generator;

import org.biobrief.util.StringHelper;

public interface GeneratorConstants
{
	public static final String WORKBOOK_LOCAL_TEMPLATE_PREFIX="-";
		
	public enum ContainerType {panel, fieldset, none};
	public enum RenderMode{angular, freemarker}
	
	public enum Icon
	{
		FORM("fa fa-wpforms"),
		GRID("fa fa-list"),
		FILE("fa fa-file-o"),
		DOWNLOAD("fa fa-download"),
		SEARCH("fa fa-search"),
		ADD("fa fa-plus"),
		EDIT("fa fa-edit"),
		DELETE("fa fa-trash-o"),//fa-remove fa-close
		OPEN("fa fa-external-link"),
		RESET("fa fa-undo"),
		
		SAVE("pi pi-check"),
		CANCEL("pi pi-close"),
		
		//BOOKMARK("fa-bookmark"),
		BOOKMARK_ON("fa fa-bookmark"),
		BOOKMARK_OFF("fa fa-bookmark-o"),
		
		STAR("fa fa-star-o"),
		STAR_ON("fa fa-star"),
		STAR_OFF("fa fa-star-o");
		
		private String cls;
		
		private Icon(String cls)
		{
			this.cls=cls;
		}
			
		public String getCls(){return cls;}
		
		public static Icon find(String value, Icon dflt)
		{
			if (!StringHelper.hasContent(value) || value.equalsIgnoreCase("false"))
				return null;
			if (value.equalsIgnoreCase("true") && dflt!=null)
				return dflt;
			return Icon.valueOf(value.toUpperCase());
		}
		
		public static Icon find(String value)
		{
			return find(value, null);
		}
	}

	public enum KeyFilter
	{
		PositiveInteger("pint"),
		Integer("int"),
		PositiveNumber("pnum"),
		Number("num"),
		Hex("hex"),
		Email("email"),
		Alphabetic("alpha"),
		Alphanumeric("alphanum");
		
		private String filter;
		
		KeyFilter(String filter)
		{
			this.filter=filter;
		}
		
		public String getFilter(){return filter;}
	}
	
	public enum ControlType
	{
		dflt,
		text,
		textarea,
		select,
		checkbox,
		date
	}
}
