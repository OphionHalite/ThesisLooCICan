package canfilter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Random;

import looci.osgi.serv.components.Event;
import looci.osgi.serv.impl.LoociComponent;
import looci.osgi.serv.impl.PayloadBuilder;
import looci.osgi.serv.util.Utils;

public class CanReceiver {
	
	private CanFilterComp cancomp;
	private boolean filterRunning;
	private byte[] data;
	
	private FilterData filterData;
	private String fileName = System.getProperty("user.dir") + "/filters.ser";
	
	private Random rand;
	
	public CanReceiver(CanFilterComp comp) {
		this.cancomp = comp;
		this.filterRunning = false;
		data = new byte[] {0,1,2,3,4,5,6,7};
		rand = new Random();
		filterData = new FilterData();
		
		//Uncomment the following lines for testing purposes only
		//int filltop = 10;
		//for(int i=0; i<filltop; i++) {
		//	filterData.add(new Integer(i));
		//}
	}
	
	public void addFilter(Integer filter){
		filterData.add(filter);
	}
	
	public void setFilterOn() {
		this.filterRunning = true;
	}

	public void setFilterOff() {
		this.filterRunning = false;
	}
	
	public void deleteFilter(Integer filter){
		filterData.delete(filter);
	}
	
	public void clearFilters(){
		filterData.clear();
	}
	
	public void stop(){
		/*
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
		        try {
					oos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    } 
		}*/
	}

	public int checkFilter(){
		int id = rand.nextInt(100);
		boolean isRunning = false;
		if(isRunning){ //filterrunning
			//System.out.println("[CAN receiver] Filtering on...");
		    if(filterData.findFilter(id)){
		    	return -1;
		    } else {
		    	return id;
		    }
		}else{
			//System.out.println("[CAN receiver] Filtering off...");
				return id;
		}
	}

	public byte[] getData() {
		return data;
	}
}
