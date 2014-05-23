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

package net.boreeas.riotapi.rtmp.p2.serialization;

import net.boreeas.riotapi.rtmp.p2.serialization.amf0.Amf0ObjectSerializer;
import net.boreeas.riotapi.rtmp.p2.serialization.amf3.Amf3ObjectDeserializer;
import net.boreeas.riotapi.rtmp.p2.serialization.amf3.Amf3ObjectSerializer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies the serialization of the target class.
 * <ul>
 *     <li><i>traitName</i> is the fully qualified path of the class. Use an empty string for anonymous objects</li>
 *     <li><i>dynamic</i> determines whether additional, undeclared fields may follow (amf3 only)</li>
 *     <li><i>externalizable</i> determines whether the class specifies its own serialization process (amf3 only).
 *     If this is set to <i>true</i>, <i>serializerAmf3</i> should probably be set</li>
 *     <li><i>members</i> specifies the names of the fields of this class that should be serialized. If
 *     this is empty, all fields (excluding the ones listed in <i>excludes</i> are serialized</li>
 *     <li><i>excludes</i> specifies the fields excluded during the serialization process and only relevant
 *     if <i>members</i> is not set</li>
 *     <li><i>serializerAmf0 and serializerAmf3</i> set the serializer for this class and should only be set
 *     for externalizable objects</li>
 * </ul>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SerializationContext {
    /**
     * The fully qualified path of the class. Use an empty string for anonymous objects
     * @return the fully qualified path of the class. Use an empty string for anonymous objects
     */
    public String traitName();

    /**
     * Determines whether additional, undeclared fields may follow (amf3 only)
     * @return whether additional, undeclared fields may follow
     */
    public boolean dynamic() default false;

    /**
     * Determines whether the class specifies its own serialization process (amf3 only).
     * If this is set to <i>true</i>, <i>serializerAmf3</i> should probably be set
     * @return whether the class specifies its own serialization process
     */
    public boolean externalizable() default false;

    /**
     * Specifies the fields excluded during the serialization process and only relevant
     * if <i>members</i> is not set
     * @return the fields excluded during the serialization process
     */
    public String[] excludes() default {};

    /**
     * Specifies the names of the fields of this class that should be serialized. If
     * this is empty, all fields (excluding the ones listed in <i>excludes</i> are serialized
     * @return the names of the fields of this class that should be serialized.
     */
    public String[] members() default {};

    /**
     * Set the serializer for this class and should only be set for externalizable objects
     * @return the serializer for this class
     */
    public Class<? extends Amf3ObjectSerializer> serializerAmf3() default Amf3ObjectSerializer.class;

    /**
     * Set the serializer for this class and should only be set for externalizable objects
     * @return the serializer for this class
     */
    public Class<? extends Amf0ObjectSerializer> serializerAmf0() default Amf0ObjectSerializer.class;

    /**
     * Set the deserializer used in the Amf3 deserialization process. Should only be set for
     * externalizable objects.
     * @return the deserializer for this class
     */
    public Class<? extends Amf3ObjectDeserializer> deserializer() default Amf3ObjectDeserializer.class;
}
