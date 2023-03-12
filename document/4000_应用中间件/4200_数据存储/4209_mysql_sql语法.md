---
title: mysql_sql语法
date: 2019-05-30 00:00:00
---

## sql基本语法：旧笔记/暂存/未整理
``` SQL
--sql语言分类
1）数据查询语言DQL：SELECT
2）数据操纵语言DML：INSERT/UPDATE/DELETE
3）数据定义语言DDL：用来创建数据库中的各种对象-----表、视图、
索引、同义词、聚簇，truncate等，DDL操作是隐性提交的，不能rollback 
4）数据控制语言DCL：BEGIN/ROLLBACK/COMMIT等等逻辑控制语言

--创建删除数据库
	create database TEST
	drop database TEST
--切换数据库
	use master
	use TEST
--创建删除表
	create table tbStudent
	(
		tsid int identity(1,1) primary key not null,
		tsname nvarchar(50) not null,
		tsaddress nvarchar(50) not null
	)
	drop table tbStudent
		--约束
			--非空约束 not null
			--主键约束 primary key 唯一且不能为空
			--检查约束 check constraint 范围以及格式约束（字段插入的条件）
			--唯一约束 unique constraint 唯一，允许为空，但只能出现一次
			--默认约束 default constraint 默认值
			--外键约束 foreign key constraint 表关系
--
--查询数据
	select * from tbStudent
	--别名：as用来给查询结果的 列 起别名，as可省略，非法字符用''括起来
		select tsname as 姓名,tsaddress as '/地址/' from tbStudent
	--别名：用=号，等号还可以自定义添加列，后面可以跟任意函数，或者值
		select 
			姓名=tsname,
			'/地址/'=tsaddress,
			啦啦啦='d',
			当前系统时间=GETDATE()
		from tbStudent
	--select 可以单独使用，不跟from table
		select 
			啦啦啦='d',
			当前系统时间=GETDATE()
	--distinct关键字，针对已经查询出的结果去除重复
		select distinct tsname,tsaddress from tbStudent
	--top关键字，获取前几条数据，top一般和order by一起使用 才有意义
	--order by 列名 排序方式，  排序（desc降序，asc升序（默认））
	--所有表达式要用()括起来
		select top 5 * from tbStudent order by tsid desc
		select top (2*2) * from tbStudent order by tsid desc
		select top 30 percent * from tbStudent order by tsid desc
	--条件查询 select 列 from 表 where 条件
		select * from tbStudent where tsid<8 and tsid>3
		select * from tbStudent where tsid between 3 and 5
			--模糊查询（只针对字符串）,关键字 like,not like
				--通配符：转义将以下四个放入[]中
					-- _ 表示任意的单个字符
					-- % 表示任意多个任意字符
					-- [] 表示筛选，范围
					-- ^ 表示非
				select * from tbStudent where tsaddress like '北京__'
				select * from tbStudent where tsaddress like '北%'
				select * from tbStudent where tsname like 'yang[0-5]'
	--联合 union 两个处理过后的表必须 列个数，列数据类型相同
		select 列名1,列名2,列名3 from 表1
		union 
		select 列名1,列名2,列名3 from 表2
		--union 和 union all 都能进行联合，区别在于union联合会去除重复，并且重新排列数据
		--而 union all 不会去重也不会重排，一般用 union all
	--连接查询
		内连接(inner join)
			select * from 表1 inner join 表2 on 条件 (条件：必须是布尔表达式)
		外连接
			
	--case 用法(条件选择查询)
		--实现if else效果(case end相当于括号，when相当于if,then相当于{}里的结果)
			select 
				*,头衔=case
							when CARNO=7830001 then '菜鸟'
							when CARNO=7830002 then '老鸟'
							else '骨灰级'
						end
			from NE_CARD_FULLCARDINFO
		--实现switch效果
			select 
				*,头衔=case carno
							when 7830001 then '菜鸟'
							when 7830002 then '老鸟'
							else '骨灰级'
						end
			from NE_CARD_FULLCARDINFO
	--子查询(把一个查询的结果在另外一个查询中使用就叫子查询)
		--分类
			--独立子查询(子查询可单独执行)
				select * from NE_CARD_FULLCARDINFO
					where ptid=(select ptid from pt where ptname='亲人')
				select * from (select * from NE_CARD_FULLCARDINFO where ID>10) a
				(注意：如果将一个子查询作为另一个查询的源，必须起别名)
			--相关子查询
				select * from NE_CARD_FULLCARDINFO a where
				exists(select * from pt b where a.ptid=b.ptid and b.ptname='亲人')
				--exists()函数，只要查询到数据返回true，否则false
		--子查询实现分页数据
			--首先排序

--
--新增插入数据
	insert into tbStudent(tsname,tsaddress) values('yhmi','30')
	--数据库排序规则问题，一般在 字符串前加N防止中文乱码，字符串只能单引号
		insert into tbStudent
		values('yang3',N'北京')
	--使用union 一次向表中插入多条数据
		insert into student
		select '杨米米',45,'男','上海',1
		union all
		select '杨米米',45,'男','上海',1
		union all
		select '杨米米',45,'男','上海',1
--
--更新数据
	update tbStudent 
	set tsname=tsname+'(HAHA)',tsaddress=tsaddress+'地址'
	where tsid=2
--
--删除数据
	--delete删除数据，自动编号不会恢复默认值
		delete from tbStudent where tsid=3
	--truncate只能删除所有数据，自动编号恢复默认值
		truncate table tbStudent

--聚合函数(只针对数值使用)，对分组数据进行处理(无分组，默认整表为一组),注意:不统计空值null
	--求和 sum()
	--计数 count()
	--平均 avg()
	--最大 max()
	--最小 min()
		select SUM(tsid) as id总和 from tbStudent
		select id总和=SUM(tsid) from tbStudent
		select COUNT(*) from tbStudent
		select COUNT(tsid) from tbStudent
		select id平均值=SUM(tsid)/COUNT(*) from tbStudent
		select id平均值=AVG(tsid) from tbStudent
		select max(tsid) from tbStudent
		select min(tsid) from tbStudent
--分组group by
	--只要有group by,整个式子中的列名 只能是 group by的列名 或者 包含在聚合函数里的列名
	--having 是筛选分组后的 组数据（按照组来筛选）-
		select
			'班级人数'=COUNT(*),
			'最高分'=MAX(math),
			'最低分'=MIN(math),
			'分数和'=SUM(math),
			'平均分'=avg(math),
			'班级id'=classid
		from student
		group by classid
		having classid=1
		order by 平均分 desc

--
--复制一个新的表，但是约束并不能复制
	--表可能不存在
		select * into studentbackup20170628 from student
		select top 0 * into studentbackup20170629 from student --只复制表结构
	--表存在
		insert into studentbackup20170629
		select * from student where sex='男'
--
--null处理
	--null值无法使用=<>，必须使用 is null , is not null
	--任何值和null计算都是null，因此如果表内有null，先判断是否为null
--
--order by 语句排序
	--降序 order by 列名 desc
	--升序 order by 列名 asc 或者 升序 order by 列名
	--order by  必须在整个sql语句的最后
	--多列排序
	--用了 order by排序后叫游标，不能再用作集合嵌套使用
--
--sql 语句执行顺序
	-- 5>	select 5.1>选择列,5.2>distinct,5.3>top()
	-- 1>	from 表
	-- 2>	where 条件
	-- 3>	group by 列
	-- 4>	having 筛选条件
	-- 6>	order by 列
--
--常用函数
	--字符串函数
		--len()字符个数
		--datalength()字节的个数
		--upper()大写
		--lower()小写
		--去掉两段空格 rtrim(),ltrim()
		--字符串截取 left("",个数数字) right("",个数数字) substring("",开始数字1，个数数字2)
		print datalength('杨得到')
		print left('哈哈昂哈啊哈',5)
	--日期函数
		--getdate()
	--类型转换函数
		--cast(原表达式 as 要转换成的类型)
		--convert(类型，表达式)一般
		print convert(varchar(200),getdate(),109)
--
--视图view(只能用来查询)
	视图是用来封装查询结果集的复杂sql语句，只作引用，虚拟的，查询操作和查询表一样
--
--T-Sql编程
	--声明变量(局部)
		declare @name nvarchar(50)
		declare @age int
		declare @name nvarchar(50),@age int   --一次声明多个变量
	--为变量赋值
	set @name='123'
	--输出
	select 'sf',@name
	print '526'   --print 只能输出一个值
	--while 循环
	declare @i int=1	--声明变量同时赋值
	while @i<=100
	begin
		print 'Hello!'
		set @i=@i+1
	end
	--if
	if @i>2
	begin
		print 's'
	end
	--系统变量@@
	@@version
--
--事务	translation(保证多条更新插入语句同时成功)
	开始事务	begin translation
	提交事务	commit translation
	回滚事务	rollback translation

	begin translation
	declare @sum int=0
		update.....
		set @sum=@sum+@@error

		update.....
		set @sum=@sum+@@error

		insert.....
		set @sum=@sum+@@error
	if @sum<>0
	begin
		rollback
	end else
	begin
		commit
	end
--
--存储过程（类似方法函数）
--
--触发器

```

