package ru.practicum.kafka;

import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.kafka.common.serialization.Serializer;
import ru.practicum.avro.UserActionAvro;

import java.io.ByteArrayOutputStream;
import java.util.Map;

public class UserActionAvroSerializer implements Serializer<UserActionAvro> {

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public byte[] serialize(String topic, UserActionAvro data) {
        if (data == null) {
            return null;
        }

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            SpecificDatumWriter<UserActionAvro> writer =
                    new SpecificDatumWriter<>(UserActionAvro.getClassSchema());
            BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(out, null);
            writer.write(data, encoder);
            encoder.flush();
            return out.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("Не удалось сериализовать UserActionAvro", e);
        }
    }

    @Override
    public void close() {
    }
}