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

import junit.framework.TestCase;
import net.boreeas.riotapi.Util;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class RoflFileTest extends TestCase {

    private RoflFile file;

    @Override
    protected void setUp() throws Exception {
        file = new RoflFile(new File("PBE1-34460609.rofl"), 5, TimeUnit.SECONDS);
    }

    public void testGetters() {
        System.out.println("Signature: ");
        for (String line: Util.hexdump(file.getSignature())) {
            System.out.println("\t" + line);
        }

        System.out.println("Header length  : " + file.getHeaderLength());
        System.out.println("File length    : " + file.getFileLength());
        System.out.println("Metadata offset: " + file.getMetaDataOffset());
        System.out.println("Metadata length: " + file.getMetaDataLength());
        System.out.println("Metadata:");
        System.out.println(file.getMetaData());
        System.out.println("Payload header @ " + file.getPayloadHeaderOffset());
        System.out.println("Payload header length: " + file.getPayloadHeaderLength());
        System.out.println("Chunk count    : " + file.getChunkCount());
        for (int i = 0; i < file.getChunkCount(); i++) {
            System.out.println("Chunk [" + i + "]");
            System.out.println("\t" + file.getChunkHeader(i));
            /*
            for (String line: Util.hexdump(file.getChunk(i))) {
                System.out.println("\t" + line);
            }
            */
        }

        System.out.println("Keyframe count : " + file.getKeyFrameCount());
        for (int i = 0; i < file.getKeyFrameCount(); i++) {
            System.out.println("Keyframe [" + i + "]");
            System.out.println("\t" + file.getKeyFrameHeader(i));
            /*
            for (String line: Util.hexdump(file.getKeyFrame(i))) {
                System.out.println("\t" + line);
            }
            */
        }


    }
}