package handler;

import java.lang.reflect.Method;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tinkerforge.BrickMaster;
import com.tinkerforge.BrickServo;
import com.tinkerforge.BrickletDualRelay;
import com.tinkerforge.BrickletIndustrialQuadRelay;
import com.tinkerforge.IPConnection;

import core.Action;
import core.CameraMonoflop;
import core.Constants;
import core.DriveMonoflop;
import core.Handler;

public class RoboStackHandler extends Handler{

	private Logger logger = Logger.getLogger(RoboStackHandler.class.getName());
	private final String HOST = "robo";
	private final int PORT = 4223;
	private final String MASTER_UID = "6rkQPB";
	private final String SERVO_UID = "6QFwhz";
	private final String QUAD_RELAIS_UID = "cts";
	private final String DUAL_RELAY_UID = "bV3";
	
	private static BrickMaster masterBrick;
	private static BrickServo servoBrick;
	private static BrickletIndustrialQuadRelay quadRelais;
	private static BrickletDualRelay dualRelais;
	
	private static DriveMonoflop driveMonoflop;
	private static CameraMonoflop cameraMonoflop;
	
	private IPConnection ipConnection;

	public void serve(Action action, HttpServletRequest request, HttpServletResponse response) {
		// /RoboStack/<command>
		List<String> parameters = action.getParameters();
		if(parameters.size() != 1){
			sendTextResponse(HttpServletResponse.SC_BAD_REQUEST, "Wrong URL format, use /RoboStack/<command>", response);
			return;
		}
		String command = parameters.get(0);
		Object resultString = null;
		try {
			Method method = this.getClass().getDeclaredMethod(command);
			resultString = method.invoke(this);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error in method RoboStackHandler."+command, e);
			sendTextResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error in method RoboStackHandler."+command, response);
			return;
		}
		sendTextResponse(HttpServletResponse.SC_OK, (String)resultString, response);
		return;
	}
	
	@SuppressWarnings("unused")
	private String initialize() throws Exception{
		closeConnectionAndMonoflops();
		
		ipConnection = new IPConnection();
		masterBrick = new BrickMaster(MASTER_UID, ipConnection);
		servoBrick = new BrickServo(SERVO_UID, ipConnection);
		quadRelais = new BrickletIndustrialQuadRelay(QUAD_RELAIS_UID, ipConnection);
		dualRelais = new BrickletDualRelay(DUAL_RELAY_UID, ipConnection);
		ipConnection.connect(HOST, PORT);
		logger.log(Level.INFO, "New IPConnection to robo established");
		
		// set the Power Mode of WIFI to "Low Power"
		masterBrick.setWifiPowerMode(BrickMaster.WIFI_POWER_MODE_LOW_POWER);
		
		// configure the drive servos (ESC) 
		servoBrick.setAcceleration(Constants.servo0And1, 50000);
		
		// configure the camera servos
		servoBrick.setVelocity(Constants.servo2And3, 20000);
		
        // start camera and ESC in save mode (using monoflop relais)
        driveMonoflop = new DriveMonoflop();
        driveMonoflop.start();
        cameraMonoflop = new CameraMonoflop();
        cameraMonoflop.start();
        
        // initially stop robot and center camera
        DriveHandler driveHandler = new DriveHandler();
        driveHandler.stop();
        CameraHandler cameraHandler = new CameraHandler();
        cameraHandler.center();
        
		return "Hardware for robo initialized";
	}
	
	@SuppressWarnings("unused")
	private String cleanUp() throws Exception{
		closeConnectionAndMonoflops();
		return "Closed connection to Robo Stack";
	}

	@SuppressWarnings("unused")
	private String getVoltAndAmpere() throws Exception{
		int voltage;
		int current;
		voltage = masterBrick.getStackVoltage();
		current = masterBrick.getStackCurrent();
		return "Robo Stack Voltage: " + voltage + " mV | Current: " + current + " mA";
	}

	private void closeConnectionAndMonoflops() throws Exception {
		if(driveMonoflop != null){
			driveMonoflop.stopDriveMonoflop();
			quadRelais.setValue((0 << 0) | (0 << 1));
			driveMonoflop = null;
		}
		if(cameraMonoflop != null){
			cameraMonoflop.stopCameraMonoflop();
			dualRelais.setSelectedState((short)1, false);
			cameraMonoflop = null;
		}
		if(isConnectedOrPending(ipConnection)){
			ipConnection.disconnect();
			ipConnection = null;
		}
	}

	public static BrickServo getServoBrick(){
		return servoBrick;
	}
		
	public static BrickletIndustrialQuadRelay getQuadRelaisBricklet(){
		return quadRelais;
	}

	public static BrickletDualRelay getDualRelaisBricklet(){
		return dualRelais;
	}	

}
