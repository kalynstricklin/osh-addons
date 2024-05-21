/***************************** BEGIN LICENSE BLOCK ***************************

 Copyright (C) 2020-2021 Nicolas Garay. All Rights Reserved.

 ******************************* END LICENSE BLOCK ***************************/
package org.sensorhub.impl.sensor.pibot.drive;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.exception.GpioPinNotProvisionedException;
import com.pi4j.wiringpi.SoftPwm;
import org.sensorhub.impl.sensor.pibot.common.config.GpioEnum;
import org.sensorhub.api.common.SensorHubException;
import org.sensorhub.impl.sensor.AbstractSensorModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DriveSensor driver for the PiBot providing sensor description, control & output registration,
 * initialization and shutdown of driver and outputs.
 *
 * @author Nick Garay
 * @since Feb. 15, 2021
 */
public class DriveSensor extends AbstractSensorModule<DriveConfig> {

    private static final Logger logger = LoggerFactory.getLogger(DriveSensor.class);

    private int leftPwmPinAddress = RaspiPin.GPIO_27.getAddress();

    private int rightPwmPinAddress = RaspiPin.GPIO_23.getAddress();

    private GpioPinDigitalOutput rightMotorForwardPin;

    private GpioPinDigitalOutput rightMotorReversePin;

    private GpioPinDigitalOutput leftMotorForwardPin;

    private GpioPinDigitalOutput leftMotorReversePin;

    private DriveOutput output;

    private final GpioController gpio = GpioFactory.getInstance();

    @Override
    public void doInit() throws SensorHubException {

        super.doInit();

        // Generate identifiers
        generateUniqueID("urn:pibot:", config.serialNumber);
        generateXmlID("PIBOT", config.serialNumber);

        // Create and initialize output
//        output = new DriveOutput(this);
//
//        addOutput(output, false);
//
//        output.init();

        DriveControl control = new DriveControl(this);

        addControlInput(control);

        control.init();
    }

    @Override
    public void doStart() throws SensorHubException {

        super.doStart();

        if (null != output) {

             // Allocate necessary resources and start outputs
            output.start();
        }

        // Initialize right motor pins
        allocateRightMotorPins();

        // Initialize left motor pins
        allocateLeftMotorPins();
    }

    private void allocateRightMotorPins() {

        if (config.pinConfig.rightMotorPwm != GpioEnum.PIN_UNSET) {

            rightPwmPinAddress = config.pinConfig.rightMotorPwm.getValue();
        }

        SoftPwm.softPwmCreate(rightPwmPinAddress, 0, 100);

        if (config.pinConfig.rightMotorForward != GpioEnum.PIN_UNSET) {

            rightMotorForwardPin = gpio.provisionDigitalOutputPin(RaspiPin.getPinByAddress(config.pinConfig.rightMotorForward.getValue()));

        } else {

            rightMotorForwardPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_24);
        }
        rightMotorForwardPin.setShutdownOptions(true, PinState.LOW);

