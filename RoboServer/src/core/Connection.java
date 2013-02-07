package core;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.tinkerforge.BrickMaster;
import com.tinkerforge.BrickServo;
import com.tinkerforge.BrickletDualRelay;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;

public class Connection {

	private static Connection instance;
	
	private final String HOST = "192.168.178.18";
	private final int PORT = 4223;
	private final String MASTER_UID = "94yAGJfk2iu";
	private final String SERVO_UID = "ayQskEZifNn";
	private final String DUAL_RELAY_UID = "bV3";
	
	private BrickMaster masterBrick;
	private BrickServo servoBrick;
	private BrickletDualRelay dualRelais;
	
	private IPConnection ipConnection;
	private Logger logger = Logger.getLogger(Connection.class.getName());
	
	private Connection(){
		initializeHardware();
	}
	
	public synchronized static Connection getInstance(){
		if(instance == null){
			instance = new Connection();
		}
		return instance;
	}
	
	private void initializeHardware(){
		try{
			ipConnection = new IPConnection();
			masterBrick = new BrickMaster(MASTER_UID, ipConnection);
			servoBrick = new BrickServo(SERVO_UID, ipConnection);
			dualRelais = new BrickletDualRelay(DUAL_RELAY_UID, ipConnection);
			ipConnection.connect(HOST, PORT);
			
			// configure bricks and bricklets
			// set the Power Mode of WIFI to "Low Power"
			masterBrick.setWifiPowerMode((short)1);
		}
		catch(Exception e){
			logger.log(Level.SEVERE, "Could not initialize Conection: ", e);
			return;
		}
	}
	
	public BrickMaster getMasterBrick(){
		return masterBrick;
	}
	
	public BrickServo getServoBrick(){
		return servoBrick;
	}
	
	public BrickletDualRelay getDualRelaisBricklet(){
		return dualRelais;
	}
	
	public void destroyConnection(){
		try {
			instance = null;
			ipConnection.disconnect();
		} catch (NotConnectedException e) {
			// do nothing, we do not have a connection
		}
	}
	
	public int getStackVoltage(){
		try {
			return masterBrick.getStackVoltage();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Could not retrieve stack voltage of master brick: ", e);
			return -1;
		}
	}
}
