/**
 * Copyright (C) 2008 Dai Odahara.
 */

package com.android.google.operation;

import android.util.Log;

import com.googlecode.gchartjava.ArraysUtil;
import com.googlecode.gchartjava.AxisInfo;
import com.googlecode.gchartjava.AxisStyle;
import com.googlecode.gchartjava.BarChart;
import com.googlecode.gchartjava.BarChartDataSeries;
import com.googlecode.gchartjava.Color;
import com.googlecode.gchartjava.Data;
import com.googlecode.gchartjava.DataEncoding;
import com.googlecode.gchartjava.GChart;
import com.googlecode.gchartjava.GoogleOMeter;
import com.googlecode.gchartjava.Line;
import com.googlecode.gchartjava.LineChart;
import com.googlecode.gchartjava.LineStyle;
import com.googlecode.gchartjava.LinearGradientFill;
import com.googlecode.gchartjava.PieChart;
import com.googlecode.gchartjava.Slice;
import com.googlecode.gchartjava.SolidFill;

import static com.googlecode.gchartjava.Color.*;

import com.googlecode.gchartjava.AxisStyle.AlignmentEnum;
import static com.googlecode.gchartjava.ArraysUtil.asUnmodifiableList;

/**
 * This class manages Google Chart API. At present, each parameters are 
 * static value, but in future it handles parameters dynamically.
 * 
 * @author Dai Odahara
 * 
 */
public class ChartAPICaller {

	/** 
	 * main method. the main is never used with android. this is just for chart api calling test usage.
	 * @param args
	 */
	private static final String TAG = "ChartAPICaller";
	
	public static void main(String[] args) {
		ChartAPICaller ig = new ChartAPICaller();

		String lcurl = ig.getLineChartURL();
		System.out.println(lcurl);

		lcurl = ig.getOmeterChartURL();
		System.out.println(lcurl);

		lcurl = ig.getPieChartURL();
		System.out.println(lcurl);

	}

	public String getPieChartURL() {      
        Slice s1 = new Slice(45,new Color("CACACA"),"Safari");
        Slice s2 = new Slice(45,new Color("DF7417"),"Firefox");
        Slice s3 = new Slice(10,new Color("01A1DB"),"IE");
                        
        PieChart chart = new PieChart(s1,s2,s3);            
        chart.setTitle("A Better Web",Color.BLACK,16);
        chart.setSize(600, 300);
        chart.setThreeD(true);
        chart.setBackgroundFill(new SolidFill(new Color("FFFFFF")));
        
      return chart.createURLString(); 

}

