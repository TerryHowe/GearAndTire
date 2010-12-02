/*
** Author: Terry L. Howe,  New Jersey Institute of Technology
** txh3202@hertz.njit.edu
** Version : 17th/Jul./'96
*/
import java.awt.*;
import java.io.DataInputStream;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.MalformedURLException;


public class Gear extends java.applet.Applet {
	Tire		panelTire;
	Transmission	panelTransmission;
	TransferCase	panelTransferCase;
	Rnp		panelRnp;
	MphKph		panelMphKph;
	Panel		SuperRnp;
	Panel		SuperTransferCase;
	Panel		SuperMphKph;
	Panel		SuperButtons;
	Label		msgLabel;
	String		tireStr;
	double		valTire;
	double		valfirst;
	double		valsecond;
	double		valthird;
	double		valfourth;
	double		valfifth;
	double		valreverse;
	double		valTransferCase;
	double		valRnp;
	double		valUnits;
	boolean		problem;
	URL		cb;
	HelpWin		helpWin;
	TableWin	tableWin[];
	int		curTableWin;
	static final int		MAXOUTWIN = 2;
	static final int		MAXOUTWINX = 350;
	static final int		MAXOUTWINY = 300;
	public static final double	UNITS_MPH = 1.0;
	public static final double	UNITS_KPH = 1.609344;
	static final int		HELP_COUNT = 5;
	static final String		HELP_BUTTONS[] = {
						"main",
						"tire",
						"transmission",
						"transfercase",
						"ring and pinion"
					};
	static final String		HELP_FILES[] = {
						"main.txt",
						"tire.txt",
						"trans.txt",
						"xfer.txt",
						"rnp.txt"
					};
	public void init() {
		/*
		** Initialize output windows
		*/
		tableWin = new TableWin[MAXOUTWIN];
		tableWin[0] = new TableWin();
		tableWin[1] = new TableWin();
		curTableWin = 0;

		/*
		** Set initial location
		*/
		Point curPoint = location();
		if ((curPoint.x - TableWin.MAXOUTWINX) > 0)
			curPoint.x -= TableWin.MAXOUTWINX;
		else
			curPoint.x += size().width;
		tableWin[0].move(curPoint.x, curPoint.y);
		if ((curPoint.y - TableWin.MAXOUTWINY) > 0)
			curPoint.y -= TableWin.MAXOUTWINY;
		else
			curPoint.y += TableWin.MAXOUTWINY;
		tableWin[1].move(curPoint.x, curPoint.y);

		/*
		** Get code base
		** cb = getCodeBase();
		** cb = getDocumentBase();
		*/
		try { cb = new URL("file:c:/html/"); }
		catch (MalformedURLException e) { cb = null; }
		helpWin = new HelpWin(cb, HELP_BUTTONS, HELP_FILES, HELP_COUNT);

		/*
		** Initialize panels
		*/
		setLayout(new BorderLayout());
		SuperRnp = new Panel();
		SuperRnp.setLayout(new BorderLayout());
		SuperTransferCase = new Panel();
		SuperTransferCase.setLayout(new BorderLayout());
		SuperMphKph = new Panel();
		SuperMphKph.setLayout(new BorderLayout());

		/*
		** Tire Size
		*/
		panelTire = new Tire(this);
		SuperMphKph.add("North", panelTire);

		/*
		** MPH/KPH
		*/
		panelMphKph = new MphKph(this, MphKph.Smph);
		SuperMphKph.add("South", panelMphKph);

		/*
		** Transmission
		*/
		SuperTransferCase.add("North", new Label("Transmission", Label.CENTER));
		panelTransmission = new Transmission(this, "6.32", "3.50",
				"2.50", "1.00", "0.75", "7.32");
		SuperTransferCase.add("Center", panelTransmission);

		/*
		** Transfer Case
		*/
		panelTransferCase = new TransferCase(this, "2.62");
		SuperTransferCase.add("South", panelTransferCase);

		/*
		** R&P
		*/
		SuperRnp.add("Center", SuperTransferCase);
		panelRnp = new Rnp(this, "3.54");
		SuperRnp.add("South", panelRnp);
		SuperMphKph.add("Center", SuperRnp);
		add("North", SuperMphKph);

		/*
		** Submit and Help button
		*/
		SuperButtons = new Panel();
		SuperButtons.setLayout(new GridLayout(1, 2, 10, 10));
		SuperButtons.add(new Button("Calculate"));
		SuperButtons.add(new Button("Help"));
		add("Center", SuperButtons);

		/*
		** Add error label
		*/
		msgLabel = new Label("", Label.LEFT);
		add("South", msgLabel);
		panelTire.setfocus();
	}

	public boolean action(Event evt, Object arg) {
		if (evt.target instanceof Button) {
			if (arg.toString().equals("Help")) {
				handleHelp();
				return(true);
			}
			else if (arg.toString().equals("Calculate")) {
				handleCalculation();
				return(true);
			}
		}
		return(false);
	}

