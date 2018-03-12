# Taskmaster
Taskmaster project for 42

# Shortcut link

[Go to server code](https://github.com/AlexisVisco/Taskmaster/tree/master/server/src/main/java/fr/aviscogl/taskmaster) or [Go to client code](https://github.com/AlexisVisco/Taskmaster/tree/master/client/src/main/java/fr/aviscogl/taskmaster)

## Basic features

Your program must be able to start jobs as child processes, and keep them alive, restarting
them if necessary. It must also know at all times if these processes are alive or dead
(This must be accurate).

Information on which programs must be started, how, how many, if they must be
restarted, etc... will be contained in a configuration file, the format of which is up to you
(YAML is a good idea, for example, but use whatever you want). This configuration must
be loaded at launch, and must be reloadable, while taskmaster is running, by sending a
SIGHUP to it. When it is reloaded, your program is expected to effect all the necessary
changes to its run state (Removing programs, adding some, changing their monitoring
conditions, etc ...), but it must NOT de-spawn processes that haven’t been changed in
the reload.

Your program must have a logging system that logs events to a local file (When a
program is started, stopped, restarted, when it dies unexpectedly, when the configuration
is reloaded, etc ...)
When started, your program must remain in the foreground, and provide a control
shell to the user. It does not HAVE to be a fully-fledged shell like 42sh, but it must be
at the very least usable (Line editing, history... completion would also be nice). Take
inspiration from supervisor’s control shell, supervisorctl.
