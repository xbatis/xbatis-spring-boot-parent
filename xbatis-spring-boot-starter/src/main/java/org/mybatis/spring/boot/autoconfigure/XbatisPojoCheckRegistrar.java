/*
 *  Copyright (c) 2024-2025, Ai东 (abc-127@live.cn) xbatis.
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

import cn.xbatis.core.db.reflect.Conditions;
import cn.xbatis.core.db.reflect.Models;
import cn.xbatis.core.db.reflect.OrderBys;
import cn.xbatis.core.db.reflect.ResultInfos;
import cn.xbatis.db.Model;
import cn.xbatis.db.annotations.ConditionTarget;
import cn.xbatis.db.annotations.OrderByTarget;
import cn.xbatis.db.annotations.ResultEntity;
import db.sql.api.tookit.LambdaUtil;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;

import java.util.Map;
import java.util.function.Consumer;

public class XbatisPojoCheckRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata,
                                        BeanDefinitionRegistry registry) {
        // 获取 @XbatisPojoCheckScan 注解的属性
        Map<String, Object> annotationAttributes = annotationMetadata.getAnnotationAttributes(XbatisPojoCheckScan.class.getName());
        if (annotationAttributes != null) {
            String[] basePackages = (String[]) annotationAttributes.get(LambdaUtil.getName(XbatisPojoCheckScan::basePackages));
            String[] modelPackages = (String[]) annotationAttributes.get(LambdaUtil.getName(XbatisPojoCheckScan::modelPackages));
            String[] resultEntityPackages = (String[]) annotationAttributes.get(LambdaUtil.getName(XbatisPojoCheckScan::resultEntityPackages));
            String[] conditionTargetPackages = (String[]) annotationAttributes.get(LambdaUtil.getName(XbatisPojoCheckScan::conditionTargetPackages));
            String[] orderByTargetPackages = (String[]) annotationAttributes.get(LambdaUtil.getName(XbatisPojoCheckScan::orderByTargetPackages));

            PojoCheckInfo pojoCheckInfo = new PojoCheckInfo();
            pojoCheckInfo.setCheckModel(modelPackages != null && modelPackages.length > 0);
            pojoCheckInfo.setCheckResultEntity(resultEntityPackages != null && resultEntityPackages.length > 0);
            pojoCheckInfo.setCheckConditionTarget(conditionTargetPackages != null && conditionTargetPackages.length > 0);
            pojoCheckInfo.setCheckOrderTarget(orderByTargetPackages != null && orderByTargetPackages.length > 0);

            if (pojoCheckInfo.isCheckModel()) {
                executeCheckPackages(modelPackages, this::checkModel);
            }

            if (pojoCheckInfo.isCheckResultEntity()) {
                executeCheckPackages(resultEntityPackages, this::checkResultEntityAnnotation);
            }

            if (pojoCheckInfo.isCheckConditionTarget()) {
                executeCheckPackages(conditionTargetPackages, this::checkConditionTargetAnnotation);
            }

            if (pojoCheckInfo.isCheckOrderTarget()) {
                executeCheckPackages(orderByTargetPackages, this::checkOrderByTargetAnnotation);
            }

            //假如其中一个没有配置
            if (!pojoCheckInfo.isCheckModel() || !pojoCheckInfo.isCheckResultEntity() || !pojoCheckInfo.isCheckConditionTarget() || !pojoCheckInfo.isCheckOrderTarget()) {
                if (basePackages == null || basePackages.length == 0) {
                    return;
                }
                //以basePackages为准
                pojoCheckInfo.setCheckModel(!pojoCheckInfo.isCheckModel());
                pojoCheckInfo.setCheckResultEntity(!pojoCheckInfo.isCheckResultEntity());
                pojoCheckInfo.setCheckConditionTarget(!pojoCheckInfo.isCheckConditionTarget());
                pojoCheckInfo.setCheckOrderTarget(!pojoCheckInfo.isCheckOrderTarget());
                this.check(basePackages, pojoCheckInfo);
            }

        }
    }

    protected void executeCheckPackages(String[] targetPackages, Consumer<String[]> execution) {
        if (targetPackages == null || targetPackages.length == 0) {
            return;
        }
        execution.accept(targetPackages);
    }

    public void check(String[] basePackages, PojoCheckInfo pojoCheckInfo) {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        // 添加过滤器
        scanner.addIncludeFilter(new AssignableTypeFilter(Object.class));
        for (String basePackage : basePackages) {
            for (BeanDefinition beanDefinition : scanner.findCandidateComponents(basePackage)) {
                Class<?> clazz;
                try {
                    clazz = Class.forName(beanDefinition.getBeanClassName());
                } catch (Exception ignored) {
                    continue;
                }

                if (pojoCheckInfo.isCheckModel() && Model.class.isAssignableFrom(clazz)) {
                    Models.get(clazz);
                } else if (pojoCheckInfo.isCheckResultEntity() && clazz.isAnnotationPresent(ResultEntity.class)) {
                    ResultInfos.get(clazz);
                } else if (pojoCheckInfo.isCheckConditionTarget() && clazz.isAnnotationPresent(ConditionTarget.class)) {
                    Conditions.get(clazz);
                } else if (pojoCheckInfo.isCheckOrderTarget() && clazz.isAnnotationPresent(OrderByTarget.class)) {
                    OrderBys.get(clazz);
                }
            }
        }
    }

    public void checkModel(String[] basePackages) {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        // 添加过滤器
        scanner.addIncludeFilter(new AssignableTypeFilter(Model.class));
        for (String basePackage : basePackages) {
            for (BeanDefinition beanDefinition : scanner.findCandidateComponents(basePackage)) {
                try {
                    Class<?> clazz = Class.forName(beanDefinition.getBeanClassName());
                    Models.get(clazz);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void checkResultEntityAnnotation(String[] basePackages) {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        // 添加注解过滤器
        scanner.addIncludeFilter(new AnnotationTypeFilter(ResultEntity.class));
        for (String basePackage : basePackages) {
            for (BeanDefinition beanDefinition : scanner.findCandidateComponents(basePackage)) {
                try {
                    Class<?> clazz = Class.forName(beanDefinition.getBeanClassName());
                    ResultInfos.get(clazz);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void checkConditionTargetAnnotation(String[] basePackages) {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        // 添加注解过滤器
        scanner.addIncludeFilter(new AnnotationTypeFilter(ConditionTarget.class));
        for (String basePackage : basePackages) {
            for (BeanDefinition beanDefinition : scanner.findCandidateComponents(basePackage)) {
                try {
                    Class<?> clazz = Class.forName(beanDefinition.getBeanClassName());
                    Conditions.get(clazz);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void checkOrderByTargetAnnotation(String[] basePackages) {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        // 添加注解过滤器
        scanner.addIncludeFilter(new AnnotationTypeFilter(OrderByTarget.class));
        for (String basePackage : basePackages) {
            for (BeanDefinition beanDefinition : scanner.findCandidateComponents(basePackage)) {
                try {
                    Class<?> clazz = Class.forName(beanDefinition.getBeanClassName());
                    OrderBys.get(clazz);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
