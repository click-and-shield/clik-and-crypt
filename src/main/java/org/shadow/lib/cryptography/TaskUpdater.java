package org.shadow.lib.cryptography;

/**
 * The TaskUpdater interface provides a mechanism for updating progress information.
 * It is intended to be implemented by classes that need to track and display the progress of a long-running task.
 */

public interface TaskUpdater {

    /**
     * Updates the progress of a task with the current iteration and the total number of iterations.
     *
     * @param current The current iteration or step of the task.
     * @param total The total number of iterations or steps for the entire task.
     */

    void update(long current, long total);
}
