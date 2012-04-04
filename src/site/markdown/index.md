# Gitblit Crowd Authentication

This project provides an extension to [Gitblit](http://gitblit.com) in order to authenticate users on an [Atlassian Crowd
server](http://www.atlassian.com/software/crowd/overview).

[Gitblit](http://gitblit.com) is an open-source, pure Java stack for managing, viewing, and serving Git repositories.
It's designed primarily as a tool for small workgroups who want to host centralized repositories. It's a really good
solution to host Git repositories: easy to deploy, simple to administer, pretty powerful.

This extension customizes the Gitblit User Management to use Crowd instead of the internal user authentication.

The extension was developed and tested for Gitblit 0.9.1.

## Configuration

Configuring this extension is a two-step process:

* Preparing Crowd to accept request from your Gitblit server.
* Configuring Gitblit and the extension to connect to Crowd.

For the first step, please refer to the [Atlassian Page](http://confluence.atlassian.com/display/CROWD/Adding+an+Application),
explaining how to add an application inside your Crowd server. Select _Generic Application_ as Application Type. Notice
that the _application name_ and _password_ are required later to let Gitblit connect to Crowd. This extension does only
use the _Crowd_ service, so you don't have to enable the _Active Directory_ support.

Once the application is added to Crowd, you can start configuring the extension. For this second step, refer to the
 [usage guide](usage.html).

## License

This extension is licensed under the _Apache Software License 2.0_. This project was funded by [akquinet](http://www.akquinet.de).