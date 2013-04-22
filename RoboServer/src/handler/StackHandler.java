package handler;

import java.lang.reflect.Method;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import listener.DistanceReached;

import com.tinkerforge.BrickMaster;
import com.tinkerforge.BrickServo;
import com.tinkerforge.BrickletDistanceIR;
import com.tinkerforge.BrickletIndustrialQuadRelay;
import com.tinkerforge.IPConnection;

import core.Action;
import core.Handler;
import core.MonoflopStarter;

public class StackHandler extends Handler{

	private Logger logger = Logger.getLogger(StackHandler.class.getName());
	private final String HOST = "robo";
	private final int PORT = 4223;
	private final String MASTER_UID = "6rkQPB";
	private final String SERVO_UID = "6QFwhz";
	private final String QUAD_RELAIS_UID = "cts";
	private final String DISTANCE_IR_UID = "cXb";
	
	private static BrickMaster masterBrick;
	private static BrickServo servoBrick;
	private static BrickletIndustrialQuadRelay quadRelais;
	private static BrickletDistanceIR distanceIR;
	
	private IPConnection ipConnection;
	private MonoflopStarter monoflopThread;

	public void serve(Action action, HttpServletRequest request, HttpServletResponse response) {
		// /Stack/<command>
		List<String> parameters = action.getParameters();
		if(parameters.size() != 1){
			sendResponse(HttpServletResponse.SC_BAD_REQUEST, "Wrong URL format, use /Stack/<command>", response);
			return;
		}
		String command = parameters.get(0);
		Object resultString = null;
		try {
			Method method = this.getClass().getDeclaredMethod(command);
			resultString = method.invoke(this);
		} catch (Exception e) {
			logger.severe(e.getMessage());
			sendResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error in method Stack."+command, response);
			return;
		}
		sendResponse(HttpServletResponse.SC_OK, (String)resultString, response);
		return;
	}
	
	@SuppressWarnings("unused")
	private String initialize() throws Exception{
		if(isConnectedOrPending()){
			// forget to clean up - disconnect before connect
			if(monoflopThread != null && monoflopThread.isAlive()){
				monoflopThread.shouldRun = false;
				Thread.sleep(500);
			}
			ipConnection.disconnect();
			ipConnection = null;
		}
		ipConnection = new IPConnection();
		masterBrick = new BrickMaster(MASTER_UID, ipConnection);
		servoBrick = new BrickServo(SERVO_UID, ipConnection);
		quadRelais = new BrickletIndustrialQuadRelay(QUAD_RELAIS_UID, ipConnection);
		distanceIR = new BrickletDistanceIR(DISTANCE_IR_UID, ipConnection);
		ipConnection.connect(HOST, PORT);
		
		// set the Power Mode of WIFI to "Low Power"
		masterBrick.setWifiPowerMode(BrickMaster.WIFI_POWER_MODE_LOW_POWER);
		
        // configure distance IR sensor
		distanceIR.setDebouncePeriod(3000);
		// distance smaller than 25cm
        distanceIR.setDistanceCallbackThreshold(BrickletDistanceIR.THRESHOLD_OPTION_SMALLER, (short)250, (short)0);
        distanceIR.addDistanceReachedListener(new DistanceReached());
		
        // start camera and ESC in save mode (using monoflop relais)
        monoflopThread = new MonoflopStarter();
        monoflopThread.start();
        
        // initially stop robot and center camera
        DriveHandler driveHandler = new DriveHandler();
        driveHandler.stop();
        CameraHandler cameraHandler = new CameraHandler();
        cameraHandler.center();
        
		return "Hardware initialized";
	}
	
	@SuppressWarnings("unused")
	private String cleanUp() throws Exception{
		// stop ESC and camera and wait before killing connection
		if(monoflopThread != null && monoflopThread.isAlive()){
			monoflopThread.shouldRun = false;
			monoflopThread = null;
			Thread.sleep(500);
		}
		if(isConnectedOrPending()){
			ipConnection.disconnect();
			ipConnection = null;
		}	
		return "Closed connection to stack";
	}
	
	@SuppressWarnings("unused")
	private String getVoltAndAmpere() throws Exception{
		int voltage;
		int current;
		voltage = masterBrick.getStackVoltage();
		current = masterBrick.getStackCurrent();
		return "Voltage: " + voltage + " mV | Current: " + current + " mA";
	}

	
	private boolean isConnectedOrPending(){
		if(ipConnection == null){
			return false;
		}
		return (ipConnection.getConnectionState() == IPConnection.CONNECTION_STATE_CONNECTED ||
				ipConnection.getConnectionState() == IPConnection.CONNECTION_STATE_PENDING);
	}
	
	public static BrickServo getServoBrick(){
		return servoBrick;
	}
	
	public static BrickMaster getMasterBrick(){
		return masterBrick;
	}
	
	public static BrickletIndustrialQuadRelay getQuadRelaisBricklet(){
		return quadRelais;
	}
}