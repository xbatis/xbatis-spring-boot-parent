/*
 *  Copyright (c) 2024-2026, Ai东 (abc-127@live.cn) xbatis.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License").
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and limitations under the License.
 *
 */

package org.mybatis.spring.boot.autoconfigure;

import cn.xbatis.db.annotations.Table;
import cn.xbatis.ddl.auto.Mode;
import db.sql.api.tookit.LambdaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.ArrayList;
import java.util.List;


public class XbatisDDLAutoRegistrar implements ImportBeanDefinitionRegistrar {

    private static final Logger logger = LoggerFactory.getLogger(XbatisDDLAutoRegistrar.class);

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {
        // 获取 @XbatisDDLAutoScan 注解的属性
        AnnotationAttributes annoAttrs = AnnotationAttributes.fromMap(annotationMetadata.getAnnotationAttributes(XbatisDDLAutoScan.class.getName()));
        if (annoAttrs == null) {
            return;
        }
        this.registerBeanDefinitions(registry, new AnnotationAttributes[]{annoAttrs});
    }

    private List<XbatisDDLAutoItem> buildDDLAutoItems(AnnotationAttributes[] attributesList) {
        List<XbatisDDLAutoItem> items = new ArrayList<>();
        for (AnnotationAttributes attributes : attributesList) {
            Mode mode = (Mode) attributes.get(LambdaUtil.getName(XbatisDDLAutoScan::mode));
            if (mode == Mode.NONE) {
                continue;
            }
            ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
            String[] entityPackages = (String[]) attributes.get(LambdaUtil.getName(XbatisDDLAutoScan::entityPackages));
            scanner.addIncludeFilter(new AnnotationTypeFilter(Table.class));
            List<Class<?>> entities = new ArrayList<>();

            Class<?> markerInterface = (Class<?>) attributes.get(LambdaUtil.getName(XbatisDDLAutoScan::markerInterface));
            for (String path : entityPackages) {
                for (BeanDefinition beanDefinition : scanner.findCandidateComponents(path)) {
                    Class<?> clazz;
                    try {
                        clazz = Class.forName(beanDefinition.getBeanClassName());
                    } catch (Throwable ignored) {
                        continue;
                    }
                    if (Void.class.equals(markerInterface) || markerInterface.isAssignableFrom(clazz)) {
                        entities.add(clazz);
                    }
                }
            }
            logger.info("扫描：{},发现{}个需要DDL的实体类", String.join(",", entityPackages), entities.size());

            String dbType = attributes.get(LambdaUtil.getName(XbatisDDLAutoScan::dbType)).toString();
            String dataSource = attributes.get(LambdaUtil.getName(XbatisDDLAutoScan::dataSource)).toString();
            items.add(new XbatisDDLAutoItem(dbType, mode, entities, dataSource));
        }
        return items;
    }

    public void registerBeanDefinitions(BeanDefinitionRegistry registry, AnnotationAttributes[] attributesList) {
        if (attributesList == null || attributesList.length < 1) {
            return;
        }
        GenericBeanDefinition genericBeanDefinition = new GenericBeanDefinition();
        genericBeanDefinition.setBeanClass(XbatisDDLAuto.class);
        genericBeanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        genericBeanDefinition.getPropertyValues().add("xbatisDDLAutoItems", this.buildDDLAutoItems(attributesList));
        genericBeanDefinition.setLazyInit(false);

        registry.registerBeanDefinition("xbatisDDLAuto", genericBeanDefinition);
    }

    static class RepeatingRegistrar extends XbatisDDLAutoRegistrar {
        @Override
        public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
            AnnotationAttributes mapperScansAttrs = AnnotationAttributes
                    .fromMap(importingClassMetadata.getAnnotationAttributes(XbatisDDLAutoScans.class.getName()));
            if (mapperScansAttrs != null) {
                AnnotationAttributes[] annotationAttributes = mapperScansAttrs.getAnnotationArray("value");
                this.registerBeanDefinitions(registry, annotationAttributes);
            }
        }
    }
}
