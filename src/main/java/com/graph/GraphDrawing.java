package com.graph;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
/**
 * 
 * @author <>
 *
 */
public  class GraphDrawing implements ActionListener { 

	GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice(); // Takes in display size.
	final int unit = 20, wWidth = 1000, wHeight = 1060, sWidth = gd.getDisplayMode().getWidth(), sHeight = gd.getDisplayMode().getHeight();
	JFrame frame;
	JTextField fa, fb, fc;
	JLabel root1,root2; // Text fields where to enter the coefficients.
	ArrayList<Line> ay = new ArrayList<Line>(); // ArrayList to hold the lines.
	JPanel lines, cofs; // Panel that lists drawn lines and their respective equation & panel with coefficients.
	MyDrawPanel panel;
	JSplitPane splitPane;
	Line line;
	JLabel vertexLabel;

	public static void main(String[] args) {

		new GraphDrawing();
	}

	public GraphDrawing() {

		frame = new JFrame("Parabolo");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBackground(Color.LIGHT_GRAY);
		frame.getContentPane().setForeground(Color.WHITE);
		frame.getContentPane().setBackground(Color.ORANGE);
		fa = new JTextField(7);
		fb = new JTextField(7);
		fc = new JTextField(7);
		JLabel la = new JLabel("A: ");
		JLabel lb = new JLabel("B: ");
		JLabel lc = new JLabel("C: ");
		root1 = new JLabel("");
		root2 = new JLabel("");
		JLabel lRoot1 = new JLabel("Root1:");
		JLabel lRoot2 = new JLabel("Root2:");

		JButton drawLine = new JButton("Draw Graph!");
		JButton exit = new JButton("Exit");
		drawLine.addActionListener(this);
		exit.addActionListener(this);

		cofs = new JPanel(); // Panel where the coefficients can be entered.
		cofs.setBackground(Color.lightGray);

		cofs.add(la);
		cofs.add(fa);
		cofs.add(lb);
		cofs.add(fb);
		cofs.add(lc);
		cofs.add(fc);
		cofs.add(lRoot1);
		cofs.add(root1);
		cofs.add(lRoot2);
		cofs.add(root2);
		cofs.add(drawLine);
		cofs.add(exit);

		lines = new JPanel();
		lines.setBackground(Color.lightGray);
		lines.setLayout(new BoxLayout(lines, BoxLayout.Y_AXIS)); // Sets layout for the side-bar, giving a new line to each equation.
		lines.setPreferredSize(new Dimension(20, frame.getHeight()));

		panel = new MyDrawPanel();
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, lines, panel); // Used for resizing of side-bar.

		frame.getContentPane().add(BorderLayout.CENTER, splitPane);
		frame.getContentPane().add(BorderLayout.SOUTH, cofs);

		frame.setSize(wWidth, wHeight);
		frame.setVisible(true);
	}

	String toEquation() { // Returns the coefficients in the format y = ax^2 + bx + c as a String.

		double a = line.getA(), b = line.getB(), c = line.getC(); // Local variables to store the coefficients of the line.

		String as = Double.toString(a); // Converts values to strings.
		String bs = Double.toString(b);
		String cs = Double.toString(c);

		int ai = as.length(), bi = bs.length(), ci = cs.length(); // Lengths of values.

		if(Character.toString(as.charAt(ai - 1)).equals("0")) { // If a number is an integer, remove the .0 bit from the end.

			as = as.substring(0, ai - 2);
		}
		if(Character.toString(bs.charAt(bi - 1)).equals("0")) {

			bs = bs.substring(0, bi - 2);
		}
		if(Character.toString(cs.charAt(ci - 1)).equals("0")) {

			cs = cs.substring(0, ci - 2);
		}

		String af = a == 1 ? "x\u00B2 " : a == -1 ? "-x\u00B2 " : a == 0 ? "" : as + "x\u00B2 "; // Code for showing a (u00B2 is Unicode for superscript 2).
		String bf = b == -1 ? a == 0 ? "-x " : "- x " : a == 0 && b != 0 && b != 1 ? bs + "x " : b < 0 ? "- " + bs.substring(1) + "x " : b == 1 ? a == 0 ? "x " : "+ x " : a == 0 ? b == 0 ? "" : bs + "x " : b == 0 ? "" : "+ " + bs + "x "; // Forgive me lord for I have sinned.
		String cf = a == 0 && b == 0 ? cs : c < 0 ? "- " + cs.substring(1) : c == 0 ? "" : "+ " + cs; // Code for y-intercept string.

		return "y = " + af + bf + cf;
	}
/**
 * Adds equation to the side bar
 * @param s
 */
	void listEquation(String s) { // Adds the equation to the side-bar.
		JLabel label = new JLabel(s);
		JPanel lineButton = new JPanel();
		lineButton.setMaximumSize(new Dimension(frame.getWidth(), 30)); // Makes the different panels stack over each other.
		lineButton.add(BorderLayout.EAST, label);
		lines.add(BorderLayout.SOUTH, lineButton);
		splitPane.resetToPreferredSizes();
	}
/**
 * ActionListener method overridden
 */
	public void actionPerformed(ActionEvent ae) { 
		try { 
			if(ae.getActionCommand().equals("Exit")){
				System.exit(0);
			}
			// Take in a, b & c from text boxes.
			if(!(fa.getText().equals("") && fb.getText().equals("") && fc.getText().equals(""))) { // Don't draw a line if all 3 boxes are empty.

				String[] sabc = { fa.getText(), fb.getText(), fc.getText() };
				double[] abc = new double[3];

				for(int i = 0; i < sabc.length; i++) {

					abc[i] = (sabc[i].equals("") ? 0 : Double.parseDouble(sabc[i]));
				}

				line = new Line(abc[0], abc[1], abc[2], unit, sHeight, sWidth);
				listEquation(toEquation()); // Add equation to the side-bar.
				ay.add(line); // Add line to the array.
			}
			findRoot();
		} catch (Exception ex) {} // Does nothing if a, b or c aren't numbers.

		frame.repaint();
	}
