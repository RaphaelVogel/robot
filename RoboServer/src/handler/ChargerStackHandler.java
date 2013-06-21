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
import com.tinkerforge.IPConnection;

import core.Action;
import core.Constants;
import core.Handler;
import core.Loader;

public class ChargerStackHandler extends Handler{

	private Logger logger = Logger.getLogger(ChargerStackHandler.class.getName());
	private final String HOST = "charger";
	private final int PORT = 4223;
	private final String MASTER_UID = "6esCZX";
	private final String DUAL_RELAIS_UID = "bVY";
	private final String SERVO_UID = "6JqqH8";
	
	private static BrickMaster masterBrick;
	private static BrickServo servoBrick;
	private static BrickletDualRelay dualRelais;
	
	private IPConnection ipConnection;
	private Loader loader;
	
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
		closeConnectionAndLoader();
		
		ipConnection = new IPConnection();
		masterBrick = new BrickMaster(MASTER_UID, ipConnection);
		servoBrick = new BrickServo(SERVO_UID, ipConnection);
		dualRelais = new BrickletDualRelay(DUAL_RELAIS_UID, ipConnection);
		
		ipConnection.connect(HOST, PORT);
		logger.log(Level.INFO, "New IPConnection to charger established");
		
		// set the Power Mode of WIFI to "Low Power"
		masterBrick.setWifiPowerMode(BrickMaster.WIFI_POWER_MODE_LOW_POWER);
        
		// initialize magnet servo
		servoBrick.setPulseWidth(Constants.servo0, 800, 2100);
		servoBrick.setVelocity(Constants.servo0, 20000);
		servoBrick.setPosition(Constants.servo0, (short)-9000);
		servoBrick.enable(Constants.servo0);
		
		return "Hardware to charger initialized";
	}
	
	@SuppressWarnings("unused")
	private String roboOff() throws Exception{
		servoBrick.setPosition(Constants.servo0, (short)9000);
		servoBrick.enable(Constants.servo0);
		return "Switched Robo off";
	}
	
	@SuppressWarnings("unused")
	private String roboOn() throws Exception{
		servoBrick.setPosition(Constants.servo0, (short)-9000);
		servoBrick.enable(Constants.servo0);
		return "Switched Robo on";
	}
	
	@SuppressWarnings("unused")
	private String startCharge() throws Exception{
		if(loader != null){
			loader.stopLoading();
		}
		loader = new Loader();
		loader.start();
		return "Start Charging";
	}
	
	@SuppressWarnings("unused")
	private String stopCharge() throws Exception{
		return "Stop Charging";
	}
	
	private void closeConnectionAndLoader() throws Exception {
		if(loader != null){
			loader.stopLoading();
			loader = null;
		}
		if(isConnectedOrPending(ipConnection)){
			ipConnection.disconnect();
			ipConnection = null;
		}
	}
	
	public static BrickServo getServoBrick(){
		return servoBrick;
	}
	
	public static BrickletDualRelay getDualRelaisBricklet(){
		return dualRelais;
	}
}