import com.fazecast.jSerialComm.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.PrintWriter;
import java.util.Random;

public class Main {
    static SerialPort chosenPort;

    public static void main(String args[]){
        JFrame window = new JFrame();
        window.setTitle("LED Project");
        window.setSize(500, 100);
        window.setLayout(new BorderLayout());
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JComboBox<String> portList = new JComboBox<>();

        JRadioButton ambientButton = new JRadioButton();
        ambientButton.setText("Ambient");
        JRadioButton randomButton = new JRadioButton();
        randomButton.setText("Random");

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(ambientButton);
        buttonGroup.add(randomButton);

        JButton connectButton = new JButton("Connect");
        JPanel topPanel = new JPanel();
        topPanel.add(portList);
        topPanel.add(ambientButton);
        topPanel.add(randomButton);
        topPanel.add(connectButton);
        window.add(topPanel, BorderLayout.NORTH);

        JRadioButton animation1RadioButton = new JRadioButton();
        animation1RadioButton.setText("Animation 1");
        JRadioButton animation2RadioButton = new JRadioButton();
        animation2RadioButton.setText("Animation 2");

        animation1RadioButton.setEnabled(false);
        animation2RadioButton.setEnabled(false);

        ButtonGroup buttonGroup2 = new ButtonGroup();
        buttonGroup2.add(animation1RadioButton);
        buttonGroup2.add(animation2RadioButton);

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(animation1RadioButton);
        bottomPanel.add(animation2RadioButton);
        window.add(bottomPanel, BorderLayout.SOUTH);

        SerialPort[] portNames = SerialPort.getCommPorts();
        System.out.println("Select Port:");

        for(int i = 0; i < portNames.length; i++){
            portList.addItem(portNames[i].getSystemPortName());
        }

        connectButton.addActionListener(new ActionListener(){
            @Override public void actionPerformed(ActionEvent arg0) {
                if(connectButton.getText().equals("Connect")) {

                    chosenPort = SerialPort.getCommPort(portList.getSelectedItem().toString());
                    chosenPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
                    if(chosenPort.openPort()) {
                        connectButton.setText("Disconnect");
                        portList.setEnabled(false);

                        // create a new thread for sending data to the arduino
                        Thread thread = new Thread(){
                            @Override public void run() {
                                // wait after connecting, so the bootloader can finish
                                try {Thread.sleep(100); } catch(Exception e) {}

                                // enter an infinite loop that sends text to the arduino
                                PrintWriter output = new PrintWriter(chosenPort.getOutputStream());
                                while(true) {
                                    if (ambientButton.isSelected()){
                                        try{
                                            String colors = getAmbientColor() + ",0";
                                            output.print(colors);
                                            System.out.println("Sending arduino in ambient mode: " + colors);
                                            output.flush();
                                            try {Thread.sleep(50); } catch(Exception e) {}
                                        }catch (AWTException e){

                                        }
                                    }else if(randomButton.isSelected()){
                                        animation1RadioButton.setEnabled(true);
                                        animation2RadioButton.setEnabled(true);
                                        String animationMode;

                                        if(animation1RadioButton.isSelected()){
                                            animationMode = ",1";
                                        }else if(animation2RadioButton.isSelected()){
                                            animationMode = ",2";
                                        }else{
                                            animationMode = ",0";
                                        }

                                        String colors = getRandomColor() + animationMode;
                                        output.print(colors);
                                        System.out.println("Sending arduino in random mode: " + colors);
                                        output.flush();
                                        try {Thread.sleep(3000); } catch(Exception e) {}
                                    }
                                }
                            }
                        };
                        thread.start();
                    }
                } else {
                    // disconnect from the serial port
                    chosenPort.closePort();
                    portList.setEnabled(true);
                    connectButton.setText("Connect");
                }
            }
        });
        window.setVisible(true);
    }

    public static String getAmbientColor() throws AWTException {
        int averageRed = 0;
        int averageGreen = 0;
        int averageBlue = 0;

        Robot robot = new Robot();
        Rectangle screenRectangle = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        BufferedImage screenCapture = robot.createScreenCapture(screenRectangle);
        int screenHeight = screenRectangle.height;
        int screenWidth = screenRectangle.width;

        for(int i = 0; i < screenCapture.getWidth(); i++){
            for(int j = 0; j < screenCapture.getHeight(); j++){
                int pixelColor = screenCapture.getRGB(i, j);
                int red = (pixelColor & 0xFF0000) >> 16;
                int green = (pixelColor & 0xFF00) >> 8;
                int blue = (pixelColor & 0xFF);

                averageRed += red;
                averageGreen += green;
                averageBlue += blue;
            }
        }

        averageRed /= (screenHeight * screenWidth);
        averageGreen /= (screenHeight * screenWidth);
        averageBlue /= (screenHeight * screenWidth);

        return averageRed + "," + averageGreen + "," + averageBlue;
    }

    public static String getRandomColor(){
        Random random = new Random();
        int randomRed = random.nextInt(256);
        int randomGreen = random.nextInt(256);
        int randomBlue = random.nextInt(256);

        String colorString = randomRed + "," + randomGreen + "," + randomBlue;
        return colorString;
    }
    
}