/**
 * Inner class to draw the graph
 * @author <>
 *
 */
	class MyDrawPanel extends JPanel { // Draws the graph.
		private static final long serialVersionUID = 6193278998208292577L;
		public void paintComponent(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;
			for(int i = 0; i <= sWidth; i += unit) { // Draws grid.
				g2d.drawLine(i, 0, i, sHeight);
				g2d.drawLine(0, i, sWidth, i);
			}
			int h = ((int) ((frame.getHeight() - cofs.getHeight()) / unit / 2)) * unit; // Gives values for height and width based on the unit.
			int w = ((int) ((frame.getWidth() - lines.getWidth()) / unit / 2)) * unit;
			g2d.fillRect(0, h - 1, w * sWidth, 3); // Draws x-axis.
			g2d.fillRect(w - 1, 0, 3, h * sHeight); // Draws y-axis.
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // Makes the curve nice and smooth.
			for(int i = 0, ays = ay.size(); i < ays; i++) {
				Line l = ay.get(i);
				g2d.setColor(l.getColor());
				g2d.setStroke(new BasicStroke(l.getThickness())); // Sets thickness of parabola.
				for(int j = 1, size = l.getCoords().size(); j < size; j++) { // Draws a line from point to point.
					g2d.drawLine(w + (int) Math.round(unit * l.getCoords().get(j - 1)[0]), h - (int) Math.round(unit * l.getCoords().get(j - 1)[1]), w + (int) Math.round(unit * l.getCoords().get(j)[0]), h - (int) Math.round(unit * l.getCoords().get(j)[1])); // Draws line from dot to dot.
				}
			}
		}
	}
	/**
	 * Inner class to draw the Line in the graph
	 * @author <>
	 *
	 */
	public class Line {
		private double a, b, c;
		private Color lineColor;
		private int thickness, unit, sHeight, sWidth;
		private double vX, vY;
		private ArrayList<double[]> cords = new ArrayList<double[]>();
		public Line(double tA, double tB, double tC, Color color, int thick, int nUnit, int height, int width) {

			a = tA;
			b = tB;
			c = tC;
			lineColor = color;
			thickness = thick;
			unit = nUnit;
			sHeight = height;
			sWidth = width;
			setVertex();
			setCoords();
		}

		public Line(double tA, double tB, double tC, int nUnit, int height, int width) {

			a = tA;
			b = tB;
			c = tC;
			lineColor = Color.GREEN;
			thickness = 3;
			unit = nUnit;
			sHeight = height;
			sWidth = width;
			setVertex();
			setCoords();
		}

		double getA() {

			return a;
		}
		void setA(double tA) {

			a = tA;
		}

		double getB() {

			return b;
		}
		void setB(double tB) {

			b = tB;
		}

		double getC() {

			return c;
		}
		void setC(double tC) {

			c = tC;
		}

		Color getColor() {

			return lineColor;
		}
		void setColor(Color cl) {

			lineColor = cl;
		}

		int getThickness() {

			return thickness;
		}
		void setThickness(int nThickness) {

			thickness = nThickness;
		}

		String getVertexAsString() {

			return "(" + vX + ", " + vY + ")";
		}

		double[] getVertex() {

			return new double[] {vX, vY};
		}

		private void setVertex() {

			vX = -(b/a/2);
			vY = c - a * Math.pow((b/a/2), 2);
		}

		void setCoords() {

			double px, nx, y;

			if(!(a == 0 && b == 0)) {
				for(int i = 0; true; i++) {

					y = a * i * i + b * i + c;
					if(y > sHeight / unit) {
						px = i;
						break;
					}
				} 

				for(int i = 0; true; i--) {

					y = a * i * i + b * i + c;
					if(y > sHeight / unit) {
						nx = i;
						break;
					}
				}

				for(double i = nx; i < 0; i += 0.1) {

					y = a * i * i + b * i + c;
					cords.add(new double[] {i, y});

				}

				for(double i = 0; i < px; i += 0.1) {

					y = a * i * i + b * i + c;
					cords.add(new double[] {i, y});
				}
			} else {
				
				cords.add(new double[] {-sWidth, c});
				cords.add(new double[] {sWidth, c});
			}
		}

		ArrayList<double[]> getCoords() {

			return cords;
		}
	}

	public void findRoot(){
	NumberFormat formatter = new DecimalFormat("#0.00"); 
	double a = 0, b = 0, c = 0.0;
	try {
		a = Double.parseDouble(fa.getText());

		b = Double.parseDouble(fb.getText());

		c = Double.parseDouble(fc.getText());
	} catch (Exception e) {
		e.printStackTrace();
		return;

	}
	if ((b * b - 4 * a * c) >= 0) {
		double sol1 = (-b + Math.sqrt(b * b - 4 * a * c)) / (2 * a);
		double sol2 = (-b - Math.sqrt(b * b - 4 * a * c)) / (2 * a);
		root1.setText(String.valueOf(formatter.format(sol1)));

		root2.setText(String.valueOf(formatter.format(sol2)));

	} else {
		double sola = (-b / (2 * a));
		double solb = Math.sqrt(-(b * b - 4 * a * c)) / (2 * a);
		root1.setText(String.valueOf(formatter.format(sola + solb)) );
		root2.setText(String.valueOf(formatter.format(sola - solb)));

	}

}
}





