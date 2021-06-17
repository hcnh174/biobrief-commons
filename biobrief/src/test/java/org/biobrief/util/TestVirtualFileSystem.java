package org.biobrief.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.biobrief.util.VirtualFileSystem.IFolder;
import org.junit.jupiter.api.Test;

//gradle --stacktrace --info test --tests *TestVirtualFileSystem
public class TestVirtualFileSystem
{
	@Test
	public void load()
	{
		VirtualFileSystem vfs=createVirtualFileSystem();
		System.out.println(JsonHelper.toJson(vfs));
		assertThat(true).isEqualTo(true);
	}
	
	public static VirtualFileSystem createVirtualFileSystem()
	{
		String dir="x:";
		//String name="schedule";
		VirtualFileSystem vfs=new VirtualFileSystem(dir);//, name);
		vfs.skipPrefix(".");
		
		vfs.skipSuffix(".dat");
		vfs.skipSuffix(".yaml");
		vfs.skipSuffix(".lock");

		vfs.skipDir("呉医療センター（C）");
		vfs.skipDir("県立広島病院（B）");
		vfs.skipDir("2020年12月説明会資料");
		vfs.skipDir("【B】県立広島病院");
		vfs.skipDir("【C】呉医療センター");
		vfs.skipDir("【D】広島市立安佐市民病院");
		vfs.skipDir("123_広島大学病院_クリーンアップ");
		vfs.skipDir("ctdb");
		vfs.skipDir("dojo");
		vfs.skipDir("OLDA014809812710");
		vfs.skipDir("trashbox");
		vfs.skipDir("BAKmeetings");
		vfs.skipDir(".DS_Store");
		vfs.skipDir(".thumbnail");
		vfs.skipDir(".webview");
		vfs.skipDir(".webaxs_S");
		vfs.skipDir(".webaxs_M");
		vfs.skipDir(".webaxs_L");
		vfs.skipDir(".webaxs_LL");
		vfs.skipDir(".webaxs_3L");
		
		VirtualFileSystem.IFolder schedule=vfs.getRoot().addVirtualFolder(dir, "schedule");
		
		IFolder meeting=schedule.addVirtualFolder(dir, "meeting-2021-06-08");
		meeting.addFolder(dir+"/A107113647698");
		meeting.addFolder(dir+"/A107211364189");
		meeting.addFolder(dir+"/A104413321690");
		meeting.addFolder(dir+"/A104712534525");

		meeting=schedule.addVirtualFolder(dir, "meeting-2021-06-15");
		meeting.addFolder(dir+"/A014912776688");
		meeting.addFolder(dir+"/A105494040060");
		meeting.addFolder(dir+"/A104413321690");
		
		meeting=schedule.addVirtualFolder(dir, "meeting-2021-06-22");
		meeting.addFolder(dir+"/A105813309232");
		meeting.addFolder(dir+"/A106413648399");
		meeting.addFolder(dir+"/B102001886555");
		meeting.addFolder(dir+"/B102306438463");
		
		return vfs;
	}
}

