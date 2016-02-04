/*
 * Copyright (c) 2016 Bottle Rocket LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bottlerocketstudios.configuration.configuration;

import com.bottlerocketstudios.configuration.ApplicationConfigurationController;

public class TestConfigurationController extends ApplicationConfigurationController<TestServerConfiguration> {
    public static final String CONTROLLER_ID = "TestConfigurationController";

    @Override
    protected long getDefaultAppConfigId() {
        return isStagingAllowed() ? TestStagingServerConfiguration.CONFIGURATION_ID : TestProductionServerConfiguration.CONFIGURATION_ID;
    }

    @Override
    protected void addAllApplicationConfigurations() {
        putApplicationConfiguration(new TestProductionServerConfiguration());
        putApplicationConfiguration(new TestStagingServerConfiguration());
    }

    @Override
    protected boolean shouldExitOnChange() {
        return true;
    }
}
