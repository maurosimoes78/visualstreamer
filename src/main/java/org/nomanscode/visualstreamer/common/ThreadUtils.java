package org.nomanscode.visualstreamer.common;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

public class ThreadUtils
{

    private static final boolean USE_TRACE = false;
    private static final long LOCK_TIMEOUT = 5000;

    static private String getCaller() {

        String name = "unknown";
        try {

            StackTraceElement[] stackTraceElements = new Exception()
                    .getStackTrace();

            if (stackTraceElements.length > 3) {
                name = stackTraceElements[2].getMethodName();
            }

        }
        catch(Exception e) {
            ;
        }

        return name;
    }

    private static void error(String message, Object ...objects ) {
        try {
            for (Object obj : objects) {

                String value = "unknown";

                try {
                    value = obj.toString();
                } catch (Exception e) {
                    ;
                }

                try {
                    message = message.replaceFirst("\\{\\}", value.replace("$", "\\$"));
                }
                catch(Exception e) {
                    String s = e.getMessage();
                }
            }

            System.out.println("ERROR: " + message);
        }
        catch(Exception e) {
            System.out.println("Can't Log Error: " + e.getMessage());
        }
    }

    private static void trace(String message, Object ...objects ) {

        if (!USE_TRACE) {
            return;
        }

        try {
            for (Object obj : objects) {

                String value = "unknown";

                try {
                    value = obj.toString();
                } catch (Exception e) {
                    ;
                }

                try {
                    message = message.replaceFirst("\\{\\}", value.replace("$", "\\$"));
                }
                catch(Exception e) {
                    String s = e.getMessage();
                }
            }

            System.out.println("TRACE: " + message);
        }
        catch(Exception e) {
            System.out.println("Can't Log Trace: " + e.getMessage());
        }
    }

    public static void lock(Lock lock) throws InterruptedException
    {
        if (lock.tryLock(LOCK_TIMEOUT, TimeUnit.MILLISECONDS)) {
            trace("CURRENT_THREAD_ID: {} - THREAD_NAME: \"{}\" - ACQUIRED LOCK {} - CALLER {}", Thread.currentThread().getId(), Thread.currentThread().getName(), lock, getCaller());
        } else {
            ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

            try {
                ThreadInfo[] threads = threadMXBean.dumpAllThreads(true, true);

                StringBuilder sb = new StringBuilder();

                sb.append("\nCURRENT_THREAD_ID: ").append(Thread.currentThread().getId()).append(" - THREAD_NAME: \"").append(Thread.currentThread().getName()).append("\" - FAILED TO ACQUIRE LOCK ").append(lock).append("\n");

                sb.append("--- START THREAD DUMP ---");

                for (int i = threads.length - 1; i >= 0; i--) {
                    ThreadInfo thread = threads[i];
                    sb.append("\nThread ").append(thread.getThreadId()).append(" \"").append(thread.getThreadName()).append("\" - ").append(thread.getThreadState()).append("\n");
                    if (thread.getLockInfo() != null) {
                        sb.append("Waiting for lock: ").append(thread.getLockInfo());

                        if (thread.getLockOwnerId() >= 0) {
                            sb.append(" locked by thread ").append(thread.getLockOwnerId()).append(" \"").append(thread.getLockOwnerName()).append("\"");
                        }
                        sb.append("\n");
                    }

                    sb.append("Acquired locks: ").append(Arrays.toString(thread.getLockedSynchronizers())).append("\n");
                    sb.append("Acquired monitors: ").append(Arrays.toString(thread.getLockedMonitors())).append("\n");

                    for (StackTraceElement ste : thread.getStackTrace()) {
                        sb.append("\tat ").append(ste).append("\n");
                    }
                }
                sb.append("--- END THREAD DUMP ---\n");

                error(sb.toString());
            } catch (Exception ex) {
                error("Failed to obtain thread information: {}", ex);
            }

            lock.lockInterruptibly();
        }
    }

    public static void unlock(Lock lock)
    {
        try {
            lock.unlock();
            trace("CURRENT_THREAD_ID: {} - THREAD_NAME: \"{}\" - RELEASED LOCK {} - CALLER {}", Thread.currentThread().getId(), Thread.currentThread().getName(), lock, getCaller());
        } catch (IllegalMonitorStateException ex) {
            error("FAILED TO RELEASE LOCK: {}", ex);
        }
    }
}
