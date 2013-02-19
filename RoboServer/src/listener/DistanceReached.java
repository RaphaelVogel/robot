package listener;

import handler.DriveHandler;

import java.util.logging.Logger;

import com.tinkerforge.BrickletDistanceIR;

public class DistanceReached implements
		BrickletDistanceIR.DistanceReachedListener {
	private Logger logger = Logger.getLogger(DistanceReached.class.getName());

	@Override
	public void distanceReached(int distance) {
		DriveHandler driveHandler = new DriveHandler();
		try {
			driveHandler.stop();
		} catch (Exception e) {
			logger.severe("Distance reached callback triggered, but could not stop robot");
		}
	}
}
