package cn.ffcs.demo.main;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.ffcs.demo.db.JSONArrayHandler;
import cn.ffcs.demo.db.JSONObjectHandler;
import cn.ffcs.demo.db.MemoryFactory;
import cn.ffcs.memory.BeanHandler;
import cn.ffcs.memory.BeanListHandler;
import cn.ffcs.memory.ColumnHandler;
import cn.ffcs.memory.Memory;

public class Demo {
	public static Memory memory = MemoryFactory.getInstance();

	public static void main(String[] args) throws SQLException {
		// testCrud();
		// testCqrs();
		testPager();
	}

	public static void testCqrs() throws SQLException {
		// 清空仓库中的所有水果（数据）
		memory.update("delete from product", new Object[] {});

		// 香蕉、苹果、梨、桃、芒果、西瓜
		String[] fruit = { "Banana", "Apple", "Pear", "Peach", "Mango",
				"Watermelon" };

		// 批量入库
		List<Product> products = new ArrayList<Product>();
		for (String name : fruit) {
			Product product = new Product();
			product.setName(name);
			product.setStock(new Random().nextInt(100));
			product.setStatus(new Random().nextBoolean());
			product.setCreatedDate(new Date());
			products.add(product);
		}
		memory.create(Product.class, products);

		// 查询未上架的水果
		products = memory.query("select * from product where status = ?",
				new BeanListHandler<Product>(Product.class), false);

		for (Product product : products) {
			System.out.println(product);
			product.setStatus(true);
		}
		System.out.println("--- 分隔符 ---");

		// 上架这些水果
		memory.update(Product.class, products);

		products = memory.query("select * from product",
				new BeanListHandler<Product>(Product.class), new Object[] {});
		for (Product product : products) {
			System.out.println(product);
		}
		System.out.println("--- 分隔符 ---");

		// 查询所有水果的库存总数
		int total = memory.query("select sum(stock) from product",
				new ColumnHandler<Integer>(Integer.class), new Object[] {});
		System.out.println("共有水果：" + total + "个");
		System.out.println("--- 分隔符 ---");

		// 下架所有水果
		memory.update("update product set status = ?", false);

		products = memory.query("select * from product",
				new BeanListHandler<Product>(Product.class), new Object[] {});
		for (Product product : products) {
			System.out.println(product);
		}
	}

	public static void testCrud() throws SQLException {
		/**
		 * 创建一条记录
		 */
		Product product = new Product();
		product.setName("apple");
		product.setStock(10);
		product.setStatus(true);
		product.setCreatedDate(new Date());

		System.out.print("入库之前Product没有ID：");
		System.out.println(product);

		memory.create(Product.class, product);

		/**
		 * 读取这条记录
		 */
		product = memory.read(Product.class, product.getId());
		System.out.print("使用CRUD的read方法读取：");
		System.out.println(product);

		/**
		 * 换一种方式读取
		 */
		product = memory.query("select * from product where id = ?",
				new BeanHandler<Product>(Product.class), product.getId());
		System.out.print("使用CQRS的query方法读取：");
		System.out.println(product);

		/**
		 * 更新这条记录
		 */
		product.setStock(15);
		product.setStatus(true);
		product.setCreatedDate(new Date());
		memory.update(Product.class, product);
		// 查看结果
		product = memory.read(Product.class, product.getId());
		System.out.print("查看更新结果：");
		System.out.println(product);

		/**
		 * 删除一条记录
		 */
		memory.delete(Product.class, product.getId());
		// 查看结果
		product = memory.read(Product.class, product.getId());
		System.out.print("查看删除结果：");
		System.out.println(product);
	}

	public static void testPager() throws SQLException {
		// 清空仓库中的所有水果（数据）
		memory.update("delete from product", new Object[] {});

		// 香蕉、苹果、梨、桃、芒果、西瓜
		String[] fruit = { "Apple", "Mango", "Peach", "Banana", "Peach",
				"Pear", "Mango", "Peach", };

		// 批量入库
		List<Product> products = new ArrayList<Product>();
		for (String name : fruit) {
			Product product = new Product();
			product.setName(name);
			product.setStock(new Random().nextInt(100));
			product.setStatus(new Random().nextBoolean());
			product.setCreatedDate(new Date());
			products.add(product);
		}

		memory.create(Product.class, products);

		// 分页、IN语句、JSONArrayHandler、JSONObjectHandler的演示

		StringBuffer sql = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		sql.append("select * from product");
		
		// IN语句，挑选芒果和桃子
		memory.in(sql, params, "where", "name", Arrays.asList("Mango", "Peach")); 
		
		// 分页，每页2条，取1页的数据
		int pageSize = 2;
		int pageNo = 1;		
		memory.pager(sql, params, pageSize, pageNo); 
	
		// 以JSONArray格式打印，挑选的水果
		JSONArray ja = memory.query(sql, new JSONArrayHandler(), params);
		System.out.println(ja);
		
		// 以JSONObject格式打印，挑选的水果中的第一个
		JSONObject jo = memory.query(sql, new JSONObjectHandler(), params);
		System.out.println(jo);
	}

}
