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

import cn.xbatis.ddl.auto.Mode;
import db.sql.api.DbType;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(XbatisDDLAutoRegistrar.class)
@Repeatable(XbatisDDLAutoScans.class)
public @interface XbatisDDLAutoScan {
    /**
     * 实体类包路径；
     *
     * @return
     */
    String[] entityPackages() default {};

    /**
     * 模式
     *
     * @return
     */
    Mode mode() default Mode.CREATE;

    /**
     * 数据库类型
     *
     * @return
     */
    DbType dbType() default DbType.UNKNOWN;

    /**
     * 指定数据源
     *
     * @return 数据源的beanName
     */
    String dataSource() default "";

    /**
     * 父接口
     *
     * @return
     */
    Class<?> markerInterface() default Void.class;
}
