package com.argo.redis.impl;

import com.argo.redis.RedisBuffer;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoCallback;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;
import com.esotericsoftware.kryo.serializers.DefaultSerializers;
import de.javakaffee.kryoserializers.*;
import de.javakaffee.kryoserializers.cglib.CGLibProxySerializer;
import de.javakaffee.kryoserializers.guava.ImmutableListSerializer;
import de.javakaffee.kryoserializers.guava.ImmutableMapSerializer;
import de.javakaffee.kryoserializers.guava.ImmutableMultimapSerializer;
import de.javakaffee.kryoserializers.guava.ImmutableSetSerializer;
import de.javakaffee.kryoserializers.jodatime.JodaDateTimeSerializer;
import de.javakaffee.kryoserializers.jodatime.JodaLocalDateSerializer;
import de.javakaffee.kryoserializers.jodatime.JodaLocalDateTimeSerializer;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * 使用Kryo作为序列化
 * Created by dengyaming on 7/29/16.
 */
public class KryoPack implements RedisBuffer {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private volatile boolean kryoCreated;
    private final Set<Class> registrations = new LinkedHashSet();

    protected KryoFactory factory = new KryoFactory() {
        public Kryo create () {
            Kryo kryo = createKryo();
            return kryo;
        }
    };

    // Build pool with SoftReferences enabled (optional)
    protected KryoPool pool = new KryoPool.Builder(factory).softReferences().build();

    /**
     * 注册序列化的类
     * @param clazz
     */
    public void registerClass(Class clazz) {
        if(this.kryoCreated) {
            throw new IllegalStateException("Can\'t register class after creating kryo instance");
        } else {
            this.registrations.add(clazz);
        }
    }

    /**
     * 创建对象
     * @return
     */
    protected Kryo createKryo() {
        if(!this.kryoCreated) {
            this.kryoCreated = true;
        }

        CompatibleKryo kryo = new CompatibleKryo();
        kryo.setRegistrationRequired(false);
        kryo.register(Arrays.asList(new String[]{""}).getClass(), new ArraysAsListSerializer());
        kryo.register(GregorianCalendar.class, new GregorianCalendarSerializer());
        kryo.register(InvocationHandler.class, new JdkProxySerializer());
        kryo.register(BigDecimal.class, new DefaultSerializers.BigDecimalSerializer());
        kryo.register(BigInteger.class, new DefaultSerializers.BigIntegerSerializer());
        kryo.register(Pattern.class, new RegexSerializer());
        kryo.register(BitSet.class, new BitSetSerializer());
        kryo.register(URI.class, new URISerializer());
        kryo.register(UUID.class, new UUIDSerializer());
        UnmodifiableCollectionsSerializer.registerSerializers(kryo);
        SynchronizedCollectionsSerializer.registerSerializers(kryo);
        kryo.register(HashMap.class);
        kryo.register(ArrayList.class);
        kryo.register(LinkedList.class);
        kryo.register(HashSet.class);
        kryo.register(TreeSet.class);
        kryo.register(Hashtable.class);
        kryo.register(Date.class);
        kryo.register(Calendar.class);
        kryo.register(ConcurrentHashMap.class);
        kryo.register(SimpleDateFormat.class);
        kryo.register(GregorianCalendar.class);
        kryo.register(Vector.class);
        kryo.register(BitSet.class);
        kryo.register(StringBuffer.class);
        kryo.register(StringBuilder.class);
        kryo.register(Object.class);
        kryo.register(Object[].class);
        kryo.register(String[].class);
        kryo.register(byte[].class);
        kryo.register(char[].class);
        kryo.register(int[].class);
        kryo.register(float[].class);
        kryo.register(double[].class);

        kryo.register( Collections.EMPTY_LIST.getClass(), new CollectionsEmptyListSerializer() );
        kryo.register( Collections.EMPTY_MAP.getClass(), new CollectionsEmptyMapSerializer() );
        kryo.register( Collections.EMPTY_SET.getClass(), new CollectionsEmptySetSerializer() );
        kryo.register( Collections.singletonList( "" ).getClass(), new CollectionsSingletonListSerializer() );
        kryo.register( Collections.singleton( "" ).getClass(), new CollectionsSingletonSetSerializer() );
        kryo.register( Collections.singletonMap( "", "" ).getClass(), new CollectionsSingletonMapSerializer() );

        // register CGLibProxySerializer, works in combination with the appropriate action in handleUnregisteredClass (see below)
        kryo.register( CGLibProxySerializer.CGLibProxyMarker.class, new CGLibProxySerializer() );
        // joda DateTime, LocalDate and LocalDateTime
        kryo.register( DateTime.class, new JodaDateTimeSerializer() );
        kryo.register( LocalDate.class, new JodaLocalDateSerializer() );
        kryo.register( LocalDateTime.class, new JodaLocalDateTimeSerializer() );
        // protobuf
        // kryo.register( SampleProtoA.class, new ProtobufSerializer() ); // or override Kryo.getDefaultSerializer as shown below
        // guava ImmutableList, ImmutableSet, ImmutableMap, ImmutableMultimap, UnmodifiableNavigableSet
        ImmutableListSerializer.registerSerializers( kryo );
        ImmutableSetSerializer.registerSerializers( kryo );
        ImmutableMapSerializer.registerSerializers( kryo );
        ImmutableMultimapSerializer.registerSerializers( kryo );
        UnmodifiableCollectionsSerializer.registerSerializers( kryo );

        Iterator iterator = this.registrations.iterator();
        Class clazz;
        while(iterator.hasNext()) {
            clazz = (Class)iterator.next();
            kryo.register(clazz);
        }

        return kryo;
    }


