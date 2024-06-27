
# Pan Tilt Processing for Pi-Bot
This is an executable process based performing pan tilt controls on the blue pibot robots camera sensor!

## Configuration

Configuring the sensor requires providing a sensorML camera.xml document

These documents should be contained in _sensorML_ directory within the deployed instance of OpenSensorHub



## Necessary tools
 - Nintendo Wii Remote Linux
        - To connect a wii remote you need to be sure that the linux Bluetooth stack (Bluez) is installed on your system!

- steps to install necessary packages
- sudo apt update
- sudo apt upgrade
- sudo apt-get install bluez OR sudo apt-get install bluez libbluetooth-dev

## Sample Configuration

On a linux machine( if planning to add the virtual sensor to pibot node):
- with universal controller:
         - Open the build/distributions/node and run the node
         - open a browser and type in localhost:8181/sensorhub/admin
         - add the universal controller node
         - add the type of controller: supported types are xbox, wii, playstation
         - set up any other configurations
         - start the module:
               - wii remote can be put into discoverable mode in 2 ways:
                        1. hold button 1 and button 2
                        2. OR hold the red sync button that is located behind the battery cover on the back of the remote
                   - the driver will begin finding the remote through bluecove  
  - with wii driver:
          - Open the build/distributions/node and run the node
          - open a browser and type in localhost:8181/sensorhub/admin
          - add the wii remote driver
          -  start the module:
                - wii remote can be put into discoverable mode in 2 ways:
                            1. hold button 1 and button 2
                            2. OR hold the red sync button that is located behind the battery cover on the back of the remote
                               - the driver will begin finding the remote through bluecove 
                - the wii remote will be connected when there is 1 light turned on the wii remote!
         - start the wii module


**on the pibot:**

- scp the built node to the robots file directory
- scp the necessary process xml files as well!
    - These files need to be in the build file!
- unzip the node and copy over the xml files for each of the processes

- open the build/distribtuions/node and run the node using the sh launch.sh 
- open a browser and type in localhost:8181/sensorhub/admin

## Sensors
- add the drive, camera, and searchlight drivers
  - these do not have any configurations, so you can start these!
  - if using the virtual sensor for the remote control add that driver in the sensors:
    - Right-click the sensors tab and add ‘New SWE Virtual Sensor”
      Update module name to : wii remote
      Update sensor uid to : urn:osh:sensor:wii001
      Click the plus on observed properties
      Type in the text box : http://sensorml.com/ont/swe/property/...
    - Then under SOS endpoint:
      Remote host: ip of linux computer
      Username : admin
      Password: admin
      Resource path: /sensorhub/sos
      Remote port: 8181

Then apply changes to initialize
Then start the module
Now you should be able to see the updated data in the observation outputs! 

## Processes
- Right-click in the processes box and add a new process
- in the SensorML File put the name of the xml file for the process
     - driveProcess.xml
     - camera.xml
     - searchlightProcess.xml
     - 