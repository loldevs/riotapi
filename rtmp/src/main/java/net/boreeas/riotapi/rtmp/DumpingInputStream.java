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

package net.boreeas.riotapi.rtmp;

import lombok.AllArgsConstructor;
import net.boreeas.riotapi.Util;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by malte on 7/5/2014.
 */
@AllArgsConstructor
public class DumpingInputStream extends InputStream {
    private InputStream base;

    @Override
    public int read() throws IOException {
        int i = base.read();
        System.out.printf("[<<<] %02x%n", (i & 0xff));
        return i;
    }

    @Override
    public int read(byte[] b) throws IOException {
        int i = base.read(b);
        for (String line: Util.hexdump(b)) {
            System.out.println("[<<<] " + line);
        }
        System.out.println();

        return i;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int i = base.read(b, off, len);

        byte[] buf = new byte[len];
        System.arraycopy(b, off, buf, 0, len);
        for (String line: Util.hexdump(buf)) {
            System.out.println("[<<<] " + line);
        }
        System.out.println();

        return i;
    }
}
