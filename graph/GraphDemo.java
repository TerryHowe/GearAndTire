import java.net.*;
import java.io.*;
import java.util.*;
import java.lang.*;
import java.awt.* ;
import java.applet.*;
import csiro.graph.*;

/**
 *
 * Demo of the CSIRO graph class.
 *
 * @version 	Beta 2.0 6 April 1996
 * @author	<a href=mailto:kent.fitch@its.csiro.au>Kent Fitch</a>, Project Computing
 *		Pty Ltd under contract to ITS, <a href=http://www.csiro.au>CSIRO</a>,
 *		Canberra, Australia.  Copyright CSIRO.
 *
*/
public class GraphDemo extends Applet implements GraphEvent,  ScrollerEvents {

	private final int MAXPOINTS = 7 ;	// # of points on a bar/line graph at a time

	private final String PointNames[] = {"Widgets", "Dooberries", "P Rings", "Other"} ;
	private final Color ColorList[] = {Color.blue, Color.green, Color.yellow, 
		Color.cyan, Color.pink, Color.magenta, Color.red, Color.orange, Color.white,
		Color.darkGray, Color.black, Color.lightGray} ;

	private boolean StopNow ;
	private BarGraph bg ;
	private LineGraph lg ;
	private int bgperiod, lgperiod ;
	private Button bb, lb, eb ;
	private Label ss ;
	private Vector graphs = null ;

	private Thread mythread ;
	private Ticker ticker = null ;

	public String getAppletInfo() {
		return "Kent Fitch, CSIRO, copyright 1996" ;
	}

	public  void init() {

		// initialize control variables

		graphs = new Vector() ;
		StopNow = false ;
		bg = null ;
		lg = null ;

		// create the Applet buttons, status bar

		setLayout(new GridLayout(0,1));		// very elegant...
		bb = new Button(" Bar Graph ") ;
		lb = new Button(" Line Graph ") ;
		eb = new Button(" Exit ") ;
		ss = new Label("Initializing GraphDemo....", Label.LEFT) ;
		add(bb) ; add(lb) ; add(eb) ;
		add(ss) ;
		resize(200,200) ;
		show() ;
			
	}

	public void start() {

		showstatus("Starting...") ;
		repaint() ;
		StopNow = false ;
		Cleanup() ;
		bb.enable() ; lb.enable() ; eb.enable() ;
	
		showstatus("Ready.") ;
		repaint() ;

		mythread = Thread.currentThread() ;	

		// kick off a thread to make us up every 10 secs - we use this to
		// add some new random data to any bar or line graph on the screen
	
		ticker = new Ticker(mythread, 10000) ;  // 10 sec ticker..
		ticker.start() ;

		while(!StopNow) {	
						
			if ((bg != null) || (lg != null)) {
		
				// more random data for the bar/line graph

			   try {			
				if (bg != null) {
					int d[] = new int[4] ;
					d[0] = (int) (Math.random() * 20 + 3) ;
					d[1] = (int) (Math.random() * 10 + 2) ;
 					d[2] = (int) (Math.random() * 5 + 1) ;
					d[3] = (int) (Math.random() * 2) ;

					bg.addDataPoints(new DataPoint(d,"Month " + bgperiod , d)) ;
					bgperiod++ ;	   
	  			 	bg.draw() ;	
	  			 	bg.repaint() ;
				}
				if (lg != null) {
					int d[] = new int[4] ;
					d[0] = (int) (Math.random() * 20 + 3) ;
					d[1] = (int) (Math.random() * 10 + 2) ;
 					d[2] = (int) (Math.random() * 5 + 1) ;
					d[3] = (int) (Math.random() * 2) ;

					lg.addDataPoints(new DataPoint(d,"Month " + lgperiod , d)) ;
	   				lgperiod++ ;
	  			 	lg.draw() ;	
	  			 	lg.repaint() ;
				}
				
			  }
			  catch (Exception e) {} 
			}

			mythread.suspend() ;	// we are woken by ticker, or maybe a user event		
	
		}
		showstatus("Expunging all graphs...") ;
		
		bb.disable() ; lb.disable() ; eb.disable(); repaint() ;
		Cleanup() ;

		showstatus("Stopped...") ;

		hide() ;
	}

