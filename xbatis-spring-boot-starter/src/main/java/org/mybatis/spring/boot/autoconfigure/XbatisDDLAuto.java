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
import db.sql.api.DbType;
import db.sql.api.DbTypes;
import db.sql.api.IDbType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class XbatisDDLAuto implements BeanPostProcessor {

    private static final Logger logger = LoggerFactory.getLogger(XbatisDDLAuto.class);

    private ApplicationEventPublisher applicationEventPublisher;

    private final AtomicBoolean executed = new AtomicBoolean(false);

    private final DataSource primary;

    private final Map<String, DataSource> dataSources;

    private List<XbatisDDLAutoItem> xbatisDDLAutoItems;

    public XbatisDDLAuto(ApplicationEventPublisher applicationEventPublisher, DataSource primary, Map<String, DataSource> dataSources) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.primary = primary;
        this.dataSources = dataSources;
    }

    protected void executeAutoDDL(XbatisDDLAutoItem item) {
        DataSource realDataSource;
        if (item.getDataSource() == null || item.getDataSource().isEmpty()) {
            realDataSource = this.primary;
            if (realDataSource == null) {
                throw new RuntimeException("找不到可用的dataSource bean");
            }
        } else {
            realDataSource = dataSources.get(item.getDataSource());
            if (realDataSource == null) {
                if (this.primary instanceof AbstractRoutingDataSource) {
                    AbstractRoutingDataSource routingDataSource = (AbstractRoutingDataSource) this.primary;
                    Map<Object, DataSource> dss = routingDataSource.getResolvedDataSources();
                    realDataSource = dss.get(item.getDataSource());
                }
            }
            if (realDataSource == null) {
                throw new RuntimeException("找不到 dataSource beanName:" + item.getDataSource());
            }
        }

        IDbType dbType = DbTypes.getByName(item.getDbType());
        if (dbType == DbType.UNKNOWN) {
            dbType = DbTypeUtil.getDbType(realDataSource);
        }

        if (!item.getEntities().isEmpty()) {
            DDLAuto.of(dbType)
                    .add(item.getEntities())
                    .mode(item.getMode())
                    .execute(realDataSource);
        }
    }

    protected void autoDDL() {
        for (XbatisDDLAutoItem item : xbatisDDLAutoItems) {
            this.executeAutoDDL(item);
        }
        logger.info("Xbatis DDL Auto 全部完成，发送 XbatisDDLAutoCompleteEvent 事件");
        applicationEventPublisher.publishEvent(new XbatisDDLAutoCompleteEvent(this));
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof SqlSessionFactory && executed.compareAndSet(false, true)) {
            this.autoDDL();
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }

    public void setXbatisDDLAutoItems(List<XbatisDDLAutoItem> xbatisDDLAutoItems) {
        this.xbatisDDLAutoItems = xbatisDDLAutoItems;
    }
}
