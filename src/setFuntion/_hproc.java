package setFuntion;

import hr.common;

import java.io.File;
import java.sql.SQLException;
import java.util.Vector;

import jcx.db.talk;
import jcx.jform.hproc;
import jcx.util.convert;

import com.ysp.service.BaseFlowService;
import com.ysp.service.BaseService;
import com.ysp.service.MailService;
import com.ysp.util.DateTimeUtil;
import com.ysp.field.Mail;

public class _hproc extends hproc {
	/**
	 * �d�ߥ\��ĤT��(�u�d�ߥN�J�������)
	 * 
	 * @param t
	 * @param tablename
	 *            ��ƪ�W��
	 * @param projectname
	 *            ���W��
	 * @param query
	 *            �d�ߤ�����table�̪����
	 * @param field
	 *            �d�ߤ��������
	 * @param field_data
	 *            �d�ߤ����������
	 * @param EMPID
	 *            ���u
	 * @throws SQLException
	 * @throws Exception
	 */
	public void query(talk t, String tablename, String projectname,
			String[] query, String[] field, String[] field_data, String EMPID)
			throws SQLException, Exception {
		// try {
		String sql = "select ";
		for (int i = 0; i < query.length; i++) {
			sql += query[i] + ",";
		}
		sql += "(select F_INP_STAT from " + tablename
				+ "_FLOWC where PNO=a.PNO), 'ñ�ְO��', '�d��' from " + tablename
				+ " a where EMPID='" + EMPID + "'";
		if (field_data[0].length() != 0) {
			sql += "and a." + field[0] + " ='" + field_data[0].trim() + "'";
		}
		if (field_data[1].length() != 0 && field_data[2].length() != 0) {
			sql += "and a." + field[1] + " between '" + field_data[1].trim()
					+ " ' and '" + field_data[2].trim() + "'";
		} else if (field_data[1].length() != 0 && field_data[2].length() == 0) {
			sql += "and a." + field[1] + " >= '" + field_data[1].trim() + "'";
		} else if (field_data[1].length() == 0 && field_data[2].length() != 0) {
			sql += "and a." + field[1] + " <= '" + field_data[2].trim() + "'";
		}
		String s[][] = t.queryFromPool(sql);
		// �btable���̪�ñ�֪��A�A�P�_����s�W�@�q���׻P��������ܪ��C��
		for (int i = 0; i < s.length; i++) {
			if (s[i][query.length].trim().equals("����")
					|| s[i][query.length].trim().equals("�k��")) {
				s[i][query.length] = s[i][query.length].trim()
						+ "<font color=blue>(�w����)</font>";
			} else {
				Vector people = getApprovablePeople(projectname, "a.PNO='"
						+ s[i][0] + "'");
				StringBuffer sb = new StringBuffer();
				if (people != null) {
					if (people.size() != 0) {
						sb.append("(");
						for (int j = 0; j < people.size(); j++) {
							if (j != 0)
								sb.append(",");
							String id1 = (String) people.elementAt(j);
							String name1 = getName(id1);
							sb.append(name1 + ":" + id1);
						}
						sb.append(")");
					}
				}
				s[i][query.length] = s[i][query.length].trim()
						+ "<font color=red>(������)" + sb.toString() + "</font>";
			}
		}
		setTableData("table1", s);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
	}

	/**
	 * �`�Ϊ��dEMPID �m�W�A����
	 * 
	 * @param t
	 * @param EMPID
	 * @return hecname,DEP_NAME,ACC_NO
	 * @throws SQLException
	 * @throws Exception
	 */
	public String[][] user_info_YSH(talk t,String EMPID) throws SQLException,
			Exception {
		String sql = "select hecname,DEP_NAME,DEP_CODE  from user_info_YSH where empid = '"
				+ EMPID.trim() + "'";
		String ret_empid[][] = t.queryFromPool(sql);
		return ret_empid;
	}

