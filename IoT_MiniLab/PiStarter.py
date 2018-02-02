#author - Arpit Rastogi



import ibmiotf.device
from time import sleep # Import sleep Library
import RPi.GPIO as GPIO # Import GPIO Library
import os
import json


GPIO.setmode(GPIO.BOARD) # Use Physical Pin Numbering Scheme
publish_led=22 # LED 1 is connected to physical pin 22
response_led=18 # LED 2 is connected to physical pin 18
iot_connection_led=12
error_led=8
GPIO.setup(publish_led,GPIO.OUT,) # Make LED 1 an Output
GPIO.setup(response_led,GPIO.OUT) # Make LED 2 an Output
GPIO.setup(iot_connection_led,GPIO.OUT)
GPIO.setup(error_led,GPIO.OUT)

GPIO.output(publish_led,False)
GPIO.output(response_led,False)
GPIO.output(iot_connection_led,False)
GPIO.output(error_led,False)

BS1=False # Set Flag BS1 to indicate LED is initially off
BS2=False
options = {
 "org": "dzj2si",
 "type": "MyIoTDevice",
 "id": "Pi",
 "auth-method": "token",
 "auth-token": "Geek12345",
 "clean-session": "true"
 }


def commandProcessor(cmd):
 data=json.loads(json.dumps(cmd.data))
 print("Command received: %s" % json.dumps(cmd.data))
 if(data.get('power')=='ON'):
  GPIO.output(response_led,True)
  #sleep(7)
  #GPIO.output(response_led,False)
 else:
  GPIO.output(response_led,False)
  sleep(1)


def myOnPublishCallback():
 print("")

myQosLevel=0

print "connecting to IoT"
try:
    client = ibmiotf.device.Client(options)
    client.commandCallback = commandProcessor
    client.connect()
    GPIO.output(iot_connection_led,True)
except ibmiotf.ConnectionException as e:
    print "error",e
    GPIO.output(error_led,True)

print "connected to IoT"

while(1): # Create an infinite Loop
    #print "posting data to IoT"
    GPIO.output(publish_led,True)# turn it on
    temp = (os.popen('vcgencmd measure_temp').readline()).replace("temp=","").replace("'C\n","")
    myData={"temp":temp}
    success = client.publishEvent("status", "json", myData, myQosLevel,on_publish=myOnPublishCallback)
    if not success:
        print("Not published to IoTF")
    else:
        print("Successfully published to IoT")
    GPIO.output(publish_led,False) # Turn LED off
    sleep(10)
    GPIO.output(response_led,False)