	public void printMsg(String errMsg) {
		msgLabel.setText(errMsg);
		show();
		return;
	}
	void update(Tire in) {
		try { valTire = in.getvalTire(); }
		catch (NewEx excep) {
			panelTire.setfocus();
			printMsg(excep.toString());
			problem = true;
			return;
		}
		panelTransmission.setfocus(Transmission.FIRST);
		tireStr = in.gettireStr();
		printMsg("");
		return;
	}

	void update(Transmission in) {
		int	savecurrent;

		savecurrent = Transmission.FIRST;
		try {
			valfirst = in.getvalfirst();
			savecurrent = Transmission.SECOND;
			valsecond = in.getvalsecond();
			savecurrent = Transmission.THIRD;
			valthird = in.getvalthird();
			savecurrent = Transmission.FOURTH;
			valfourth = in.getvalfourth();
			savecurrent = Transmission.FIFTH;
			valfifth = in.getvalfifth();
			savecurrent = Transmission.REVERSE;
			valreverse = in.getvalreverse();
		}
		catch (NewEx excep) {
			panelTransmission.setfocus(savecurrent);
			printMsg(excep.toString());
			problem = true;
			return;
		}
		if (panelTransmission.setfocus())
			panelTransferCase.setfocus();
		printMsg("");
		return;
	}
	void update(TransferCase in) {
		try {
			valTransferCase = in.getvalTransferCase();
		}
		catch (NewEx excep) {
			printMsg(excep.toString());
			problem = true;
			return;
		}
		panelRnp.setfocus();
		printMsg("");
		return;
	}
	void update(Rnp in) {
		try {
			valRnp = in.getvalRnp();
		}
		catch (NewEx excep) {
			printMsg(excep.toString());
			problem = true;
			return;
		}
		panelTire.setfocus();
		printMsg("");
		return;
	}
	void update(MphKph in) {
		valUnits = UNITS_MPH;
		if (in.mphkph.getCurrent().getLabel().equals(in.Skph))
			valUnits = UNITS_KPH;
		return;
	}

	void handleHelp() {
		helpWin.display();
		return;
	}

	void handleCalculation() {

		/*
		** Make sure we have all the data
		*/
		problem = false;
		update(panelTire); if (problem) return;
		update(panelTransmission); if (problem) return;
		update(panelTransferCase); if (problem) return;
		update(panelRnp); if (problem) return;
		update(panelMphKph); if (problem) return;

		/*
		** Print if all is well
		*/
		tableWin[curTableWin].print(valTire, valfirst, valsecond,
			valthird, valfourth, valfifth, valreverse,
			valTransferCase, valRnp, valUnits,
			tireStr);
		curTableWin = (curTableWin + 1) % MAXOUTWIN;
		
	}

	public String getAppletInfo() {
		return("Gear and Tire Form copyright 1996 Terry L. Howe");
	}
}

class NewEx extends Exception {
	String	sptr;
	NewEx(String errMsg) {
		super();
		sptr = new String(errMsg);
	}
	public String toString() {
		return(sptr);
	}
}

class TableWin extends Frame {
	Panel		outPanel;
	Panel		outerPanel;
	Panel		titlePanel;
	TextField	rnpField;
	GraphWin	gw;
	double		savevalTire;
	double		savevalfirst;
	double		savevalsecond;
	double		savevalthird;
	double		savevalfourth;
	double		savevalfifth;
	double		savevalreverse;
	double		savevalTransferCase;
	double		savevalRnp;
	double		savevalUnits;
	double		savevalRpm;
	String		savetireStr;
	public static final int		MAXOUTWINX = 350;
	public static final int		MAXOUTWINY = 400;
	static final double		INCHTOMILE = 1.578283e-05;
	static final double		MINTOHOUR = 60;

	TableWin() {
		/*
		** Initialize output windows
		*/
		super();
		outPanel = new Panel();
		outerPanel = new Panel();
		titlePanel = new Panel();
		resize(MAXOUTWINX, MAXOUTWINY);
		savevalRpm = 2500;
		gw = null;
	}

