package org.biobrief.generator;

import java.util.Map;

import org.biobrief.util.DataFrame;

import com.google.common.collect.Maps;

public enum I18n
{
	open(""),
	
	// I18N_START
	todo("[todo]"),
	blank(""),
	dateFormat("Y/m/d"),
	main_title("消化器・代謝内科　患者情報管理システム"),
	error("Error"),
	current_user("利用者"),
	login("ログイン"),
	logout("ログアウト"),
	manual("マニュアル"),
	interview_sheet("問診・初診時データシートdaa"),
	announcements("お知らせ"),
	no_announcements("No announcements"),
	login_failed("Invalid username or password."),
	confirm_logout("システムからログアウトしますか？"),
	confirm_delete_patient("選択された患者情報を全て削除しますか？"),
	notify_patient_added("患者情報を追加しました"),
	notify_patient_deleted("患者情報を全て削除しました"),
	notify_deleted("削除しました"),
	notify_saved("保存しました"),
	notify_added("Item added"),
	notify_passwords_do_not_match("Passwords do not match"),
	username("ユーザーID"),
	password("パスワード"),
	changepassword("パスワードを変更する"),
	hyphen("－"),
	add("新規"),
	addsnp("新規(SNP)"),
	edit("編集"),
	save("保存"),
	del("削除"),
	position("表示順"),
	reset("リセット"),
	close("閉じる"),
	sync("リロード"),
	success("Success"),
	users("ログインユーザー"),
	confirm_delete("削除確認"),
	confirm_delete_items("この項目を削除してもよろしいですか？"),
	confirm_reset("ﾘｾｯﾄ確認"),
	confirm_reset_form("この項目をリセットしてもよろしいですか？"),
	output("出力"),
	upload("アップロード"),
	excel("Excel"),
	note("備考"),
	cancel("キャンセル"),
	detection_type("検査法"),
	name("名称"),
	hirodaiId("広大ID"),
	romaji("ローマ字"),
	firstHcc("初発HCC"),
	hccRelapse("再発HCC"),
	patients("患者一覧"),
	add_patient("新規患者登録"),
	add_snp_patient("SNP新規患者登録"),
	browser("患者ブラウザ"),
	edit_announcement("お知らせ登録"),
	announcement_title("タイトル"),
	announcement_text("内容"),
	announcement_startdate("表示開始日"),
	announcement_enddate("表示終了日"),
	announcement_author("作成・更新者"),
	hospitals("病院・施設"),
	edit_hospital("病院施設ﾏｽﾀ登録"),
	hospital_name("病院施設名称"),
	hospital_kana("病院施設名称ｶﾅ"),
	hospital_abbr("病院施設略名称"),
	hospital_address("住所"),
	hospital_zipcode("郵便番号"),
	hospital_phone("電話番号"),
	prefecture("府県"),
	latitude("緯度"),
	longitude("経度"),
	url("URL"),
	initials("肝研ID頭文字"),
	edit_initial("肝研ID頭文字ﾏｽﾀ登録"),
	initial_name("肝研ID頭文字名称"),
	initial_note("備考"),
	therapies("治療種類"),
	edit_therapy("治療種類ﾏｽﾀ登録"),
	therapy_name("治療種類名称"),
	therapy_combo("治療方法"),
	therapy_checklabel_na("核酸アナログ"),
	therapy_checklabel_ifn("IFN"),
	therapy_checklabel_seq("SeqTx"),
	therapy_note("備考"),
	drugs("治療薬"),
	edit_drug("治療薬ﾏｽﾀ登録"),
	drug_kegg("KEGG"),
	drug_name("治療薬名称"),
	drug_kana("治療薬名称ｶﾅ"),
	drug_abbr("治療薬略名称"),
	drug_checkboxgroup("ウィルス"),
	drug_checklabel_hbv("HBV"),
	drug_checklabel_hcv("HCV"),
	drug_combo("治療薬種類"),
	drug_unit("単位"),
	drug_startdate("使用開始日"),
	drug_enddate("使用終了日"),
	doctors("主治医"),
	edit_doctor("主治医ﾏｽﾀ登録"),
	doctor_name("主治医名前"),
	doctor_kana("主治医名前ｶﾅ"),
	doctor_hospital_abbr("病院施設略名称"),
	doctor_email("E-Mail"),
	diagnoses("診断"),
	diagnose_name("診断名称"),
	diagnose_checkboxgroup("診断区分"),
	diagnose_checklabel_before("初診時"),
	diagnose_checklabel_after("最終診断"),
	diagnose_note("備考"),
	outcomes("効果判定"),
	edit_outcome("効果判定ﾏｽﾀ登録"),
	outcome_name("効果判定名称"),
	outcome_description("備考"),
	outcome_combo("効果判定区分"),
	reasons("理由"),
	genotypes("Genotypes"),
	edit_genotype("Genotypeﾏｽﾀ登録"),
	genotype_name("Genotype"),
	genotype_combo("ウィルス"),
	genotype_checklabel_hbv("HBV"),
	genotype_checklabel_hcv("HCV"),
	genotype_note("備考"),
	hcvGenotype("HCV genotype"),
	hbvGenotype("HBV genotype"),
	m_labtests("電カル採血項目"),
	edit_labtests("電カル採血項目ﾏｽﾀ登録"),
	labtests_name("採血項目名称"),
	labtests_unit("単位"),
	labtests_low("基準値(最小)"),
	labtests_high("基準値(最高)"),
	labtests_note("備考"),
	labtests_checkboxgroup("出力先"),
	labtests_hbv("HBV"),
	labtests_hcv("HCV"),
	labtests_nbnc("NBNC"),
	labtests_hcc("HCC"),
	checkup("電カル採血項目"),
	m_labtestformat("採血取込フォーマット"),
	edit_labtestformat("採血取込フォーマット登録"),
	labtestformat_name("フォーマット名称"),
	labtestformat_type("フォーマットタイプ"),
	labtestformat_description("備考"),
	edit_user("ログインユーザーマスタ登録"),
	user_repassword("確認用パスワード"),
	user_name("表示ユーザー名"),
	user_kana("フリガナ"),
	user_email("E-mail"),
	user_role("利用権限"),
	user_affiliation("所属"),
	user_enabled("使用可能"),
	itemsroles("項目別権限"),
	items_name("項目名"),
	roles_name("権限"),
	itemsroles_edit("能否"),
	labtests("採血"),
	labresults("採血データ"),
	labtest_name("項目名"),
	labtest_result("結果"),
	labtest_range("基準値"),
	labtest_hbvhcv("HBV/HCV"),
	savedSerumSample("保存血清"),
	labresultdate("採血日"),
	edit_labresult("採血データ 登録"),
	dbno("DBNo"),
	patient_kana("フリガナ"),
	patient_name("患者名"),
	hirodai_id("広大ID"),
	patient_maiden("旧姓"),
	sex("性別"),
	birthdate("生年月日"),
	mainHospital("主治療施設"),
	mainHospitalNo("主治療施設ID"),
	mainHospitalFirstDate("主治療施設初診日"),
	mainDoctor("主治療施設外来主治医"),
	subHospital("副治療施設"),
	deceased("生死"),
	lifeAndDeath("生死確認"),
	dateOfDeath("生死確認日"),
	hospital_no("ID"),
	outpatient_doctor("外来主治医"),
	first_date("初診日"),
	first_date_age("初診時年齢"),
	introduce("紹介"),
	introducingHospital("紹介元"),
	introducedHospital("紹介先"),
	causeOfDeath("死因"),
	causeOfDeathNote("備考"),
	turningpoint("経過観察"),
	diagnosischange("経過中の診断の変更"),
	occupational_history("職業歴"),
	age("年齢"),
	age_at("歳時"),
	age_from("歳～"),
	kg("kg"),
	cm("cm"),
	sai("歳"),
	kai("回"),
	alive("生"),
	dead("死"),
	updating("更新中"),
	history("備考"),
	edit_diagnose("診断登録"),
	first_diagnose("初診時診断"),
	last_diagnose_date("最終診断確認日"),
	last_diagnose("最終診断"),
	edit_turningpoint("経過観察登録"),
	turningpoint_type("転機"),
	turningpoint_date("転機年月日"),
	drug_kind("治療種類"),
	AsylumLiverTherapy("肝庇護療法"),
	drug_tx("治療薬"),
	drug_kindtx("治療種類 / 治療薬"),
	amount("容量"),
	treatment_capacity("治療容量"),
	treatment_startdate("開始日"),
	treatment_enddate("終了・中止日"),
	treatment_continuedate("継続確認日"),
	treatment_seqtx_check("SeqTx"),
	treatment_startObjective1("開始目的１"),
	treatment_startObjective2("開始目的２"),
	treatment_startState("開始状況"),
	treatment_stopReason("中止理由"),
	treatment_startReason("開始理由"),
	treatment_dosingPeriod("投与期間"),
	treatment_outcome("効果"),
	treatment_outcome_br("効果(BR)"),
	treatment_outcome_vr("効果(VR)"),
	treatment_note("備考"),
	dosage("治療項目"),
	dosages("治療項目"),
	treatment_labresults("治療・採血関連付け"),
	edit_treatmentitem("治療項目登録"),
	examdate("検査日"),
	disease("病名"),
	biopsy_hospital("施行施設"),
	biopsy_doctor("入院主治医"),
	load_labresults("採血データ取込"),
	load_biopsyresults("肝生検データ取込"),
	load_interviews("問診取込"),
	output_labupdate("採血データ更新対象者出力"),
	labupdate_confirm("更新対象者出力確認"),
	labupdate_confirm_detail("採血データの更新対象者を出力しますか？"),
	edit_outputdata("出力情報登録"),
	outputdata_dataname("出力情報名"),
	outputdata_username("管理者"),
	outputdata_note("備考"),
	outputdata_sqlnote("SQL"),
	userqueries("患者情報出力"),
	edit_userquery("出力情報登録"),
	userquery_dataname("出力情報名"),
	userquery_username("管理者"),
	userquery_description("Description"),
	userquery_note("備考"),
	userquery_sqlnote("SQL"),
	basic("基本"),
	basicinfo("基本情報"),
	basicinfo_hospital("基本情報(患者名・主治療施設等)"),
	basicinfo_body("基本情報(身体情報)"),
	basicinfo_family("基本情報(既往歴・家族歴・診断)"),
	basicinfo_interview("基本情報(問診)"),
	basicinfo_progress("基本情報(経過・その他治療)"),
	download("ダウンロード"),
	diagnosis("診断"),
	conditions("診断名"),
	familyHospital("かかりつけの病院"),
	hbv("HBV"),
	hcv("HCV"),
	nbnc("NBNC"),
	hcc("HCC"),
	snp("SNP"),
	biopsy("肝生検"),
	treatment("治療内容"),
	treatments("治療"),
	doctor("治医"),
	hospital("病院施設"),
	search("検索"),
	print("印刷"),
	submit("設定"),
	hospitalizations("通院履歴"),
	drugresistance("耐性株"),
	edit_drugresistance("耐性株"),
	edit_hospitalization("通院登録"),
	tagfilter("タグフィルタ"),
	period("治療経過"),
	ifntx("IFN治療"),
	natx("核酸アナログ治療"),
	seqtx("Sequential治療"),
	othertx("保存治療・他"),
	tx_details("治療内容"),
	relation("関連付け"),
	relation_view("関連付け参照"),
	relation_output("関連情報出力"),
	relation_import("関連情報取込"),
	masters("マスター管理"),
	dataentry("データ入力"),
	loaddata("出力取込"),
	nav_main("ﾒｲﾝﾒﾆｭｰ"),
	nav_patients("データ入力"),
	nav_loaddata("出力取込"),
	nav_masters("マスター管理"),
	nav_printSheet("データシート印刷"),
	nav_admin("管理"),
	admin("管理"),
	patienttree_title("基本情報"),
	biopsy_date("施行年月日"),
	fibrosis("線維症"),
	activity("炎症活動"),
	biopsy_purpose("肝生検目的"),
	hai("HAI score"),
	normal("健常人"),
	father("父"),
	mother("母"),
	siblings("兄弟・姉妹"),
	other("他"),
	firstExam("初診時"),
	lastExam("最終"),
	initialDiagnosis("初診時診断"),
	acute("急性増悪"),
	treatmentHistory("病歴"),
	detectionType("検査法"),
	drug_resistance("薬剤耐性株"),
	lastDiagnosis("最終診断"),
	lastDiagnosisDate("最終確認日"),
	lastExamDate("最終受診日"),
	lastUpdatedDate("データ更新日"),
	status("更新終了"),
	lastUpdated("最終更新"),
	importType("取込種別"),
	source("取込種別"),
	recentActivity("履歴"),
	substitutions("coreｱﾐﾉ酸変異"),
	edit_substitution("coreｱﾐﾉ酸変異登録"),
	logviewer("log viewer"),
	waitTitle("Loading"),
	waitMessage("Loading..."),
	loading("Loading..."),
	warning("Warning"),
	unreachable("Server is unreachable"),
	notification("Notification"),
	noRecordsSelected("Please select a row"),
	listvalues("List values"),
	edit_listvalue("Edit list value"),
	listvalue_type("Type"),
	listvalue_name("名称"),
	listvalue_subtype("Subtype"),
	aliases("Aliases"),
	revisions("改訂"),
	revision("改訂"),
	totalCount("rows"),
	txcount("回目"),
	txnum("Tx回数"),
	TITLE("広島大学病院　消化器・代謝内科　患者情報管理システム"),
	PATIENTS("患者一覧"),
	BASIC("基本"),
	OVERVIEW("一覧"),
	CONDITIONS("診断名"),
	BIOPSIES("肝生検"),
	CHECKUPS("採血"),
	LABTESTS("電カル採血項目"),
	BASICINFO("基本情報"),
	BASICINFO_HOSPITAL("患者名・主治療施設等"),
	BASICINFO_BODY("身体情報"),
	BASICINFO_FAMILY("既往歴・家族歴・診断"),
	BASICINFO_INTERVIEW("問診"),
	BASICINFO_PROGRESS("経過・その他治療"),
	BACKWARD_COMPATIBILITY("下位互換入力"),
	HOSPITALIZATIONS("通院履歴"),
	SNP("SNPs"),
	HBV("HBV"),
	HBVF("HBV FileMaker"),
	HCV("HCV"),
	HCVF("HCV FileMaker"),
	INTERVIEW_SHEET("問診票/初診時シート"),
	NBNC("NBNC"),
	HCC("HCC"),
	NORMAL("Normal"),
	TREATMENTS("治療"),
	DOSAGES("薬治療"),
	IFN_TREATMENT("IFN治療"),
	NA_TREATMENT("核酸ｱﾅﾛｸﾞ治療"),
	OTHER_TREATMENT("保存治療・他"),
	SEQ_TREATMENT("Sequential治療"),
	TREATMENT_HISTORY("治療履歴"),
	PERIOD("時期"),
	CHECKUP_DATE("採血日"),
	JP_SPACE(""),
	PATIENT_REVISION_HISTORY("Revisions"),
	LIFESTYLECHANGES("生活習慣変更"),
	legacy("旧データ"),
	legacydata("旧データ"),
	fmfirstexam("肝研患者基本データシート"),
	fmhbv("HBVfile"),
	fmhcv("HCVfile"),
	fmbiopsy("肝生検"),
	accessmatome("肝研患者基本データシート"),
	accesspatient("tbl個人"),
	accesssnp("tblSNP"),
	accesssnpanon("SNP匿名化"),
	accessbiopsy("tbl個人肝生検"),
	accessbiopsyanon("tbl肝生検匿名化番号"),
	accesstx("tbl個人TX"),
	naika("電子カルテ"),
	access("アクセス"),
	filemaker("ファイルメーカー"),
	interview("問診票"),
	txsheet("IFNシート"),
	lifestyle_disease("生活習慣病"),
	complications("合併症"),
	facets("絞込条件"),
	field("ﾌｨｰﾙﾄﾞ"),
	legacydatafield("旧システムデータ"),
	fieldmetadata("メタデータ"),
	fieldchanges("変更"),
	fieldcounts("カウント"),
	emptyText("No records"),
	caseType("ウイルス"),
	lifestylechanges("生活習慣変更"),
	hospitalmap("病院地図"),
	JSON("JSON"),
	variant("突然変異"),
	variants("突然変異"),
	files("ファイル"),
	home("Home"),
	solr("Solr"),
	ICON_BLUE("icon-bullet-blue"),
	ICON_GREEN("icon-bullet-green"),
	ICON_GREY("icon-bullet-grey"),
	ICON_RED("icon-bullet-red"),
	ICON_BASIC_INFO("icon-basicinfo"),
	ICON_PATIENT_DATA("icon-patientdata"),
	ICON_HOSPITAL("icon-hospital"),
	ICON_FILEMAKER("icon-filemaker"),
	ICON_VIRUS("icon-virus"),
	ICON_MICROSCOPE("icon-microscope"),
	ICON_TESTTUBE("icon-testtubes"),
	ICON_SYRINGE("icon-syringe"),
	ICON_PILLS("icon-pills"),
	ICON_FIRSTAID("icon-firstaid"),
	legacy_access_biopsy("肝生検"),
	legacy_access_snpanon("SNP匿名化"),
	legacy_access_snp("SNP"),
	legacy_access_tx("個人TX"),
	legacy_filemaker_biopsy("肝生検"),
	legacy_filemaker_firstexam("肝研患者基本"),
	legacy_filemaker_hbv("HBV"),
	legacy_filemaker_hcv("HCV"),
	legacy_interview_form("Interview"),
	legacy_txsheet_daa("経口二剤"),
	legacy_txsheet_dcvasv("DCV/ASV"),
	legacy_txsheet_interferon("IFNシート"),
	legacy_txsheet_simeprevir("Simeprevir"),
	legacy_txsheet_telaprevir24("Telaprevir 24w"),
	legacy_txsheet_telaprevir48("Telaprevir 48w"),
	legacy_txsheet_sofosbuvir("SOF/RBV"),
	settings("設定"),
	nafld("NAFLD"),
	patient("患者"),
	condition("症状"),
	hospitalization("入院"),
	dateValue("日付値"),
	substitution("突然変異"),
	turningPoint("転機"),
	snpGenotypeReport("SNPの遺伝子型レポート"),
	weight("体重"),
	height("身長"),
	exam("身体検査"),
	alcohol("酒"),
	ctscan("CTスキャン"),
	mcu("MCU"),
	occupation("職業"),
	ogtt("OGTT"),
	previousTreatment("前処置"),
	ultrasound("超音波"),
	hbvLabResult("HBV 採血データ"),
	hcvLabResult("HCV 採血データ"),
	nafldLabResult("NAFLD 採血データ"),
	labResult("採血データ"),
	json("JSON"),
	biopsies("肝生検"),
	reports("レポート"),
	expand("開"),
	export("Export");
	// I18N_END
	
	private final String text;
	
	I18n(String text)
	{
		this.text=text;
	}
	
	public String getText(){return text;}
	
	
	private static final String CONFIG_FILE="data/dictionary/i18n.txt";
	
	public static Map<String,String> load()
	{
		Map<String, String> i18n=Maps.newLinkedHashMap();
		DataFrame<String> dataframe=DataFrame.parseTabFile(CONFIG_FILE);
		for (String name : dataframe.getRowNames())
		{
			String value=dataframe.getStringValue("value", name);
			i18n.put(name, value);
		}
		return i18n;
	}
}