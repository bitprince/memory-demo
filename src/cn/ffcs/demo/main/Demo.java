package cn.ffcs.demo.main;

import java.sql.SQLException;
import java.util.List;

import cn.ffcs.demo.db.MemoryFactory;
import cn.ffcs.memory.BeanListHandler;
import cn.ffcs.memory.Memory;

public class Demo {
	public static Memory memory = MemoryFactory.getInstance();

	public static void main(String[] args) throws SQLException {
		testQuery();
		testCreate();
		testQuery();
	}

	public static void testQuery() throws SQLException {
		List<Product> products = memory.query("select * from product",
				new BeanListHandler<Product>(Product.class), new Object[] {});
		for (Product product : products) {
			System.out.println(product.getId() + "," + product.getName() + ","
					+ product.getStock());
		}
	}
	
	public static void testCreate() throws SQLException {
		Product product = new Product();
		product.setName("pear");
		product.setStock(10);
		memory.create(Product.class, product);		
	}
}
