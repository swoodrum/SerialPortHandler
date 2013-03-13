package com.scott.barometer.serialport;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.util.ArrayList;

import org.apache.log4j.Logger;

public class Utils {
	@SuppressWarnings("unchecked")
	public static ArrayList<String> getCOMPorts() {
		ArrayList<String> ports = new ArrayList<String>();
		java.util.Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier
				.getPortIdentifiers();
		while (portEnum.hasMoreElements()) {
			CommPortIdentifier portId = portEnum.nextElement();
			if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				getLogger().debug("Found serial port: " + portId.getName());
				ports.add(portId.getName());
			}
		}
		return ports;
	}

	public static SerialPort getSerialPort(String portId) {
		SerialPort port = null;
		try {
			CommPortIdentifier portIdentifier = CommPortIdentifier
					.getPortIdentifier(portId);
			CommPort commPort = portIdentifier.open("SerialPortHandler",
					Integer.parseInt("2000"));
			port = (SerialPort) commPort;
			port.setSerialPortParams(Integer.parseInt("9600"),
					SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);
		} catch (Exception e) {
			getLogger().warn(e.getMessage(), e);
		}
		return port;
	}

	private static Logger getLogger() {
		return Logger.getLogger(Utils.class);
	}

}
