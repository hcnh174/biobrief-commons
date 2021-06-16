package org.biobrief.util;

public interface Constants
{
	public static final String BASE_DIR=FileHelper.getBaseDirectory();
	public static final String DATA_DIR=BASE_DIR+"/data";
	public static final String CONFIG_DIR=DATA_DIR+"/config";
	public static final String TEMP_DIR=BASE_DIR+"/.temp";
	public static final String TMP_DIR=TEMP_DIR+"/tmp";
	public static final String LOG_DIR=TEMP_DIR+"/logs";
	public static final String SCRIPTS_DIR=BASE_DIR+"/biobrief-scripts";
	
	public static final String TIMESTAMP=DateHelper.getTimestamp();
	public static final String DATE_PATTERN=DateHelper.YYYYMMDD_PATTERN;
	public static final String TIME_PATTERN="HH':'mm':'ss";
	public static final String DATETIME_PATTERN=DateHelper.DATETIME_PATTERN;
	public static final String SHORTDATE_PATTERN="MM'/'dd";
	public static final String NULL="NULL";
	public static final String MULTI_DELIMITER="|";
	public static final String MERGE_MULTI_DELIMITER="$";
	public static final String ND="ND";
	
	public static final String FASTA_SUFFIX=".fasta";
	public static final String GENBANK_SUFFIX=".gbk";
	public static final String GENPEPT_SUFFIX=".gpt";
	
	public static final String ID="id";
	public static final String NAME="name";
	public static final String CREATED_DATE="createdDate";
	public static final String CREATED_BY="createdBy";
	public static final String LAST_MODIFIED_DATE="lastModifiedDate";
	public static final String LAST_MODIFIED_BY="lastModifiedBy";
	
	public static final String NAME_LABEL="名称";
	public static final String CREATED_DATE_LABEL="作成日";
	public static final String CREATED_BY_LABEL="作成者";
	public static final String LAST_MODIFIED_DATE_LABEL="データ更新日";
	public static final String LAST_MODIFIED_BY_LABEL="データ更新者";
	
	public static final String ALPHABET="ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
	@ExportedEnum public enum UpdateStatus {NULL,更新中,終了}
	@ExportedEnum public enum Presence {NULL,有,無,不明}
	@ExportedEnum public enum FirstLast {FIRST, LAST}
	@ExportedEnum public enum ArraySelect {FIRST, LAST, ALL, MAX, MIN}
	@ExportedEnum public enum DateMask {NULL,日付不明, 年のみ判明, 年月のみ判明, 月日のみ判明}//要調査
	@ExportedEnum public enum EditType{NONE, VIEW, EDIT}
	@ExportedEnum public enum Sex {NULL,男,女}
	@ExportedEnum public enum IO {input, output}
	
	@ExportedEnum public enum Bool
	{
		NULL("NULL"),
		TRUE("有"),
		FALSE("無");
		
		private String display;
		
		Bool(String display)
		{
			this.display=display;
		}
		
		public String getDisplay(){return display;}
		
		@Override
		public String toString()
		{
			return display;
		}
	}
	
	@ExportedEnum public enum PersistenceType
	{
		jdbc(false, true),
		jpa(true, true),
		mongo(true, false);
		
		@ExportedField private final boolean sql;
		private final boolean orm;

		PersistenceType(boolean orm, boolean sql)
		{
			this.orm=orm;
			this.sql=sql;
		}
		
		public boolean isEscape()
		{
			return !orm;
		}
		
		public boolean isSql(){return sql;}
		public boolean isOrm(){return orm;}
	}
	
