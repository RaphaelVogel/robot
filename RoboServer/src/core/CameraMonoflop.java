package core;

import handler.StackHandler;

import java.util.logging.Logger;

import com.tinkerforge.BrickletIndustrialQuadRelay;

/**
 * This class starts the camera using the monoflop relais 
 * during initialization of the robot.
 * Every 2 seconds a request is send to renew the monoflop. If it 
 * fails an communication error is assumed and the relais automatically
 * switches off the camera
 */
public class CameraMonoflop extends Thread {
	private Logger logger = Logger.getLogger(CameraMonoflop.class.getName());
	
	private boolean shouldRun = true;
	
	public void stopCameraMonoflop() throws Exception{
		shouldRun = false;
		Thread.sleep(100); // after that someone can safely set the relais again
	}
	
	public void run(){
		BrickletIndustrialQuadRelay relais = StackHandler.getQuadRelaisBricklet();
		while(shouldRun){
			try {
				relais.setMonoflop((1 << 2), (1 << 2), 3000);
				CameraMonoflop.sleep(2000);
			} catch (Exception e) {
				logger.severe("Camera Monoflop call failed");
			}
		}
	}

}
