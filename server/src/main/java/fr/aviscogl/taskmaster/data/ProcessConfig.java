package fr.aviscogl.taskmaster.data;

import java.util.HashMap;

public class ProcessConfig {
    String name;
    String command;
    String args;
    String workingDirectory;
    String umask;
    boolean launchAtStart;

    RestartType needRestart;

    int[] expectedExitStatus;
    int amount;
    int expectedTimeToBeValid;
    int howManyRestart;
    int signalToStop;
    HashMap<String, String> environement;
}