	public void stop() {

		StopNow = true ;  // why dont I mythread.resume() here ?
	}

	void Cleanup() {

		if (ticker != null) { 	
			ticker.stop() ;		// stop the heartbeat
			ticker = null ;
		}

		for (int i=0;i<graphs.size();i++) {	// expunge any graphs
			Frame f = (Frame) graphs.elementAt(i) ;
			try {
				f.dispose() ;
			}
			catch (Exception e) {} 

		}
		graphs = new Vector() ;

	}

	public boolean handleEvent (Event e) {

		if (" Bar Graph ".equals(e.arg)) {
		       	bg = CreateNewBarGraph() ;
			return true ;
		}
		else if (" Line Graph ".equals(e.arg)) {
			lg = CreateNewLineGraph() ;
			return true ;
		}
		else if ((" Exit ".equals(e.arg)) | (e.id == Event.WINDOW_DESTROY) ) {
			StopNow = true ;
     			mythread.resume() ;
			return true ;

		}
		return false ;
		
	}
	
	public BarGraph CreateNewBarGraph() {

	    showstatus("Creating new BarGraph...") ;

	    BarGraph bg = new BarGraph("Sales of Things", this) ;
	    bg.setMaxDataSets(MAXPOINTS) ;
	    bg.defineSets(PointNames.length) ;	// how many data categories

	    for (int i=0;i<PointNames.length;i++)  
			bg.addSet(i,PointNames[i], ColorList[i]) ;  // used to build legend

	    bg.setYlegend("units") ;   

	    for (bgperiod=1;bgperiod<6;bgperiod++) {	// show 5 sets initially...

		int d[] = new int[4] ;

		d[0] = (int) (Math.random() * 20 + 3) ;
		d[1] = (int) (Math.random() * 10 + 2) ;
 		d[2] = (int) (Math.random() * 5 + 1) ;
		d[3] = (int) (Math.random() * 2) ;

		String t = "Month " + bgperiod ;

		bg.addDataPoints(new DataPoint(d, t, d)) ;
	   }

   	   GraphFrame gf = createFrame("Demo Bar Graph") ;	// create the GraphFrame to hold the GraphCanvas
	   gf.AddGraphCanvas(bg) ;

 		// just for a demo, change the menu!

	   MenuBar mb = new MenuBar();

	   Menu fm = new Menu("File");
           fm.add("Exit");
	   mb.add(fm) ;

	   Menu hm = new Menu("Help") ;
	   hm.add("About...") ;
	   hm.add("How to click") ;	// this is the new thing I want - pity I have to rebuild the whole menu!
	   mb.add(hm) ;
	   mb.setHelpMenu(hm);
	
	   gf.setMenuBar(mb) ;

       	   gf.show() ;
	   gf.resize(600,290) ;
	   bg.draw() ;			// tell the Graph to recalculate the picture
	   bg.show() ;			// make sure it is showing

	   bg.repaint() ;		// and redraw

	   showstatus("BarGraph created.") ;
	   return bg ;
	 }	

	public LineGraph CreateNewLineGraph() {

	    showstatus("Creating new LineGraph...") ;
	    LineGraph lg = new LineGraph("Sales of Things", this) ;
	    lg.setMaxDataSets(MAXPOINTS) ;
	    lg.defineSets(PointNames.length) ;	// how many data categories

	    for (int i=0;i<PointNames.length;i++) 
			lg.addSet(i,PointNames[i], ColorList[i]) ;

	    lg.setYlegend("units") ;
	    
	    for (lgperiod=1;lgperiod<6;lgperiod++) {	// show 5 sets initially...

		int d[] = new int[4] ;

		d[0] = (int) (Math.random() * 20 + 3) ;
		d[1] = (int) (Math.random() * 10 + 2) ;
 		d[2] = (int) (Math.random() * 5 + 1) ;
		d[3] = (int) (Math.random() * 2) ;

		String t = "Month " + lgperiod ;

		lg.addDataPoints(new DataPoint(d, t, d)) ;
	   }
 
           GraphFrame gf = createFrame("Demo Line Graph") ;	// create the GraphFrame to hold the GraphCanvas
   	   gf.AddGraphCanvas(lg) ;
   	   gf.show() ;
   	   gf.resize(580,300) ;
	   lg.draw() ;	
	   lg.show() ;
	   lg.repaint() ;

	   showstatus("LineGraph created.") ;
	   return lg ;
	 }	


