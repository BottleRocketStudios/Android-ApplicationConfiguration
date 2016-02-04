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

import android.content.Context;

public class TestStagingServerConfiguration extends TestServerConfiguration {

    public static final int CONFIGURATION_ID = 2;

    //Public for testing validation, do not do this.
    public static final String SERVER_HOST = "http://staging.api.com";

    @Override
    public long getId() {
        return CONFIGURATION_ID;
    }

    @Override
    public void init(Context context) {
        setName("Staging");
        setServerHost(SERVER_HOST);
        setBaseApiPath("/v1");
    }

    @Override
    public boolean isProduction() {
        return false;
    }
}
