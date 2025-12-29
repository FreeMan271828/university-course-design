# 项目设计与总结

## 模型设计

### Java模型

1. 日期
    属性：年月日时分
2. 用户
   1. id (String)
   2. name (String)
   3. password (String)
3. 热搜
   1. id (String)
   2. title (String)
4. (abstract) comment
   1. id (String)
   2. user (User) (1 to 1)
   3. content (String)
   4. date (Date) (1 to 1)
   5. favorNum (int)
5. 帖子
   1. id (String)
   2. title (String)
   3. user (User) (1 to 1)
   4. date (Date) (1 to 1)
   5. content (String)
   6. favorNum (int)
   7. photo (String)
   8. comments (List< Comment>) (1 to *)
6. 评论 extend comment
   1. replies (List< Reply>) (1 to *)
7. 回复 extend comment

### 网页模型

1. HomePage.html 综合页面未登录‘
   1. Posts.html
   2. Header.html
   3. Login.html
   4. SideBox.html
2. HomePage_LogSuccess.html 综合页面已登录
   1. HeaderLogSuccess.html
   2. Posts.html
   3. SideBox.html
3. UserPage.html 用户页面未登录
   1. Header.html
   2. SideBox.html
   3. Login.html
   4. NotFind.html
4. UserPage_LogSuccess.html 用户页面已登录
   1. HeaderLogSuccess.html
   2. SideBox.html
   3. NotFind.html
5. 网页可复用组件
   1. Posts.html 帖子模块
      1. 根据后端传入的posts信息，渲染posts
      2. 显示点赞数。可以为帖子点赞，增加点赞数。
      3. 登录后增加评论
   2. Header.html 头部模块未登录
      1. 进行页面的选择，信息的搜索
      2. 嵌入登录按钮
   3. HeaderLogSuccess.html 头部模块已登录
      1. 进行页面的选择，信息的搜索
      2. 根据登录信息展示user.name
      3. 登出按钮
   4. Login.html 登录模块
      1. user.name user.password的输入
      2. 传入后端进行登录、注册验证
   5. SideBox.html 侧边模块
      1. 根据传入的热搜以及网站数据进行加载
   6. NotFind.html
      1. 在用户页面显示未找到用户

## 工时评估

### 后端开发

1. 完成SpringBoot框架搭建
   1. 评估工时  小于1工时
   2. 实际工时  小于1工时
2. 完成model开发
   1. 评估工时  小于1工时
   2. 实际工时  小于1工时
3. 完成service开发
   1. 评估工时  3工时
   2. 实际工时  6工时（算上bug）
4. 完成control开发
   1. 评估工时  4工时
   2. 实际工时  6工时

### 前段开发

#### 组件开发

1. Posts.html开发
   1. 评估工时  8工时
   2. 实际工时  约8工时
2. Header.html开发
   1. 评估工时  3工时
   2. 实际工时  2工时
3. HeaderLogSuccess.html开发
   1. 评估工时  3工时
   2. 实际工时  2工时
4. Login.html开发
   1. 评估工时  5工时
   2. 实际工时  5工时
5. SideBox.html开发
   1. 评估工时  2工时
   2. 实际工时  1工时
6. NotFind.html开发
   1. 评估工时  1工时
   2. 实际工时  小于1工时

#### 页面开发（组件整合以及页面调整）

1. HomePage.html开发
   1. 评估工时  8工时
   2. 实际工时  10工时
2. HomePage_LogSuccess.html开发
   1. 评估工时  8工时
   2. 实际工时  10工时
3. UserPage.html开发
   1. 评估工时  3工时
   2. 实际工时  2工时
4. UserPage_LogSuccess.html开发
   1. 评估工时  3工时
   2. 实际工时  1工时