	public GraphFrame createFrame(String title) {		// creates a Frame for our Graph

		GraphFrame gf ;

		gf = new GraphFrame(title, this) ;

		graphs.addElement(gf) ;				// we remember all the Frames so that we
								// can clean them up at the end
		return gf ;
	}
	

	public void graphSingleLeft(Point p, Object set, int point, Component caller) {

		// user has single left clicked

		if (caller instanceof BarGraph) {
			int d[] = (int[]) set ;		// the array of points originally passed
  		        showstatus("Creating new PieGraph...") ;

			PieGraph pg = new PieGraph(PointNames[point] + " drill down", this) ;

       			GraphFrame gf = createFrame("Demo Pie") ; // create the GraphFrame to hold the GraphCanvas
			gf.AddGraphCanvas(pg) ;
		
			initDrilldownPie(pg, d, point) ;
			gf.show() ;
			gf.resize(300,250) ;
			pg.draw() ;
			pg.show() ;
			pg.repaint() ;
			showstatus("PieGraph created.") ;
		}
		else {
			showstatus("left click ignored") ;
		}
	}
	
	public void graphSingleRight(Point p, Object set, int point, Component caller) {

		// user has single right clicked

		String s[] = setupPopupInfo(set, point, caller) ;

		PopupInfo popup = new PopupInfo(p, s);
		GraphCanvas c = (GraphCanvas) caller ;
		c.setPopup(popup) ;
		c.draw() ;
		c.repaint() ;

	}

	public void graphSingleRightShift(Point p, Object set, int point, Component caller) {
	}

	public void graphDoubleRight(Point p, Object set, int point, Component caller) {
	}

	public void  graphSingleLeftShift(Point p, Object set, int point, Component caller) {

		// user has SHIFT+single left clicked

		if (caller instanceof BarGraph) {

			int d[] = (int[]) set ;
			showstatus("Creating new Scroller...") ;							
			String s[][] = GenTable(d) ;		// create the data for the table body!	
			String h[] = new String[5] ;		// some old rubbish for column headings
			h[0] = "Customer" ;       h[1] = "Units" ;
			h[2] = "Backlogged" ;	  h[3] = "Contact Name" ;
			h[4] = "Golf Handicap" ; 
			
			Scroller sc = new Scroller("Sales details", h, s, this) ;

			sc.SetHeadingAlignment(Scroller.ALL, Scroller.CENTER) ;  // all headings will be centered
				
			sc.SetColumnAlignment(Scroller.ALL, Scroller.RIGHT) ;    // default data cols to right aligned
			sc.SetColumnAlignment(0, Scroller.LEFT) ;		 // make some exceptions...
			sc.SetColumnAlignment(3, Scroller.CENTER) ;

			sc.show() ;
			showstatus("Scroller created.") ;
  		        graphs.addElement(sc) ;	

		}
	}

	public String[] setupPopupInfo(Object set, int point, Object caller) {

		// called from the single right click method to generate some rext for the popup

		if (caller instanceof BarGraph) {
			int d[] = (int[]) set ;
			String s[] = new String[3] ;
			
			s[0] = "Sample Popup" ;
			s[1] = "Point:" + PointNames[point] ;
			int total = 0 ;
			for (int i=0;i<d.length;i++) total = total + d[i] ;
			s[2] = "This Point: " + d[point] + " out of Total: " + total ;
			

			return s ;
		}

		if (caller instanceof PieGraph) {
			String names[] = {"Regular", "Family", "Large", "Economy", "King Size" } ;
			String s[] = new String[2] ;
			int d[] = (int[]) set ;
			
			s[0] = "You clicked in " + names[point] ;
			s[1] = "Value: " + d[point] ;

			return s ;
		}
		return null ;
	}

	public void graphShutDown(Component caller) {

		// called when the graph is closed by the user

		showstatus("Graph closed.") ;
		if (caller instanceof BarGraph) {
			if (bg == (BarGraph) caller) bg = null ;
		}
		else if (caller instanceof LineGraph) {
			if (lg == (LineGraph) caller) lg = null ;
		}
	}

