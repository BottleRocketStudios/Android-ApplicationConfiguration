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

import com.bottlerocketstudios.configuration.ApplicationConfiguration;

/**
 * TestServerConfiguration specific extension of ApplicationConfiguration
 */
public abstract class TestServerConfiguration implements ApplicationConfiguration {

    private String mName;
    private String mServerHost;
    private String mBaseApiPath;

    @Override
    public String getName() {
        return mName;
    }

    protected void setName(String name) {
        mName = name;
    }

    public String getServerHost() {
        return mServerHost;
    }

    protected void setServerHost(String serverHost) {
        mServerHost = serverHost;
    }

    public String getBaseApiPath() {
        return mBaseApiPath;
    }

    protected void setBaseApiPath(String baseApiPath) {
        mBaseApiPath = baseApiPath;
    }
}
