package setFuntion;

import java.sql.SQLException;
import java.util.Vector;

import jcx.db.talk;
import jcx.jform.bRule;

public class _bRule extends bRule{
	/**
	 * 取得主管的id
	 * @param t
	 * @param EMPID
	 * @return dep_chief,parent_no
	 * @throws SQLException
	 * @throws Exception
	 */
	public String[][] getchief(talk t,String EMPID) throws SQLException, Exception{
		String sql = "select b.dep_chief,b.parent_no from hruser a,hruser_dept_bas b where a.dept_no=b.dep_no and a.empid='"+EMPID+"'";
		String chief[][] = t.queryFromPool(sql);
		return chief;		
	}
	/**
	 * 取得上級主管的id
	 * @param t
	 * @param parent_no
	 * @return dep_chief
	 * @throws SQLException
	 * @throws Exception
	 */
	public String[][] getparent_chief(talk t,String parent_no) throws SQLException, Exception{
		String sql= "select dep_chief from hruser_dept_bas where dep_no='"+parent_no+"'";
		String p_chief[][] = t.queryFromPool(sql);
		return p_chief;
	}
	/**
	 * 取得YSH資訊部門的NO
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
	 * 取得YSH資訊部門的主管ID
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
	@Override
	public Vector getIDs(String arg0) throws Throwable {
		// TODO Auto-generated method stub
		return null;
	}

}
