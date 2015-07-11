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

package net.boreeas.riotapi.loginqueue;

import lombok.extern.log4j.Log4j;
import net.boreeas.riotapi.RequestException;
import net.boreeas.riotapi.com.riotgames.platform.account.management.AccountManagementException;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by malte on 7/13/2014.
 */
@Log4j
public class QueueTimer extends Thread {

    private static final int MAX_CLOUDFLARE_ERROR_RETRY = 3;

    private LoginProvider loginQueue;
    private String user;
    private String password;

    private long overrideDelay;

    private CountDownLatch latch = new CountDownLatch(1);
    private boolean isError = false;
    private AuthResult result;
    private RuntimeException ex;
    private int cloudflareErrorCounter = 0;

    public QueueTimer(LoginProvider loginQueue, String user, String password) {
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
        } else if (result != null) {
            return result;
        } else {
            run();
            if (isError) throw ex;
            if (result != null) return result;
            throw new IllegalStateException("No result after run");
        }
    }

    public void cancel() {
        latch.countDown();
    }

    public long getCurrentDelay() {
        if (overrideDelay > 0) {
            return overrideDelay;
        }

        if (result != null) {
            return result.getDelay();
        }

        throw new IllegalStateException("Result not available yet");
    }

    public long getPosition() {
        if (result != null) {
            return result.getPosition();
        }

        throw new IllegalStateException("Result not available yet");
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


            } catch (RequestException ex) {
                int type = ex.getErrorType().code;
                if ((type >= RequestException.ErrorType.CLOUDFLARE_GENERIC.code && type <= RequestException.ErrorType.CLOUDFLARE_SSL_HANDSHAKE_FAILED.code)
                        && cloudflareErrorCounter < MAX_CLOUDFLARE_ERROR_RETRY) {
                    cloudflareErrorCounter++;
                    log.warn("Cloudflare error: " + type + " - Reattempting ("  + cloudflareErrorCounter + "/" + MAX_CLOUDFLARE_ERROR_RETRY + ")");
                } else {
                    this.ex = ex;
                    isError = true;
                    latch.countDown();
                }
            } catch (RuntimeException ex) {
                this.ex = ex;
                isError = true;
                latch.countDown();
            }
        }
    }
}