	public void print(double valTire, double valfirst, double valsecond,
			double valthird, double valfourth, double valfifth,
			double valreverse, double valTransferCase,
			double valRnp, double valUnits,
			String tireStr) {

		/*
		** Save values
		*/
		savevalTire = valTire;
		savevalfirst = valfirst;
		savevalsecond = valsecond;
		savevalthird = valthird;
		savevalfourth = valfourth;
		savevalfifth = valfifth;
		savevalreverse = valreverse;
		savevalTransferCase = valTransferCase;
		savevalRnp = valRnp;
		savevalUnits = valUnits;
		savetireStr = new String(tireStr);

		/*
		** Create output window
		*/
		this.removeAll();
		outPanel.removeAll();
		outerPanel.removeAll();
		titlePanel.removeAll();
		this.setLayout(new BorderLayout());
		outerPanel.setLayout(new BorderLayout());
		titlePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		rnpField = new TextField(Integer.toString((int)savevalRpm), 5);
		if (valUnits != Gear.UNITS_MPH) {
			this.setTitle("Gear and Tire Table: KPH");
			titlePanel.add(new Label("KPH @ "));
			titlePanel.add(rnpField);
			titlePanel.add(new Label(" RPM"));
		}
		else {
			this.setTitle("Gear and Tire Table: MPH");
			titlePanel.add(new Label("MPH @ "));
			titlePanel.add(rnpField);
			titlePanel.add(new Label(" RPM"));
		}
		this.add("North", titlePanel);
		outerPanel.add("North", new Label("with " + tireStr + " tires and " + valRnp + ":1 Ring and Pinion", Label.CENTER));
		outPanel.setLayout(new GridLayout(11, 4, 10, 10));

		/*
		** Do calculations
		*/
		double rear;
		double rearlow;
		rear = (valTire * Math.PI * INCHTOMILE) / valRnp;
		rear *= valUnits * savevalRpm * MINTOHOUR;
		rearlow = rear / valTransferCase;
		outPanel.add(new Label("", Label.LEFT));
		outPanel.add(new Label("", Label.LEFT));
		outPanel.add(new Label("", Label.LEFT));
		outPanel.add(new Label("", Label.LEFT));
		outPanel.add(new Label("", Label.LEFT));
		outPanel.add(new Label("", Label.LEFT));
		outPanel.add(new Label("High", Label.LEFT));
		outPanel.add(new Label("Low", Label.LEFT));
		outPanel.add(new Label("", Label.LEFT));
		outPanel.add(new Label("", Label.LEFT));
		outPanel.add(new Label(formatRatio(1.0)));
		outPanel.add(new Label(formatRatio(valTransferCase)));
		outPanel.add(new Label("First", Label.LEFT));
		outPanel.add(new Label(formatRatio(valfirst)));
		outPanel.add(new Label(Integer.toString((int)(rear / valfirst))));
		outPanel.add(new Label(Integer.toString((int)(rearlow / valfirst))));
		outPanel.add(new Label("Second", Label.LEFT));
		outPanel.add(new Label(formatRatio(valsecond)));
		outPanel.add(new Label(Integer.toString((int)(rear / valsecond))));
		outPanel.add(new Label(Integer.toString((int)(rearlow / valsecond))));
		outPanel.add(new Label("Third", Label.LEFT));
		outPanel.add(new Label(formatRatio(valthird)));
		outPanel.add(new Label(Integer.toString((int)(rear / valthird))));
		outPanel.add(new Label(Integer.toString((int)(rearlow / valthird))));
		outPanel.add(new Label("Fourth", Label.LEFT));
		outPanel.add(new Label(formatRatio(valfourth)));
		outPanel.add(new Label(Integer.toString((int)(rear / valfourth))));
		outPanel.add(new Label(Integer.toString((int)(rearlow / valfourth))));
		outPanel.add(new Label("Fifth", Label.LEFT));
		outPanel.add(new Label(formatRatio(valfifth)));
		outPanel.add(new Label(Integer.toString((int)(rear / valfifth))));
		outPanel.add(new Label(Integer.toString((int)(rearlow / valfifth))));
		outPanel.add(new Label("Reverse", Label.LEFT));
		outPanel.add(new Label(formatRatio(valreverse)));
		outPanel.add(new Label(Integer.toString((int)(rear / valreverse))));
		outPanel.add(new Label(Integer.toString((int)(rearlow / valreverse))));
		outPanel.add(new Label("", Label.LEFT));
		outPanel.add(new Label("", Label.LEFT));
		outPanel.add(new Label("", Label.LEFT));
		outPanel.add(new Label("", Label.LEFT));
		outPanel.add(new Label("Crawl Ratio", Label.LEFT));
		outPanel.add(new Label(Integer.toString((int)(valfirst * valTransferCase * valRnp)) + ":1"));
		outerPanel.add("Center", outPanel);
		if (valUnits != Gear.UNITS_MPH) {
			outerPanel.add("South", new Label(("* Your KPH may vary")));
		}
		else {
			outerPanel.add("South", new Label(("* Your MPH may vary")));
		}
		this.add("Center", outerPanel);
		this.show();
		rnpField.requestFocus();

		/*
		** See if there is a Graph
		*/
		if (gw != null)
			return;

		/*
		** Make Graph
		*/
		int	j;
		int	k;
		int	xlab = 0;
		int	ylab = 0;
		int	namesCnt = 5;
		int	valuesCnt = 18;
		double	curRat;
		double	curRpm;
		rear /= savevalRpm;
		rearlow /= savevalRpm;
		gw = new GraphWin(namesCnt, valuesCnt);
		if (valUnits != Gear.UNITS_MPH) {
			gw.setWindowTitle("Gear and Tire Graph: KPH");
			gw.setPageTitle("KPH with " + tireStr + " tires and " + valRnp + ":1 Ring and Pinion");
			for (ylab=0; ylab<140; ylab+=10) {
				gw.setYlabel(ylab);
			}
			gw.setYname("KPH");
		}
		else {
			gw.setWindowTitle("Gear and Tire Graph: MPH");
			gw.setPageTitle("MPH with " + tireStr + " tires and " + valRnp + ":1 Ring and Pinion");
			for (ylab=0; ylab<=80; ylab+=10) {
				gw.setYlabel(ylab);
			}
			gw.setYname("MPH");
		}
		gw.setLow(valTransferCase);
		for (j=0; j<namesCnt; j++) {
			switch (j) {
			case Transmission.FIRST:
				curRat = valfirst;
				gw.setNames("first");
				break;
			case Transmission.SECOND:
				curRat = valsecond;
				gw.setNames("second");
				break;
			case Transmission.THIRD:
				curRat = valthird;
				gw.setNames("third");
				break;
			case Transmission.FOURTH:
				curRat = valfourth;
				gw.setNames("fourth");
				break;
			case Transmission.FIFTH:
				curRat = valfifth;
				gw.setNames("fifth");
				break;
			case Transmission.REVERSE:
			default:
				curRat = valreverse;
				gw.setNames("reverse");
				break;
			}
			curRpm = 0.0;
			for (k=0; k<valuesCnt; k++) {
				gw.setValues((rear / curRat) * curRpm);
				curRpm += 250.0;
			}
		}
		xlab = 0;
		for (j=0; j<valuesCnt; j++) {
			gw.setXlabel(xlab);
			xlab += 250;
		}
		gw.draw();
		return;
	}

