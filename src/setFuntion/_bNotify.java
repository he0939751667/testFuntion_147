package setFuntion;

import java.sql.SQLException;
import java.util.Vector;

import jcx.db.talk;
import jcx.jform.bNotify;
import jcx.util.convert;

import com.ysp.service.BaseService;
import com.ysp.service.MailService;
public class _bNotify extends bNotify{
	/**
	 * 取得簽核者的mail
	 * @param t
	 * @param vid
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public Vector getmail(talk t,Vector vid) throws SQLException, Exception{
		Vector V2 = new Vector();
		// 查詢簽核者的email
		for (int i = 0; i < vid.size(); i++) {
			String sql = "select EMAIL from HRUSER where EMPID = '"
					+ convert.ToSql(vid.elementAt(i).toString()) + "' ";
			String r1[][] = t.queryFromPool(sql);
			if (r1.length == 0)
				continue;
			V2.addElement(r1[0][0].trim());
		}
		if (V2.size() == 0) return null;
		return V2;
	}
	/**
	 * 寄出mail
	 * @param V2
	 * @param flowService
	 * @param title
	 * @param content
	 * @throws Exception 
	 */
	public void sendmail(Vector V2,MailService mail,String title,String content) throws Exception{
		
		String usr[] = (String[]) V2.toArray(new String[0]);
		String sendRS = mail.sendMailbccUTF8(usr, title, content, null, "",
				"text/plain");

		if (sendRS.trim().equals("")) {
			message("EMAIL已寄出通知");
		} else {
			message("EMAIL寄出失敗");
		}
	}
	/**
	 * 設定第一次欄位資料
	 * @param field
	 * @return
	 */
	public void set_first_table_data(String[] field){
		for(int i=0;i<field.length;i++){
			field[i] = getValue(field[i]);
		}
		String first_data[][] = {field};
		for(int i=0;i<first_data.length;i++){
			setTableData("table1", first_data);
		}
	}
	/**
	 * 設定新增一筆的table資料
	 * @param getData
	 * @param field
	 */
	public void set_total_table_data(String[][] getData,String[] field){
		for(int i=0;i<field.length;i++){
			field[i] = getValue(field[i]);
		}
		String tot_data[][] = new String[getData.length][field.length];
		for(int i=0;i<getData.length-1;i++){
			for(int j=0;j<field.length;j++){
				tot_data[i][j] = getData[i][j];
			}
		}
		for(int i=0;i<field.length;i++){
			tot_data[getData.length-1][i] = field[i];
		}
		for(int i=0;i<tot_data.length;i++){
			setTableData("table1", tot_data);
		}
	}
	/**
	 * 清除欄位資料
	 * @param field
	 */
	public void clear_field(String[] field){
		for(int i=0;i<field.length;i++){
			setValue(field[i],"");
		}
	}
	@Override
	public void actionPerformed(String arg0) throws Throwable {
		// TODO Auto-generated method stub
		
	}

}
