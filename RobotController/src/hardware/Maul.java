package hardware;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import com.tinkerforge.BrickMaster;
import com.tinkerforge.BrickletDistanceIR;
import com.tinkerforge.BrickletDistanceIR.DistanceReachedListener;
import com.tinkerforge.IPConnection;
import com.tinkerforge.IPConnection.ConnectedListener;
import com.tinkerforge.NotConnectedException;

public class Maul implements ConnectedListener, DistanceReachedListener{
    private final String host = "charger";
    private final int port = 4223;
    private IPConnection ipcon;
    private BrickMaster master;
    private BrickletDistanceIR ir;
    private short threshold;
    private boolean thresholdSet = false;
    
    public void initialize() {
    	ipcon = new IPConnection();
    	ipcon.addConnectedListener(this);
        while(true) {
            try {
                // try to connect
            	ipcon.connect(host, port);
                break;
            } catch(Exception e) {
            	e.printStackTrace();
            }
            try {
                Thread.sleep(1000);
            } catch(InterruptedException ei) {
            }
        }
	}

    
	@Override
	public void connected(short connectReason) {
		master = new BrickMaster("6esCZX", ipcon);
		ir = new BrickletDistanceIR("cXb", ipcon);
		try {
			master.setWifiPowerMode(BrickMaster.WIFI_POWER_MODE_LOW_POWER);
			if(thresholdSet){
				initDistanceIR(threshold);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Connected to Master....");
	}

    
	@Override
	public void distanceReached(int distance) {
		System.out.println("Threshold reached; was "+threshold+ "; new distance: "+distance);
	    try
	    {
	        Clip clip = AudioSystem.getClip();
	        clip.open(AudioSystem.getAudioInputStream(new File("A3.WAV")));
	        clip.start();
	    }
	    catch (Exception exc)
	    {
	        exc.printStackTrace(System.out);
	    }
	    
	    
	}
    
    
    public IPConnection getIPConnection(){
    	return ipcon;
    }
    
    public BrickletDistanceIR getIR(){
    	return ir;
    }
    
    public void initDistanceIR(short threshold){
    	this.threshold = threshold;
    	this.thresholdSet = true;
    	try {
    		ir.addDistanceReachedListener(this);
    		ir.setDebouncePeriod(3000);
			ir.setDistanceCallbackThreshold('<', (short)(threshold), (short)0);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    
    
    public static void main(String[] args) {
    	final Maul maul = new Maul();
		maul.initialize();
		try {
			Thread.sleep(2000);
			System.out.println("Hardware initialized.....");
			int currentDistance = maul.getIR().getDistance();
			
			System.out.println("Current distance: "+ currentDistance+ " mm");
			int threshold = currentDistance - 10;
			System.out.println("Setting treshhold to: "+threshold + " mm");
			maul.initDistanceIR((short)threshold);
			
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
			bufferedReader.readLine();
			
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