	String formatRatio(double rat) {
		String	ptr;
		int	j;
		rat += 0.0001;
		ptr = new String(Double.toString(rat));
		j = (ptr.length() < 4) ? ptr.length() : 4;
		return(ptr.substring(0, j));
	}

	public boolean handleEvent(Event evt) {
		if (evt.id == evt.WINDOW_DESTROY)
			hide();
		return(super.handleEvent(evt));
	}

	public boolean action(Event evt, Object arg) {
		if (evt.target instanceof TextField) {
			savevalRpm = Double.valueOf("0" + arg).doubleValue();
			print(savevalTire, savevalfirst, savevalsecond,
				savevalthird, savevalfourth, savevalfifth,
				savevalreverse, savevalTransferCase,
				savevalRnp, savevalUnits,
				savetireStr);
			return(true);
		}
		return(false);
	}
}

class RatioPanel extends Panel {

	double convertRatio(String prefix, String sptr) throws NewEx {
		String	subStr;
		int	strLen;
		int	deccnt;
		int	j;

		/*
		** Get important part
		*/
		strLen = (sptr.length() < 4) ? sptr.length() : 4;
		if (sptr.indexOf(':') > 0)
			strLen = (sptr.indexOf(':') < strLen) ? sptr.indexOf(':') : strLen;
		subStr = sptr.substring(0, strLen);

		/*
		** Check for 0 length
		*/
		if (strLen == 0)
			return(0.0);

		/*
		** Check for digits
		*/
		deccnt = 0;
		for (j=0; j<strLen; j++) {
			if (subStr.charAt(j) == '.') {
				if (deccnt == 0) {
					++deccnt;
					continue;
				}
				throw(new NewEx(prefix + ": More than one decimal place"));
			}
			if ((subStr.charAt(j) < '0') || (subStr.charAt(j) > '9')) {
				throw(new NewEx(prefix + ": Character " + (j + 1) + " is not a digit"));
			}
		}

		return(Double.valueOf(subStr).doubleValue());
	}
}

class Transmission extends RatioPanel {
	TextField	first;
	TextField	second;
	TextField	third;
	TextField	fourth;
	TextField	fifth;
	TextField	reverse;
	Panel		firstPanel;
	Panel		secondPanel;
	Panel		thirdPanel;
	Panel		fourthPanel;
	Panel		fifthPanel;
	Panel		reversePanel;
	Gear		outerparent;
	int		curField;
	public static final int	FIRST = 0;
	public static final int	SECOND = 1;
	public static final int	THIRD = 2;
	public static final int	FOURTH = 3;
	public static final int	FIFTH = 4;
	public static final int	REVERSE = 5;
	public static final String[]	gearString =
			{"1st", "2nd", "3rd", "4th", "5th", "Rev"};

	Transmission(Gear target, String ifirst, String isecond, String ithird, String ifourth, String ififth, String ireverse) {
		/*
		** Save parent
		*/
		curField = FIRST;
		this.outerparent = target;
		setLayout(new GridLayout(3, 2));

		/*
		** first
		*/
		firstPanel = new Panel();
		firstPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		first = new TextField(ifirst, 4);
		firstPanel.add(new Label(gearString[0], Label.RIGHT));
		firstPanel.add(first);
		add(firstPanel);

		/*
		** second
		*/
		secondPanel = new Panel();
		secondPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		second = new TextField(isecond, 4);
		secondPanel.add(new Label(gearString[1], Label.RIGHT));
		secondPanel.add(second);
		add(secondPanel);

		/*
		** third
		*/
		thirdPanel = new Panel();
		thirdPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		third = new TextField(ithird, 4);
		thirdPanel.add(new Label(gearString[2], Label.RIGHT));
		thirdPanel.add(third);
		add(thirdPanel);

		/*
		** fourth
		*/
		fourthPanel = new Panel();
		fourthPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		fourth = new TextField(ifourth, 4);
		fourthPanel.add(new Label(gearString[3], Label.RIGHT));
		fourthPanel.add(fourth);
		add(fourthPanel);

		/*
		** fifth
		*/
		fifthPanel = new Panel();
		fifthPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		fifth = new TextField(ififth, 4);
		fifthPanel.add(new Label(gearString[4], Label.RIGHT));
		fifthPanel.add(fifth);
		add(fifthPanel);

		/*
		** reverse
		*/
		reversePanel = new Panel();
		reversePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		reverse = new TextField(ireverse, 4);
		reversePanel.add(new Label(gearString[5], Label.RIGHT));
		reversePanel.add(reverse);
		add(reversePanel);
	}

