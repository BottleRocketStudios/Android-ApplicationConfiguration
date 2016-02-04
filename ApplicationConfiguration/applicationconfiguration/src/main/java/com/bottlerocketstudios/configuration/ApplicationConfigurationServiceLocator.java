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
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

/**
 * Application wide service locator for one or more ApplicationConfigurationControllers. A call
 * to initialize should be placed in the application object's onCreate. This will show a warning toast
 * on release builds with staging allowed.
 */
public class ApplicationConfigurationServiceLocator {
    private static final String TAG = ApplicationConfigurationServiceLocator.class.getSimpleName();
    
    private Map<String, ApplicationConfigurationController<?>> mControllers;
    private Context mContext;
    private Boolean mStagingAllowed;
    private boolean mDebugBuild;

    private ApplicationConfigurationServiceLocator() {
        mControllers = new HashMap<>();
    }
    
    /**
     * SingletonHolder is loaded on the first execution of Singleton.getInstance()
     * or the first access to SingletonHolder.INSTANCE, not before.
     */
    private static class SingletonHolder {
        public static final ApplicationConfigurationServiceLocator instance = new ApplicationConfigurationServiceLocator();
    }

    /**
     * Get the instance or create it. (inherently thread safe Bill Pugh pattern)
     */
    public static ApplicationConfigurationServiceLocator getInstance() {
        return SingletonHolder.instance;
    }
    
    @SuppressWarnings("unused")
    public void initialize(Context context, boolean allowStaging, boolean isDebugBuild) {
        mContext = context.getApplicationContext();
        mStagingAllowed = allowStaging;
        mDebugBuild = isDebugBuild;
        if (isStagingAllowed() && !mDebugBuild) {
            Toast.makeText(mContext, "DEBUG: STAGING ALLOWED ON A RELEASE BUILD.", Toast.LENGTH_LONG).show();
        }
    }

    public boolean isStagingAllowed() {
        return mStagingAllowed;
    }

    public boolean isDebugBuild() {
        return mDebugBuild;
    }

    /**
     * Initialize the supplied configuration controller and associate it with the supplied identifier
     */
    public static ApplicationConfigurationController initConfigurationController(String controllerId, ApplicationConfigurationController<?> controller) {
        getInstance().putConfigurationController(controllerId, controller);
        getInstance().initializeController(controllerId, controller);
        return controller;
    }

    private void putConfigurationController(String controllerId, ApplicationConfigurationController<?> controller) {
        mControllers.put(controllerId, controller);
    }

    private void initializeController(String controllerId, ApplicationConfigurationController<?> controller) {
        controller.initialize(mContext, controllerId, isStagingAllowed(), isDebugBuild());
    }

    /**
     * Get configuration associated with the controllerId
     */
    public static <T extends ApplicationConfigurationController<?>> T getConfigurationController(String controllerId, Class<T> configurationControllerClass) {
        return configurationControllerClass.cast(getInstance().mControllers.get(controllerId));
    }

    public static <AC extends ApplicationConfiguration, T extends ApplicationConfigurationController<AC>> AC getCurrentConfiguration(String controllerId, Class<T> configurationControllerClass) {
        return getConfigurationController(controllerId, configurationControllerClass).getCurrentApplicationConfiguration();
    }
    
}
