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

import android.content.Context;

public interface ApplicationConfiguration {
    /**
     * Return a unique long value for configuration.
     */
    public long getId();

    /**
     * Return a string which will name this configuration. Can be used for convenience to display when
     * selecting environments.
     */
    public String getName();

    /**
     * Load any resources that require a context on initialization.
     */
    public void init(Context context);

    /**
     * Determine if this configuration can be used in production. Only one configuration in a set
     * should be allowed to do this.
     */
    public boolean isProduction();

}
