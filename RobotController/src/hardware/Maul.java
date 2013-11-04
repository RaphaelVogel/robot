package hardware;

import com.tinkerforge.BrickMaster;
import com.tinkerforge.BrickletDistanceIR;
import com.tinkerforge.BrickletDistanceIR.DistanceReachedListener;
import com.tinkerforge.IPConnection;
import com.tinkerforge.IPConnection.ConnectedListener;
import com.tinkerforge.IPConnection.EnumerateListener;
import com.tinkerforge.NotConnectedException;

public class Maul implements EnumerateListener, ConnectedListener, DistanceReachedListener{
    private final String host = "charger";
    private final int port = 4223;
    private IPConnection ipcon;
    private BrickMaster master;
    private BrickletDistanceIR ir;
    private short threshold;
    private boolean thresholdSet;
    
    public void initialize() {
    	ipcon = new IPConnection();
        while(true) {
            try {
                // try to connect
            	ipcon.connect(host, port);
                break;
            } catch(Exception e) {
            	e.printStackTrace();
            }
            try {
                Thread.sleep(2000);
            } catch(InterruptedException ei) {
            }
        }
        
        ipcon.addEnumerateListener(this);
        ipcon.addConnectedListener(this);
        
        while(true) {
            try {
            	// try to configure hardware
                ipcon.enumerate();
                break;
            } catch(NotConnectedException e) {
            	e.printStackTrace();
            }
            try {
                Thread.sleep(2000);
            } catch(InterruptedException ei) {
            }
        }
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
				master = new BrickMaster(uid, ipcon);
				try {
					master.setWifiPowerMode(BrickMaster.WIFI_POWER_MODE_LOW_POWER);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			// configure IR
			if(deviceIdentifier == BrickletDistanceIR.DEVICE_IDENTIFIER){
				ir = new BrickletDistanceIR(uid, ipcon);
				try {
				ir.setDebouncePeriod(1000);
		        ir.addDistanceReachedListener(this);
		        if(thresholdSet){
		        	ir.setDistanceCallbackThreshold('<', (short)(threshold), (short)0);
		        }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}    
    
	@Override
	public void distanceReached(int distance) {
		if(thresholdSet)
			System.out.println("Threshold reached; was "+threshold+ "; new distance: "+distance);
	}
    
    
    public IPConnection getIPConnection(){
    	return ipcon;
    }
    public BrickletDistanceIR getIR(){
    	return ir;
    }
    public void setTreshhold(short threshold){
    	this.threshold = threshold;
    	this.thresholdSet = true;
    }
    
    
    public static void main(String[] args) {
    	final Maul maul = new Maul();
		maul.initialize();
		System.out.println("Hardware initialized.....");
		try {
			int currentDistance = maul.getIR().getDistance();
			System.out.println("Current distance: "+ currentDistance+ " mm");
			int threshold = currentDistance - 10;
			System.out.println("Setting treshhold to: "+threshold + " mm");
			maul.getIR().setDistanceCallbackThreshold('<', (short)(threshold), (short)0);
			maul.setTreshhold((short)threshold);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		Runtime.getRuntime().addShutdownHook(new Thread(){
			public void run(){
				try {
					maul.getIPConnection().disconnect();
				} catch (NotConnectedException e) {
					e.printStackTrace();
				}
			}
		});
    }
}