	public double getvalfirst() throws NewEx {
		return(convertRatio("Transmission: first", first.getText()));
	}
	public double getvalsecond() throws NewEx {
		return(convertRatio("Transmission: second", second.getText()));
	}
	public double getvalthird() throws NewEx {
		return(convertRatio("Transmission: third", third.getText()));
	}
	public double getvalfourth() throws NewEx {
		return(convertRatio("Transmission: fourth", fourth.getText()));
	}
	public double getvalfifth() throws NewEx {
		return(convertRatio("Transmission: fifth", fifth.getText()));
	}
	public double getvalreverse() throws NewEx {
		return(convertRatio("Transmission: reverse", reverse.getText()));
	}

	public void setfocus(int index) {
		curField = index;
		setfocus();
	}

	public boolean setfocus() {
		switch (curField) {
		case FIRST:
			first.requestFocus();
			break;
		case SECOND:
			second.requestFocus();
			break;
		case THIRD:
			third.requestFocus();
			break;
		case FOURTH:
			fourth.requestFocus();
			break;
		case FIFTH:
			fifth.requestFocus();
			break;
		case REVERSE:
			reverse.requestFocus();
			break;
		}
		curField = (curField + 1) % 6;
		if (curField == SECOND)
			return(true);
		return(false);
	}

	public boolean action(Event evt, Object arg) {
		if (evt.target instanceof TextField) {
			outerparent.update(this);
			return(true);
		}
		return(false);
	}
}

class TransferCase extends RatioPanel {
	TextField	low;
	Gear		outerparent;

	TransferCase(Gear target, String ilow) {

		/*
		** Save parent
		*/
		this.outerparent = target;

		/*
		** Make transmission fields
		*/
		low = new TextField(ilow, 4);

		/*
		** Add 'em
		*/
		add(new Label("Transfer Case Low: ", Label.RIGHT));
		add(low);
	}

	public double getvalTransferCase() throws NewEx {
		return(convertRatio("Transfercase", low.getText()));
	}

	public void setfocus() {
		low.requestFocus();
	}

	public boolean action(Event evt, Object arg) {
		if (evt.target instanceof TextField) {
			outerparent.update(this);
			return(true);
		}
		return(false);
	}
}

class Rnp extends RatioPanel {
	TextField	rnp;
	Gear		outerparent;

	Rnp(Gear target, String irnp) {

		/*
		** Save parent
		*/
		this.outerparent = target;

		/*
		** Make Rnp field
		*/
		setLayout(new FlowLayout(FlowLayout.CENTER));
		rnp = new TextField(irnp, 4);

		/*
		** Add 'em
		*/
		add(new Label("Ring and Pinion: ", Label.RIGHT));
		add(rnp);
	}

	public double getvalRnp() throws NewEx {
		return(convertRatio("Ring and Pinion", rnp.getText()));
	}

	public void setfocus() {
		rnp.requestFocus();
	}

	public boolean action(Event evt, Object arg) {
		if (evt.target instanceof TextField) {
			outerparent.update(this);
			return(true);
		}
		return(false);
	}
}

class Tire extends Panel {
	Gear		outerparent;
	TextField	textTire;
	String		tireStr;
	static final int		MAXTIRESTR = 11;
	static final double		MMTOINCH = 0.03937008;

	Tire(Gear target) {

		/*
		** Save parent
		*/
		this.outerparent = target;

		/*
		** Create type specific input method
		*/
		setLayout(new FlowLayout(FlowLayout.CENTER));
		add(new Label("Enter tire size ", Label.CENTER));
		textTire = new TextField("", (MAXTIRESTR + 1));
		add(textTire);
	}

	public double getvalTire() throws NewEx {
		String	ptr;

		/*
		** Get value
		*/
		ptr = textTire.getText().toUpperCase();

		/*
		** Metric check
		*/
		if (ptr.startsWith("P"))
			return(convertMetric(ptr.substring(1, ptr.length())));
		if (ptr.startsWith("LT"))
			return(convertMetric(ptr.substring(2, ptr.length())));
		if ((ptr.length() > 8) && (ptr.charAt(3) == '/'))
			return(convertMetric(ptr));

		return(convertEnglish(ptr));
	}

	public String gettireStr() {
		return(tireStr);
	}

