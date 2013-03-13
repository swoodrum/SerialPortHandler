package com.scott.barometer.serialport;

import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

import akka.actor.UntypedActor;

public class SerialPortBroker extends UntypedActor implements
		SerialPortEventListener {

	private SerialPort serialPort;
	private String portId;

	private byte[] buffer = new byte[1024];

	private InputStream inputStream;

	public SerialPortBroker(String portId) {
		this.portId = portId;
	}

	@Override
	public void preStart() {
		serialPort = Utils.getSerialPort(portId);
		try {
			inputStream = serialPort.getInputStream();
			serialPort.addEventListener(this);
			serialPort.notifyOnDataAvailable(true);
			//getContext().actorFor("akka://MyActors/user/receiver").tell(
					//"serialport up", getSelf());
		} catch (Exception e1) {
			getLogger().fatal(e1);
			e1.printStackTrace();
		}
	}

	private Logger getLogger() {
		return Logger.getLogger(SerialPortBroker.class);
	}

	@Override
	public void onReceive(Object msg) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void postStop() {
		serialPort.close();
		getLogger().debug("Serial port closed from Akka...");
		//getContext().actorFor("akka://MyActors/user/receiver").tell(
				//"serialport down", getSelf());
	}

	@Override
	public void serialEvent(SerialPortEvent arg0) {
		int data;

		try {
			int len = 0;
			while ((data = inputStream.read()) > -1) {
				if (data == '\n') {
					break;
				} else if (data == '*') {
					break;
				}
				buffer[len++] = (byte) data;
			}
			String theData = new String(buffer, 0, len);
			getLogger().debug(theData);

			if (theData.startsWith("9") || theData.startsWith("1")) {
				getContext().actorFor("akka://MyActors/user/receiver").tell(
						theData, getSelf());
			}

		} catch (IOException e) {
			e.printStackTrace();
			getLogger().debug(e);
		}

	}

}
