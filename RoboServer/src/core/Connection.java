package core;

import java.io.IOException;
import java.util.logging.Level;

import java.util.logging.Logger;
import com.tinkerforge.BrickMaster;
import com.tinkerforge.BrickServo;
import com.tinkerforge.IPConnection;
import com.tinkerforge.IPConnection.TimeoutException;

public class Connection {

	private static Connection instance;
	
	private final String HOST = "192.168.178.18";
	private final int PORT = 4223;
	private final String MASTER_UID = "94yAGJfk2iu";
	private final String SERVO_UID = "ayQskEZifNn";
	
	private BrickMaster masterBrick = new BrickMaster(MASTER_UID);
	private BrickServo servoBrick = new BrickServo(SERVO_UID);
	
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
			ipConnection = new IPConnection(HOST, PORT);
		}
		catch(IOException e){
			logger.log(Level.SEVERE, "Could not get IPConnection: ", e);
			return;
		}
		try{
			ipConnection.addDevice(masterBrick);
			ipConnection.addDevice(servoBrick);
			// set the Power Mode of WIFI to "Low Power"
			masterBrick.setWifiPowerMode((short)1);
		}
		catch(Exception e){
			logger.log(Level.SEVERE, "Initialization of Bricks failed: ", e);
			return;
		}
	}
	
	public BrickMaster getMasterBrick(){
		return masterBrick;
	}
	
	public BrickServo getServoBrick(){
		return servoBrick;
	}
	
	public void destroyConnection(){
		ipConnection.destroy();
	}
	
	public int getStackVoltage(){
		try {
			return masterBrick.getStackVoltage();
		} catch (TimeoutException e) {
			logger.log(Level.SEVERE, "Could not retrieve stack voltage of master brick: ", e);
			return -1;
		}
	}
}