	public double convertMetric(String metStr) throws NewEx {
		double	width;
		double	aspectRatio;
		double	wheelSize;
		double	result;
		String	subStr;
		int	j;
		int	strLen;

		/*
		** Check if it is long enough
		*/
		strLen = metStr.length();
		if (strLen < 9) {
			throw(new NewEx("Tire: Input not long enough for metric type.  e.g.: 225/75R15"));
		}

		/*
		** Basic Validate
		*/
		if ((metStr.charAt(3) != '/') && (metStr.charAt(3) != '\\')) {
			throw(new NewEx("Tire: A back slash(/) must appear as the fourth character.  e.g.: 225/75R15"));
		}
		if ((metStr.charAt(6) != 'r') && (metStr.charAt(6) != 'R')) {
			throw(new NewEx("Tire: A 'R' must appear as the seventh character.  e.g.: 225/75R15"));
		}

		/*
		** Check for digits
		*/
		for (j=0; j<strLen; j++) {
			if ((j == 3) || (j == 6))
				continue;
			if ((metStr.charAt(j) < '0') || (metStr.charAt(j) > '9')) {
				throw(new NewEx("Tire: Character " + (j + 1) + " is not a digit"));
			}
		}

		/*
		** Get width
		*/
		subStr = metStr.substring(0, 3);
		width = Double.valueOf(subStr).doubleValue();

		/*
		** Get aspect ratio
		*/
		subStr = metStr.substring(4, 6);
		aspectRatio = Double.valueOf(subStr).doubleValue();

		/*
		** Get wheelSize
		*/
		subStr = metStr.substring(7, 9);
		wheelSize = Double.valueOf(subStr).doubleValue();


		/*
		** Calculate, convert, and return
		*/
		result = ((((width * aspectRatio) / 100) * MMTOINCH) * 2) + wheelSize;
		/*
		**  Debugging
		**
		** throw(new NewEx("Tire: Convert: " + width + "/" + aspectRatio + "R" + wheelSize + " = " + result + "\""));
		*/
		tireStr = new String(width + "/" + aspectRatio + "R" + wheelSize);
		return(result);;
	}

	public double convertEnglish(String engStr) throws NewEx {
		String	subStr;
		int	deccnt;
		int	j;

		/*
		** Check that something was passed
		*/
		if (engStr.length() < 1) {
			throw(new NewEx("Tire: Tire size not entered"));
		}

		/*
		** Get important part (diameter)
		*/
		deccnt = 0;
		j = (engStr.length() < 4) ? engStr.length() : 4;
		if (engStr.indexOf('X') > 0)
			j = (engStr.indexOf('X') < j) ? engStr.indexOf('X') : j;
		if (engStr.indexOf('"') > 0)
			j = (engStr.indexOf('"') < j) ? engStr.indexOf('"') : j;
		if (engStr.indexOf('S') > 0)
			j = (engStr.indexOf('S') < j) ? engStr.indexOf('S') : j;
		subStr = engStr.substring(0, j);

		/*
		** Validate import part
		*/
		for (j=0; j<subStr.length(); j++) {
			if (subStr.charAt(j) == '.') {
				if (deccnt == 0) {
					++deccnt;
					continue;
				}
				throw(new NewEx("Tire: More than one decimal place"));
			}
			if ((subStr.charAt(j) < '0') || (subStr.charAt(j) > '9')) {
				throw(new NewEx("Tire: Character " + (j + 1) + " is not a digit"));
			}
		}
		tireStr = new String(subStr + "\"");

		return(Double.valueOf(subStr).doubleValue());
	}

	public void setfocus() {
		textTire.requestFocus();
	}

	public boolean action(Event evt, Object arg) {
		if (evt.target instanceof TextField) {
			outerparent.update(this);
			return(true);
		}
		return(false);
	}
}

class MphKph extends Panel {
	CheckboxGroup	mphkph;
	Gear		outerparent;
	public static final String		Smph = "Miles per hour";
	public static final String		Skph = "Kilometers per hour";

	MphKph(Gear target, String imphkph) {
		boolean	kphtrue;
		boolean	mphtrue;

		/*
		** Save parent
		*/
		this.outerparent = target;

		/*
		** Set default
		*/
		kphtrue = false;
		mphtrue = true;
		if (imphkph.equals(this.Skph)) {
			kphtrue = true;
			mphtrue = false;
		}

		/*
		** Set up check box
		*/
		mphkph = new CheckboxGroup();
		add(new Checkbox(Smph, mphkph, mphtrue));
		add(new Checkbox(Skph, mphkph, kphtrue));
	}

	public boolean action(Event evt, Object arg) {
		if (evt.target instanceof CheckboxGroup) {
			outerparent.update(this);
			return(true);
		}
		return(false);
	}
}

class HelpWin extends Frame {
	HelpScreen	helpScreen;
	Thread		helpThread;
	String		options[];
	String		files[];
	int		count;
	MenuBar		mb;
	Menu		mu;
	static final int		MAXHELPX = 550;
	static final int		MAXHELPY = 400;

