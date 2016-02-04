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

package com.bottlerocketstudios.configuration;

import android.test.AndroidTestCase;

import com.bottlerocketstudios.applicationconfiguration.BuildConfig;
import com.bottlerocketstudios.configuration.configuration.TestConfigurationController;
import com.bottlerocketstudios.configuration.configuration.TestServerConfiguration;
import com.bottlerocketstudios.configuration.configuration.TestStagingServerConfiguration;

public class ConfigurationTest extends AndroidTestCase {
    public void testConfiguration() {
        ApplicationConfigurationServiceLocator.getInstance().initialize(
                getContext(),
                true,
                BuildConfig.DEBUG);
        ApplicationConfigurationServiceLocator.initConfigurationController(
                TestConfigurationController.CONTROLLER_ID,
                new TestConfigurationController());

        TestConfigurationController testConfigurationController = ApplicationConfigurationServiceLocator.getConfigurationController(TestConfigurationController.CONTROLLER_ID, TestConfigurationController.class);
        TestServerConfiguration testServerConfiguration = ApplicationConfigurationServiceLocator.getCurrentConfiguration(TestConfigurationController.CONTROLLER_ID, TestConfigurationController.class);
        assertSame("Did not get same configuration", testConfigurationController.getCurrentApplicationConfiguration(), testServerConfiguration);
        assertEquals("Server was not staging server", TestStagingServerConfiguration.SERVER_HOST, testServerConfiguration.getServerHost());
    }
}
