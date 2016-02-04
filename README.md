Application Configuration - Android
============

### Purpose
This library handles configuration management for Staging/Production environments and notifies of non-production flavors on release type builds. The application can have multiple "configuration domains" which identify a slice of functionality you wish to modify. For example: There are 3 server environments prod/staging/qa. There are two advertising environments prod/staging. This means that Server and Avertising are two independently varying configuration domains. They should be built as two separate ApplicationConfigurationController objects.

### Components

*   ApplicationConfiguration - This is the interface that all of your application configurations must implement. It will contain all of the application-specific configuration values for the configuration domain it covers.
*   ApplicationConfigurationController - You must extend the ApplicationConfigurationController to contain ApplicationConfiguration instances for each independent configuration domain.
*   ApplicationConfigurationControllerListener - You can register listeners that are notified when a user switches environments to e.g. invalidate login credentials or flush cache. After all listeners have been notified of a configuration change, System.exit will be called.
*   ApplicationConfigurationServiceLocator - Houses instances of ApplicationConfigurationControllers for each configuration domain.

### Usage
Add staging and production product flavors or add the attribute to your existing flavors. Include the library in your project with the compile directive in your dependencies section of your build.gradle.

        android {
            ...
            productFlavors {
                staging {
                    buildConfigField "boolean", "ALLOW_STAGING", "true"
                }
                production {
                    buildConfigField "boolean", "ALLOW_STAGING", "false"
                }
            }
        }

        repositories {
            ...
            jcenter()
        }
        
        ...

        dependencies {
            ...
            compile 'com.bottlerocketstudios:applicationconfiguration:1.0.3'
        }

#### Application Object
The application object is created first before any Activities, Services, etc are created. This makes it a useful place to initialize the app-wide configuration. The BuildConfig.ALLOW_STAGING constant was created by gradle in your productFlavors above.

        MyApplication extends Application {
        
            @Override
            public void onCreate() {
                initConfiguration();
                
                //Everything that relies on the configuration goes below this line.            
            }
            
            private void initConfiguration() {
                //The BuildConfig.ALLOW_STAGING value is set by your application's build flavor.
                ApplicationConfigurationServiceLocator.getInstance().initialize(
		                this, 
		                BuildConfig.ALLOW_STAGING, 
		                BuildConfig.DEBUG);
		        ApplicationConfigurationServiceLocator.initConfigurationController(
		                MyServerConfigurationController.CONTROLLER_ID, 
		                new MyServerConfigurationController());
            }
        
        }
        
#### Manifest
You must specify your application object in your Manifest if you have not already.

		<application
			...
			android:name=".MyApplication"/>

#### Configuration Controller
You can have as many ApplicationConfigurationControllers as you want as long as they use unique CONTROLLER_ID values and register in the ApplicationConfigurationServiceLocator uniquely.
        
        MyServerConfigurationController extends ApplicationConfigurationController<MyServerConfiguration> {
	        //Avoid using IDs that could be broken by Proguard 
	        //e.g. MyServerConfigurationController.class.getSimpleName();
            public static final String CONTROLLER_ID = "MyServerConfigurationController";

		    @Override
		    protected long getDefaultAppConfigId() {
			    //Pick a default configuration e.g. use the staging configuration for staging builds. 
		        return isStagingAllowed() ? 
			        MyStagingConfiguration.CONFIGURATION_ID : MyProductionConfiguration.CONFIGURATION_ID;
		    }
		
		    @Override
		    protected void addAllApplicationConfigurations() {
		        putApplicationConfiguration(new MyProductionConfiguration());
		        putApplicationConfiguration(new MyStagingConfiguration());
		    }
		
		    @Override
		    protected boolean shouldExitOnChange() {
		        return true;
		    }       
        }

#### Base Configuration
		
		public abstract class MyServerConfiguration implements ApplicationConfiguration {

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

#### Production Configuration
You must have exactly one configuration per ApplicationConfigurationController that returns true for isProduction().

		public class MyProductionConfiguration extends MyServerConfiguration {

		    public static final int CONFIGURATION_ID = 1;
		
		    @Override
		    public long getId() {
		        return CONFIGURATION_ID;
		    }
		
		    @Override
		    public void init(Context context) {
		        setName(context.getString(R.string.my_production_configuration_name));
		        setServerHost(context.getString(R.string.my_production_server_host));
		        setBaseApiPath(context.getString(R.string.my_production_server_base_api_path));
		    }
		
		    @Override
		    public boolean isProduction() {
		        return true;
		    }
		
		}

#### Staging Configuration
You can have multiple staging/qa/dev/whatever configurations that will only be usable if BuildConfig.ALLOW_STAGING is true. Each id must be unique within the ApplicationConfigurationController.

		public class MyStagingConfiguration extends MyServerConfiguration {

		    public static final int CONFIGURATION_ID = 2;
		
		    @Override
		    public long getId() {
		        return CONFIGURATION_ID;
		    }
		
		    @Override
		    public void init(Context context) {
		        setName(context.getString(R.string.pets_staging_configuration_name));
		        setServerHost(context.getString(R.string.pets_staging_server_host));
		        setBaseApiPath(context.getString(R.string.pets_staging_server_base_api_path));
		    }
		
		    @Override
		    public boolean isProduction() {
		        return false;
		    }
		}
		
#### Using the configuration
		
		//Anywhere you just want the current configuration
		MyServerConfiguration myServerConfiguration = ApplicationConfigurationServiceLocator.getCurrentConfiguration(
			MyServerConfigurationController.CONTROLLER_ID, 
			MyServerConfigurationController.class);

		//Somewhere in the application where you want to get the configuration controller instead, perhaps to register a change listener.
		MyServerConfigurationController myServerConfigurationController = ApplicationConfigurationServiceLocator.getConfigurationController(
			MyServerConfigurationController.CONTROLLER_ID, 
			MyServerConfigurationController.class);
        
### Build
This project must be built with gradle. 

*   Version Numbering - The version name should end with "-SNAPSHOT" for non release builds. This will cause the resulting binary, source, and javadoc files to be uploaded to the snapshot repository in Maven as a snapshot build. Removing snapshot from the version name will publish the build on jcenter. If that version is already published, it will not overwrite it.
*   Execution - To build this libarary, associated tasks are dynamically generated by Android build tools in conjunction with Gradle. Example command for the production flavor of the release build type: 
    *   Build and upload: `./gradlew --refresh-dependencies clean lint uploadToMaven`
    *   Build only: `./gradlew --refresh-dependencies clean lint jarRelease`