	/**
	 * �s�W��Ʀܸ�Ʈw
	 * 
	 * @param t
	 * @param tablename
	 * @param field
	 * @param field_data
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public String[][] add_data(talk t, String tablename, String[] field,
			String[] field_data) throws SQLException, Exception {
		// �B�zfield ex.insert into xxx (XXX,XXX,XXX...
		for (int i = 0; i < field.length - 1; i++) {
			field[i] = field[i] + ",";
		}
		String MUSER = getUser();
		Vector SQL = new Vector();
		// �s�W�D�ɤΨ�y�{
		String sql = "insert into " + tablename + " (";
		for (int i = 0; i < field.length; i++) {
			sql += field[i];
		}
		sql += ") values (%PNO%,";
		for (int i = 0; i < field_data.length - 1; i++) {
			sql += "'" + convert.ToSql(field_data[i]) + "',";
		}
		sql += "'" + convert.ToSql(field_data[field_data.length - 1]) + "')";
		SQL.addElement(sql);
		String chief = "�����D��";
		String now = getNow();
		String sc1 = "insert into " + tablename
				+ "_FLOWC (PNO,F_INP_STAT,F_INP_ID,F_INP_TIME,F_INP_INFO)";
		sc1 += "values (%PNO%,'" + chief + "','" + MUSER + "','" + now
				+ "','�ݳB�z')";
		String sc2 = "insert into " + tablename
				+ "_FLOWC_HIS (PNO,F_INP_STAT,F_INP_ID,F_INP_TIME,F_INP_INFO)";
		sc2 += "values (%PNO%,'�ݳB�z','" + MUSER + "','" + now + "','�ݳB�z')";
		String now1 = DateTimeUtil.getApproveAddSeconds(1);
		String sc3 = "insert into " + tablename
				+ "_FLOWC_HIS (PNO,F_INP_STAT,F_INP_ID,F_INP_TIME,F_INP_INFO)";
		sc3 += "values (%PNO%,'" + chief + "','" + MUSER + "','" + now1 + "','"
				+ chief + "')";

		SQL.addElement(sc1);
		SQL.addElement(sc2);
		SQL.addElement(sc3);
		// �B�z�渹
		while (true) {
			String strNewNo = getToday("YYYYmmdd");
			String strNewNo1 = "001";
			sql = "select max(PNO) from " + tablename + " where  PNO like '"
					+ strNewNo + "%' ";
			String s[][] = getTalk().queryFromPool(sql);
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
			message("��Ʋ��ʧ���");
			return null;
		}
	}

	/**
	 * �`�Ϊ��ϥ�PNO�h�d�ߤ@�Ǫ�檺��ƨó]�wfield���
	 * 
	 * @param talbename
	 * @param field
	 * @param PNO
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public String[][] selectfield(String tablename, String[] field, String PNO)
			throws SQLException, Exception {
		talk t = getTalk();
		for (int i = 0; i < field.length - 1; i++) {
			field[i] = field[i] + ",";
		}
		String sql = "select ";
		for (int i = 0; i < field.length; i++) {
			sql += field[i];
		}
		sql += " from " + tablename + " where PNO='" + PNO + "'";
		String ret[][] = t.queryFromPool(sql);
		for (int i = 0; i < field.length - 1; i++) {
			field[i] = field[i].substring(0, field[i].length() - 1);
		}
		for (int i = 0; i < field.length; i++) {
			setValue(field[i], ret[0][i]);
		}
		return ret;
	}

	/**
	 * �d�߬Y�i�����
	 * 
	 * @param talbename
	 * @param field
	 * @param PNO
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public String[][] selectfromwhere(String tablename, String[] field,
			String PNO) throws SQLException, Exception {
		talk t = getTalk();
		for (int i = 0; i < field.length - 1; i++) {
			field[i] = field[i] + ",";
		}
		String sql = "select ";
		for (int i = 0; i < field.length; i++) {
			sql += field[i];
		}
		sql += " from " + tablename + " where PNO='" + PNO + "'";
		String ret[][] = t.queryFromPool(sql);
		for (int i = 0; i < field.length - 1; i++) {
			field[i] = field[i].substring(0, field[i].length() - 1);
		}
		return null;
	}

	/**
	 * ����H������ƨS����g
	 * 
	 * @param field
	 * @param field_name
	 * @return
	 */
	public boolean forget_field(String[] field, String[] field_name) {
		Boolean forget = true;
		for (int i = 0; i < field.length; i++) {
			if (field[i].length() == 0) {
				message(field_name[i] + "���o����!");
				forget = false;
				break;
			}
		}
		return forget;
	}

