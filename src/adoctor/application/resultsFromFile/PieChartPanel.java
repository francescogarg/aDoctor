package adoctor.application.resultsFromFile;

import javax.swing.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class PieChartPanel extends JPanel {
    private static final int SIZE = 120;
    private static final int OVAL_X = 120;
    private static final int OVAL_Y = 20;
    private static final int OVAL_SIZE = 7;

    private String [] labels;
    private double [] values;  // values are percentages
    private Color[] colors;




    public PieChartPanel(String[] labels, double[] values) {
        this.labels=labels;
        this.values=values;

        if (labels.length<=6){
            this.colors = new Color[6];

            colors[0]=new Color(92,167,147);
            colors[1]=new Color(162,184,108);
            colors[2]=new Color(235,200,68);
            colors[3]=new Color(241,108,32);
            colors[4]=new Color(192,46,29);
            colors[5]=new Color(15,91,120);
        }
        else{
            this.colors=new Color[12];
            colors[0]=new Color(92,167,147);
            colors[1]=new Color(162,184,108);
            colors[2]=new Color(235,200,68);
            colors[3]=new Color(241,108,32);
            colors[4]=new Color(192,46,29);
            colors[5]=new Color(15,91,120);
            colors[6]=new Color(19,149,186);
            colors[7]=new Color(217,78,31);
            colors[8]=new Color(236,170,56);
            colors[9]=new Color(17,120,153);
            colors[10]=new Color(239,139,44);
            colors[11]=new Color(13,60,85);
        }

        this.setBackground(new Color(60,63,65));
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int startAngle = 0;
        int arcAngle = 0;

        //for(int i=0; i<labels.length; i++) {
        for(int i=labels.length-1; i>=0; i--) { 	//do this to match the order in the JavaFX example
            // Draw each wedge
            g.setColor(colors[i]);
            arcAngle = (int) ((values[i] / 100.0) * 360.0);
            g2.fillArc(SIZE/4, SIZE/4, SIZE/2, SIZE/2, startAngle, arcAngle);
            startAngle += arcAngle;

            // Draw each label
            g2.fillOval(OVAL_X, OVAL_Y + (i * 13), OVAL_SIZE, OVAL_SIZE);
            g2.drawString(labels[i], OVAL_X+20, OVAL_Y+8 + (i * 13));
        }

    } //end of paintComponent

}
