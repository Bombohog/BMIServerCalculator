package client;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class Client extends Application {

    @Override
    public void start(Stage clientStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("client.fxml"));
        clientStage.setTitle("BMI Client");
        clientStage.setScene(new Scene(root, 360, 275));
        clientStage.show();
    }

    @FXML
    public void initialize() {
        startClient();
    }

    @FXML
    TextArea clientTextArea;
    @FXML
    TextField clientWeight;
    @FXML
    TextField clientInches;
    @FXML
    TextField clientFeet;

    Socket socket;

    DataInputStream inputFromClient;
    DataOutputStream outputToClient;

    private void startClient() {

        try {
            socket = new Socket("localhost", 8000);

            inputFromClient = new DataInputStream(socket.getInputStream());
            outputToClient = new DataOutputStream(socket.getOutputStream());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void submitData() {

        try {
            // output to server
            clientTextArea.appendText("Weight: " + clientWeight.getText());
            clientTextArea.appendText("Height: " + clientInches.getText() + " and " + clientFeet.getText());
            outputToClient.writeUTF(clientWeight.getText());
            outputToClient.writeUTF(clientInches.getText());
            outputToClient.writeUTF(clientFeet.getText());

            // input from server
            clientTextArea.appendText(inputFromClient.readUTF());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
