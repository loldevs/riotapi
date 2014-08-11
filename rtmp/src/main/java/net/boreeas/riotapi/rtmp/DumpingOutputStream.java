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
import java.io.OutputStream;

/**
 * Created by malte on 7/5/2014.
 */
@AllArgsConstructor
public class DumpingOutputStream extends OutputStream {
    private OutputStream base;


    @Override
    public void write(int b) throws IOException {
        System.out.printf("[>>>] %02x%n", (b & 0xff));
        base.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        for (String line: Util.hexdump(b)) {
            System.out.println("[>>>] " + line);
        }
        System.out.println();
        base.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        byte[] buf = new byte[len];
        System.arraycopy(b, off, buf, 0, len);
        for (String line: Util.hexdump(buf)) {
            System.out.println("[>>>] " + line);
        }
        System.out.println();

        base.write(b, off, len);
    }
}
