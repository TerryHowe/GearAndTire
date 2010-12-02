/* Author: Jun Kaneko,  Keio University
** t93116jk@sfc.keio.ac.jp
** Version : 8th/Jul./'96
** Permission to use,copy,modify this software
** as long as the source is noted.
*/

import java.awt.*;
/*
import java.io.*;
import java.lang.*;
import java.util.*;
import java.applet.*;
*/

class GraphWindow extends Frame {
	LineGraph	lineGraph;

	GraphWindow(int namesCnt, int valuesCnt) {
		setLayout(new BorderLayout());
		lineGraph = new LineGraph(namesCnt, valuesCnt);
		add("Center", lineGraph);
		resize(550,650);
	}

	public void setNames(String newval) {
		lineGraph.setNames(newval);
	}
	public void setXlabel(int newval) {
		lineGraph.setXlabel(newval);
	}
	public void setYlabel(int newval) {
		lineGraph.setYlabel(newval);
	}
	public void setValues(int newval) {
		lineGraph.setValues(newval);
	}
}

class LineGraph extends Canvas {
	int		xlabels[];
	int		ylabels[];
	String		names[];
	double		values[][];
	int		valuesNum;
	int		namesNum;
	int		curName;
	int		curValue;
	int		curXlabel;
	int		curYlabel;
	double		decode = 1;

	LineGraph(int namesCnt, int valuesCnt) {
		this.valuesNum = valuesCnt;
		this.namesNum = namesCnt;
		this.values = new double[namesCnt][valuesCnt];
		this.names = new String[namesCnt];
		this.xlabels = new int[valuesCnt];
		this.ylabels = new int[valuesCnt];
		curName = -1;
		curValue = -1;
		curXlabel = -1;
		curYlabel = -1;
	}

	public void setNames(String newname) {
		++curName;
		names[curName] = new String(newname);
	}
	public void setXlabel(int newxlabels) {
		++curXlabel;
		xlabels[curXlabel] = newxlabels;
	}
	public void setYlabel(int newylabels) {
		++curYlabel;
		ylabels[curYlabel] = newylabels;
		decode = 260 / newylabels; /* ???260 magic number??? */
	}
	public void setValues(int newvalue) {
		++curValue;
		values[curName][curValue] = newvalue;
	}
	public void paint(Graphics g) {
		this.white(g);
	}

	public void update(Graphics g) {
		int	j;
		for (j=0; j<namesNum; j++) {
			this.line(g, j);
		}
	}

	public void white(Graphics g) {

		g.setColor(Color.white);
		g.fillRect(10,10,500,290);
		g.setColor(Color.black);
		g.drawRect(10,10,500,290);
		g.drawRect(10,290,500,10);

		if (valuesNum < 8) {
			g.drawRect(10,310,90 + 50*valuesNum, 15*namesNum);
		}
		else if (8 <= valuesNum && valuesNum < 16) {
			g.drawRect(10,310,490, 15*namesNum);
			g.drawRect(10,325 + namesNum*15,90 + 50*(valuesNum-8), 15*namesNum);
		}
		else {
			g.drawRect(10,310,490, 15*namesNum);
			g.drawRect(10,325 + namesNum*15,490, 15*namesNum);
			g.drawRect(10,340 + namesNum*30,90 + 50*(valuesNum-16), 15*namesNum);
		}


		for (int j=0;j < namesNum;j++) {
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
				g.setColor(Color.orange);break;
			default:
				g.setColor(Color.yellow);break;
			}

			g.setFont(new Font("Dialog",Font.BOLD, 12));
			g.drawString(names[j],20,310 + (j+1)*15);
			g.setColor(Color.black);
			g.drawString("" + (j+1),12,310 + (j+1)*15);
			g.drawLine(18,310+j*15,18,310+(j+1)*15);

			if (valuesNum <= 8)
				g.drawLine(10,310 + (j + 1)*15,100 + 50*valuesNum,310 + (j + 1)*15);
			else if (9 <= valuesNum && valuesNum <= 16) {
				g.drawLine(10,310 + (j + 1)*15,490,310 + (j + 1)*15);
				g.drawLine(10,325 + namesNum*15 + (j + 1)*15,100 + 50*(valuesNum-8),325 + namesNum*15 +(j + 1)*15);
			}
			else {
				g.drawLine(10,310 + (j + 1)*15,490,310 + (j + 1)*15);
				g.drawLine(10,325 + namesNum*15 + (j + 1)*15,490,325 + namesNum*15 +(j + 1)*15);
				g.drawLine(10,340 + namesNum*30 + (j + 1)*15,100 + 50*(valuesNum-16),340 + namesNum*30 +(j + 1)*15);
			}

			g.setFont(new Font("Dialog",Font.PLAIN,4));

			for (int i=0;i < valuesNum;i++) {
				if (i < 8) {
					g.drawString("" + values[j][i],i*50+100,310+(j+1)*15);
					g.drawLine(i*50+95,295+(j+1)*15,i*50+95,310+(j + 1)*15);
					g.drawString("" + xlabels[i],i*50+100,310);
				}
				else if (8 <= i && i < 16) {
					g.drawString("" + values[j][i],(i-8)*50+100,325+namesNum*15+(j+1)*15);
					g.drawLine((i-8)*50+95,310 + namesNum*15 + (j+1)*15,(i-8)*50+95,325+namesNum*15+(j + 1)*15);
					g.drawString("" + xlabels[i],(i-8)*50+100,325+namesNum*15);
				}
				else {
					g.drawString("" + values[j][i],(i-16)*50+100,340+namesNum*30+(j+1)*15);
					g.drawLine((i-16)*50+95,325 + namesNum*30 + (j+1)*15,(i-16)*50+95,340+namesNum*30+(j + 1)*15);
					g.drawString("" + xlabels[i],(i-16)*50+100,340+namesNum*30);
				}

			}
		}
		return;
	}

	public void line(Graphics g,int indx) {
		int		axe;

		/*
		** Set color
		*/
		switch (indx) {
		case 0:
			g.setColor(Color.red);break;
		case 1:
			g.setColor(Color.blue);break;
		case 2:
			g.setColor(Color.green);break;
		case 3:
			g.setColor(Color.orange);break;
		default:
			g.setColor(Color.yellow);break;
		}

		/*
		** Draw line
		*/
		axe = 20;
		for (int i=0;i < valuesNum ;i++) {
			if (i == (valuesNum - 1)) {
/*
				g.drawString("" + values[indx][i],axe,290- (int)(values[indx][i]*decode));
*/
				g.drawString("" + xlabels[i],axe+5,300);
				g.drawLine(axe,290,axe,295);
				axe = axe + (500/valuesNum);
				break;
			}

			g.drawLine(axe,290 - (int)(values[indx][i]*decode) ,axe+(500/valuesNum), 290- (int)(values[indx][i+1]*decode));
/*
			g.drawString("" + values[indx][i],axe,290- (int)(values[indx][i]*decode));
*/
			g.drawLine(axe,290,axe,295);
			g.drawString("" + xlabels[i],axe+5,300);

				axe = axe + (500/valuesNum);
		}
	}
}
