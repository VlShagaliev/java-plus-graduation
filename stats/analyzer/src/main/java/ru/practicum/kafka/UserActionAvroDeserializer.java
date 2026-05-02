package ru.practicum.kafka;

import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.kafka.common.serialization.Deserializer;
import ru.practicum.avro.UserActionAvro;

import java.nio.ByteBuffer;
import java.util.Map;

public class UserActionAvroDeserializer implements Deserializer<UserActionAvro> {

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public UserActionAvro deserialize(String topic, byte[] data) {
        if (data == null || data.length == 0) {
            return null;
        }

        try {
            SpecificDatumReader<UserActionAvro> reader =
                    new SpecificDatumReader<>(UserActionAvro.getClassSchema());
            BinaryDecoder decoder = DecoderFactory.get().binaryDecoder(data, null);
            return reader.read(null, decoder);
        } catch (Exception binaryException) {
            try {
                return UserActionAvro.fromByteBuffer(ByteBuffer.wrap(data));
            } catch (Exception singleObjectException) {
                IllegalStateException ex =
                        new IllegalStateException("Не удалось десериализовать UserActionAvro");
                ex.addSuppressed(binaryException);
                ex.addSuppressed(singleObjectException);
                throw ex;
            }
        }
    }

    @Override
    public void close() {
    }
}