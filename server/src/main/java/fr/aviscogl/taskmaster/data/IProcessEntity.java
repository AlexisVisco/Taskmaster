package fr.aviscogl.taskmaster.data;

import java.util.Optional;

public interface IProcessEntity {

    Optional<Long> getPid();

    long getDuration();

    default String getStringDuration() {
        long seconds = getDuration();
        long absSeconds = Math.abs(seconds);
        String positive = String.format(
                "%d:%02d:%02d",
                absSeconds / 3600,
                (absSeconds % 3600) / 60,
                absSeconds % 60);
        return seconds < 0 ? "-" + positive : positive;
    }

    long getAmountRestartBecauseFail();

    long getAmountRestart();

    ProcessConfig getConfig();

    ProcessStatus getStatus();

    String getParentName();

    String getCurrentName();

    boolean isAlive();

    void stop();

    void restart();
}
