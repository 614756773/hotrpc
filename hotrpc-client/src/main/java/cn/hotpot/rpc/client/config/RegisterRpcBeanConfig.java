package cn.hotpot.rpc.client.config;

import cn.hotpot.rpc.common.annotation.RpcCaller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Proxy;
import java.util.LinkedHashMap;
import java.util.Map;

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
                            ProxyFactory.createProxy(aClass));
                    proxyBeans.put(produceBeanName(aClass), proxyBean);
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

    /**
     * 将cn.hotpot.rpc.client.config.UserService之类的字符串
     * 转换为
     * userService
     */
    private String produceBeanName(Class<?> aClass) {
        String str = aClass.getSimpleName();
        char c = str.charAt(0);
        return (c + "").toLowerCase() + str.substring(1, str.length());
    }

    private void registersAllRpcProxyBean(Map<String, Object> proxyBeans) {
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
        proxyBeans.forEach(beanFactory::registerSingleton);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
