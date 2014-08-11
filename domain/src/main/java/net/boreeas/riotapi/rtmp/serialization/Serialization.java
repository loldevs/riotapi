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

package net.boreeas.riotapi.rtmp.serialization;

import net.boreeas.riotapi.rtmp.serialization.amf0.Amf0ObjectDeserializer;
import net.boreeas.riotapi.rtmp.serialization.amf0.Amf0ObjectSerializer;
import net.boreeas.riotapi.rtmp.serialization.amf3.Amf3ObjectDeserializer;
import net.boreeas.riotapi.rtmp.serialization.amf3.Amf3ObjectSerializer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created on 5/26/2014.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Serialization {
    public String name() default "";
    public String[] noncanonicalNames() default {};
    public boolean dynamic() default false;
    public boolean externalizable() default false;
    public boolean deserializeOnly() default false;
    public Class<? extends Amf3ObjectSerializer> amf3Serializer() default Amf3ObjectSerializer.class;
    public Class<? extends Amf0ObjectSerializer> amf0Serializer() default Amf0ObjectSerializer.class;
    public Class<? extends Amf3ObjectDeserializer> amf3Deserializer() default Amf3ObjectDeserializer.class;
    public Class<? extends Amf0ObjectDeserializer> amf0Deserializer() default Amf0ObjectDeserializer.class;
}
