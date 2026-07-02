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

import java.util.List;

public class XbatisDDLAutoItem {

    private final String dbType;

    private final Mode mode;

    private final List<Class<?>> entities;

    private final String dataSource;

    public XbatisDDLAutoItem(String dbType, Mode mode, List<Class<?>> entities, String dataSource) {
        this.dbType = dbType;
        this.mode = mode;
        this.entities = entities;
        this.dataSource = dataSource;
    }

    public String getDbType() {
        return dbType;
    }

    public Mode getMode() {
        return mode;
    }

    public List<Class<?>> getEntities() {
        return entities;
    }

    public String getDataSource() {
        return dataSource;
    }
}
