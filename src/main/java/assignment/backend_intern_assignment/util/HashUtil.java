package assignment.backend_intern_assignment.util;

import assignment.backend_intern_assignment.dto.EventIngestRequest;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;


public class HashUtil {

    public static String computeHash(EventIngestRequest req) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String data = req.getEventId()
                    + req.getEventTime()
                    + req.getMachineId()
                    + req.getDurationMs()
                    + req.getDefectCount();

            byte[] hash = md.digest(data.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();

        } catch (Exception e) {
            throw new RuntimeException("Failed to compute hash", e);
        }
    }
}
