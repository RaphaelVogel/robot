package core;

import handler.StackHandler;

import java.util.logging.Logger;

import com.tinkerforge.BrickletDualRelay;

/**
 * This class starts the ESC and the camera using the monoflop relais 
 * during initialization of the robot.
 * Every 2 seconds a request is send to renew the monoflop. If it 
 * fails an communication error is assumed and the relais automatically
 * switches off ESC and camera
 */
public class MonoflopStarter extends Thread {
	private Logger logger = Logger.getLogger(MonoflopStarter.class.getName());
	private BrickletDualRelay relais = StackHandler.getDualRelaisBricklet();
	
	public boolean shouldRun = true;
	
	public void run(){
		while(shouldRun){
			try {
				relais.setMonoflop((short)1, true, 3000);
				relais.setMonoflop((short)2, true, 3000);
				MonoflopStarter.sleep(2000);
			} catch (Exception e) {
				logger.severe("Monoflop calls failed");
			}
		}
	}

}