	public boolean graphMenu(Event e, Component caller) {

		// called when the user clicks on a menu item not recognized by the
		// graph class (ie, something we added to the menu bar)

		if ("How to click".equals(e.arg)) {

			String s[] = new String[6] ;
			
			s[0] = "Demo Menu response in a popup..." ;
			s[1] = "" ;
			s[2] = "Simple Instructions:" ;
			s[3] = "  left click for drill down pie" ;
			s[4] = "  right click for properties of a bar element" ;
			s[5] = "  shift+left click for scrolling grid" ;
		
			PopupInfo popup = new PopupInfo(new Point(10,10), s);
			GraphCanvas c = (GraphCanvas) caller ;
			c.setPopup(popup) ;
			c.draw() ;
			c.repaint() ;

		}
		return false ;
	}

	public boolean scrollerMenu(Event e, Scroller caller) {
		
		return false ;
	}

	public void scrollerSingleLeft(int row, int col, Object caller) {
		showstatus("scroller click row:"+row+", col="+col) ;
	}
	
	public void scrollerShutDown(Object caller) {
		showstatus("Scroller closed.") ;	
	}

	public void initDrilldownPie(PieGraph pg, int[] d, int point) {
	
		// this method just has all the tedious logic for generating pie graph data

		int total = d[point] ;

		pg.setCaption("Break down of "+ PointNames[point]+ " - total of " + total + " units") ;

		String names[] = {"Regular", "Family", "Large", "Economy", "King Size" } ;
		int count ;
		if (total < 2) count = 1 ;
		else if (total < 3) count = 2 ;
		else if (total < 9) count = 3 ;
		else count = 5 ; 
		 
		pg.defineSets(count) ;	// how many data categories
		int j = 0 ;
	        
    		for (int i=0;i<count;i++) 
			pg.addSet(i, names[i], ColorList[i]) ;
		

		int nd[] = new int[count] ;
		for (int i=0;i<count;i++) {
			if (i == (count - 1)) nd[i] = total ;
			else {
				nd[i] = total / 2 ;
				total = total - nd[i] ;
			}
		}
	
		pg.addDataPoints(new DataPoint(nd, "umm..", nd) ) ;
		
	}


	public String[][] GenTable(int[] d) {

		// this method just generates a lot of old garbage for the scroller data

		String Customers[] = {"Acme Freight", "Project Computing", "Micro Bannana", 
				      "Osso's", "Belconnen Fish Market"} ;
		String ContactNames[] = {"Roland and Sonia", "Harry Feroka", "Jim someone",
				      "Jack Osso", "umm..."} ;
		String GolfHandicap[] = {"32", "85", "12", "d.n.p", "mullet"} ;

		int count ;		
		int total = 0 ;
		for (int i=0;i<d.length;i++) total = total + d[i] ;

		if (total < 2) count = 1 ;
		else if (total < 3) count = 2 ;
		else if (total < 9) count = 3 ;
		else count = 5 ; 
		 
			
		String t[][] = new String[count][5] ;
		int units ;

		for (int i=0;i<count;i++) {
			if (i == (count - 1)) units = total ;
			else {
				units = total / 2 ;
				total = total - units ;
			}
			t[i][0] = Customers[i] ;
			t[i][1] = "" + units ;
			t[i][2] = "" + (units / 2) ;
			t[i][3] = ContactNames[i] ;
			t[i][4] = GolfHandicap[i] ; 
		}

		return t ;
	
	}


	public void showstatus(String m) {
		debug.print("showstatus:"+m) ;  // give to the java console as well...
		ss.setText(m) ;
		repaint() ;
	}

}

//  The Ticker class just hangs around, going to sleep and resuming the specified thread
//  at the specified interval.

class Ticker extends Thread {

	Thread owner ;
	int sleeptime ;

	public Ticker(Thread o, int s) {

		owner = o ;
		sleeptime = s ;
	}


	public void run() {

		while(true) {
			try {
				sleep(sleeptime) ;
			}
			catch (Exception e) {
			} 
			owner.resume() ;
		}
	}
}
		
