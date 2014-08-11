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

package net.boreeas.riotapi;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import lombok.SneakyThrows;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Miscellaneous helper methods
 * Created on 4/14/2014.
 */
public class Util {
    /**
     * Standardize a username for use in the API
     * @param name The name
     * @return The standard form of the name, which is lowercase with all spaces removed
     */
    public static String standardizeSummonerName(String name) {
        return name.toLowerCase().replace(" ", "");
    }

    public static List<String> hexdump(byte[] data) {

        List<String> result = new ArrayList<>(data.length / 16);
        for (int i = 0; i < data.length; i += 16) {
            StringBuilder builder = new StringBuilder();
            StringBuilder asChar = new StringBuilder();
            for (int j = i; j < i + 16 && j < data.length; j++) {
                if (j == i + 8) {
                    builder.append("   ");
                    asChar.append(" ");
                } else if (j != i) {
                    builder.append(" ");
                }
                builder.append(String.format("%02x", data[j]));
                asChar.append(isPrintable((char) data[j]) ? (char) data[j] : '.');
            }

            for (int j = builder.length(); j < 49; j++) {
                builder.append(' ');
            }

            result.add(builder + " | " + asChar);
        }
        return result;
    }

    public static boolean isPrintable(char c) {
        Character.UnicodeBlock block = Character.UnicodeBlock.of(c);
        return !Character.isISOControl(c) && block != null && block != Character.UnicodeBlock.SPECIALS;
    }

    public static <A extends Annotation> A searchClassHierarchy(Class<?> cls, Class<A> annotation) {
        while (cls != null) {
            if (cls.isAnnotationPresent(annotation)) {
                return cls.getAnnotation(annotation);
            } else {
                cls = cls.getSuperclass();
            }
        }

        return null;
    }

    @SneakyThrows(IOException.class)
    public static String getConnectionInfoIpAddr() {
        try (InputStreamReader reader = new InputStreamReader(new URL(Shard.CONN_INFO_SERVICE).openStream())) {
            return new Gson().fromJson(reader, ConnInfo.class).ipAddr;
        } catch (IOException ex) {
            throw ex;
        }
    }

    private static class ConnInfo {
        @SerializedName("ip_address")
        String ipAddr;
    }
}
