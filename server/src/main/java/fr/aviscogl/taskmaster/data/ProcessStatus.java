package fr.aviscogl.taskmaster.data;

import fr.aviscogl.taskmaster.util.Color;

public enum ProcessStatus {
    LAUNCHING(Color.YELLOW + "Launching..." + Color.RESET),
    LAUNCHED(Color.GREEN + "Launched" + Color.RESET),
    TERMINATING(Color.PURPLE + "Terminating..." + Color.RESET),
    TERMINATED(Color.RED + "Terminated" + Color.RESET);

    private String state;

    ProcessStatus(String state) {

        this.state = state;
    }

    public String getState() {
        return state;
    }
}
