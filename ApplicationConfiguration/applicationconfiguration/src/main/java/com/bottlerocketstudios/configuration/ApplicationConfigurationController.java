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
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Manages the state and ability to switch application configurations for different aspects of the application.
 * Will warn when in a non production mode. Each type of configuration that switch modes should have its
 * own ApplicationConfigurationController managed by the ApplicationConfigurationServiceLocator.
 */
public abstract class ApplicationConfigurationController<T extends ApplicationConfiguration> {
    
    private static final String TAG = ApplicationConfigurationController.class.getSimpleName();

    protected static final long INVALID_CONFIG_ID = -1;

    private Context mContext;
    
    private Boolean mStagingAllowed;
    private Long mSelectedConfigId;
    private long mProductionConfigId = INVALID_CONFIG_ID;
    private String mSettingKeyAddition;
    private Set<ApplicationConfigurationControllerListener> mListeners;
    private Map<Long, T> mApplicationConfigurationMap;

    public ApplicationConfigurationController() {
        mListeners = new HashSet<>();
        mApplicationConfigurationMap = new HashMap<>();
    }
    
    @SuppressWarnings("unused")
    public void initialize(Context context, String keyAddition, boolean stagingAllowed, boolean isDebugBuild) {
        mContext = context.getApplicationContext();
        mStagingAllowed = stagingAllowed;
        mSettingKeyAddition = keyAddition;

        addAllApplicationConfigurations();
        initAllApplicationConfigurations();
        validateAllApplicationConfigurations();

        if (isStagingEnabled() && !isDebugBuild) {
            Toast.makeText(mContext, "DEBUG: STAGING IS IN USE FOR " + keyAddition, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Are staging environments allowed for this controller.
     */
    public boolean isStagingAllowed() {
        return mStagingAllowed;
    }

    private boolean isStagingEnabled() {
        return getSelectedConfigId() != getProductionConfigId();
    }

    protected long getSelectedConfigId() {
        if (!isStagingAllowed()) {
            //Cannot be enabled if it is not allowed, ignore configuration value.
            mSelectedConfigId = getProductionConfigId();
        }

        if (mSelectedConfigId == null) {
            //Lazy load from config on first read.
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
            mSelectedConfigId = sharedPrefs.getLong(getSettingKey(), getDefaultAppConfigId());
        }

        return mSelectedConfigId;
    }

    /**
     * Select the specified application configuration by Id. This method will notify all listeners then
     * shutdown the application by calling System.exit(0);
     */
    public void setSelectedConfigId(long applicationConfigId) {
        T newApplicationConfig = getApplicationConfigurationById(applicationConfigId);
        if (isStagingAllowed() && newApplicationConfig != null && applicationConfigId != getSelectedConfigId()) {
            mSelectedConfigId = applicationConfigId;
            SharedPreferences.Editor sharedPrefsEditor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
            sharedPrefsEditor.putLong(getSettingKey(), applicationConfigId);
            sharedPrefsEditor.commit();
            Log.i(TAG, "Notifying listeners for switch to " + newApplicationConfig.getName() + ".");
            for (ApplicationConfigurationControllerListener listener: mListeners) {
                listener.onStagingSwitch(mContext, newApplicationConfig);
            }
            Log.i(TAG, "Shutting down VM to affect staging switch.");
            System.exit(0);
        }
    }
    
    protected String getSettingKey() {
        return "com.bottlerocketstudios.configuration.ApplicationConfigurationController."
                + mSettingKeyAddition + ".selectedConfigId";
    }

    /**
     * Register interest in configuration changes.
     */
    public void addListener(ApplicationConfigurationControllerListener<T> listener) {
        mListeners.add(listener);
    }

    /**
     * Unregister interest in configuration changes.
     */
    public void removeListener(ApplicationConfigurationControllerListener<T> listener) {
        mListeners.remove(listener);
    }

    protected void putApplicationConfiguration(T applicationConfiguration) {
        mApplicationConfigurationMap.put(applicationConfiguration.getId(), applicationConfiguration);
        if (applicationConfiguration.isProduction()) {
            if (getProductionConfigId() == INVALID_CONFIG_ID) {
                setProductionConfigId(applicationConfiguration.getId());
            } else {
                throw new IllegalStateException("You have added two configurations to the same set which both report isProduction() == true");
            }
        }
    }

    private Map<Long, T> getApplicationConfigurationMap() {
        return mApplicationConfigurationMap;
    }

    private T getApplicationConfigurationById(long id) {
        return mApplicationConfigurationMap.get(id);
    }

    /**
     * Get the currently selected application configuration.
     */
    public T getCurrentApplicationConfiguration() {
        T applicationConfiguration = getApplicationConfigurationById(getSelectedConfigId());
        if (applicationConfiguration == null) {
            Log.w(TAG, "Selected Configuration was null. Falling back to production configuration.");
            applicationConfiguration = getApplicationConfigurationById(getProductionConfigId());
        }
        return applicationConfiguration;
    }

    /**
     * Return the ID for the production configuration.
     */
    public long getProductionConfigId() {
        return mProductionConfigId;
    }

    private void setProductionConfigId(long productionConfigId) {
        mProductionConfigId = productionConfigId;
    }

    private void initAllApplicationConfigurations() {
        for (T appConfig: getApplicationConfigurationMap().values()) {
            appConfig.init(mContext);
        }
    }

    private void validateAllApplicationConfigurations() {
        if (getProductionConfigId() == INVALID_CONFIG_ID) {
            throw new IllegalStateException("None of the application configurations report isProduction() == true");
        }

        if (getApplicationConfigurationMap().size() == 0) {
            throw new IllegalStateException("No application configurations were provided");
        }
    }

    /**
     * Return the ID for the configuration ID that is considered default for this build. Builds in which
     * staging is not allowed will ignore this value altogether and use the production environment.
     */
    protected abstract long getDefaultAppConfigId();

    /**
     * Call putApplicationConfiguration for all of your application configuration objects. Exactly one
     * must return isProduction() == true. After this call the init method for your configurations will
     * be called.
     */
    protected abstract void addAllApplicationConfigurations();

    /**
     * Return true if you want the application to be killed immediately after storing the newly selected
     * environment and waiting for notified listeners to complete.
     */
    protected abstract boolean shouldExitOnChange();

}
