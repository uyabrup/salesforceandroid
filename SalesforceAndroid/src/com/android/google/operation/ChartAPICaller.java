/**
 * Copyright (C) 2008 Dai Odahara.
 */

package com.android.google.operation;

import com.googlecode.gchartjava.ArraysUtil;
import com.googlecode.gchartjava.AxisInfo;
import com.googlecode.gchartjava.AxisStyle;
import com.googlecode.gchartjava.BarChart;
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

public class ChartAPICaller {

	public static void main(String[] args) {
		ChartAPICaller ig = new ChartAPICaller();

		String lcurl = ig.getLineChartURL();
		System.out.println(lcurl);

		lcurl = ig.getOmeterChartURL();
		System.out.println(lcurl);

		lcurl = ig.getPieChartURL();
		System.out.println(lcurl);

	}

	public ChartAPICaller() {
	}

	/** returns Pie url */
	public String getPieChartURL() {
		Slice s1 = new Slice(45, new Color("CACACA"), "Safari");
		Slice s2 = new Slice(45, new Color("DF7417"), "Firefox");
		Slice s3 = new Slice(10, new Color("01A1DB"), "IE");

		PieChart chart = new PieChart(s1, s2, s3);
		chart.setTitle("A Better Web", Color.WHITE, 16);
		chart.setSize(600, 300);
		chart.setThreeD(true);
		chart.setBackgroundFill(new SolidFill(new Color("304040")));

		return chart.createURLString();

	}

	/** returns O-meter url */
	public String getOmeterChartURL() {
		// GoogleOMeter chart = new GoogleOMeter(90,"Archieved",new
		// Color("1148D4"), new Color("5766DE"), new Color("DB3270"), new
		// Color("D41111"));
		GoogleOMeter chart = new GoogleOMeter(90, "Goal", new Color("FF0000"),
				new Color("FF6633"), new Color("FFFF00"), new Color("99FF00"),
				new Color("009900"));
		chart.setSize(500, 250);
		/** http://chart.apis.google.com/chart?chs=225x125&cht=gom&chd=t:70&chl=Hello
		// chart.setBackgroundFill(new SolidFill(new Color("1F1D1D")));
		*/
		chart.setBackgroundFill(new SolidFill(new Color("304040")));

		String url = chart.createURLString();
		return url;
	}

	public String getLineChartURL() {
		// Defining Line
		Line line1 = new Line(new Data(0, 45, 35, 75, 90), new Color("CA3D05"),
				"Lead");
		line1.setLineStyle(new LineStyle(3, 1, 0));

		Line line2 = new Line(new Data(80, 60, 35, 20, 10), SKYBLUE, "Opp");
		line2.setLineStyle(new LineStyle(3, 1, 0));

		// Defining chart.
		LineChart chart = new LineChart(line1, line2);
		chart.setSize(500, 450);
		chart.setTitle("Opporutynity vs Lead|(in billions of deal)", WHITE, 22);
		chart.setDataEncoding(DataEncoding.EXTENDED);

		// Defining axis info and styles
		AxisStyle axisStyle = new AxisStyle(WHITE, 18, AlignmentEnum.CENTER);

		AxisInfo xAxis = new AxisInfo("June", "July", "Aug", "Sep");

		xAxis.setAxisStyle(axisStyle);
		AxisInfo xAxis2 = new AxisInfo("2008", "2008", "2008", "2008");
		xAxis2.setAxisStyle(axisStyle);
		AxisInfo yAxis = new AxisInfo("", "25", "50", "75", "100");
		AxisInfo xAxis3 = new AxisInfo(0, 100, ArraysUtil
				.asUnmodifiableList(50f), "Month");
		xAxis3.setAxisStyle(new AxisStyle(WHITE, 22, AlignmentEnum.CENTER));
		yAxis.setAxisStyle(axisStyle);
		AxisInfo yAxis2 = new AxisInfo(0, 100, ArraysUtil
				.asUnmodifiableList(50f), "USD");
		yAxis2.setAxisStyle(new AxisStyle(WHITE, 22, AlignmentEnum.CENTER));
		yAxis2.setAxisStyle(axisStyle);

		// Adding axis info to chart.
		chart.addXAxisInfo(xAxis);
		chart.addXAxisInfo(xAxis2);
		chart.addXAxisInfo(xAxis3);
		chart.addYAxisInfo(yAxis);
		chart.addYAxisInfo(yAxis2);
		chart.setGrid(new LineStyle(1, 3, 2), 20, 20);

		// Defining background and chart fills.
		// chart.setBackgroundFill(new SolidFill(new Color("1F1D1D")));
		chart.setBackgroundFill(new SolidFill(new Color("304040")));

		LinearGradientFill fill = new LinearGradientFill(0,
				new Color("363433"), 1);
		fill.addColorAndOffset(new Color("2E2B2A"), 0);
		chart.setAreaFill(fill);
		return chart.createURLString();
	}

}
