package cn.hotpot.rpc.client.config;

import cn.hotpot.rpc.common.annotation.RpcCaller;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;

import java.io.IOException;
import java.util.Set;

/**
 * @author qinzhu
 * @since 2019/12/9
 */
@Configuration
public class Config implements ApplicationContextAware, InitializingBean {
    private ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() throws Exception {
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter((metadataReader, metadataReaderFactory) -> {
            AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();
            System.out.println(annotationMetadata.getClassName() + "+++++++++++++++++++++++++++++");
            if (annotationMetadata.getClassName().equals(RpcCaller.class.getName())) {
                return true;
            }
            return false;
        });
        Set<BeanDefinition> definitions = scanner.findCandidateComponents("cn.hotpot");
        definitions.forEach(e -> { // TODO
            String className = e.getBeanClassName();
            System.out.println(className);
        });
        beanFactory.registerSingleton("", "");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
