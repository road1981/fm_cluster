package com.cloud.fmnode.startup;

import com.cloud.fmnode.config.FlyMoreClusterConfig;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Bios implements ImportSelector, ImportBeanDefinitionRegistrar {

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        List<String> biosArr = FlyMoreClusterConfig.getBiosClassArr();

        return biosArr.toArray(new String[biosArr.size()]);
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
    }
}
