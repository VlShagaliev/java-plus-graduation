package ru.practicum.kafka;

import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import ru.practicum.avro.UserActionAvro;

import java.nio.ByteBuffer;

public class UserActionAvroDeserializer implements Deserializer<UserActionAvro> {

    private final SpecificDatumReader<UserActionAvro> reader;
    private BinaryDecoder decoder;

    public UserActionAvroDeserializer() {
        this.reader = new SpecificDatumReader<>(UserActionAvro.getClassSchema());
    }

    @Override
    public UserActionAvro deserialize(String topic, byte[] data) {
        if (data == null) {
            throw new SerializationException(
                    "Получен null payload для UserActionAvro из топика " + topic
            );
        }

        try {
            decoder = DecoderFactory.get().binaryDecoder(data, decoder);
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
}