package ru.practicum.kafka;

import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.kafka.common.serialization.Deserializer;
import ru.practicum.avro.EventSimilarityAvro;

import java.nio.ByteBuffer;

public class EventSimilarityAvroDeserializer implements Deserializer<EventSimilarityAvro> {

    @Override
    public EventSimilarityAvro deserialize(String topic, byte[] data) {
        if (data == null || data.length == 0) {
            return null;
        }

        try {
            SpecificDatumReader<EventSimilarityAvro> reader =
                    new SpecificDatumReader<>(EventSimilarityAvro.getClassSchema());
            BinaryDecoder decoder = DecoderFactory.get().binaryDecoder(data, null);
            return reader.read(null, decoder);
        } catch (Exception binaryException) {
            try {
                return EventSimilarityAvro.fromByteBuffer(ByteBuffer.wrap(data));
            } catch (Exception singleObjectException) {
                IllegalStateException ex =
                        new IllegalStateException("Не удалось десериализовать EventSimilarityAvro");
                ex.addSuppressed(binaryException);
                ex.addSuppressed(singleObjectException);
                throw ex;
            }
        }
    }
}