package org.motechproject.http.agent.listener;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;

import org.apache.http.HttpException;

public class RetriableTask<T> implements Callable<T> {

    private Callable<T> task;
    public static final int DEFAULT_NUMBER_OF_RETRIES = 1;
    public static final long DEFAULT_WAIT_TIME = 0;

    private int numberOfRetries; // total number of tries
    private int numberOfTriesLeft; // number left
    private long timeToWait; // wait interval

    public RetriableTask(Callable<T> task) {
        this(DEFAULT_NUMBER_OF_RETRIES, DEFAULT_WAIT_TIME, task);
    }

    public RetriableTask(int numberOfRetries, long timeToWait, Callable<T> task) {
        this.numberOfRetries = numberOfRetries;
        numberOfTriesLeft = this.numberOfRetries;
        this.timeToWait = timeToWait;
        this.task = task;
    }

    public T call() throws HttpException, InterruptedException {
        T t = null;
        while (numberOfTriesLeft > 0) {
            try {
                t = (T) task.call();
            } catch (InterruptedException e) {
                throw e;
            } catch (CancellationException e) {
                throw e;
            } catch (Exception e) {
                numberOfTriesLeft--;
                if (numberOfTriesLeft > 0) {
                    Thread.sleep(timeToWait);
                    continue;
                } else {
                    throw (HttpException) e;
                }
            }
            return t;
        }
        return t;
    }
}