        if (config.pinConfig.rightMotorReverse != GpioEnum.PIN_UNSET) {

            rightMotorReversePin = gpio.provisionDigitalOutputPin(RaspiPin.getPinByAddress(config.pinConfig.rightMotorReverse.getValue()));

        } else {

            rightMotorReversePin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_25);
        }
        rightMotorReversePin.setShutdownOptions(true, PinState.LOW);
    }

    private void allocateLeftMotorPins() {

        if (config.pinConfig.leftMotorPwm != GpioEnum.PIN_UNSET) {

            leftPwmPinAddress = config.pinConfig.leftMotorPwm.getValue();
        }

        SoftPwm.softPwmCreate(leftPwmPinAddress, 0, 100);

        if (config.pinConfig.leftMotorForward != GpioEnum.PIN_UNSET) {

            leftMotorForwardPin = gpio.provisionDigitalOutputPin(RaspiPin.getPinByAddress(config.pinConfig.leftMotorForward.getValue()));

        } else {

            leftMotorForwardPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_28);
        }
        leftMotorForwardPin.setShutdownOptions(true, PinState.LOW);

        if (config.pinConfig.leftMotorReverse != GpioEnum.PIN_UNSET) {

            leftMotorReversePin = gpio.provisionDigitalOutputPin(RaspiPin.getPinByAddress(config.pinConfig.leftMotorReverse.getValue()));

        } else {

            leftMotorReversePin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_29);
        }
        leftMotorReversePin.setShutdownOptions(true, PinState.LOW);
    }

    @Override
    public void doStop() throws SensorHubException {

        super.doStop();

        if (null != output) {

            output.stop();
        }

        SoftPwm.softPwmWrite(leftPwmPinAddress, 0);
        SoftPwm.softPwmWrite(rightPwmPinAddress, 0);

        releasePin(rightMotorForwardPin);
        releasePin(rightMotorReversePin);
        releasePin(leftMotorForwardPin);
        releasePin(leftMotorReversePin);
    }

    private void releasePin(GpioPinDigitalOutput pin) {

        try {

            if (pin != null) {

                pin.setState(PinState.LOW);
                gpio.unprovisionPin(pin);
            }

        } catch (GpioPinNotProvisionedException e) {

            logger.error("Exception shutting down: { }", e);
        }
    }

    @Override
    public boolean isConnected() {

        // Determine if sensor is connected
//        return output.isAlive();
        return true;
    }

    protected void move(DriveDirection direction, double power) {

        switch (direction) {

            case FORWARD:
                leftMotorForwardPin.setState(PinState.HIGH);
                rightMotorForwardPin.setState(PinState.HIGH);
                leftMotorReversePin.setState(PinState.LOW);
                rightMotorReversePin.setState(PinState.LOW);

                SoftPwm.softPwmWrite(leftPwmPinAddress, (int) power);
                SoftPwm.softPwmWrite(rightPwmPinAddress, (int) power);
                break;

            case FORWARD_TURN_LEFT:
                leftMotorForwardPin.setState(PinState.LOW);
                rightMotorForwardPin.setState(PinState.HIGH);
                leftMotorReversePin.setState(PinState.HIGH);
                rightMotorReversePin.setState(PinState.LOW);

                SoftPwm.softPwmWrite(leftPwmPinAddress, (int) (power * 0.50));
                SoftPwm.softPwmWrite(rightPwmPinAddress, (int) (power));
                break;

            case FORWARD_TURN_RIGHT:
                leftMotorForwardPin.setState(PinState.HIGH);
                rightMotorForwardPin.setState(PinState.LOW);
                leftMotorReversePin.setState(PinState.LOW);
                rightMotorReversePin.setState(PinState.HIGH);

                SoftPwm.softPwmWrite(leftPwmPinAddress, (int) (power));
                SoftPwm.softPwmWrite(rightPwmPinAddress, (int) (power * 0.50));
                break;

            case SPIN_LEFT:
                leftMotorForwardPin.setState(PinState.LOW);
                rightMotorForwardPin.setState(PinState.HIGH);
                leftMotorReversePin.setState(PinState.HIGH);
                rightMotorReversePin.setState(PinState.LOW);

                SoftPwm.softPwmWrite(leftPwmPinAddress, (int) power);
                SoftPwm.softPwmWrite(rightPwmPinAddress, (int) power);
                break;

            case SPIN_RIGHT:
                leftMotorForwardPin.setState(PinState.HIGH);
                rightMotorForwardPin.setState(PinState.LOW);
                leftMotorReversePin.setState(PinState.LOW);
                rightMotorReversePin.setState(PinState.HIGH);

                SoftPwm.softPwmWrite(leftPwmPinAddress, (int) power);
                SoftPwm.softPwmWrite(rightPwmPinAddress, (int) power);
                break;

            case REVERSE_TURN_LEFT:
                leftMotorForwardPin.setState(PinState.LOW);
                rightMotorForwardPin.setState(PinState.LOW);
                leftMotorReversePin.setState(PinState.HIGH);
                rightMotorReversePin.setState(PinState.HIGH);

                SoftPwm.softPwmWrite(leftPwmPinAddress, (int) (power * 0.50));
                SoftPwm.softPwmWrite(rightPwmPinAddress, (int) (power));
                break;

            case REVERSE_TURN_RIGHT:
                leftMotorForwardPin.setState(PinState.LOW);
                rightMotorForwardPin.setState(PinState.LOW);
                leftMotorReversePin.setState(PinState.HIGH);
                rightMotorReversePin.setState(PinState.HIGH);

                SoftPwm.softPwmWrite(leftPwmPinAddress, (int) (power));
                SoftPwm.softPwmWrite(rightPwmPinAddress, (int) (power * 0.50));
                break;

            case REVERSE:
                leftMotorForwardPin.setState(PinState.LOW);
                rightMotorForwardPin.setState(PinState.LOW);
                leftMotorReversePin.setState(PinState.HIGH);
                rightMotorReversePin.setState(PinState.HIGH);

                SoftPwm.softPwmWrite(leftPwmPinAddress, (int) power);
                SoftPwm.softPwmWrite(rightPwmPinAddress, (int) power);
                break;

            case STOP:
            case UNKNOWN:
            default:
                leftMotorForwardPin.setState(PinState.LOW);
                rightMotorForwardPin.setState(PinState.LOW);
                leftMotorReversePin.setState(PinState.LOW);
                rightMotorReversePin.setState(PinState.LOW);

                SoftPwm.softPwmWrite(leftPwmPinAddress, 0);
                SoftPwm.softPwmWrite(rightPwmPinAddress, 0);
                break;
        }

        // Wait for 500 milliseconds
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < 500) ;
    }

    public void forward(double power){
        move(DriveDirection.FORWARD, power);
    }

    public void reverse(double power){
        move(DriveDirection.REVERSE, power);
    }
    public void spinLeft(double power){
        move(DriveDirection.SPIN_LEFT, power);
    }
    public void spinRight(double power){
        move(DriveDirection.SPIN_RIGHT, power);
    }
    public void stop(double power){
        move(DriveDirection.STOP, 0);
    }
}