	//https://www.iban.com/country-codes
	@ExportedEnum public enum Country
	{
		AF("Afghanistan", "AFG", 4),
		AL("Albania", "ALB", 8),
		DZ("Algeria", "DZA", 12),
		AS("American Samoa", "ASM", 16),
		AD("Andorra", "AND", 20),
		AO("Angola", "AGO", 24),
		AI("Anguilla", "AIA", 660),
		AQ("Antarctica", "ATA", 10),
		AG("Antigua and Barbuda", "ATG", 28),
		AR("Argentina", "ARG", 32),
		AM("Armenia", "ARM", 51),
		AW("Aruba", "ABW", 533),
		AU("Australia", "AUS", 36),
		AT("Austria", "AUT", 40),
		AZ("Azerbaijan", "AZE", 31),
		BS("Bahamas (the)", "BHS", 44),
		BH("Bahrain", "BHR", 48),
		BD("Bangladesh", "BGD", 50),
		BB("Barbados", "BRB", 52),
		BY("Belarus", "BLR", 112),
		BE("Belgium", "BEL", 56),
		BZ("Belize", "BLZ", 84),
		BJ("Benin", "BEN", 204),
		BM("Bermuda", "BMU", 60),
		BT("Bhutan", "BTN", 64),
		BO("Bolivia (Plurinational State of)", "BOL", 68),
		BQ("Bonaire, Sint Eustatius and Saba", "BES", 535),
		BA("Bosnia and Herzegovina", "BIH", 70),
		BW("Botswana", "BWA", 72),
		BV("Bouvet Island", "BVT", 74),
		BR("Brazil", "BRA", 76),
		IO("British Indian Ocean Territory (the)", "IOT", 86),
		BN("Brunei Darussalam", "BRN", 96),
		BG("Bulgaria", "BGR", 100),
		BF("Burkina Faso", "BFA", 854),
		BI("Burundi", "BDI", 108),
		CV("Cabo Verde", "CPV", 132),
		KH("Cambodia", "KHM", 116),
		CM("Cameroon", "CMR", 120),
		CA("Canada", "CAN", 124),
		KY("Cayman Islands (the)", "CYM", 136),
		CF("Central African Republic (the)", "CAF", 140),
		TD("Chad", "TCD", 148),
		CL("Chile", "CHL", 152),
		CN("China", "CHN", 156),
		CX("Christmas Island", "CXR", 162),
		CC("Cocos (Keeling) Islands (the)", "CCK", 166),
		CO("Colombia", "COL", 170),
		KM("Comoros (the)", "COM", 174),
		CD("Congo (the Democratic Republic of the)", "COD", 180),
		CG("Congo (the)", "COG", 178),
		CK("Cook Islands (the)", "COK", 184),
		CR("Costa Rica", "CRI", 188),
		HR("Croatia", "HRV", 191),
		CU("Cuba", "CUB", 192),
		CW("Curaçao", "CUW", 531),
		CY("Cyprus", "CYP", 196),
		CZ("Czechia", "CZE", 203),
		CI("Côte d'Ivoire", "CIV", 384),
		DK("Denmark", "DNK", 208),
		DJ("Djibouti", "DJI", 262),
		DM("Dominica", "DMA", 212),
		DO("Dominican Republic (the)", "DOM", 214),
		EC("Ecuador", "ECU", 218),
		EG("Egypt", "EGY", 818),
		SV("El Salvador", "SLV", 222),
		GQ("Equatorial Guinea", "GNQ", 226),
		ER("Eritrea", "ERI", 232),
		EE("Estonia", "EST", 233),
		SZ("Eswatini", "SWZ", 748),
		ET("Ethiopia", "ETH", 231),
		FK("Falkland Islands (the) [Malvinas]", "FLK", 238),
		FO("Faroe Islands (the)", "FRO", 234),
		FJ("Fiji", "FJI", 242),
		FI("Finland", "FIN", 246),
		FR("France", "FRA", 250),
		GF("French Guiana", "GUF", 254),
		PF("French Polynesia", "PYF", 258),
		TF("French Southern Territories (the)", "ATF", 260),
		GA("Gabon", "GAB", 266),
		GM("Gambia (the)", "GMB", 270),
		GE("Georgia", "GEO", 268),
		DE("Germany", "DEU", 276),
		GH("Ghana", "GHA", 288),
		GI("Gibraltar", "GIB", 292),
		GR("Greece", "GRC", 300),
		GL("Greenland", "GRL", 304),
		GD("Grenada", "GRD", 308),
		GP("Guadeloupe", "GLP", 312),
		GU("Guam", "GUM", 316),
		GT("Guatemala", "GTM", 320),
		GG("Guernsey", "GGY", 831),
		GN("Guinea", "GIN", 324),
		GW("Guinea-Bissau", "GNB", 624),
		GY("Guyana", "GUY", 328),
		HT("Haiti", "HTI", 332),
		HM("Heard Island and McDonald Islands", "HMD", 334),
		VA("Holy See (the)", "VAT", 336),
		HN("Honduras", "HND", 340),
		HK("Hong Kong", "HKG", 344),
		HU("Hungary", "HUN", 348),
		IS("Iceland", "ISL", 352),
		IN("India", "IND", 356),
		ID("Indonesia", "IDN", 360),
		IR("Iran (Islamic Republic of)", "IRN", 364),
		IQ("Iraq", "IRQ", 368),
		IE("Ireland", "IRL", 372),
		IM("Isle of Man", "IMN", 833),
		IL("Israel", "ISR", 376),
		IT("Italy", "ITA", 380),
		JM("Jamaica", "JAM", 388),
		JP("Japan", "JPN", 392),
		JE("Jersey", "JEY", 832),
		JO("Jordan", "JOR", 400),
		KZ("Kazakhstan", "KAZ", 398),
		KE("Kenya", "KEN", 404),
		KI("Kiribati", "KIR", 296),
		KP("Korea (the Democratic People's Republic of)", "PRK", 408),
		KR("Korea (the Republic of)", "KOR", 410),
		KW("Kuwait", "KWT", 414),
		KG("Kyrgyzstan", "KGZ", 417),
		LA("Lao People's Democratic Republic (the)", "LAO", 418),
		LV("Latvia", "LVA", 428),
		LB("Lebanon", "LBN", 422),
		LS("Lesotho", "LSO", 426),
		LR("Liberia", "LBR", 430),
		LY("Libya", "LBY", 434),
		LI("Liechtenstein", "LIE", 438),
		LT("Lithuania", "LTU", 440),
		LU("Luxembourg", "LUX", 442),
		MO("Macao", "MAC", 446),
		MG("Madagascar", "MDG", 450),
		MW("Malawi", "MWI", 454),
		MY("Malaysia", "MYS", 458),
		MV("Maldives", "MDV", 462),
		ML("Mali", "MLI", 466),
		MT("Malta", "MLT", 470),
		MH("Marshall Islands (the)", "MHL", 584),
		MQ("Martinique", "MTQ", 474),
		MR("Mauritania", "MRT", 478),
		MU("Mauritius", "MUS", 480),
		YT("Mayotte", "MYT", 175),
		MX("Mexico", "MEX", 484),
		FM("Micronesia (Federated States of)", "FSM", 583),
		MD("Moldova (the Republic of)", "MDA", 498),
		MC("Monaco", "MCO", 492),
		MN("Mongolia", "MNG", 496),
		ME("Montenegro", "MNE", 499),
		MS("Montserrat", "MSR", 500),
		MA("Morocco", "MAR", 504),
		MZ("Mozambique", "MOZ", 508),
		MM("Myanmar", "MMR", 104),
		NA("Namibia", "NAM", 516),
		NR("Nauru", "NRU", 520),
		NP("Nepal", "NPL", 524),
		NL("Netherlands (the)", "NLD", 528),
		NC("New Caledonia", "NCL", 540),
		NZ("New Zealand", "NZL", 554),
		NI("Nicaragua", "NIC", 558),
		NE("Niger (the)", "NER", 562),
		NG("Nigeria", "NGA", 566),
		NU("Niue", "NIU", 570),
		NF("Norfolk Island", "NFK", 574),
		MP("Northern Mariana Islands (the)", "MNP", 580),
		NO("Norway", "NOR", 578),
		OM("Oman", "OMN", 512),
		PK("Pakistan", "PAK", 586),
		PW("Palau", "PLW", 585),
		PS("Palestine, State of", "PSE", 275),
		PA("Panama", "PAN", 591),
		PG("Papua New Guinea", "PNG", 598),
		PY("Paraguay", "PRY", 600),
		PE("Peru", "PER", 604),
		PH("Philippines (the)", "PHL", 608),
		PN("Pitcairn", "PCN", 612),
		PL("Poland", "POL", 616),
		PT("Portugal", "PRT", 620),
		PR("Puerto Rico", "PRI", 630),
		QA("Qatar", "QAT", 634),
		MK("Republic of North Macedonia", "MKD", 807),
		RO("Romania", "ROU", 642),
		RU("Russian Federation (the)", "RUS", 643),
		RW("Rwanda", "RWA", 646),
		RE("Réunion", "REU", 638),
		BL("Saint Barthélemy", "BLM", 652),
		SH("Saint Helena, Ascension and Tristan da Cunha", "SHN", 654),
		KN("Saint Kitts and Nevis", "KNA", 659),
		LC("Saint Lucia", "LCA", 662),
		MF("Saint Martin (French part)", "MAF", 663),
		PM("Saint Pierre and Miquelon", "SPM", 666),
		VC("Saint Vincent and the Grenadines", "VCT", 670),
		WS("Samoa", "WSM", 882),
		SM("San Marino", "SMR", 674),
		ST("Sao Tome and Principe", "STP", 678),
		SA("Saudi Arabia", "SAU", 682),
		SN("Senegal", "SEN", 686),
		RS("Serbia", "SRB", 688),
		SC("Seychelles", "SYC", 690),
		SL("Sierra Leone", "SLE", 694),
		SG("Singapore", "SGP", 702),
		SX("Sint Maarten (Dutch part)", "SXM", 534),
		SK("Slovakia", "SVK", 703),
		SI("Slovenia", "SVN", 705),
		SB("Solomon Islands", "SLB", 90),
		SO("Somalia", "SOM", 706),
		ZA("South Africa", "ZAF", 710),
		GS("South Georgia and the South Sandwich Islands", "SGS", 239),
		SS("South Sudan", "SSD", 728),
		ES("Spain", "ESP", 724),
		LK("Sri Lanka", "LKA", 144),
		SD("Sudan (the)", "SDN", 729),
		SR("Suriname", "SUR", 740),
		SJ("Svalbard and Jan Mayen", "SJM", 744),
		SE("Sweden", "SWE", 752),
		CH("Switzerland", "CHE", 756),
		SY("Syrian Arab Republic", "SYR", 760),
		TW("Taiwan (Province of China)", "TWN", 158),
		TJ("Tajikistan", "TJK", 762),
		TZ("Tanzania, United Republic of", "TZA", 834),
		TH("Thailand", "THA", 764),
		TL("Timor-Leste", "TLS", 626),
		TG("Togo", "TGO", 768),
		TK("Tokelau", "TKL", 772),
		TO("Tonga", "TON", 776),
		TT("Trinidad and Tobago", "TTO", 780),
		TN("Tunisia", "TUN", 788),
		TR("Turkey", "TUR", 792),
		TM("Turkmenistan", "TKM", 795),
		TC("Turks and Caicos Islands (the)", "TCA", 796),
		TV("Tuvalu", "TUV", 798),
		UG("Uganda", "UGA", 800),
		UA("Ukraine", "UKR", 804),
		AE("United Arab Emirates (the)", "ARE", 784),
		GB("United Kingdom of Great Britain and Northern Ireland (the)", "GBR", 826),
		UM("United States Minor Outlying Islands (the)", "UMI", 581),
		US("United States of America (the)", "USA", 840),
		UY("Uruguay", "URY", 858),
		UZ("Uzbekistan", "UZB", 860),
		VU("Vanuatu", "VUT", 548),
		VE("Venezuela (Bolivarian Republic of)", "VEN", 862),
		VN("Viet Nam", "VNM", 704),
		VG("Virgin Islands (British)", "VGB", 92),
		VI("Virgin Islands (U.S.)", "VIR", 850),
		WF("Wallis and Futuna", "WLF", 876),
		EH("Western Sahara", "ESH", 732),
		YE("Yemen", "YEM", 887),
		ZM("Zambia", "ZMB", 894),
		ZW("Zimbabwe", "ZWE", 716),
		AX("Åland Islands", "ALA", 248);
		
