# jxc-admin
ERP进销存管理系统

## 模块划分：
### 系统用户&角色&权限模块：
    - 对系统用户帐号数据的管理
    - 基于RBAC体系，用户通过角色，间接授予权限
    - 基于Spring Security框架，完成用户登录相关、权限管理相关的功能。具有不同权限的用户，所能访问的后端接口和能展示的前端页面不同。
### 基础资料
    - 供应商信息管理
    - 客户信息管理
    - 商品信息管理
    - 商品初始库存管理
### 面向供应商的进货、退货模块
    - 进货订单的维护
    - 退货订单的维护
    - 商品库存的维护
### 面向客户的销售、退货模块
    - 销售订单的维护
    - 退货订单的维护
    - 商品库存的维护
### 统计
    - 对各种单据的简单统计
---
涉及技术
    - 后端：SpringBoot、Spring Security、Mybatis-Plus
    - 前端：Layui、Freemarker
    - 其他：通过Kaptcha生成验证码图片、通过zTree做前端的数据层级结构展示。
---
## 说明：
    - 登录账号：用户名为asdf，密码为asdf
      用户管理模块中，新建用户的密码默认为asdf，后续用户可自行登录，单独修改
      
    - 权限管理中，不同权限显示的页面不同，可通过以下三个账号，完成功能的测试。整个项目仅对系统管理部分（用户、角色、权限）做了权限管理，其它模块部分并未区分权限，所有用户都能访问
      账号：grant1，密码：asdf ---> 权限：仅用户管理
      账号：grant2，密码：asdf ---> 权限：用户管理、角色管理
      账号：grant3，密码：asdf ---> 权限：用户管理、角色管理、菜单管理
    
    - 单据查询中，左侧默认展示所有单据，右侧默认展示所有单据中商品信息的汇总，可通过左侧单据表中的按钮，使右侧仅显示某一单据的具体信息。
