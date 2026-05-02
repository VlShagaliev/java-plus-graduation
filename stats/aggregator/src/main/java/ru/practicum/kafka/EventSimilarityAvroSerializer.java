package ru.practicum.kafka;

import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.kafka.common.serialization.Serializer;
import ru.practicum.avro.EventSimilarityAvro;

import java.io.ByteArrayOutputStream;
import java.util.Map;

public class EventSimilarityAvroSerializer implements Serializer<EventSimilarityAvro> {

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public byte[] serialize(String topic, EventSimilarityAvro data) {
        if (data == null) {
            return null;
        }

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            SpecificDatumWriter<EventSimilarityAvro> writer =
                    new SpecificDatumWriter<>(EventSimilarityAvro.getClassSchema());
            BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(out, null);
            writer.write(data, encoder);
            encoder.flush();
            return out.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("Не удалось сериализовать EventSimilarityAvro", e);
        }
    }

    @Override
    public void close() {
    }
}