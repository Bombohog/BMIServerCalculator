package server;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Server extends Application {

    @Override
    public void start(Stage clientStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("server.fxml"));
        clientStage.setTitle("BMI Server");
        clientStage.setScene(new Scene(root, 600, 410));
        clientStage.show();
    }

    @FXML
    public void initialize() {
        startServer();
    }

    @FXML
    TextArea serverTextArea;

    String pattern = "MM/dd/yyyy HH:mm:ss";
    DateFormat df = new SimpleDateFormat(pattern);

    private int clientNo = 0;

    private void startServer() {

        // Running server on another thread
        new Thread( () -> {

            int port = 8000;
            try {
                ServerSocket serverSocket = new ServerSocket(port);
                serverTextArea.appendText("BMI Server started at: " + df.format(new Date()));

                while (true) {
                    System.out.println("Accepting connection");
                    Socket socket = serverSocket.accept();
                    System.out.println("Connected to a client at: " + df.format(new Date()));

                    clientNo++;

                    serverTextArea.appendText("Starting thread for client " + clientNo);

                    InetAddress inetAddress = socket.getInetAddress();
                    serverTextArea.appendText("Client " + clientNo + "'s host name is " + inetAddress.getHostName() + "\n");
                    serverTextArea.appendText("Client " + clientNo + "'s IP Address is " + inetAddress.getHostAddress() + "\n");

                    // Creating new thread for the client
                    new Thread(new HandleClient(socket, serverTextArea, clientNo)).start();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        });

    }

}

class HandleClient implements Runnable {

    private int clientNo;
    private Socket socket;
    private TextArea serverTextArea;

    public HandleClient(Socket socket, TextArea textArea, int clientNo) {
        this.socket = socket;
        this.serverTextArea = textArea;
        this.clientNo = clientNo;
    }

    @Override
    public void run() {

        try {
            DataInputStream inputFromClient = new DataInputStream(socket.getInputStream());
            DataOutputStream outputToClient = new DataOutputStream(socket.getOutputStream());

            double weight = inputFromClient.readDouble();
            double inches = inputFromClient.readDouble();
            double feet = inputFromClient.readDouble();
            double bmi = BMI(weight, feet, inches);
            bmi = (bmi * 100) / 100;

            String result = "BMI is " + bmi + " " + BMIStatus(bmi);
            serverTextArea.appendText("Client " + clientNo + "'s " + result);
            outputToClient.writeUTF(result);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public double BMI(double weight, double feet, double inches) {

        final double kilogramsPerPound = 0.45359237;
        final double metersPerFeet = 0.3048;
        final double metersPerInch = 0.0254;

        double heightInMeter = (feet * metersPerFeet) + (inches * metersPerInch);
        weight *= kilogramsPerPound;

        return weight / Math.pow(heightInMeter, 2);
    }

    public static String BMIStatus(double bmi) {

        if (bmi < 18.5) {
            return "Underweight";
        } else if (bmi >= 18.5 && bmi < 25) {
            return "Normal weight";
        } else if (bmi >= 25 && bmi < 30) {
            return "Overweight";
        } else if (bmi >= 30 && bmi < 35) {
            return "Class 1 obesity";
        } else if (bmi >= 35 && bmi < 40) {
            return "Class 2 obesity";
        } else {
            return "Class 3 obesity";
        }

    }

}