```
--重复数据只显示一条：
select min(id) id,b,c from tb group by b,c
 
--删除重复数据：
delete from tb where rowid not in (select min(rowid) from tb group by b,c)

--分页
SELECT * FROM 
(
SELECT ROW_NUMBER() OVER(order by A.CarModel) AS Row, A.* 
from EnergyTypeSt_New A 
WHERE 条件
) TT
WHERE TT.Row between {0} and {1}

startIndex * pageSize + 1, startIndex * pageSize + pageSize

--Oracle 自增id
	--Oracle用 <序列sequence+触发器trigger>的方式使数据表的一列或多列实现自增

	--1. 创建序列Sequences
		create sequence NE_FULLCARDINFO_ID	--创建的序列名
		minvalue 1
		nomaxvalue
		start with 1
		increment by 1
		nocache;
	--2. 在表中创建触发器trigger
		create or replace trigger TRIGGER_ID	--TRIGGER_ID触发器名
		before insert
		on NE_CARD_FULLCARDINFO	--NE_CARD_FULLCARDINFO操作的表名
		for each row
		declare
		-- local variables here
		nextid number;
		begin
		IF :new.ID IS NULL or :new.ID=0 THEN	--ID是列名
			select  NE_FULLCARDINFO_ID.nextval	--NE_FULLCARDINFO_ID创建的序列名
			into nextid
			from sys.dual;
			:new.ID:=nextid;	--ID是列名
		end if;
		end TRIGGER_ID;	--TRIGGER_ID触发器名
	--3. Oracle中创建、修改、删除序列
		--http://www.cnblogs.com/nicholas_f/articles/1525585.html

```