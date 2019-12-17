# hotrpc
使用Netty实现RPC服务

### 一、项目结构  
#### hotrpc-common 
- 主要代码
	- 自定义注解，用于标志代理类
	- netty的相关操作，用于服务之间的通信
		- `netty client`和`netty server`
		- 编码器，解码器
	- 服务接口的定义以及对服务实现类的扫描注册
		- 将服务实现类进行实例化
		- 当客户端调用服务时，找到相应的实现类，用实现类进行代理

该子项目主要用于客户端和服务端之间的通信，自定义了一个请求协议，请求格式如下：

| header | dataLength | data|
| --- | --- | --- |
| 614756773 | variable | variable  |
- 一次完整的请求一共有3个部分
	- `header`占4字节为固定值`614756773`，作为一个`魔数`使用，以此来校验请求是否合法
	- `dateLength `占4字节，为一个变量，用来表示data有多少字节
	- `data`，传输的数据

#### hotrpc-server
- 主要代码
	- 定义服务实现类

#### hotrpc-client
- 主要代码
	- 扫描标有RpcCaller注解的接口，并代理，然后注册成bean放入spring容器中
	- 定义controller，并且从spring容器中获取到`代理服务`

---

### 二、主流程
#### 客户端
1. 启动SpringBootApplication
1. 扫描标有RPC注解的类
2. 注册到spring容器中
	- 注册时使用jdk动态代理该类`代理类东西有点多看下面`
3. 从spring容器中获得该类，调用方法
- jdk动态代理过程
	- 先将服务接口封装成一个`Request`对象，包括设置`className`，`methodName`，`params`等
	- 实例化一个`Client`对象，然后通过该对象将请求发送出去
	- 阻塞等待响应
	- 从响应结果中获取数据并返回

#### 服务端
1. 实例化`Server`并且启动
2. 检查`Server`状态，保证不会重复启动
3. 把rpc服务实现类扫描出来然后实例化，缓存在Map对象中
4. 若有请求来临
	- 对请求进行解码
	- 获取到请求体，从而找到对应的rpc服务实现类
	- 调用服务实现类
	- 对调用结果进行编码
	- 发送结果给客户端

---

### 三、优化点
- 目前如果需要定义一个rpc服务，必须在`hotrpc-common`里先定义接口，然后再`hotrpc-server`中定义实现类。
可以改成cglib动态代理，这样就不必再定义接口了，也能让`hotrpc-common`封装的更好
- `hotrpc-client`中需要定义如何去扫描RpcCaller，如何进行代理。
这样做很不好，应该放在`hotrpc-common`包中去做这些事情。
- 由于进行扫描类操作，是使用的spring的工具类`ClassPathScanningCandidateComponentProvider`所以都写死了包路径为`cn.hotpot`。
可以使用`org.reflections`工具类来进行扫描。

--- 
#### 源码
[github](https://github.com/614756773/hotrpc)