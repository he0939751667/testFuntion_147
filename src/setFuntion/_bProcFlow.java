package setFuntion;

import java.io.File;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Vector;

import jcx.db.talk;
import jcx.jform.bProcFlow;
import jcx.util.convert;

import com.ysp.service.BaseService;
import com.ysp.service.MailService;

public class _bProcFlow extends bProcFlow {
	/**
	 * 更新表單的field資料
	 * 
	 * @param tablename
	 * @param field
	 * @param PKfield
	 * @return
	 */
	public void UPDATE_DATA(talk t, String tablename, String[] field,
			String[] field_data, String PNO) {
		StringBuffer sb = new StringBuffer();
		sb.append("update " + tablename + " set ");
		for (int i = 0; i < field_data.length - 1; i++) {
			field_data[i] = field_data[i] + "',";
		}
		for (int i = 0; i < field_data.length; i++) {
			sb.append(field[i] + "='" + field_data[i]);
		}
		sb.append("' where PNO='" + convert.ToSql(PNO) + "'");
		addToTransaction(sb.toString());
		message("更新完成");
	}
	
	/**
	 * 處理上傳的檔案
	 * 
	 * @param tot_UPLOADS
	 * @return
	 */
	public String[] UPLOAD(String[] tot_UPLOADS) {
		String UPLOAD[] = new String[tot_UPLOADS.length];
		File F1;
		for (int i = 0; i < tot_UPLOADS.length; i++) {
			if (tot_UPLOADS[i].trim().length() != 0) {
				F1 = getUploadFile(tot_UPLOADS[i]);
				if (F1 != null) {
					UPLOAD[i] = ""+F1;
				} else {
					UPLOAD[i] = "";
				}
			} else {
				UPLOAD[i] = "";
			}
		}
		return UPLOAD;
	}
	
	/**
	 * 更新資料+上傳檔案
	 * @param tablename
	 * @param tot_UPLOADS
	 * @param UPLOADS
	 * @param field
	 * @param field_data
	 * @param PNO
	 */
	public void process_save(String tablename, String[] tot_UPLOADS,
			String[] UPLOADS, String[] field, String[] field_data, String PNO) {
		String[] UPLOAD = new String[tot_UPLOADS.length];
		File F1;
		StringBuffer sb = new StringBuffer();
		boolean up = false;
		for (int i = 0; i < UPLOADS.length; i++) {
			if (UPLOADS[i].length() != 0) {
				up = true;
			}
		}
		sb.append("update " + tablename + " set");
		for (int i = 0; i < tot_UPLOADS.length; i++) {
			F1 = getUploadFile(tot_UPLOADS[i]);
			if (F1 != null) {
				UPLOAD[i] = " '" + F1 + "' ";
				sb.append(" " + tot_UPLOADS[i] + " = " + UPLOAD[i].trim() + ",");
			}
		}
		String sql = "";
		for (int i = 0; i < field.length - 1; i++) {
			sql += field[i] + " ='" + convert.ToSql(field_data[i].trim())
					+ "', ";
		}
		sql += field[field.length - 1] + " ='"+ convert.ToSql(field_data[field.length - 1].trim())+"'";
		sb.append(sql + " where PNO ='" + convert.ToSql(PNO) + "' ");
		addToTransaction(sb.toString());
	}

	/**
	 * 新增一筆資料到資料表
	 * 
	 * @param tablename
	 * @param field
	 * @param field_data
	 * @param PNO
	 * @return
	 */
	public void INSERT_DATA(String tablename, String[] field,
			String[] field_data) {
		for (int i = 0; i < field.length - 1; i++) {
			field[i] = field[i] + ",";
		}
		StringBuffer sb = new StringBuffer();
		sb.append("insert into " + tablename + " (");
		for (int i = 0; i < field.length; i++) {
			sb.append(field[i]);
		}
		sb.append(" ) values (");
		for (int i = 0; i < field_data.length - 1; i++) {
			sb.append("'" + field_data[i] + "',");
		}
		sb.append("'" + field_data[field_data.length - 1] + "')");
		addToTransaction(sb.toString());
		message("新增完成");
	}

