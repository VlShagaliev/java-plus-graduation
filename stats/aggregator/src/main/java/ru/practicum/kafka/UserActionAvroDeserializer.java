package ru.practicum.kafka;

import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.kafka.common.errors.SerializationException;
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
        if (data == null) {
            throw new SerializationException(
                    "Получен null payload для UserActionAvro из топика " + topic
            );
        }

        if (data.length == 0) {
            throw new SerializationException(
                    "Получен пустой payload для UserActionAvro из топика " + topic
            );
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
                SerializationException ex = new SerializationException(
                        "Не удалось десериализовать UserActionAvro из топика " + topic
                );
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