package cn.ffcs.demo.db;

import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import cn.ffcs.memory.ResultSetHandler;

/**
 * 
 * 
 * @Description:
 * @Copyright: Copyright (c) 2013 FFCS All Rights Reserved
 * @Company: 北京福富软件有限公司
 * @author 黄君毅 2013-8-5
 * @version 1.00.00
 * @history:
 * 
 */
public class JSONArrayHandler implements ResultSetHandler<JSONArray> {

	private boolean camel;
	private SimpleDateFormat sdf;

	public JSONArrayHandler() {
		this(true);
	}	
	
	public JSONArrayHandler(boolean camel) {
		this.camel = camel;
		this.sdf = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
	}


	@Override
	public JSONArray handle(ResultSet rs) throws SQLException {
		JSONArray array = new JSONArray();

		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();

		while (rs.next()) {
			if (columnCount == 1) {
				array.put(rs.getObject(1));
				continue;
			}
			JSONObject jsonObj = new JSONObject();
			for (int i = 1; i <= columnCount; i++) {
				String columnName = rsmd.getColumnLabel(i);
				Object value = rs.getObject(columnName);
				if (value == null)
					value = "";

				if (value instanceof Date) {
					value = rs.getTimestamp(columnName);
					value = sdf.format((Date) value);
				}				
				
				if (value instanceof Clob) {
					Clob clob = (Clob)value;
					value = clob.getSubString((long)1,(int)clob.length());
				}
				
				try {
					if (camel) {
						columnName = underscore2Camel(columnName);
					}
					jsonObj.put(columnName, value);
				} catch (JSONException e) {
					throw new SQLException(e);
				}
			}
			array.put(jsonObj);
		}
		return array;
	}

	
	private String underscore2Camel(String underscore) {
		StringBuffer buf = new StringBuffer();
		underscore = underscore.toLowerCase();
		Matcher m = Pattern.compile("_([a-z])").matcher(underscore);
		while (m.find()) {
			m.appendReplacement(buf, m.group(1).toUpperCase());
		}
		return m.appendTail(buf).toString();
	}	
}
