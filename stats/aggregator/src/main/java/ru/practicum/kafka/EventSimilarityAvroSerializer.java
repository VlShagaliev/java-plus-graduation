package ru.practicum.kafka;

import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.kafka.common.serialization.Serializer;
import ru.practicum.avro.EventSimilarityAvro;

import java.io.ByteArrayOutputStream;

public class EventSimilarityAvroSerializer implements Serializer<EventSimilarityAvro> {

    private final SpecificDatumWriter<EventSimilarityAvro> writer;
    private BinaryEncoder encoder;
    private final ByteArrayOutputStream out;

    public EventSimilarityAvroSerializer() {
        this.writer = new SpecificDatumWriter<>(EventSimilarityAvro.getClassSchema());
        this.out = new ByteArrayOutputStream();
        this.encoder = EncoderFactory.get().binaryEncoder(out, null);
    }

    @Override
    public byte[] serialize(String topic, EventSimilarityAvro data) {
        if (data == null) {
            return null;
        }

        try {
            out.reset();
            encoder = EncoderFactory.get().binaryEncoder(out, encoder);
            writer.write(data, encoder);
            encoder.flush();
            return out.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("Не удалось сериализовать EventSimilarityAvro", e);
        }
    }
}