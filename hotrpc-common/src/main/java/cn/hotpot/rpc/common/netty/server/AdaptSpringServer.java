package cn.hotpot.rpc.common.netty.server;

import cn.hotpot.rpc.common.annotation.RpcReplier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;

import java.util.Map;
import java.util.Set;

/**
 * @author qinzhu
 * @since 2019/12/17
 */
@Slf4j
public class AdaptSpringServer extends Server{

    @Override
    void registerService(Map<String, Object> serviceMap) {
        ClassPathScanningCandidateComponentProvider componentProvider = new ClassPathScanningCandidateComponentProvider(false);
        componentProvider.addIncludeFilter((metadataReader, metadataReaderFactory) -> {
            log.debug(metadataReader.getClassMetadata().getClassName());
            return metadataReader.getAnnotationMetadata().getAnnotationTypes().contains(RpcReplier.class.getName());
        });

        Set<BeanDefinition> beanDefinitions = componentProvider.findCandidateComponents("cn.hotpot");
        beanDefinitions.forEach(beanDefinition -> {
            String className = beanDefinition.getBeanClassName();
            try {
                Class<?> aClass = Class.forName(className);
                Object service = aClass.newInstance();
                serviceMap.put(aClass.getInterfaces()[0].getName(), service);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