	 /**
	 * �H�eemail
	 *
	 * @param t
	 * @param EMPID
	 * @param flowService
	 * @throws Throwable
	 */
	 public void sendMail(talk t, String EMPID, BaseService service, String title,
	 String email_content) throws Throwable {
			String chief[][] = getchief(EMPID);
			String[] usr = null;
			Vector vc = new Vector();
			String r2[][] = t.queryFromPool("select EMAIL from HRUSER where EMPID = '" + convert.ToSql(chief[0][0]) + "' ");
			if (r2.length > 0) {
				for (int i = 0; i < r2.length; i++) {
					vc.addElement(r2[0][0].trim());
				}
			}
			usr = (String[]) vc.toArray(new String[0]);
			String HRSYS[][] = t.queryFromPool("select HRADDR from HRSYS");
			String title1 = "";
			if (HRSYS.length != 0) {
				if (HRSYS[0][0].trim().length() != 0) {
					if (HRSYS[0][0].trim().toUpperCase().startsWith("HTTP"))
						title1 = "(" + HRSYS[0][0].trim() + ")";
					else
						title1 = "(http://" + HRSYS[0][0].trim() + ")";
				}
			}
			title += title1;
			String content = "" + title + "\r\n";
			content += email_content;
			if ((usr.length != 0) && (!content.trim().equals(""))) {
				try {
					MailService mail = new MailService(service);
					String sendRS = mail.sendMailbccUTF8(usr, title, content, null, "", "text/plain");
				} catch (Exception e) {
					System.out.println("" + e);
				}
			}
	 }

	/**
	 * �M�ũҦ����
	 * 
	 * @param t
	 * @param field
	 */
	public void Clear_field(talk t, String[] field) {
		for (int i = 0; i < field.length; i++) {
			setValue(field[i], "");
		}
	}

