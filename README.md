# xbatis-spring-boot-parent

## 介绍
xbatis springboot启动器，基于mybatis-spring-boot-starter改动，改动很小，只是替换成xbatis的配置类

# 快速开始

## 1. 基于spring-boot开发 (已引入spring、springboot 基本依赖，创建SpringApplication.run即可启动)

### 1.1 springboot2 maven 集成

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>cn.xbatis</groupId>
            <artifactId>xbatis-spring-boot-parent</artifactId>
            <version>1.9.3-M2</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<dependencies>
    <dependency>
        <groupId>cn.xbatis</groupId>
        <artifactId>xbatis-spring-boot-starter</artifactId>
    </dependency>
</dependencies>
```

### 1.2 springboot3 maven 集成

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>cn.xbatis</groupId>
            <artifactId>xbatis-spring-boot-parent</artifactId>
            <version>1.9.3-M2-spring-boot3</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<dependencies>
    <dependency>
        <groupId>cn.xbatis</groupId>
        <artifactId>xbatis-spring-boot-starter</artifactId>
    </dependency>
</dependencies>
```

#### 1.3 数据源 配置

配置spring boot配置文件

```yaml
spring.datasource.url=jdbc:mysql://localhost/test
spring.datasource.username=dbuser
spring.datasource.password=dbpass
```

或者 自己实例一个 DataSource 也可以

```java

@Configuration(proxyBeanMethods = false)
public class DatasourceConfig {

    @Bean
    public DataSource getDataSource() {
        return new EmbeddedDatabaseBuilder()
                .setName("test_db")
                .setType(EmbeddedDatabaseType.H2)
                .addScript("schema.sql")
                .build();
    }
}

```

### 其他配置(可不配置)

#### 数据库命名规则配置(可不配置，在项目启动时配置)

```java

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        MybatisMpConfig.setTableUnderline(true); //数据库表是否下划线规则 默认 true
        MybatisMpConfig.setColumnUnderline(true); ///数据库列是否下划线规则 默认 true
        SpringApplication.run(DemoApplication.class, args);
    }
}
```

### springboot Application下安全检查配置
```java
@XbatisPojoCheckScan(basePackages = "com.sys.**.POJO")
public class ApiApplication {
    public static void main(String[] args) {
        try {
            SpringApplication.run(ApiApplication.class, args);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
```
### springboot 开发环境下安全检查配置（推荐）
```java
@Profile("dev")
@Configuration
@XbatisPojoCheckScan(basePackages = "com.sys.api.POJO.DTO")
public class XbatisSafeCheckConfig {
}
```
> 只有dev环境才检查；其他环境不检查（加速启动时间）
>
### XbatisPojoCheckScan属性说明
| 属性名                     | 说明                                    |
|-------------------------|---------------------------------------|
| basePackages            | 基础包路径                                 |
| modelPackages           | Model类的包扫描路径；如果没填，则使用basePackages的路径  |
| resultEntityPackages    | VO类的包扫描路径；如果没填，则使用basePackages的路径     |
| conditionTargetPackages | 对象转条件的类的包扫描路径；如果没填，则使用basePackages的路径 |
| orderByTargetPackages   | 对象转排序的类的包扫描路径；如果没填，则使用basePackages的路径 |

#### 数据库类型配置(可不配置，默认MYSQL)

```yaml
mybatis:
  configuration:
    databaseId: MYSQL
```

> 更多数据库支持，请查看类：db.sql.api.DbType

> 启动springboot即可，非常简单

### 一切都就绪，启动即可

> 1.添加此依赖，无需再添加mybatis依赖

> 2.包含 mybatis、mybatis-spring、 mybatis-spring-boot-starter 所有功能（支持原有mybatis的所有功能）

> 3.更多mybatis 配置参数，参考 https://mybatis.org/spring-boot-starter/mybatis-spring-boot-autoconfigure/zh_CN/index.html

> 4.参考示例：https://gitee.com/xbatis/xbatis/tree/master/xbatis-spring-boot-demo

> 5.更多 mybatis 用法，参考：
> mybatis：https://mybatis.org/mybatis-3/zh_CN/index.html
>
> mybatis spring: https://mybatis.org/spring/zh_CN/index.html

> 6.更多xbatis 用法，参考作者编写的test case:(包含各种简单，复杂的CRUD操作案例)
>
> https://gitee.com/xbatis/xbatis/tree/master/xbatis-core/src/test/java/com/mybatis/mp/core/test/testCase
