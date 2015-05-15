# 例子演示

## 1 准备工作

### 1.1 创建表和对象

在mysql数据库test中,创建一张商品表：

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
}
```
