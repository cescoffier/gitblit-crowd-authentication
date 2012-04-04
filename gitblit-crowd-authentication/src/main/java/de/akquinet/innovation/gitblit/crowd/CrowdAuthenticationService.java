/*
 * Copyright 2012 akquinet
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.akquinet.innovation.gitblit.crowd;

import com.atlassian.crowd.integration.rest.service.factory.RestCrowdClientFactory;
import com.atlassian.crowd.model.user.User;
import com.atlassian.crowd.service.client.CrowdClient;
import com.gitblit.GitBlit;
import com.gitblit.IStoredSettings;
import com.gitblit.models.TeamModel;
import com.gitblit.models.UserModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Gitblit User Service using Crowd.
 */
public class CrowdAuthenticationService extends com.gitblit.GitblitUserService {

    CrowdClient m_client;
    RestCrowdClientFactory m_crowdFactory;
    IStoredSettings m_configuration;

    private static final Logger logger = LoggerFactory.getLogger(CrowdAuthenticationService.class);
    
    @Override
    public UserModel authenticate(String username, char[] password) {
        logger.info("User " + username + " is trying to log in");
        try {
            User user = m_client.authenticateUser(username, new String(password));
            logger.info("User " + username + " authenticated");

            UserModel um = new UserModel(user.getName());

            // Check whether we want to set all user as admin.
            if (m_configuration.getBoolean(Keys.crowd.autoAdmin, false)) {
                um.canAdmin = true;
            }

            // Check whether we want to create a gitblit user for new user.
            if (m_configuration.getBoolean(Keys.crowd.autoCreateUser, true)) {
                if (! serviceImpl.getAllUsernames().contains(user.getName())) {
                    logger.info("The user " + um.getName() + " is unknown so far, gonna create a gitblit user");
                    TeamModel tm = getDefaultTeam();
                    if (tm != null) {
                        tm.addUser(um.getName());
                        um.teams.add(tm);
                        serviceImpl.updateTeamModel(tm);
                    }
                    serviceImpl.updateUserModel(um);

                    logger.info("The user " + um.getName() + " added, also added to the default team if enabled");
                }
            }
            return um;
        } catch (Exception e) {
            logger.info("User " + username + " can't authenticate", e);
        }
        return null;
    }

    @Override
    public void setup(IStoredSettings settings) {
        m_configuration = settings;

        logger.info("Configuring " + this.getClass().getName());
        try {
            m_crowdFactory = new RestCrowdClientFactory();
            String crowdUrl = m_configuration.getString(Keys.crowd.serverUrl, null);
            String applicationName = m_configuration.getString(Keys.crowd.applicationName, null);
            String applicationPassword = m_configuration.getString(Keys.crowd.applicationPassword, null);
            if (crowdUrl == null  || applicationName == null  || applicationPassword == null) {
                logger.error("Crowd configuration missing - url:" + crowdUrl
                        + " applicationName:" + applicationName
                        + " applicationPassword: " + applicationPassword);
                throw new IllegalArgumentException("Crowd configuration missing");
            }
            m_client = m_crowdFactory.newInstance(crowdUrl, applicationName, applicationPassword);

            logger.info("Crowd client created for " + applicationName + " on " + crowdUrl);
        } catch (Exception e) {
            logger.error("Can't create Crowd client", e);
            throw new IllegalStateException(e);
        }

        File realmFile = GitBlit.getFileOrFolder(Keys.crowd.gitblitUsers, "users.conf");
        logger.info("Gitblit Internal User Service file: " + realmFile.getAbsolutePath());
        serviceImpl = createUserService(realmFile);

        logger.info("Gitblit Internal User Service delegating to " + serviceImpl.toString());
        
        // Check whether we have a default team
        if (getDefaultTeamName() != null) {
            logger.info("Default team name configured to " + getDefaultTeamName());
            if (! serviceImpl.getAllTeamNames().contains(getDefaultTeamName())) {
                logger.info("The " + getDefaultTeamName() + " team does not exist, creating it");
                TeamModel team = new TeamModel(getDefaultTeamName());
                serviceImpl.updateTeamModel(team);
                logger.info("The " + getDefaultTeamName() + " was created");
            }

        }

        // All users are part of the default team
        TeamModel team = getDefaultTeam();
        if (team != null) {
            logger.info("Sanity check - set existing user to the default team");
            for (UserModel user : serviceImpl.getAllUsers()) {
                team.addUser(user.getName());
                user.teams.add(team);
                serviceImpl.updateTeamModel(team);
                serviceImpl.updateUserModel(user);
                logger.info("Sanity check - User " + user.getName() + " was added to team " + team.name);
            }
        }
    }

    public String getDefaultTeamName() {
        return m_configuration.getString(Keys.crowd.defaultTeam, null);
    }

    public TeamModel getDefaultTeam() {
        String team = getDefaultTeamName();
        if (team != null) {
            return  serviceImpl.getTeamModel(team);
        } else {
            logger.info("No default team configured.");
            return null;
        }

    }

    @Override
    public boolean supportsCookies() {
        return false; //TODO Cookies support
    }

}
