import java.time.LocalDate;
import java.util.*;

public class Main {
    private static final String STUDENTS_FILE = "students.csv";
    private static final String ATTENDANCE_FILE = "attendance.csv";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        FileHandler fh = new FileHandler(STUDENTS_FILE, ATTENDANCE_FILE);
        StudentManager sm = new StudentManager(fh);
        AttendanceManager am = new AttendanceManager(fh);

        System.out.println("=== Student Attendance Management System ===");
        while (true) {
            printMenu();
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1":
                    addStudentFlow(sc, sm);
                    break;
                case "2":
                    viewStudentsFlow(sm);
                    break;
                case "3":
                    markAttendanceFlow(sc, sm, am);
                    break;
                case "4":
                    viewAttendanceReportFlow(sc, sm, am);
                    break;
                case "5":
                    viewDailySheetFlow(sc, sm, am);
                    break;
                case "6":
                    System.out.println("Exiting. Goodbye!");
                    sc.close();
                    return;
                default:
                    System.out.println("Invalid choice. Enter 1-6.");
            }
        }
    }

    private static void printMenu() {
        System.out.println("\nMenu:");
        System.out.println("1. Add Student");
        System.out.println("2. View Students");
        System.out.println("3. Mark Attendance (today)");
        System.out.println("4. View Attendance Report (per student)");
        System.out.println("5. View Daily Attendance Sheet (by date)");
        System.out.println("6. Exit");
        System.out.print("Enter choice: ");
    }

    private static void addStudentFlow(Scanner sc, StudentManager sm) {
        System.out.print("Enter Student ID: ");
        String id = sc.nextLine().trim();
        System.out.print("Enter Student Name: ");
        String name = sc.nextLine().trim();
        try {
            Student s = new Student(id, name);
            boolean added = sm.addStudent(s);
            if (added) System.out.println("Student added: " + s);
            else System.out.println("Student ID already exists. Use a unique ID.");
        } catch (IllegalArgumentException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    private static void viewStudentsFlow(StudentManager sm) {
        List<Student> students = sm.getAllStudents();
        if (students.isEmpty()) {
            System.out.println("No students found.");
            return;
        }
        System.out.println("\nStudents:");
        students.forEach(s -> System.out.println(" - " + s));
    }

    private static void markAttendanceFlow(Scanner sc, StudentManager sm, AttendanceManager am) {
        List<Student> students = sm.getAllStudents();
        if (students.isEmpty()) {
            System.out.println("No students to mark. Add students first.");
            return;
        }
        LocalDate date = LocalDate.now();
        System.out.println("Marking attendance for date: " + date);
        Map<String, Boolean> marks = new LinkedHashMap<>();
        for (Student s : students) {
            String input = "";
            while (true) {
                System.out.print("Is " + s.getName() + " present? (P/A) [default A]: ");
                input = sc.nextLine().trim();
                if (input.isEmpty()) input = "A";
                if (input.equalsIgnoreCase("P") || input.equalsIgnoreCase("A")) break;
                System.out.println("Enter 'P' for present or 'A' for absent.");
            }
            marks.put(s.getId(), input.equalsIgnoreCase("P"));
        }
        am.markAttendanceBatch(marks, date);
        System.out.println("Attendance saved for " + date);
    }

    private static void viewAttendanceReportFlow(Scanner sc, StudentManager sm, AttendanceManager am) {
        Map<String, AttendanceManager.AttendanceStats> stats = am.computeAllStats();
        List<Student> students = sm.getAllStudents();
        if (students.isEmpty()) {
            System.out.println("No students in system.");
            return;
        }

        System.out.println("\nAttendance Report:");
        for (Student s : students) {
            AttendanceManager.AttendanceStats st = stats.get(s.getId());
            int total = st == null ? 0 : st.totalDays;
            int present = st == null ? 0 : st.presentDays;
            double percent = st == null ? 0.0 : st.percentage();
            System.out.printf("%s | Total: %d | Present: %d | Attendance: %.2f%%\n",
                    s.getName(), total, present, percent);
        }
    }

    private static void viewDailySheetFlow(Scanner sc, StudentManager sm, AttendanceManager am) {
        System.out.print("Enter date (YYYY-MM-DD) or press Enter for today: ");
        String d = sc.nextLine().trim();
        LocalDate date = d.isEmpty() ? LocalDate.now() : LocalDate.parse(d);
        List<AttendanceRecord> list = am.getRecordsByDate(date);
        if (list.isEmpty()) {
            System.out.println("No attendance records for " + date);
            return;
        }
        Map<String, String> byId = new LinkedHashMap<>();
        for (AttendanceRecord r : list) byId.put(r.getStudentId(), r.isPresent() ? "Present" : "Absent");

        System.out.println("\nDaily Sheet for " + date + ":");
        sm.getAllStudents().forEach(s -> {
            String status = byId.getOrDefault(s.getId(), "Not Marked");
            System.out.println(s.getId() + " | " + s.getName() + " -> " + status);
        });
    }
}
