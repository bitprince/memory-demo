# 例子演示

## 1 准备工作

在mysql数据库test中,创建一张商品表(product)，同时创建对应的对象(Product)。这里列一些命名规则：

- 表名、字段名是下划线连接，对应类名和属性名则是驼峰式
- 主键的命名为id, mysql使用自增主键，oracle使用序列，序列的命名为表明_seq，对应属性为long类型的id
- 时间类型，mysql为datetime, oracle为date，对应属性为java.uitl.Date类型
- 布尔类型，mysql为tiny(1), oracle为number(1), 对应属性为boolean类型

尽管memory也提供主键的名和值自定义的接口，但不建议使用，因为这样可能会增加沟通协作的成本。

``` sql
CREATE TABLE `product` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `name` varchar(32) NOT NULL COMMENT '商品名称',
  `stock` int(11) NOT NULL COMMENT '商品库存',
  `created_date` datetime NOT NULL COMMENT '创建时间',
  `status` tinyint(1) NOT NULL COMMENT '上架状态',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
```

``` java
public class Product {
	private long id; // ID
	private String name; // 名称
	private int stock; // 库存
	private Date createdDate; // 创建时间
	private boolean status; // 状态，是否已上架
	
	// getter/setter
}
```

### 2 获取清瘦的记录者-memory单例工厂

``` java
public class MemoryFactory {
	private MemoryFactory() {}

	private static class SingletonHolder {
		/**
		 * 如果使用MYSQL		
		 **/
		public static final Memory MEMORY = new Memory(new SimpleDataSource());
		// public static final Memory MEMORY = new Memory(getDataSource());		
		
		/**
		 * 如果使用Oracle
		 **/
		// public static final Memory MEMORY = new Memory(new SimpleDataSource(), true);	
		// public static final Memory MEMORY = new Memory(getDataSource(), true);
		
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
```

### 3、增删改查 
    一个方法演示增删改查常用的API：create、read、update、delete方法的使用。
``` java
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
```

### 4、命令与查询
	一个方法演示命令与查询常用的API：update和query方法的使用。
``` java
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
	
```
### 5、分页和IN语句
    一个方法演示分页和IN语句的API：pager和in方法的使用。
``` java
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
```

### 5 更多内容

#### 1、下载完整的[例子](https://github.com/bitprince/memory-demo/archive/master.zip)，跑跑看；
#### 2、了解清瘦的记录者背后的[思考](https://github.com/bitprince/memory/blob/master/README.md)。

(end)