	/**
	 * �B�z�渹
	 * 
	 * @param t
	 * @param tablename
	 * @throws SQLException
	 * @throws Exception
	 */
	public void Process_PNO(talk t, String tablename) throws SQLException,
			Exception {

		try {
			String strNewNo = getToday("YYYYmmdd");
			String strNewNo1 = "001";
			String sql = "select max(PNO) from " + tablename
					+ " where  PNO like '" + strNewNo + "%' ";
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
			setValue("PNO", PNO);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * �s�W�@����ƨ��ƪ�
	 * 
	 * @param tablename
	 * @param field
	 * @param field_data
	 * @param PNO
	 * @return
	 * @throws Exception
	 * @throws SQLException
	 */
	public void INSERT_DATA(talk t, String tablename, String[] field,
			String[] field_data) throws SQLException, Exception {
		Vector SQL = new Vector();
		for (int i = 0; i < field.length - 1; i++) {
			field[i] = field[i] + ",";
		}
		String sql = "insert into " + tablename + " (PNO,";
		for (int i = 0; i < field.length; i++) {
			sql += field[i];
		}
		sql += " ) values (%PNO%,";
		for (int i = 0; i < field_data.length - 1; i++) {
			sql += "'" + field_data[i] + "',";
		}
		sql += "'" + field_data[field_data.length - 1] + "')";
		SQL.addElement(sql);
		// �B�z�渹
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
		message("�s�W����");
	}

	/**
	 * �̳渹��s���e
	 * 
	 * @param tablename
	 * @param field
	 * @param field_data
	 * @param PNO
	 * @throws SQLException
	 * @throws Exception
	 */
	public void update_data(talk t,String tablename, String[] field,
			String[] field_data, String PNO) throws SQLException, Exception {
		String sql = "update " + tablename + " set ";
		for (int i = 0; i < field.length - 1; i++) {
			sql += field[i] + "='" + field_data[i] + "',";
		}
		sql += field[field.length - 1] + "='" + field_data[field.length - 1]
				+ "' where PNO='" + PNO + "'";
		t.execFromPool(sql);
		message("��s����!");
	}

	/**
	 * �]�w��ƪ��Ҧ��������
	 * 
	 * @param tablename
	 * @param field
	 * @param table_PNO
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public String[][] setDetail(String tablename, String[] field,
			String table_PNO) throws SQLException, Exception {
		talk t = getTalk();
		// �B�zfield ex.select xx,xx,xx
		for (int i = 0; i < field.length - 1; i++) {
			field[i] = field[i] + ",";
		}
		// �}�lselect
		String sql = "select ";
		for (int i = 0; i < field.length; i++) {
			sql += field[i];
		}
		sql += " from " + tablename + " a where a.PNO='" + table_PNO + "'";
		String ret[][] = t.queryFromPool(sql);
		// �B�zfield ��^��
		for (int i = 0; i < field.length - 1; i++) {
			field[i] = field[i].substring(0, field[i].length() - 1);
		}
		// setValue�Ҧ����
		for (int i = 0; i < field.length; i++) {
			setValue(field[i], ret[0][i].trim());
		}
		return ret;
	}

	/**
	 * �B�z�W�Ǫ��ɮ�
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
	 * �B�z�U�����ɮ�
	 * @param tot_FF
	 * @param download
	 */
	public void download_field(String[] tot_FF, String[] download) {
		for (int i = 0; i < tot_FF.length; i++) {
			if (tot_FF[i].trim().length() == 0)
				continue;
			setValue(download[i],
					"<a href=\"" + getDownloadURL(tot_FF[i].trim())
							+ "\">�U��</a>");
		}
	}
	/**
	 * �B�z�U�Ԧ����
	 * @param t
	 * @param condition ����
	 * @param Reference �]Reference�����
	 * @throws SQLException
	 * @throws Exception
	 */
	public void setPULL(talk t,String condition,String Reference) throws SQLException, Exception{
		String sql = "select EMPID,HECNAME from user_info_YSH where "+condition;
		String empid[][] = t.queryFromPool(sql);
		Vector V1 = new Vector();
		Vector V2 = new Vector();
		V1.addElement("");
		V2.addElement("");
		for(int i=0;i<empid.length;i++){
			V1.addElement(empid[i][0]+" "+empid[i][1]);
			V2.addElement(empid[i][0]);
		}
		setReference(Reference, V1, V2);
	}
	
	/**
	 * ���oYSH��T������NO
	 * @param t
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public String[][] getYSHIT_NO() throws SQLException, Exception{
		talk t = getTalk();
		String sql = "select DEP_NO from HRUSER_DEPT_BAS where DEP_CODE='00900'";
		String ret[][] = t.queryFromPool(sql);
		return ret;
	}
	
	/**
	 * ���oYSH��T�������D��ID
	 * @param t
	 * @param DEPT
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public String[][] getIT_CHIEF(talk t,String DEPT) throws SQLException, Exception{
		String sql = "select DEP_CHIEF from HRUSER_DEPT_BAS where DEP_NO='"+DEPT+"'";
		String ret[][] = t.queryFromPool(sql);
		return ret;
	}
	
	/**
	 * ���o�D�ު�id
	 * @param t
	 * @param EMPID
	 * @return dep_chief,parent_no
	 * @throws SQLException
	 * @throws Exception
	 */
	public String[][] getchief(String EMPID) throws SQLException, Exception{
		talk t = getTalk();
		String sql = "select b.dep_chief,b.parent_no from hruser a,hruser_dept_bas b where a.dept_no=b.dep_no and a.empid='"+EMPID+"'";
		String chief[][] = t.queryFromPool(sql);
		return chief;		
	}
	/**
	 * ���o�W�ťD�ު�id
	 * @param t
	 * @param parent_no
	 * @return dep_chief
	 * @throws SQLException
	 * @throws Exception
	 */
	public String[][] getparent_chief(String parent_no) throws SQLException, Exception{
		talk t = getTalk();
		String sql= "select dep_chief from hruser_dept_bas where dep_no='"+parent_no+"'";
		String p_chief[][] = t.queryFromPool(sql);
		return p_chief;
	}
	@Override
	public String action(String arg0) throws Throwable {
		// TODO Auto-generated method stub
		return null;
	}

}