    public String getBarChartURL(String header, String footer, String leftLabel){
        //Defining data series.
        BarChartDataSeries team1 = new BarChartDataSeries(new Data(1800,3000),BLUEVIOLET,"Closed");
        //BarChartDataSeries team2 = new BarChartDataSeries(new Data(8,35,11,5),ORANGERED,"Team B");
        //BarChartDataSeries team3 = new BarChartDataSeries(new Data(10,50,30,70),LIMEGREEN,"Team C");
        
        //Instantiating chart.
        //BarChart chart = new BarChart(team1,team2,team3);
        BarChart chart = new BarChart(team1);

        //Defining axis info and styles
        AxisStyle axisStyle = new AxisStyle(BLACK,18,AlignmentEnum.CENTER);
        AxisInfo score = new AxisInfo(0,3000,asUnmodifiableList(50f),leftLabel);
        score.setAxisStyle(axisStyle);
        AxisInfo year = new AxisInfo(0,3000,asUnmodifiableList(50f),footer);
        year.setAxisStyle(axisStyle);
        
        //Adding axis info to chart.
        chart.addXAxisInfo(new AxisInfo(1800, 3000));
        chart.addYAxisInfo(new AxisInfo(0,4000));
        chart.addYAxisInfo(score);
        chart.addXAxisInfo(year);
        
        chart.setSize(600, 500);
        chart.setSpaceBetweenGroupsOfBars(100);
        //chart.setTitle(header,BLACK,16);
        //chart.setGrid(new LineStyle(3,3,2), 10, 10);
        chart.setBackgroundFill(new SolidFill(ALICEBLUE));
        LinearGradientFill fill = new LinearGradientFill(0,LAVENDER,1);
        fill.addColorAndOffset(WHITE,0);
        chart.setAreaFill(fill);
        Log.v(TAG, chart.createURLString());
        //String expectedString = "http://chart.apis.google.com/chart?cht=bvg&chs=600x450&chxs=1,000000,13,0|3,000000,13,0&chts=000000,16&chxp=1,50.0|3,50.0&chf=bg,s,F0F8FF|c,lg,0,E6E6FA,1.0,FFFFFF,0.0&chdl=Team+A|Team+B|Team+C&chd=e:jMbhmZ0e,FIWZHCDN,GagATNsz&chtt=Team+Scores&chg=10,10,3,2&chbh=23,4,20&chxr=0,0,100|1,0,100|3,0,100&chxt=y,y,x,x&chco=8A2BE2,FF4500,32CD32&chxl=1:|Score|2:|2002|2003|2004|2005|3:|Year";
       return "http://chart.apis.google.com/chart?cht=bvs&chs=600x500&chg=25,25&chxt=x,y&chxl=0:|1800|3000|1:|0|1000|2000|3000&chco=FFC266&chd=t:1800,3000&chds=0,3000&chbh=100&chdl=Closed&chxt=x,y&chxs=0,0000dd,22|1,00aa00,22";
        //return chart.createURLString();
        // assertEquals("Junit error", expectedString,chart.createURLString());
}

	
	
public String getOmeterChartURL() {
        //GoogleOMeter chart = new GoogleOMeter(90,"Archieved",new Color("1148D4"), new Color("5766DE"), new Color("DB3270"), new Color("D41111"));
		GoogleOMeter chart = new GoogleOMeter(90,"Goal",new Color("FF0000"), new Color("FF6633"), new Color("FFFF00"), new Color("99FF00"), new Color("009900"));
    	chart.setSize(500, 250);
    	//http://chart.apis.google.com/chart?chs=225x125&cht=gom&chd=t:70&chl=Hello
        //chart.setBackgroundFill(new SolidFill(new Color("1F1D1D")));
        chart.setBackgroundFill(new SolidFill(new Color("FFFFFF")));

        String url = chart.createURLString();
        return url;
}

public String getLineChartURL() {
        //Defining Line
        Line line1 = new Line(new Data(0,45,35,75,90),new Color("CA3D05"),"Lead");
        line1.setLineStyle(new LineStyle(3,1,0));

        Line line2 = new Line(new Data(80,60,35,20,10),SKYBLUE,"Opp");
        line2.setLineStyle(new LineStyle(3,1,0));

        //Defining chart.
        LineChart chart = new LineChart(line1,line2);
        chart.setSize(500, 450);
        chart.setTitle("Opporutynity vs Lead|(in billions of deal)",WHITE,22);
        chart.setDataEncoding(DataEncoding.EXTENDED);
        
        //Defining axis info and styles
        AxisStyle axisStyle = new AxisStyle(BLACK,18,AlignmentEnum.CENTER);
        
        AxisInfo xAxis = new AxisInfo("June","July","Aug","Sep");
        
        xAxis.setAxisStyle(axisStyle);
        AxisInfo xAxis2 = new AxisInfo("2008","2008","2008","2008");
        xAxis2.setAxisStyle(axisStyle);
        AxisInfo yAxis = new AxisInfo("","25","50","75","100");
        AxisInfo xAxis3 = new AxisInfo(0,100,ArraysUtil.asUnmodifiableList(50f),"Month");
        xAxis3.setAxisStyle(new AxisStyle(BLACK,22,AlignmentEnum.CENTER));
        yAxis.setAxisStyle(axisStyle);
        AxisInfo yAxis2 = new AxisInfo(0,100,ArraysUtil.asUnmodifiableList(50f),"JPN");
        yAxis2.setAxisStyle(new AxisStyle(BLACK,22,AlignmentEnum.CENTER));
        yAxis2.setAxisStyle(axisStyle);
        
        //Adding axis info to chart.
        chart.addXAxisInfo(xAxis);
        chart.addXAxisInfo(xAxis2);
        chart.addXAxisInfo(xAxis3);
        chart.addYAxisInfo(yAxis);
        chart.addYAxisInfo(yAxis2);
        chart.setGrid(new LineStyle(1,3,2), 20, 20);
        
        //Defining background and chart fills.
        //chart.setBackgroundFill(new SolidFill(new Color("1F1D1D")));
        chart.setBackgroundFill(new SolidFill(new Color("FFFFFF")));

        
        LinearGradientFill fill = new LinearGradientFill(0,new Color("FFFAFA"),1);
        fill.addColorAndOffset(new Color("FFEFD5"),0);
        chart.setAreaFill(fill);
        //System.out.println(chart.createURLString());
        //String expectedString = "http://chart.apis.google.com/chart?cht=lc&chs=600x450&chxs=0,FFFFFF,12,0|1,FFFFFF,12,0|2,FFFFFF,12,0|3,FFFFFF,12,0|4,FFFFFF,14,0&chts=FFFFFF,14&chls=3,1,0|3,1,0&chxp=1,50.0|4,50.0&chf=bg,s,1F1D1D|c,lg,0,363433,1.0,2E2B2A,0.0&chdl=myWebsite.com|myCompetition.com&chd=e:AAczWZv.5m,zMmZWZMzGa&chtt=Web+Traffic|(in+billions+of+hits)&chg=20,20,3,2&chxr=1,0,100|4,0,100&chxt=y,y,x,x,x&chco=CA3D05,87CEEB&chxl=0:||25|50|75|100|1:|Hits|2:|Nov|Dec|Jan|Feb|Mar|3:|2007|2007|2008|2008|2008|4:|Month";
        return chart.createURLString();
	}
}