	HelpWin(URL cb, String bs[], String fs[], int cnt) {
		int	j;

		/*
		** Init frame
		*/
		setTitle("Gear and Tire Help");
		resize(MAXHELPX, MAXHELPY);
		setLayout(new BorderLayout());

		/*
		** Copy options and files
		*/
		mu = new Menu("Help");
		count = cnt;
		files = new String[count];
		options = new String[count];
		for (j=0; j<count; j++) {
			files[j] = new String(fs[j]);
			options[j] = new String(bs[j]);
			mu.add(new MenuItem(bs[j]));
		}

		/*
		** Create help screen
		*/
		helpThread = null;
		helpScreen = new HelpScreen(this, cb, MAXHELPX, MAXHELPY);
		add("Center", helpScreen);

		/*
		** Create menu
		*/
		mb = new MenuBar();
		setMenuBar(mb);
		mb.add(mu);
		mb.setHelpMenu(mu);
	}

	public void display() {
		startThread(0);
	}

	public void display(String name) {
		int	j;

		/*
		** Find button
		*/
		for (j=0; j<count; j++) {
			if (options[j].equals(name)) {
				startThread(j);
				break;
			}
		}
		return;
	}

	void startThread(int index) {


		/*
		** Kill old thread
		*/
		if (helpThread != null)
			helpThread.stop();

		/*
		** Start thread
		*/
		helpThread = new Thread(helpScreen);
		if (! helpScreen.setURL(files[index])) {
			show();
			return;
		}

		helpThread.start();
		show();
	}

	public boolean handleEvent(Event evt) {
		if (evt.id == evt.WINDOW_DESTROY)
			hide();
		return(super.handleEvent(evt));
	}

	public boolean action(Event evt, Object arg) {
		if (evt.target instanceof MenuItem) {
			display((String)arg);
			return(true);
		}
		return(false);
	}
}

class HelpScreen extends Panel implements Runnable {
	TextArea	area;
	Label		topLabel;
	URL		saveCb;
	URL		theURL;
	HelpWin		gh;

	HelpScreen(HelpWin hep, URL cb, int maxx, int maxy) {

		gh = hep;
		saveCb = cb;
		area = new TextArea("", maxx, maxy);
		topLabel = new Label("Getting text ... main", Label.CENTER);
		setLayout(new BorderLayout());
		add("North", topLabel);
		add("Center", area);
	}

	public boolean setURL(String fileName) {

		/*
		** Get URL
		*/
		try { theURL = new URL(saveCb, fileName); }
		catch (MalformedURLException e) {
			add("North", new Label(("Bad URL: " + theURL), Label.CENTER));
			return(false);
		}
		topLabel.setText("Getting text ... " + fileName);
		show();
		return(true);
	}

	public void run() {
		int		j;
		InputStream	conn;
		DataInputStream	data;
		String		line;
		StringBuffer	buf = new StringBuffer();

		/*
		** Open, Read, and write
		*/
		try {
			conn = theURL.openStream();
			data = new DataInputStream(new BufferedInputStream(conn));

			while ((line = data.readLine()) != null) {
				buf.append(line + "\n");
			}
			conn.close();
		}
		catch (IOException e) {
			buf.append("\n***\n");
			buf.append("*** Error reading: " + e.getMessage());
			buf.append("\n***\n");
		}
		area.setText(buf.toString());
		topLabel.setText("");
		show();
	}
}

/* Author: Jun Kaneko,  Keio University
** t93116jk@sfc.keio.ac.jp
** Version : 8th/Jul./'96
** Permission to use,copy,modify this software
** as long as the source is noted.
*/

class GraphWin extends Frame {
	GraphLine	graphLine;
	Checkbox	lowCheckbox;
	Panel		lowPanel;

	GraphWin(int namesCnt, int valuesCnt) {
		setLayout(new BorderLayout());
		graphLine = new GraphLine(namesCnt, valuesCnt);
		add("Center", graphLine);
		resize(600,480);
	}

	public void setWindowTitle(String stit) {
		setTitle(stit);
		return;
	}
	public void setPageTitle(String stit) {
		add("North", new Label(stit, Label.CENTER));
		return;
	}

	public void setYname(String newval) {
		graphLine.setYname(newval);
	}
	public void setNames(String newval) {
		graphLine.setNames(newval);
	}
	public void setXlabel(int newval) {
		graphLine.setXlabel(newval);
	}
	public void setYlabel(int newval) {
		graphLine.setYlabel(newval);
	}
	public void setValues(double newval) {
		graphLine.setValues(newval);
	}
	public void setLow(double newval) {
		graphLine.setLow(newval);
		lowPanel = new Panel();
		lowPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		lowPanel.add(lowCheckbox = new Checkbox("Low range " + newval + ":1"));
		add("South", lowPanel);
	}

	public void draw() {
		graphLine.update();
		show();
	}

	public boolean action(Event evt, Object arg) {
		if (evt.target instanceof Checkbox) {
			graphLine.goLow();
			draw();
			return(true);
		}
		return(false);
	}

