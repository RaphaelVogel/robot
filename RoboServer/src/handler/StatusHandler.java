package handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tinkerforge.BrickMaster;
import com.tinkerforge.IPConnection.TimeoutException;

import core.Action;
import core.Connection;
import core.Handler;

public class StatusHandler extends Handler{

	private BrickMaster masterBrick = Connection.getInstancxe().getMasterBrick();

	public void serve(Action action, HttpServletRequest request, HttpServletResponse response) {
		int voltage;
		int current;
		try {
			voltage = masterBrick.getStackVoltage();
			current = masterBrick.getStackCurrent();
		} catch (TimeoutException e) {
			sendResponse(HttpServletResponse.SC_BAD_REQUEST, "Could not retrieve current and voltage of stack", response);
			return;
		}
		sendResponse(HttpServletResponse.SC_OK, "Voltage: " + voltage + " mV | Current: " + current + " mA", response);
	}

}