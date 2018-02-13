#author - Arpit Rastogi



import ibmiotf.device
from time import sleep # Import sleep Library
import RPi.GPIO as GPIO # Import GPIO Library
import os
import json


GPIO.setmode(GPIO.BOARD) # Use Physical Pin Numbering Scheme
publish_led=22 # LED 1 is connected to physical pin 22
cpu_temp_led=18 # LED 2 is connected to physical pin 18
mobile_bulb=23
iot_connection_led=12
error_led=8
GPIO.setup(publish_led,GPIO.OUT,) # Make LED 1 an Output
GPIO.setup(cpu_temp_led,GPIO.OUT) # Make LED 2 an Output
GPIO.setup(mobile_bulb,GPIO.OUT) # Make LED 2 an Output
GPIO.setup(iot_connection_led,GPIO.OUT)
GPIO.setup(error_led,GPIO.OUT)

GPIO.output(publish_led,False)
GPIO.output(cpu_temp_led,False)
GPIO.output(iot_connection_led,False)
GPIO.output(error_led,False)
GPIO.output(mobile_bulb,False)


#read the IoT cred from file
cred = json.loads(open("./sample_iot_cred.txt","r").read())
options = cred

def commandProcessor(cmd):
 data=json.loads(json.dumps(cmd.data))
 print("Command received: %s" % json.dumps(cmd.data))
 if(data.get('ledpower')=='ON'):
  GPIO.output(cpu_temp_led,True)
  #sleep(7)
  #GPIO.output(cpu_temp_led,False)
 elif(data.get('bulbpower')=='ON'):
  GPIO.output(mobile_bulb,True)
 elif(data.get('bulbpower')=='OFF'):
  GPIO.output(mobile_bulb,False)
 else:
  GPIO.output(cpu_temp_led,False)
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
    GPIO.output(cpu_temp_led,False)
