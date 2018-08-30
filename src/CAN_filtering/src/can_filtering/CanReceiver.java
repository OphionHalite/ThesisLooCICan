package can_filtering;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import looci.osgi.serv.components.Event;
import looci.osgi.serv.impl.LoociComponent;
import looci.osgi.serv.impl.PayloadBuilder;
import looci.osgi.serv.util.Utils;

import de.entropia.can.CanSocket;
import de.entropia.can.CanSocket.Mode;
import de.entropia.can.CanSocket.CanFrame;
import de.entropia.can.CanSocket.CanId;
import de.entropia.can.CanSocket.CanInterface;;

public class CanReceiver {
	private CanSocket can;
	private CanInterface canif;
	private CanFilterComp cancomp;
	private Thread canthread;
	private boolean isrunningcanthread;
	private boolean filterRunning;
	private CanFrame frame;
	private FilterData filterData;
	private String fileName = System.getProperty("user.dir") + "/filters.ser";
	
	public CanReceiver(CanFilterComp comp) {
		this.cancomp = comp;
		this.filterRunning = false;

		FileInputStream streamIn;
		try {
			streamIn = new FileInputStream(fileName);
		} catch (FileNotFoundException e1) {;
			// Use relative path for Unix systems
			File f = new File(fileName);
			f.getParentFile().mkdirs(); 
			try {
				f.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			filterData = new FilterData();
			ObjectOutputStream oos = null;
			FileOutputStream fout = null;
			try{
			    fout = new FileOutputStream(fileName, true);
			    oos = new ObjectOutputStream(fout);
			    oos.writeObject(filterData);
			} catch (Exception ex) {
			    ex.printStackTrace();
			} finally {
			    if(oos != null){
			        try {
						oos.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			    } 
			}
			streamIn = null;
			try {
				streamIn = new FileInputStream(fileName);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//e1.printStackTrace();
		}
	    ObjectInputStream ois = null;
	    try {
	    	ois = new ObjectInputStream(streamIn);
			filterData = (FilterData) ois.readObject();
			ois.close();
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.canthread = new Thread(new Runnable() {
			public void run(){
				isrunningcanthread = true;
				while(isrunningcanthread){
					recvMessage();
				}
			}
		});
		
		try{
			can = new CanSocket(Mode.RAW);
			canif = new CanInterface(can,"can0");
			can.bind(canif);
			System.out.println("[CAN receiver] Setting up CAN interface...");
			System.out.println("MTU: " + can.getMtu("can0"));
			System.out.println("FD-MTU: " + can.CAN_FD_MTU);
			System.out.println("can interface: " + canif.getIfName());
			
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	
	public void addFilter(int filter){
		filterData.add(filter);
	}
	
	public void setFilterOn() {
		this.filterRunning = true;
	}

	public void setFilterOff() {
		this.filterRunning = false;
	}
	
	public void deleteFilter(int filter){
		filterData.delete(filter);
	}
	
	public void clearFilters(){
		filterData.clear();
	}
	
	public void start(){
		System.out.println("[CAN receiver] Listening to messages...");
		canthread.start();
	}
	
	public void stop() throws IOException{
		ObjectOutputStream oos = null;
		FileOutputStream fout = null;
		try{
		    fout = new FileOutputStream(fileName, false);
		    oos = new ObjectOutputStream(fout);
		    oos.writeObject(filterData);
		} catch (Exception ex) {
		    ex.printStackTrace();
		} finally {
		    if(oos != null){
		        oos.close();
		    } 
		}
		canthread.interrupt();
	}
		
	public void recvMessage(){
		try{
			frame = can.recv();
			if(filterRunning){
				System.out.println("[CAN receiver] Filtering on...");
			    if(!filterData.findFilter(frame.getCanId()._canId)){
			    	cancomp.sendCanMessage(frame);
			    }
			}else{
				System.out.println("[CAN receiver] Filtering off...");
				cancomp.sendCanMessage(frame);
			}
		} catch (IOException e){
			System.out.println(e);
		}
	}
}
