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

/**
 * Listener interface for the application configuration controller. A strong reference is retained to these
 * listeners when added to the ApplicationConfigurationController and they will be notified of
 * events related to application configuration switches.
 */
public interface ApplicationConfigurationControllerListener <T extends ApplicationConfiguration>{
    /**
     * A staging configuration change has occurred, clear any cached configuration or data that is subject
     * to change when switching to staging.
     */
    public void onStagingSwitch(Context context, T applicationConfiguration);
}
