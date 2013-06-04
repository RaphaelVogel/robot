package handler;

import java.lang.reflect.Method;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tinkerforge.BrickMaster;
import com.tinkerforge.IPConnection;

import core.Action;
import core.Handler;

public class ChargerStackHandler extends Handler{

	private Logger logger = Logger.getLogger(ChargerStackHandler.class.getName());
	private final String HOST = "charger";
	private final int PORT = 4223;
	private final String MASTER_UID = "";
	
	private static BrickMaster masterBrick;
	
	private IPConnection ipConnection;

	public void serve(Action action, HttpServletRequest request, HttpServletResponse response) {
		// /ChargerStack/<command>
		List<String> parameters = action.getParameters();
		if(parameters.size() != 1){
			sendTextResponse(HttpServletResponse.SC_BAD_REQUEST, "Wrong URL format, use /ChargerStack/<command>", response);
			return;
		}
		String command = parameters.get(0);
		Object resultString = null;
		try {
			Method method = this.getClass().getDeclaredMethod(command);
			resultString = method.invoke(this);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error in method ChargerStackHandler."+command, e);
			sendTextResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error in method ChargerStack."+command, response);
			return;
		}
		sendTextResponse(HttpServletResponse.SC_OK, (String)resultString, response);
		return;
	}
	
	@SuppressWarnings("unused")
	private String initialize() throws Exception{
		closeConnection();
		
		ipConnection = new IPConnection();
		masterBrick = new BrickMaster(MASTER_UID, ipConnection);
		ipConnection.connect(HOST, PORT);
		logger.log(Level.INFO, "New IPConnection to charger established");
		
		// set the Power Mode of WIFI to "Low Power"
		masterBrick.setWifiPowerMode(BrickMaster.WIFI_POWER_MODE_LOW_POWER);
        
		return "Hardware to charger  initialized";
	}
	
	@SuppressWarnings("unused")
	private String cleanUp() throws Exception{
		closeConnection();
		return "Closed connection to charger";
	}

	@SuppressWarnings("unused")
	private String getVoltAndAmpere() throws Exception{
		int voltage;
		int current;
		voltage = masterBrick.getStackVoltage();
		current = masterBrick.getStackCurrent();
		return "Charger voltage: " + voltage + " mV | Charger current: " + current + " mA";
	}
	
	
	private void closeConnection() throws Exception {
		if(isConnectedOrPending(ipConnection)){
			ipConnection.disconnect();
			ipConnection = null;
		}
	}
	
}