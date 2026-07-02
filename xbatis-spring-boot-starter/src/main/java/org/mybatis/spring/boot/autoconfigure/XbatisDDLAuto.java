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

import cn.xbatis.core.dbType.DbTypeUtil;
import cn.xbatis.ddl.auto.DDLAuto;
import cn.xbatis.ddl.auto.Mode;
import db.sql.api.DbType;
import db.sql.api.IDbType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class XbatisDDLAuto implements BeanPostProcessor {

    private ApplicationEventPublisher applicationEventPublisher;

    private final AtomicBoolean executed = new AtomicBoolean(false);

    protected DataSource primary;

    protected Map<String, DataSource> dataSources;

    protected List<Class<?>> entities;

    protected Mode mode;

    protected IDbType dbType;

    protected String dataSource;

    public XbatisDDLAuto(ApplicationEventPublisher applicationEventPublisher, DataSource primary, Map<String, DataSource> dataSources) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.primary = primary;
        this.dataSources = dataSources;
    }

    public void setEntities(List<Class<?>> entities) {
        this.entities = entities;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public void setDbType(IDbType dbType) {
        this.dbType = dbType;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }


    protected void autoDDL() {
        DataSource ds;
        if (this.dataSource == null || this.dataSource.isEmpty()) {
            ds = this.primary;
            if (ds == null) {
                throw new RuntimeException("找不到可用的dataSource bean");
            }
        } else {
            ds = dataSources.get(this.dataSource);
            if (ds == null) {
                if (this.primary instanceof AbstractRoutingDataSource) {
                    AbstractRoutingDataSource routingDataSource = (AbstractRoutingDataSource) this.primary;
                    Map<Object, DataSource> dss = routingDataSource.getResolvedDataSources();
                    ds = dss.get(this.dataSource);
                }
            }
            if (ds == null) {
                throw new RuntimeException("找不到 dataSource beanName:" + this.dataSource);
            }
        }

        IDbType dbType = this.dbType;
        if (dbType == DbType.UNKNOWN) {
            dbType = DbTypeUtil.getDbType(ds);
        }

        DDLAuto.of(dbType)
                .add(this.entities)
                .mode(this.mode)
                .execute(ds);

        applicationEventPublisher.publishEvent(new XbatisDDLAutoCompleteEvent());
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof SqlSessionFactory && executed.compareAndSet(false, true)) {
            this.autoDDL();
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
