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

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(XbatisPojoCheckRegistrar.class)
public @interface XbatisPojoCheckScan {
    /**
     * 基础包路径；无法modelPackages、resultEntityPackages、conditionTargetPackages、orderByTargetPackages没有特殊指定；则使用此配置
     *
     * @return
     */
    String[] basePackages() default {};

    /**
     * 要扫描的Model包路径
     */
    String[] modelPackages() default {};

    /**
     * 要扫描的@ResultEntity类包路径
     */
    String[] resultEntityPackages() default {};

    /**
     * 要扫描的@ConditionTarget类包路径
     */
    String[] conditionTargetPackages() default {};

    /**
     * 要扫描的@OrderByTarget类包路径
     */
    String[] orderByTargetPackages() default {};
}
