import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;

import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * this is the class that allows for the creation of charts but doesn't handle chart ui
 */
public class Charts {
    private final AttendanceDatabase attendanceDatabase;
    private final StudentDatabase studentDatabase;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE"); //for bar labels

    /**
     * constructor to create a new Charts
     * @param attendanceDatabase the attendance database from which to pull data
     * @param studentDatabase the student database from which to pull data
     */
    public Charts(AttendanceDatabase attendanceDatabase, StudentDatabase studentDatabase) {
        this.attendanceDatabase = attendanceDatabase;
        this.studentDatabase = studentDatabase;
    }

    /**
     * returns an observable list that can be used to generate a bar chart ui for one student
     * @param studentId student id of that student
     * @param start start date for which to include data
     * @param end end date for which to include data
     * @return an observable list that can be used to generate a bar chart ui for one student
     * @throws SQLException exception if something database related fails
     */
    public ObservableList<XYChart.Data<String, Number>> getStudentDataBarChart(int studentId, LocalDate start, LocalDate end) throws SQLException {
        List<AttendanceRecord> records = attendanceDatabase.getDateRangeRecords(start, end);
        ObservableList<XYChart.Data<String, Number>> data = FXCollections.observableArrayList();
        int minutes;
        while (!start.isAfter(end)) {
            minutes = 0;
            for (AttendanceRecord record : records) {
                if (record.getStudentId() == studentId && record.getSignInTime().toLocalDate().equals(start) && record.getSignOutTime() != null) {
                    minutes += (int) Duration.between(record.getSignInTime(), record.getSignOutTime()).toMinutes();
                }
            }
            data.add(new XYChart.Data<>(start.format(formatter), minutes/60.0));
            start = start.plusDays(1);
        }
        return data;
    }

    /**
     * returns an observable list that can be used to generate a bar chart ui for all students
     * @param start start date for which to include data
     * @param end end date for which to include data
     * @return an observable list that can be used to generate a bar chart ui for all students
     * @throws SQLException exception if something database related fails
     */
    public ObservableList<XYChart.Data<String, Number>> getAllStudentDataBarChart(LocalDate start, LocalDate end) throws SQLException {
        List<Student> students = studentDatabase.getAllStudents();
        List<AttendanceRecord> records = attendanceDatabase.getDateRangeRecords(start, end);
        ObservableList<XYChart.Data<String, Number>> data = FXCollections.observableArrayList();
        int minutes;
        for (Student student : students) {
            minutes = 0;
            for (AttendanceRecord record : records) {
                if (record.getStudentId() == student.getId() && record.getSignOutTime() != null) {
                    minutes += (int) Duration.between(record.getSignInTime(), record.getSignOutTime()).toMinutes();
                }
            }
            data.add(new XYChart.Data<>(student.getName(), minutes/60.0));
        }
        return data;
    }

    /**
     * returns an observable list that can be used to generate a pie chart ui for all students
     * @param start start date for which to include data
     * @param end end date for which to include data
     * @return an observable list that can be used to generate a pie chart ui for all students
     * @throws SQLException exception if something database related fails
     */
    public ObservableList<PieChart.Data> getAllStudentDataPieChart(LocalDate start, LocalDate end) throws SQLException {
        List<Student> students = studentDatabase.getAllStudents();
        List<AttendanceRecord> records = attendanceDatabase.getDateRangeRecords(start, end);
        ObservableList<PieChart.Data> slices = FXCollections.observableArrayList();
        int total = 0;
        for (AttendanceRecord record : records) {
            if(record.getSignOutTime() != null) {
                total += (int) Duration.between(record.getSignInTime(), record.getSignOutTime()).toMinutes();
            }
        }
        if (total == 0) {
            return slices;
        }
        for (Student student : students) {
            int minutes = 0;
            for (AttendanceRecord record : records) {
                if (record.getStudentId() == student.getId() && record.getSignOutTime() != null) {
                    minutes += (int) Duration.between(record.getSignInTime(), record.getSignOutTime()).toMinutes();
                }
            }
            slices.add(new PieChart.Data(student.getName() + " " + String.format("%.2f", (minutes*100.0)/total), minutes/60.0));
        }
        return slices;
    }

    /**
     * returns date that is 7 days from the current date
     * @return date that is 7 days from the current date
     */
    public static LocalDate[] past7Days() {
        return new LocalDate[]{LocalDate.now().minusDays(6), LocalDate.now()};
    }

    /**
     * returns date that is 30 days from the current date
     * @return date that is 30 days from the current date
     */
    public static LocalDate[] past30Days() {
        return new LocalDate[]{LocalDate.now().minusDays(29), LocalDate.now()};
    }

    /**
     * returns all dates from start to current
     * @return all dates from start to current
     */
    public static LocalDate[] allTime() {
        return new LocalDate[]{LocalDate.of(2000, 1, 1), LocalDate.now()};
    }

}
