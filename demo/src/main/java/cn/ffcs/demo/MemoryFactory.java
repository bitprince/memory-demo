package cn.ffcs.demo;

import cn.ffcs.memory.Memory;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * 
 * 
 * @Description:
 * @Copyright: Copyright (c) 2013 FFCS All Rights Reserved
 * @Company: 北京福富软件有限公司
 * @author 黄君毅 2013-8-4
 * @version 1.00.00
 * @history:
 * 
 */
public class MemoryFactory {

	private MemoryFactory() {
	}

	private static class SingletonHolder {
//		public static final Memory MEMORY = new Memory(new SimpleDataSource());
		public static final Memory MEMORY = new Memory(getDataSource());

	}

	public static Memory getInstance() {
		return SingletonHolder.MEMORY;
	}

	/**
	 * 在容器(Tomcat)运行状态下，可使用getDataSource()
	 */
	public static final DataSource getDataSource() {
		try {
			Context context = new InitialContext();
			return (DataSource) context.lookup("java:comp/env/jdbc/test");
		} catch (NamingException e) {
			throw new RuntimeException(e);
		}
	}
}