		private String country;
		private String threeletter;
		private Integer code;
		
		Country(String country, String threeletter, Integer code)
		{
			this.country=country;
			this.threeletter=threeletter;
			this.code=code;
		}
		
		public String getCountry(){return this.country;}
		public String getThreeletter(){return this.threeletter;}
		public Integer getCode(){return this.code;}
	}
	
	//https://www.elastic.co/guide/en/elasticsearch/reference/6.2/mapping-types.html
	@ExportedEnum public enum ElasticType
	{
		DATE("date"),
		FLOAT("float"),
		INTEGER("integer"),
		BOOLEAN("boolean"),
		STRING("text"),
		KEYWORD("keyword"),
		COMPLETION("completion");
		
		private String type;
		
		ElasticType(String type)
		{
			this.type=type;
		}
		
		public String getType(){return type;}
		
		@Override
		public String toString()
		{
			return type;
		}
	}
	
	@ExportedEnum public enum FlagType
	{
		NULL(""),
		H,
		L,
		X;
		
		private String display;
		
		FlagType()
		{
			this.display=this.name();
		}
		
		FlagType(String display)
		{
			this.display=display;
		}
		
		public String getDisplay(){return display;}
		
		public static FlagType find(String value)
		{
			for (FlagType flagType : values())
			{
				if (flagType.name().equalsIgnoreCase(value))
					return flagType;
			}
			return NULL;
		}
	}
	
	// ENUMS_START
	// ENUMS_END
}
