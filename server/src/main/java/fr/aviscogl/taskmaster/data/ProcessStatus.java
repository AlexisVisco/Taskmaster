package fr.aviscogl.taskmaster.data;

import fr.aviscogl.taskmaster.util.Color;

public enum ProcessStatus {
    LAUNCHING(Color.YELLOW + "⚫" + Color.RESET),
    LAUNCHED(Color.GREEN + "⚫" + Color.RESET),
    TERMINATING(Color.PURPLE + "⚫" + Color.RESET),
    TERMINATED(Color.RED + "⚫" + Color.RESET);

    private String state;

    ProcessStatus(String state) {

        this.state = state;
    }

    public String getState() {
        return state;
    }
}
