# MySensor
A simple sensor mobile application for signal detection.



#### Progression Table

| Date          | Progress                      | Description                                                  |
| ------------- | ----------------------------- | ------------------------------------------------------------ |
| Oct 3, 2022   | Information analysis          | Analyzing requirements for sensor and functions needed by mobile application. |
| Oct 4, 2022   | Basic interface and GUI setup | Setup framework for sensor mobile application.               |
| Oct 5, 2022ee | Connection between fragments  | Making a data-sharing class for connection between fragments. |
| Oct 6, 2022   | Build and release apk         | Make last adjustment and generate apk from codes.            |



#### Fragments functional overview

| Title            | Fragments                                      | Description                                                  |
| ---------------- | ---------------------------------------------- | ------------------------------------------------------------ |
| Sensor           | ![](README/sensor-fragment-16650218617233.png) | List of sensors assigned with tasks where signal detection is based on the data provided in the Data Fragment. The green chip allows the respective sensor to be switched on and off. Sensors would be doing task if it is switched on, where turns no signal when switched off or no signal detected. The yellow add button is to add sensors accordingly to the list for task executing. The purple update button is to update newest status of each sensor. |
| Data Record      | ![](README/data-fragment-16650218706145.png)   | List of data records based on the sensors added in the Sensor Fragment. Updates on data would cause changes in the status of each sensor. |
| Detection Record | ![](README/record-fragment-16650218838687.png) | List of detection records having the name of sensors and the time of detections. |

