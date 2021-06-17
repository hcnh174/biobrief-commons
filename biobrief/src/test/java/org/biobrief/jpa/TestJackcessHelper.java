package org.biobrief.jpa;

import org.junit.jupiter.api.Test;

import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.Table;


//gradle --stacktrace --info test --tests *TestJackcessHelper
public class TestJackcessHelper
{	
	//private static final String DATAROOM_DIR="h:/sync/dataroom";
	private static final String DATAROOM_DIR="d:/sync/dataroom";
	
	//@Test @SuppressWarnings("unused")
	public void inspectTable()
	{
		String filename=DATAROOM_DIR+"/肝臓疾患File.mdb";
		Database db=JackcessHelper.openDatabase(filename);
		Table table=JackcessHelper.getTable(db, "まとめ");
		//assertThat(table.getColumns().size()).isGreaterThan(1);
	}
}