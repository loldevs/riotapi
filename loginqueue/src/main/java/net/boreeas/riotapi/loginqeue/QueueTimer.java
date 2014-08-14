/*
 * Copyright 2014 The LolDevs team (https://github.com/loldevs)
 *
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

package net.boreeas.riotapi.loginqeue;

import net.boreeas.riotapi.com.riotgames.platform.account.management.AccountManagementException;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by malte on 7/13/2014.
 */
public class QueueTimer extends Thread {

    private LoginQueue loginQueue;
    private String user;
    private String password;

    private long overrideDelay;

    private CountDownLatch latch = new CountDownLatch(1);
    private boolean isError = false;
    private AuthResult result;
    private AccountManagementException ex;

    public QueueTimer(LoginQueue loginQueue, String user, String password) {
        super("Queue Timer for " + user);
        this.loginQueue = loginQueue;
        this.user = user;
        this.password = password;
    }


    public String await() throws InterruptedException {
        this.latch.await();
        return getResultOrError().getToken();
    }

    public AuthResult await(long timeout, TimeUnit unit) throws InterruptedException {
        this.latch.await(timeout, unit);
        return getResultOrError();
    }


    private AuthResult getResultOrError() {
        if (isError) {
            throw ex;
        } else {
            return result;
        }
    }

    public void cancel() {
        latch.countDown();
    }

    public long getCurrentDelay() {
        return overrideDelay > 0 ? overrideDelay : result.getDelay();
    }

    public long getPosition() {
        return result.getPosition();
    }

    public boolean isFinished() {
        return latch.getCount() == 0;
    }

    public void run() {
        while (latch.getCount() > 0) { // We count down as soon as we get through the queue, so we can abuse this as a finished flag

            try {
                result = loginQueue.getAuthToken(user, password);
                if (result.getStatus() == AuthResult.Status.OK) {
                    latch.countDown();
                } else {
                    try {
                        Thread.sleep(overrideDelay > 0 ? overrideDelay : result.getDelay());
                    } catch (InterruptedException ex) {
                        throw new AccountManagementException("Queue interrupted", ex);
                    }
                }


            } catch (AccountManagementException ex) {
                this.ex = ex;
                isError = true;
                latch.countDown();
            }
        }
    }
}
