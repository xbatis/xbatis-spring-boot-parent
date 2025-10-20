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


public class PojoCheckInfo {

    private boolean checkModel;

    private boolean checkResultEntity;

    private boolean checkConditionTarget;

    private boolean checkOrderTarget;

    public boolean isCheckModel() {
        return checkModel;
    }

    public void setCheckModel(boolean checkModel) {
        this.checkModel = checkModel;
    }

    public boolean isCheckResultEntity() {
        return checkResultEntity;
    }

    public void setCheckResultEntity(boolean checkResultEntity) {
        this.checkResultEntity = checkResultEntity;
    }

    public boolean isCheckConditionTarget() {
        return checkConditionTarget;
    }

    public void setCheckConditionTarget(boolean checkConditionTarget) {
        this.checkConditionTarget = checkConditionTarget;
    }

    public boolean isCheckOrderTarget() {
        return checkOrderTarget;
    }

    public void setCheckOrderTarget(boolean checkOrderTarget) {
        this.checkOrderTarget = checkOrderTarget;
    }
}
