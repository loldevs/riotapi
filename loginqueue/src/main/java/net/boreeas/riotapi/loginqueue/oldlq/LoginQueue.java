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

package net.boreeas.riotapi.loginqueue.oldlq;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import lombok.SneakyThrows;
import net.boreeas.riotapi.RequestException;
import net.boreeas.riotapi.Shard;
import net.boreeas.riotapi.com.riotgames.platform.account.management.InvalidCredentialsException;
import net.boreeas.riotapi.loginqueue.AuthResult;
import net.boreeas.riotapi.loginqueue.LoginProvider;
import net.boreeas.riotapi.loginqueue.QueueTimer;
import net.boreeas.riotapi.loginqueue.Ticker;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by malte on 7/11/2014.
 */
public class LoginQueue implements LoginProvider {
    private WebTarget tgt;

    public LoginQueue(Shard shard) {
        tgt = ClientBuilder.newClient().target(shard.loginQueue).path("login-queue/rest/queue/authenticate");
    }

    @SneakyThrows
    public AuthResult getAuthToken(String user, String password) {
        String payload = String.format("payload=user=%s,password=%s", URLEncoder.encode(user, "UTF-8"), URLEncoder.encode(password, "UTF-8"));
        Response response = tgt.request().post(Entity.entity(payload, MediaType.APPLICATION_FORM_URLENCODED));


        if (response.getStatus() == 403) {
            throw new InvalidCredentialsException("Invalid username or password");
        } else if (response.getStatus() != 200) {
            throw new RequestException(response.getStatus());
        }


        String json;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader((InputStream) response.getEntity()))) {
            json = reader.readLine();
        } catch (IOException ex) {
            throw new RequestException("Error reading JSON", ex);
        }

        JsonObject result = new JsonParser().parse(json).getAsJsonObject();
        switch (result.get("status").getAsString()) {
            case "FAILED":
                throw new RequestException("Login failed: " + result.get("reason").getAsString());
            case "LOGIN":
                IngameCredentials credentials = new Gson().fromJson(result.get("inGameCredentials"), IngameCredentials.class);
                return new AuthResultImpl(result.get("token").getAsString(), credentials);
            case "QUEUE":
                Type type = new TypeToken<List<Ticker>>(){}.getType();
                List<Ticker> tickers = new Gson().fromJson(result.get("tickers"), type);
                return new AuthResultImpl(result.get("delay").getAsInt(), result.get("node").getAsInt(), tickers);
            default:
                throw new RequestException(result.toString());
        }
    }


    public QueueTimer waitInQueue(String user, String password) {
        QueueTimer timer = new QueueTimer(this, user, password);
        timer.start();
        return timer;
    }

    @SneakyThrows
    public String waitInQueueBlocking(String user, String password) {
        return waitInQueue(user, password).await();
    }
}
