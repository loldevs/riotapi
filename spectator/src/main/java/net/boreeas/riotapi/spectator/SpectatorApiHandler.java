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

package net.boreeas.riotapi.spectator;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import net.boreeas.riotapi.RequestException;
import net.boreeas.riotapi.Shard;
import net.boreeas.riotapi.spectator.rest.ChunkInfo;
import net.boreeas.riotapi.spectator.rest.FeaturedGame;
import net.boreeas.riotapi.spectator.rest.FeaturedGameList;
import net.boreeas.riotapi.spectator.rest.GameMetaData;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created on 4/28/2014.
 */
public class SpectatorApiHandler {
    public static final DateFormat DATE_FMT = new SimpleDateFormat("MMM dd, YYYY hh:mm:ss a");
    private static final String TOKEN = "ritopls";

    private Gson gson = new Gson();
    private WebTarget defaultTarget;
    private WebTarget consumerTarget;

    public SpectatorApiHandler(Shard region) {
        Client c = ClientBuilder.newClient();
        defaultTarget = c.target(region.spectatorUrl);
        defaultTarget = defaultTarget.path("observer-mode").path("rest");
        consumerTarget = defaultTarget.path("consumer");
    }

    /**
     * Retrieves the current version of the spectator server
     * @return the current version as a string
     */
    public String getCurrentVersion() {
        return readAsString(consumerTarget.path("version"));
    }

    /**
     * Retrieves a list of featured games from the spectator server
     * @return A FeaturedGameList, containing a list of games as well as the refresh rate
     */
    public FeaturedGameList getFeaturedGameListDto() {
        WebTarget tgt = defaultTarget.path("featured");
        return gson.fromJson($(tgt), FeaturedGameList.class);
    }

    /**
     * Retrieves a list of featured games from the spectator server
     * @return A list of games
     */
    public List<FeaturedGame> getFeaturedGames() {
        return getFeaturedGameListDto().getGameList();
    }

    public GameMetaData getGameMetaData(Shard platform, long gameId) {
        WebTarget tgt = consumerTarget.path("getGameMetaData").path(platform.spectatorPlatformName).path("" + gameId).path("1").path(TOKEN);
        return gson.fromJson($(tgt), GameMetaData.class);
    }

    public ChunkInfo getLastChunkInfo(Shard platform, long gameId) {
        WebTarget tgt = consumerTarget.path("getLastChunkInfo").path(platform.spectatorPlatformName).path("" + gameId).path("1").path(TOKEN);
        return gson.fromJson($(tgt), ChunkInfo.class);
    }

    /**
     * Returns an encrypted and compressed chunk
     * @param platform The target platform
     * @param gameId The target game
     * @param chunkId The target chunk
     * @return The chunk, encrypted and zip-compressed
     */
    public byte[] getEncryptedChunk(Shard platform, long gameId, int chunkId) {
        WebTarget tgt = consumerTarget.path("getGameDataChunk").path(platform.spectatorPlatformName).path(gameId + "/" + chunkId).path(TOKEN);
        return readAsByteArray(tgt);
    }

    /**
     * Returns an encrypted and compressed keyframe
     * @param platform The target platform
     * @param gameId The target game
     * @param keyframeId The target chunk
     * @return The chunk, encrypted and zip-compressed
     */
    public byte[] getEncryptedKeyframe(Shard platform, long gameId, int keyframeId) {
        WebTarget tgt = consumerTarget.path("getKeyFrame").path(platform.spectatorPlatformName).path(gameId + "/" + keyframeId).path(TOKEN);
        return readAsByteArray(tgt);
    }

    public InProgressGame openGame(Shard platform, long gameId, String encryptionKey) {
        return new InProgressGame(this, platform, gameId, encryptionKey);
    }

    public InProgressGame openFeaturedGame(FeaturedGame game) {
        return new InProgressGame(this, game.getPlatformId(), game.getGameId(), game.getEncryptionKey());
    }


    /**
     * Open the request to the web target and returns an InputStreamReader for the message body
     * @param target the web target to access
     * @return the reader for the message body
     */
    private InputStreamReader $(WebTarget target) {

        Response response = target.request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        if (response.getStatus() != 200) {
            throw new RequestException(response.getStatus(), RequestException.ErrorType.getByCode(response.getStatus()));
        }

        return new InputStreamReader(getInputStream(response));
    }

    private InputStream getInputStream(Response response) {
        return (java.io.InputStream) response.getEntity();
    }

    @SneakyThrows
    private String readAsString(WebTarget tgt) {
        return new BufferedReader($(tgt)).readLine();
    }

    private byte[] readAsByteArray(WebTarget tgt) {


        Response response = tgt.request().accept(MediaType.APPLICATION_OCTET_STREAM_TYPE).get();
        if (response.getStatus() != 200) {
            throw new RequestException(response.getStatus(), RequestException.ErrorType.getByCode(response.getStatus()));
        }


        try (BufferedInputStream in = new BufferedInputStream(getInputStream(response))) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();


            byte[] buffer = new byte[1024];

            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }

            return out.toByteArray();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