	public boolean handleEvent(Event evt) {
		if (evt.id == evt.WINDOW_DESTROY)
			dispose();
		return(super.handleEvent(evt));
	}
}

class GraphLine extends Canvas {
	int		xlabels[];
	int		ylabels[];
	String		yName;
	String		names[];
	double		mod;
	double		low;
	double		values[][];
	double		decode = 1;
	int		valuesNum;
	int		namesNum;
	int		curName;
	int		curValue;
	int		curXlabel;
	int		curYlabel;
	static final int	MINAXE = 40;
	static final int	MINAYE = 10;
	static final int	MAXAXE = 500;
	static final int	MAXAYE = 300;
	static final int	AXE = MINAXE + 40;
	static final int	AYE = MAXAYE - 20;

	GraphLine(int namesCnt, int valuesCnt) {
		this.valuesNum = valuesCnt;
		this.namesNum = namesCnt;
		this.values = new double[namesCnt][valuesCnt];
		this.names = new String[namesCnt];
		this.xlabels = new int[valuesCnt];
		this.ylabels = new int[valuesCnt];
		for (int i=0; i<valuesNum ;i++) this.ylabels[i] = 0;
		curName = -1;
		curValue = -1;
		curXlabel = -1;
		curYlabel = -1;
		low = 1.0;
		mod = 1.0;
	}

	public void setYname(String newname) {
		yName = new String(newname);
	}
	public void setNames(String newname) {
		++curName;
		curValue = -1;
		names[curName] = new String(newname);
	}
	public void setXlabel(int newxlabels) {
		++curXlabel;
		xlabels[curXlabel] = newxlabels;
	}
	public void setYlabel(int newylabels) {
		++curYlabel;
		ylabels[curYlabel] = newylabels;
	}
	public void setValues(double newvalue) {
		++curValue;
		values[curName][curValue] = newvalue;
	}
	public void setLow(double newvalue) {
		low = newvalue;
	}

	public void goLow() {
		if (mod == 1.0)
			mod = low;
		else
			mod = 1.0;
	}

	public void update() {
		repaint();
	}

	public void paint(Graphics g) {
		int		axe;
		int		aye;
		int		j;

		/*
		** Set background
		*/
		g.setColor(Color.white);
		g.fillRect(MINAXE,MINAYE,MAXAXE,MAXAYE);
		g.setColor(Color.black);
		g.drawRect(MINAXE,MINAYE,MAXAXE,MAXAYE);

		/*
		** Draw X axis labels
		*/
		axe = AXE;
		g.drawLine(AXE,AYE,MAXAXE,AYE);
		for (int i=0;i < valuesNum ;i++) {
			if ((i % 2) == 0) {
				g.setColor(Color.black);
				g.drawLine(axe,AYE,axe,(AYE + 5));
				g.drawString("" + xlabels[i],axe+5,300);
				g.setColor(Color.pink);
				g.drawLine(axe,AYE,axe,MINAYE);
				g.setColor(Color.black);
			}
			axe = axe + ((MAXAXE-AXE)/valuesNum);
		}
		g.drawString("RPM",((MAXAXE-MINAXE)/2) + MINAXE, (MAXAYE+30));

		/*
		** Draw Y axis labels
		*/
		aye = AYE;
		g.drawLine(AXE,MINAYE,AXE,AYE);
		for (int i=0; i<curYlabel ;i++) {
			g.setColor(Color.black);
			g.drawLine(AXE,aye,(AXE-5),aye);
			g.drawString("" + ylabels[i],(AXE-25),aye);
			g.setColor(Color.pink);
			g.drawLine(AXE,aye,MAXAXE,aye);
			g.setColor(Color.black);
			aye -= ((AYE-MINAYE)/curYlabel);
		}
		g.drawString(yName,2,((MAXAYE-MINAYE)/2) + MINAYE);
		g.drawLine(AXE,AYE,MAXAXE,AYE);

		/*
		** Draw lines
		*/
		decode = 3.3333;
		/*decode = (AYE - MINAYE) / ylabels[curYlabel];*/
		for (j=0; j<namesNum; j++) {
			/*
			** Set color
			*/
			switch (j) {
			case 0:
				g.setColor(Color.red);break;
			case 1:
				g.setColor(Color.blue);break;
			case 2:
				g.setColor(Color.green);break;
			case 3:
				g.setColor(Color.black);break;
			case 4:
				g.setColor(Color.yellow);break;
			default:
				g.setColor(Color.orange);break;
			}

			/*
			** Generate key
			*/
			g.drawString(names[j],20 + ((j + 1) * 60),380);

			/*
			** Draw line
			*/
			axe = AXE;
			for (int i=0; i<(valuesNum-1) ;i++) {
				g.drawLine(axe,AYE - (int)(values[j][i]*decode/mod) ,axe+((MAXAXE-AXE)/valuesNum), AYE- (int)(values[j][i+1]*decode/mod));
/*
				g.drawString(("" + (values[j][i])), axe, (AYE - (int)(values[j][i]*decode/mod)));
*/
				axe = axe + ((MAXAXE-AXE)/valuesNum);
			}
		}
	}
}
