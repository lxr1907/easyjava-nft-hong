# easyJava

#### 介绍
任意库表的增删改查接口，可以传入表名称，灵活传入字段，Map接收任意类型，list<Map>返回任意类型


#### 软件架构
软件架构说明
redis+mybatis+springboot2.2+java13

#### 安装教程
1. 在mysql创建easyJava库，执行databasesql目录下的sql文件
2. 安装redis，windows版本在redisForWin目录下可以找到
3. 修改resources/applications.propertites，以及resources目录下的其他配置文件，设定mysql地址，redis地址，服务端口等
4.  maven install
5.  java -jar easyJava.jar
6. 服务默认启动在[http://localhost:8080/easyJava](http://)
7. jmelody监控地址[http://localhost:8080/easyJava/monitoring](http://)

#### 使用说明

1.  查询库中所有表名.
需要配置替换BaseDao.xml内的库名'easyJava' 
    
[http://localhost:8080/easyJava/getBaseTableName](http://)
返回值：

```
{"data":{"data":{"list":[{"table_name":"user"}]},"count":1,"pageNo":1,"pageSize":10,"pages":1},"code":200,"message":"成功"}

```

2.  查询某个表的所有字段名,例如user表。
    [http://localhost:8080/easyJava/getBaseColumns?tableName=user](http://)
    返回值：
```
{"data":{"data":{"list":[{"Field":"id","Comment":"","Type":"int(11) unsigned","Null":"NO","Extra":"auto_increment","Privileges":"select,insert,update,references","Key":"PRI"},{"Field":"account","Comment":"账号","Type":"varchar(255)","Null":"NO","Extra":"","Privileges":"select,insert,update,references","Collation":"utf8_general_ci","Key":""},{"Field":"password","Comment":"密码","Type":"varchar(255)","Null":"NO","Extra":"","Privileges":"select,insert,update,references","Collation":"utf8_general_ci","Key":""},{"Field":"status","Comment":"状态1正常","Type":"int(11)","Null":"NO","Extra":"","Privileges":"select,insert,update,references","Default":"1","Key":""}]},"count":4,"pageNo":1,"pageSize":10,"pages":1},"code":200,"message":"成功"}
```

3.  查询表数据。
   [ http://localhost:8080/easyJava/getBaseList?tableName=user&pageSize=10&pageNo=1](http://)
    返回值：
  ```
{"data":{"data":{"list":[{"password":"e10adc3949ba59abbe56e057f20f883e","id":1,"account":"admin","status":1}]},"count":1,"pageNo":1,"pageSize":10,"pages":1},"code":200,"message":"成功"}
```

4.  登录（后面新增修改需要登录得到token）。
    http://localhost:8080/easyJava/login?account=admin&password=123456
    成功则返回值：
```
{"data":{"data":{"id":1,"account":"admin","status":1,"token":"06ROIzsCgvNBAbrABhr4zA=="}},"code":200,"message":"成功"}
```

5.  新增。
    post请求，传入token，tableName，表内其他字段
   [ http://localhost:8080/easyJava/insertBase?tableName=user](http://)

6.  修改。
    post请求，传入token，tableName,id(id作为所有表的默认自增主键)，表内其他字段
    [http://localhost:8080/easyJava/updateBaseByPrimaryKey?tableName=user&id=1](http://)

#### 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request


#### 码云特技

1.  使用 Readme\_XXX.md 来支持不同的语言，例如 Readme\_en.md, Readme\_zh.md
2.  码云官方博客 [blog.gitee.com](https://blog.gitee.com)
3.  你可以 [https://gitee.com/explore](https://gitee.com/explore) 这个地址来了解码云上的优秀开源项目
4.  [GVP](https://gitee.com/gvp) 全称是码云最有价值开源项目，是码云综合评定出的优秀开源项目
5.  码云官方提供的使用手册 [https://gitee.com/help](https://gitee.com/help)
6.  码云封面人物是一档用来展示码云会员风采的栏目 [https://gitee.com/gitee-stars/](https://gitee.com/gitee-stars/)
