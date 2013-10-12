package hardware;

import com.tinkerforge.BrickMaster;
import com.tinkerforge.BrickServo;
import com.tinkerforge.BrickletDualRelay;
import com.tinkerforge.BrickletIndustrialQuadRelay;
import com.tinkerforge.IPConnection;
import com.tinkerforge.IPConnection.ConnectedListener;
import com.tinkerforge.IPConnection.EnumerateListener;
import com.tinkerforge.NotConnectedException;

import hardware.Constants;

public class HardwareManager implements EnumerateListener, ConnectedListener {
	private IPConnection ipcon;
	private static BrickMaster master;
	private static BrickServo servo;
	private static BrickletIndustrialQuadRelay quadRelais;
	private static BrickletDualRelay dualRelais;

	public HardwareManager(IPConnection ipcon) {
		this.ipcon = ipcon;
	}

	@Override
	public void connected(short connectReason) {
		if (connectReason == IPConnection.CONNECT_REASON_AUTO_RECONNECT) {
			// connection was lost but reconnect was possible -> reconfigure the bricks/bricklets
			while (true) {
				try {
					ipcon.enumerate();
					break;
				} catch (NotConnectedException e) {
					System.out.println("");
				}

				try {
					Thread.sleep(2000);
				} catch (InterruptedException ei) {
				}
			}
		}
	}

	@Override
	public void enumerate(String uid, String connectedUid, char position,
			short[] hardwareVersion, short[] firmwareVersion,
			int deviceIdentifier, short enumerationType) {

		// configure bricks on first connect (ENUMERATION_TYPE_CONNECTED) 
		// or configure bricks if enumerate was raised externally (ENUMERATION_TYPE_AVAILABLE)
		if (enumerationType == IPConnection.ENUMERATION_TYPE_CONNECTED
				|| enumerationType == IPConnection.ENUMERATION_TYPE_AVAILABLE) {
			
			// configure master brick
			if (deviceIdentifier == BrickMaster.DEVICE_IDENTIFIER) {
				master = new BrickMaster(connectedUid, ipcon);
				try {
					master.setWifiPowerMode(BrickMaster.WIFI_POWER_MODE_LOW_POWER);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			// configure servo brick
			if(deviceIdentifier == BrickServo.DEVICE_IDENTIFIER){
				servo = new BrickServo(connectedUid, ipcon);
				try {
					// configure the drive servos (ESC) 
					servo.setAcceleration(Constants.servo0And1, 50000);
					// configure the camera servos
					servo.setVelocity(Constants.servo2And3, 20000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		}
	}
	
	public static BrickMaster getMasterBrick(){
		return master;
	}
	public static BrickServo getServoBrick(){
		return servo;
	}
	public static BrickletIndustrialQuadRelay getQuadRelais(){
		return quadRelais;
	}
	public static BrickletDualRelay getDualRelais(){
		return dualRelais;
	}
}