	public Vector FLOWHistory(talk t, String[][] vid) throws SQLException,
			Exception {
		String ausr[] = new String[vid.length];
		for (int i = 0; i < vid.length; i++) {
			ausr[i] = vid[i][1].trim();
		}
		HashSet set = new HashSet();
		set.addAll(Arrays.asList(ausr));
		String usr[] = (String[]) set.toArray(new String[0]);

		Vector V2 = new Vector();
		for (int i = 0; i < usr.length; i++) {
			String sql = "select EMAIL from HRUSER where EMPID = '"
					+ convert.ToSql(usr[i]) + "' ";
			String r1[][] = t.queryFromPool(sql);
			if (r1.length == 0)
				continue;
			V2.addElement(r1[0][0].trim());
		}
		return V2;
	}

	/**
	 * 發送mail給所有簽核者
	 * 
	 * @param t
	 * @param get_tot_user
	 * @param title
	 * @param content
	 * @param flowService
	 * @param EMPID
	 * @throws SQLException
	 * @throws Exception
	 */
	public void sendallmail(Vector V2, MailService mail, String title,
			String content) throws SQLException, Exception {
		String allusr[] = (String[]) V2.toArray(new String[0]);
		String sendRS = mail.sendMailbccUTF8(allusr, title, content, null, "",
				"text/plain");
		if (sendRS.trim().equals("")) {
			message("EMAIL已寄出通知");
		} else {
			message("EMAIL寄出失敗");
		}
	}

	/**
	 * 新增資料
	 * 
	 * @param tablename
	 * @param field
	 * @param table_data
	 * @throws SQLException
	 * @throws Exception
	 */
	public void INSERT_TABLE_DATA(String tablename, String[] field,
			String[] tot_data) throws SQLException, Exception {
		talk t = getTalk();
		Vector SQL = new Vector();
		String sql = "insert into " + tablename + " (";
		for (int i = 0; i < field.length - 1; i++) {
			sql += field[i] + ",";
		}
		sql += field[field.length - 1] + " ) values (%PNO%,";
		for (int i = 0; i < tot_data.length - 1; i++) {
			sql += "'" + tot_data[i] + "',";
		}
		sql += "'" + tot_data[tot_data.length - 1] + "')";

		SQL.addElement(sql);
		// 處理單號
		String strNewNo = getToday("YYYYmmdd");
		String strNewNo1 = "001";
		sql = "select max(PNO) from " + tablename + " where  PNO like '"
				+ strNewNo + "%' ";
		String s[][] = t.queryFromPool(sql);
		try {
			int d = Integer.parseInt(s[0][0].trim().substring(8, 11));
			d = d + 1001;
			strNewNo1 = "" + d;
			strNewNo1 = strNewNo1.trim().substring(1);
		} catch (Exception e) {
			strNewNo1 = "001";
		}
		String PNO = strNewNo + strNewNo1.trim();
		String se[] = new String[SQL.size()];
		for (int i = 0; i < SQL.size(); i++) {
			String sqle = SQL.elementAt(i).toString();
			sqle = convert.replace(sqle.trim(), "%PNO%", PNO);
			se[i] = sqle.trim();
		}
		t.execFromPool(se);
		message("新增完成");
	}

	/**
	 * 刪除
	 * 
	 * @param t
	 * @param tablename
	 * @param PNO
	 * @throws SQLException
	 * @throws Exception
	 */
	public void deleteData(String tablename, String PNO) throws SQLException,
			Exception {
		addToTransaction("delete from " + tablename + " WHERE PNO ='" + PNO
				+ "'");
		addToTransaction("delete from " + tablename + "_FLOWC WHERE PNO ='"
				+ PNO + "'");
		addToTransaction("delete from " + tablename + "_FLOWC_HIS WHERE PNO ='"
				+ PNO + "'");
		message("刪除完成");
	}

	private void addToTransaction(String[] se) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean action(String arg0) throws Throwable {
		// TODO Auto-generated method stub
		return false;
	}

}
