package cn.hotpot.rpc.client.config;

import cn.hotpot.rpc.client.Constants;
import cn.hotpot.rpc.common.annotation.RpcCaller;
import cn.hotpot.rpc.common.netty.client.Client;
import cn.hotpot.rpc.common.netty.model.Request;
import cn.hotpot.rpc.common.netty.model.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author qinzhu
 * @since 2019/12/9
 * 扫描标有RpcCaller注解的接口，并代理，然后注册成bean放入spring容器中
 */
@Configuration
@Slf4j
public class RegisterRpcBeanConfig implements ApplicationContextAware, InitializingBean {
    private ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() throws Exception {
        final Map<String, Object> proxyBeans = new LinkedHashMap<>();

        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter((metadataReader, metadataReaderFactory) -> {
            String className = metadataReader.getAnnotationMetadata().getClassName();
            if (metadataReader.getAnnotationMetadata().getAnnotationTypes().contains(RpcCaller.class.getName())) {
                try {
                    Class<?> aClass = Class.forName(className);
                    Object proxyBean = Proxy.newProxyInstance(aClass.getClassLoader(),
                            new Class<?>[]{aClass},
                            new CustomizeInvocationHandler());
                    proxyBeans.put(aClass.getName(), proxyBean);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            return true;
        });
        scanner.findCandidateComponents("cn.hotpot");
        log.info("already scan RpcCaller");

        registersAllRpcProxyBean(proxyBeans);
    }

    private void registersAllRpcProxyBean(Map<String, Object> proxyBeans) {
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
        proxyBeans.forEach(beanFactory::registerSingleton);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private static class CustomizeInvocationHandler implements InvocationHandler {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            log.info("proxy invoke");
            Client client = new Client(Constants.HOST, Constants.PORT);
            Request request = new Request()
                    .setId(UUID.randomUUID().toString())
                    .setClassName(method.getDeclaringClass().getName())
                    .setMethodName(method.getName())
                    .setParams(args)
                    .setParamTypes(method.getParameterTypes());
            Response response = client.send(request);
            Throwable exp = response.getThrowable();
            if (exp != null) {
                throw exp;
            }
            return response.getResult();
        }
    }
}
