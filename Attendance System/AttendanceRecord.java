import java.time.LocalDate;

public class AttendanceRecord {
    private final String studentId;
    private final LocalDate date;
    private final boolean present;

    public AttendanceRecord(String studentId, LocalDate date, boolean present) {
        if (studentId == null || studentId.trim().isEmpty()) throw new IllegalArgumentException("studentId empty");
        this.studentId = studentId.trim();
        this.date = date;
        this.present = present;
    }

    public String getStudentId() {
        return studentId;
    }

    public LocalDate getDate() {
        return date;
    }

    public boolean isPresent() {
        return present;
    }

    public String toCsv() {
        return escape(studentId) + "," + date.toString() + "," + (present ? "P" : "A");
    }

    public static AttendanceRecord fromCsv(String line) {
        String[] parts = line.split(",", -1);
        if (parts.length < 3) throw new IllegalArgumentException("Invalid attendance CSV line");
        String sid = unescape(parts[0]);
        LocalDate d = LocalDate.parse(parts[1]);
        boolean p = parts[2].trim().equalsIgnoreCase("P");
        return new AttendanceRecord(sid, d, p);
    }

    private static String escape(String s) {
        if (s.contains(",") || s.contains("\"")) {
            s = s.replace("\"", "\"\"");
            return "\"" + s + "\"";
        }
        return s;
    }

    private static String unescape(String s) {
        s = s.trim();
        if (s.startsWith("\"") && s.endsWith("\"")) {
            s = s.substring(1, s.length()-1).replace("\"\"", "\"");
        }
        return s;
    }

    @Override
    public String toString() {
        return studentId + " | " + date + " | " + (present ? "Present" : "Absent");
    }
}
