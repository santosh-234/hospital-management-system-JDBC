package org.hospitalmngmt;

import com.mysql.cj.jdbc.Driver;

import javax.naming.CompositeName;
import javax.print.Doc;
import java.sql.*;
import java.util.Scanner;

public class HospitalManagement {
    private static final String url = "jdbc:mysql://localhost:3306/hospital";
    private static final String username = "root";
    private static final String password = "root234";

    public static void main(String[] args) {

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }

        Scanner scanner = new Scanner(System.in);

        try{
            Connection connection = DriverManager.getConnection(url,username,password);
            Patient patient = new Patient(connection,scanner);
            Doctor doctor = new Doctor(connection);

            while (true){

                System.out.println("\n--------HOSPITAL MANAGEMENT SYSTEM--------\n");
                System.out.println("1. Add Patient");
                System.out.println("2. View Patients");
                System.out.println("3. View Doctors");
                System.out.println("4. Book Appointment");
                System.out.println("5. Exit");
                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();

                switch (choice){

                    case 1:
                        //add patient
                        patient.addPatient();
                        break;
                    case 2:
                        //view patient
                        patient.viewPatients();
                        break;
                    case 3:
                        //view doctors
                        doctor.viewDoctors();
                        break;
                    case 4:
                        //Book appointment
                        bookAppointment(connection,scanner,patient,doctor);
                        break;
                    case 5:
                        System.out.println("Thank you...");
                        return;
                    default:
                        System.out.println("Enter a valid choice....");
                        break;
                }

            }


        }catch (SQLException e){
            e.printStackTrace();
        }
    }


    public static void bookAppointment(Connection connection, Scanner scanner, Patient patient, Doctor doctor){
        System.out.print("Enter patient id: ");
        int patientId = scanner.nextInt();
        System.out.print("Enter doctor id: ");
        int doctorId = scanner.nextInt();

        System.out.print("Enter appointment date(yyyy/mm/dd):");
        String appointmentDate = scanner.next();

        if(patient.getPatientById(patientId) && doctor.getDoctoById(doctorId)){
            if(checkDoctorAvailability(doctorId,appointmentDate,connection)){
                String appointmentQuery = "insert into appointment(patient_id,doctor_id,appointment_date)values(?,?,?)";

                try{
                    PreparedStatement preparedStatement = connection.prepareStatement(appointmentQuery);

                    preparedStatement.setInt(1,patientId);
                    preparedStatement.setInt(2,doctorId);
                    preparedStatement.setString(3,appointmentDate);

                    int rowsAffected = preparedStatement.executeUpdate();
                    if(rowsAffected > 0){
                        System.out.println("Appointment booked...");

                    }else{
                        System.out.println("Failed to book appointment...");
                    }

                }catch (SQLException e){
                    e.printStackTrace();
                }


            }else {
                System.out.println("Doctor not available on this date...");
            }

        }else {
            System.out.println("Doctor or Patient doesn't exists");
        }
    }

    public static boolean checkDoctorAvailability(int doctorId, String appointmentDate, Connection connection){
        String query = "select count(*) from appointment where doctor_id = ? and appointment_date = ?";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,doctorId);
            preparedStatement.setString(2,appointmentDate);

            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                int count = resultSet.getInt(1);
                if(count == 0){
                    return true;
                }else {
                    return false;
                }
            }

    }catch (SQLException e){
            e.printStackTrace();
        }

        return false;
    }
}
