/*
 * Copyright 2014 Malte Schütze
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.boreeas.riotapi.rtmp.amf;

import lombok.AllArgsConstructor;
import lombok.Delegate;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created on 4/15/2014.
 */
@NoArgsConstructor
@AllArgsConstructor
public class Amf3Header {
    private String name;
    private boolean mustUnderstand;

    @Delegate
    @Getter
    private TypedObject data;


    public static Amf3Header fromInputStream(DataInputStream in) throws IOException {
        Amf3Header header = new Amf3Header();

        header.name = in.readUTF();
        header.mustUnderstand = in.readUnsignedByte() == 1;

        int length = in.readInt();
        header.data = new AmfInputStream(in).decodeObject();


        return header;
    }
}