    @Override
    public <T> T read(final byte[] bytes, Class<T> c) throws IOException {
        if (null == bytes || bytes.length == 0){
            return null;
        }

        return pool.run(new KryoCallback<T>() {
            @Override
            public T execute(Kryo kryo) {
                if (logger.isDebugEnabled()){
                    logger.debug("kryo: {}", kryo);
                }
                ByteArrayInputStream buffer = new ByteArrayInputStream(bytes);
                Input input = new Input(buffer, 32);
                try {
                    T o = (T)kryo.readClassAndObject(input);
                    return o;
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    return null;
                }finally {
                    try {
                        input.close();
                        buffer.close();
                    } catch (IOException e) {

                    }
                }
            }
        });
    }

    @Override
    public <T> List<T> read(List<byte[]> bytes, Class<T> c) throws IOException {
        if (null == bytes || bytes.size() == 0){
            return Collections.emptyList();
        }

        return pool.run(new KryoCallback<List<T>>() {
            @Override
            public List<T> execute(Kryo kryo) {
                if (logger.isDebugEnabled()){
                    logger.debug("kryo: {}", kryo);
                }
                List<T> ret = Collections.emptyList();
                for (int i = 0; i < bytes.size(); i++) {
                    byte[] buf = bytes.get(i);
                    if (null == buf || buf.length == 0){
                        ret.add(null);
                        continue;
                    }

                    ByteArrayInputStream buffer = new ByteArrayInputStream(buf);
                    Input input = new Input(buffer, 32);
                    try {
                        T o = (T)kryo.readClassAndObject(input);
                        ret.add(o);
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        ret.add(null);
                    }finally {
                        try {
                            input.close();
                            buffer.close();
                        } catch (IOException e) {

                        }
                    }
                }
                return ret;
            }
        });
    }

    @Override
    public <T> byte[] write(T v) throws IOException {
        if (null == v){
            return null;
        }

        return pool.run(new KryoCallback<byte[]>() {
            @Override
            public byte[] execute(Kryo kryo) {
                if (logger.isDebugEnabled()){
                    logger.debug("kryo: {}", kryo);
                }

                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                Output output = new Output(buffer, 32);

                try {
                    kryo.writeClassAndObject(output, v);
                    output.flush();
                    output.close();
                    byte[] bytes = buffer.toByteArray();
                    return bytes;
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    return null;
                }finally {
                    try {
                        buffer.close();
                    } catch (IOException e) {

                    }
                }
            }
        });

    }

}
