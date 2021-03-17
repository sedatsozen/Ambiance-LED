#include <ctype.h>

#define redPin 9
#define greenPin 6
#define bluePin 5

class Color{
  public:
    static int redValue;
    static int greenValue;
    static int blueValue;
    static int animationMode;

    void parseColors(String colorString);
    void turnOnLed();
    static void brightenRed();
    static void brightenGreen();
    static void brightenBlue();
    static void fadeRed();
    static void fadeGreen();
    static void fadeBlue();
    void animate();
    void animate2();
    void printColors();
    void testAnimation();
};

int Color::redValue;
int Color::greenValue;
int Color::blueValue;
int Color::animationMode;

void Color::parseColors(String colorString){
  int firstCommaIndex = colorString.indexOf(',');
  int secondCommaIndex = colorString.indexOf(',', firstCommaIndex + 1);
  int thirdCommaIndex = colorString.indexOf(',', secondCommaIndex + 1);
      
  redValue = (colorString.substring(0,firstCommaIndex)).toInt();
  greenValue = (colorString.substring(firstCommaIndex + 1, secondCommaIndex)).toInt();
  blueValue = (colorString.substring(secondCommaIndex + 1)).toInt();
  animationMode = (colorString.substring(thirdCommaIndex + 1)).toInt();
}

void Color::turnOnLed(){
  analogWrite(redPin, redValue);
  analogWrite(greenPin, greenValue);
  analogWrite(bluePin, blueValue);
}

void Color::animate(){
  int timeInterval = 100;
  double redStep = (double) redValue / (double) timeInterval;
  double greenStep = (double) greenValue / (double) timeInterval;
  double blueStep = (double) blueValue / (double) timeInterval;
  double tempRed = 0, tempGreen = 0, tempBlue = 0;

  Serial.println(redStep);
  Serial.println(greenStep);
  Serial.println(blueStep);

  for(int i = 0; i < timeInterval; i++){
    delay(10);
    tempRed = tempRed + redStep;
    tempGreen = tempGreen + greenStep;
    tempBlue = tempBlue + blueStep;

    analogWrite(redPin, tempRed);
    analogWrite(greenPin, tempGreen);
    analogWrite(bluePin, tempBlue);

    Serial.println(tempRed);
    Serial.println(tempGreen);
    Serial.println(tempBlue);    
  }

  Serial.println("////////////");
  Serial.println(tempRed);
  Serial.println(tempGreen);
  Serial.println(tempBlue);
  Serial.println("////////////");  

  for(int i = 0; i < timeInterval; i++){
    delay(10);
    tempRed = tempRed - redStep;
    tempGreen = tempGreen - greenStep;
    tempBlue = tempBlue - blueStep;

    analogWrite(redPin, tempRed);
    analogWrite(greenPin, tempGreen);
    analogWrite(bluePin, tempBlue);

    Serial.println(tempRed);
    Serial.println(tempGreen);
    Serial.println(tempBlue);
  }

}

void Color::animate2(){
  for(int i = 0; i < redValue; i++){
    analogWrite(redPin, i);
    delay(2);
  }

  delay(2);

  for(int i = 0; i < greenValue; i++){
    analogWrite(greenPin, i);
    delay(2);
  }

  delay(2);

  for(int i = 0; i < blueValue; i++){
    analogWrite(bluePin, i);
    delay(2);
  }

  delay(20);

  for(int i = redValue; i >= 0; i--){
    analogWrite(redPin, i);
    delay(2);
  }

  delay(2);

  for(int i = greenValue; i >= 0; i--){
    analogWrite(greenPin, i);
    delay(2);
  }

  delay(2);

  for(int i = blueValue; i >= 0; i--){
    analogWrite(bluePin, i);
    delay(2);
  }
}

void Color::printColors(){
  Serial.print("Red: ");
  Serial.println(redValue);

  Serial.print("Green: ");
  Serial.println(greenValue);

  Serial.print("Blue: ");
  Serial.println(blueValue);
}

void Color::testAnimation(){
  
}

void setup() {
  pinMode(redPin, OUTPUT);
  pinMode(greenPin, OUTPUT);
  pinMode(bluePin, OUTPUT);

  Serial.begin(9600);
  Serial.setTimeout(50); //!!!!!!!!!!!!!!!!!!!!!  CHANGED
}


void loop() {
  
  Color color;
  while(Serial.available() == 0){
    
  }

  String colorString = Serial.readString();
  
  color.parseColors(colorString);

  if(Color::animationMode == 0){
    color.turnOnLed();
  }else if(Color::animationMode == 1){
    color.animate();
  }else if(Color::animationMode == 2){
    color.animate2();
  }
  
}


  
