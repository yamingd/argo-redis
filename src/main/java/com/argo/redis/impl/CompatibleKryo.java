package com.argo.redis.impl;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.serializers.JavaSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by dengyaming on 7/29/16.
 */
public class CompatibleKryo extends Kryo {

    private static final Logger logger = LoggerFactory.getLogger(CompatibleKryo.class);

    public CompatibleKryo() {
    }

    public static boolean checkZeroArgConstructor(Class clazz) {
        try {
            clazz.getDeclaredConstructor(new Class[0]);
            return true;
        } catch (NoSuchMethodException var2) {
            return false;
        }
    }

    public Serializer getDefaultSerializer(Class type) {
        if(type == null) {
            throw new IllegalArgumentException("type cannot be null.");
        } else if(!type.isArray() && !checkZeroArgConstructor(type)) {
            if(logger.isWarnEnabled()) {
                logger.warn(type + " has no zero-arg constructor and this will affect the serialization performance");
            }

            return new JavaSerializer();
        } else {
            return super.getDefaultSerializer(type);
        }
    }
}
