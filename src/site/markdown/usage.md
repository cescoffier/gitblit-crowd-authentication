# Configuring the Crowd Extension

This page explains how to use the Crowd authentication support in Gitblit. We make the assumption that Gitblit is
running within Tomcat and is deployed in _GITBLIT_DIR_. This directory should probably look like: _tomcat-webapps/gitblit_.

## Deploying the extension to Giblit

The _dist_ package contains all required dependencies, so you just have to copy
_gitblit-crowd-authentication-$version-dist.jar_ in _GITBLIT_DIR/WEB-INF/lib_.

## Configuring the extension

The extension uses the configuration support from Gitblit. In this context, the configuration is written in:
  _GITBLIT_DIR_/WEB-INF/web.xml_

Two tasks are required:

* instructing Gitblit to use the Crowd extension
* configuring the extension

### Configuring Gitblit to use the Crowd extension

In the configuration file, search for the _real.userService_ parameter, and replace the value by:
_de.akquinet.innovation.gitblit.crowd.CrowdAuthenticationService_.

    	<context-param>
    		<param-name>realm.userService</param-name>
    		<!--<param-value>/opt/gitblit/users.properties</param-value>-->
    		<param-value>de.akquinet.innovation.gitblit.crowd.CrowdAuthenticationService</param-value>
    	</context-param>

### Configuring the Crowd access

Three parameters are required to use the extension:

* crowd.serverUrl: indicates the Crowd server url
* crowd.applicationName: indicates the application name (registered in the Crowd server)
* crowd.applicationPassword: indicates the applicaiton password (set in the Crowd server for this application)

So, add those parameters to the configuration file such as in:

        <context-param>
            <param-name>crowd.serverUrl</param-name>
            <param-value>http://172.20.201.194:8095/crowd</param-value>
        </context-param>
        <context-param>
            <param-name>crowd.applicationName</param-name>
            <param-value>MY_APP_NAME</param-value>
        </context-param>
        <context-param>
            <param-name>crowd.applicationPassword</param-name>
            <param-value>MY_APP_PASSWORD</param-value>
        </context-param>

### Configuring the extension

The extension can be configured using a couple of settings:

* crowd.autoAdmin: all authenticated users are set as admin (false by default)
* crowd.autoCreateUser: creates a Gitblit user to each authenticated user (recommended, true by default)
* crowd.gitblitUsers: if you use _autoCreateUser_, set the path to the file where gitblit users and teams are stored.
* crowd.defaultTeam: indicates the name of the team to which all users will be added. The team is created if not existing.
(null by default, so no default team). This setting is useful to give access to a set of repositories automatically.

        <context-param>
            <param-name>crowd.autoAdmin</param-name>
            <param-value>true</param-value>
        </context-param>
        <context-param>
            <param-name>crowd.autoCreateUser</param-name>
            <param-value>true</param-value>
        </context-param>
        <context-param>
       	    <param-name>crowd.gitblitUsers</param-name>
       	    <param-value>/opt/gitblit/users.properties</param-value>
        </context-param>
        <context-param>
            <param-name>crowd.defaultTeam</param-name>
            <param-value>akquinet</param-value>
        </context-param>


### All in one

        <context-param>
        	<param-name>realm.userService</param-name>
        	<param-value>de.akquinet.innovation.gitblit.crowd.CrowdAuthenticationService</param-value>
        </context-param>
        <context-param>
            <param-name>crowd.serverUrl</param-name>
            <param-value>http://172.20.201.194:8095/crowd</param-value>
        </context-param>
        <context-param>
            <param-name>crowd.applicationName</param-name>
            <param-value>MY_APP_NAME</param-value>
        </context-param>
        <context-param>
            <param-name>crowd.applicationPassword</param-name>
            <param-value>MY_APP_PASSWORD</param-value>
        </context-param>
        <context-param>
             <param-name>crowd.autoAdmin</param-name>
             <param-value>true</param-value>
         </context-param>
         <context-param>
             <param-name>crowd.autoCreateUser</param-name>
             <param-value>true</param-value>
         </context-param>
         <context-param>
            <param-name>crowd.gitblitUsers</param-name>
            <param-value>/opt/gitblit/users.properties</param-value>
        </context-param>
         <context-param>
             <param-name>crowd.defaultTeam</param-name>
             <param-value>akquinet</param-value>
         </context-param>

