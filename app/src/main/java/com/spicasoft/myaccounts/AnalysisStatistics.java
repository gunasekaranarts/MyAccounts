package com.spicasoft.myaccounts;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import Database.MyAccountsDatabase;
import POJO.AnalysisSummary;
import POJO.TransactionFilter;
import Utils.AppPreferences;

/**
 * Created by USER on 25-04-2018.
 */

public class AnalysisStatistics extends Fragment implements OnChartValueSelectedListener {
    BarChart mChartSummary,mChartExpense;
    TextView Add_Keyword,update_default_keyword;
    MyAccountsDatabase mSqlHelper;
    AnalysisSummary summary;
    AppCompatImageButton lnk_refresh;
    AppCompatButton btn_reset;
    AppCompatEditText from_date,to_date;
    TransactionFilter filter;

    final String[] GeneralSummary = {"Income", "", "Expense","", "Savings","","Outstanding"};
    ArrayList<String> keywords ;
    final DecimalFormat format = new DecimalFormat("##,##,##,##0.00");
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_analysis, container, false);
        mChartSummary = (BarChart) view.findViewById(R.id.chart_summary);
        mChartExpense=(BarChart) view.findViewById(R.id.mChartExpense);
        lnk_refresh=(AppCompatImageButton) view.findViewById(R.id.lnk_refresh);
        from_date=(AppCompatEditText) view.findViewById(R.id.from_date);
        to_date=(AppCompatEditText) view.findViewById(R.id.to_date);
        btn_reset=(AppCompatButton) view.findViewById(R.id.btn_reset);
        Add_Keyword=(TextView)view.findViewById(R.id.Add_Keyword);
        update_default_keyword=(TextView)view.findViewById(R.id.update_default_keyword);
        mChartSummary.setOnChartValueSelectedListener(this);
        mSqlHelper=new MyAccountsDatabase(getActivity());
        if(AppPreferences.getInstance(getContext()).getDefaultKeywords().equals(""))
        {
            AppPreferences.getInstance(getContext()).setDefaultKeywords("home?food?room rent");
        }
        keywords = new ArrayList<String>(Arrays.asList(AppPreferences.getInstance(getContext()).getDefaultKeywords().split("\\?")));
        DrawSummaryChart();


        from_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDialogTransDate("From Date", 1);
            }
        });
        to_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDialogTransDate("To Date", 2);
            }
        });
        lnk_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
                Calendar cal1 = null, cal2 = null;
                if(!from_date.getText().toString().equals("")) {
                    try {
                        Date dates = format.parse(from_date.getText().toString());
                        cal1 = Calendar.getInstance();
                        cal1.setTime(dates);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                if(!to_date.getText().toString().equals("")) {
                    try {
                        Date datess = format.parse(to_date.getText().toString());
                        cal2 = Calendar.getInstance();
                        cal2.setTime(datess);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                if((!from_date.getText().toString().equals(""))&&(!to_date.getText().toString().equals(""))) {
                    if (cal1.after(cal2)) {
                        showAlertWithCancel("To date should be greater than the From date");
                    }
                }
                DrawSummaryChart();
            }
        });
        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                from_date.setText(null);
                to_date.setText(null);
                keywords= new ArrayList<String>(Arrays.asList(AppPreferences.getInstance(getContext()).getDefaultKeywords().split("\\?")));
                DrawSummaryChart();
            }
        });
        Add_Keyword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.dialog_add_graph_item);
                final AppCompatEditText txt_keyword=(AppCompatEditText) dialog.findViewById(R.id.txt_keyword);
                Button Ok = (Button) dialog.findViewById(R.id.btn_add);

                Ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(txt_keyword.getText().toString().equals("")){
                            txt_keyword.setError("Should not be empty!!!");
                            txt_keyword.requestFocus();
                        }else {
                            keywords.add(txt_keyword.getText().toString());
                            dialog.dismiss();
                            summary=mSqlHelper.getGraphData(filter,keywords);
                            DrawExpenseChart();
                        }

                    }
                });
                dialog.show();
            }
        });

        update_default_keyword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.dialog_update_default_graph_item);
                final AppCompatEditText txt_keyword1=(AppCompatEditText) dialog.findViewById(R.id.txt_keyword1);
                final AppCompatEditText txt_keyword2=(AppCompatEditText) dialog.findViewById(R.id.txt_keyword2);
                final AppCompatEditText txt_keyword3=(AppCompatEditText) dialog.findViewById(R.id.txt_keyword3);
                Button Update = (Button) dialog.findViewById(R.id.btn_update);

                txt_keyword1.setText(keywords.get(0));
                txt_keyword2.setText(keywords.get(1));
                txt_keyword3.setText(keywords.get(2));
                Update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(txt_keyword1.getText().toString().equals("")){
                            txt_keyword1.setError("Should not be empty!!!");
                            txt_keyword1.requestFocus();
                        }else if(txt_keyword2.getText().toString().equals("")){
                            txt_keyword2.setError("Should not be empty!!!");
                            txt_keyword2.requestFocus();
                        }  else if(txt_keyword3.getText().toString().equals("")){
                            txt_keyword3.setError("Should not be empty!!!");
                            txt_keyword3.requestFocus();
                        }
                        else {
                            AppPreferences.getInstance(getContext()).setDefaultKeywords(
                                    txt_keyword1.getText().toString()+"?"+
                                            txt_keyword2.getText().toString()+"?"+
                                            txt_keyword3.getText().toString()
                            );
                            keywords=new ArrayList<String>(Arrays.asList(AppPreferences.getInstance(getContext()).getDefaultKeywords().split("\\?")));
                            dialog.dismiss();
                            summary=mSqlHelper.getGraphData(filter,keywords);
                            DrawExpenseChart();
                        }

                    }
                });

                dialog.show();

            }
        });
        return view;
    }
    IAxisValueFormatter custom = new IAxisValueFormatter() {

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            String result="";
            if(value<1)
                result=format.format(value);
            if(value>=1 && value<100)
                result=format.format(value)+"K";
            if(value>=100 && value<1000)
                result=format.format(value/100)+"L";
            if(value>=1000 && value<10000)
                result=format.format(value/1000)+"C";
            return result;
        }
    };

    private void DrawSummaryChart() {
        filter=new TransactionFilter();
        if(!from_date.getText().toString().equals(""))
            filter.setFromDate(from_date.getText().toString());
        if(!to_date.getText().toString().equals(""))
            filter.setToDate(to_date.getText().toString());

        summary=mSqlHelper.getGraphData(filter,keywords);
        mChartSummary.setDrawBarShadow(false);
        mChartSummary.setDrawValueAboveBar(true);
        mChartSummary.animateXY(2000,3000);
        mChartSummary.getDescription().setEnabled(false);
        mChartSummary.setMaxVisibleValueCount(20);
        mChartSummary.setPinchZoom(false);
        mChartSummary.setDrawGridBackground(false);

        XAxis xAxis = mChartSummary.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //xAxis.setTypeface(mTfLight);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(GeneralSummary));

        YAxis leftAxis = mChartSummary.getAxisLeft();
        //leftAxis.setTypeface(mTfLight);
        leftAxis.setLabelCount(8, false);
        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis rightAxis = mChartSummary.getAxisRight();
        rightAxis.setEnabled(false);
//        rightAxis.setDrawGridLines(false);
        //rightAxis.setTypeface(mTfLight);
//        rightAxis.setLabelCount(8, false);
//        rightAxis.setValueFormatter(custom);
//        rightAxis.setSpaceTop(15f);
//        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)


        Legend l = mChartSummary.getLegend();
        l.setEnabled(false);
//        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
//        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
//        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
//        l.setDrawInside(false);
//        l.setForm(Legend.LegendForm.SQUARE);
//        l.setFormSize(9f);
//        l.setTextSize(11f);
//        l.setXEntrySpace(4f);

        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0f, (summary.getIncome()/(float)1000)));
        entries.add(new BarEntry(2f, (summary.getExpense()/(float)1000)));
        entries.add(new BarEntry(4f, (summary.getSaving()/(float)1000)));
        entries.add(new BarEntry(6f, (summary.getCredit()/(float)1000)));


        BarDataSet set = new BarDataSet(entries, "Summary");
        set.setColors(new int[]{Color.GREEN,Color.RED,Color.BLUE,Color.DKGRAY});
        set.setValueTextSize(11f);
        set.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return "₹"+format.format(value*1000);
            }
        });
        BarData data = new BarData(set);
        data.setBarWidth(0.7f); // set custom bar width
        mChartSummary.setData(data);
        mChartSummary.setFitBars(true); // make the x-axis fit exactly all bars
        mChartSummary.invalidate();
        DrawExpenseChart();
        // l.setExtra(ColorTemplate.VORDIPLOM_COLORS, new String[] { "abc",
        // "def", "ghj", "ikl", "mno" });
        // l.setCustom(ColorTemplate.VORDIPLOM_COLORS, new String[] { "abc",
        // "def", "ghj", "ikl", "mno" });

//        XYMarkerView mv = new XYMarkerView(this, xAxisFormatter);
//        mv.setChartView(mChart); // For bounds control
//        mChart.setMarker(mv); // Set the marker to the chart
//
//        setData(12, 50);
    }

    private void DrawExpenseChart()
    {

        mChartExpense.setDrawBarShadow(false);
        mChartExpense.setDrawValueAboveBar(true);
        mChartExpense.animateXY(2000,3000);
        mChartExpense.getDescription().setEnabled(false);
        mChartExpense.setMaxVisibleValueCount(20);
        mChartExpense.setPinchZoom(false);

        mChartExpense.setDrawGridBackground(false);

        //IAxisValueFormatter xAxisFormatter = new DayAxisValueFormatter(mChartSummary);

        XAxis xAxis = mChartExpense.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //xAxis.setTypeface(mTfLight);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(keywords));



        YAxis leftAxis = mChartExpense.getAxisLeft();
        //leftAxis.setTypeface(mTfLight);
        leftAxis.setLabelCount(8, false);
        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis rightAxis = mChartExpense.getAxisRight();
        rightAxis.setEnabled(false);
//        rightAxis.setDrawGridLines(false);
        //rightAxis.setTypeface(mTfLight);
//        rightAxis.setLabelCount(8, false);
//        rightAxis.setValueFormatter(custom);
//        rightAxis.setSpaceTop(15f);
//        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)


        Legend l = mChartExpense.getLegend();
        l.setEnabled(false);
//        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
//        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
//        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
//        l.setDrawInside(false);
//        l.setForm(Legend.LegendForm.SQUARE);
//        l.setFormSize(9f);
//        l.setTextSize(11f);
//        l.setXEntrySpace(4f);

        List<BarEntry> entries = new ArrayList<>();
        ArrayList<Integer> values=summary.getKeywords();
        for(int i=0;i<values.size();i++){
            entries.add(new BarEntry((float)i, (values.get(i)/(float)1000)));
        }
//        entries.add(new BarEntry(0f, (summary.getKeyword1()/(float)1000)));
//        entries.add(new BarEntry(2f, (summary.getKeyword3()/(float)1000)));
//        entries.add(new BarEntry(4f, (summary.getKeyword2()/(float)1000)));
        //entries.add(new BarEntry(6f, (summary.getCredit()/(float)1000)));


        BarDataSet set = new BarDataSet(entries, "Expense Summary");
        set.setColors(new int[]{Color.GREEN,Color.RED,Color.BLUE,Color.DKGRAY});
        set.setValueTextSize(11f);
        set.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return "₹"+format.format(value*1000);
            }
        });
        BarData data = new BarData(set);
        data.setBarWidth(0.3f); // set custom bar width
        mChartExpense.setData(data);
        mChartExpense.setFitBars(true); // make the x-axis fit exactly all bars
        mChartExpense.invalidate();
        // l.setExtra(ColorTemplate.VORDIPLOM_COLORS, new String[] { "abc",
        // "def", "ghj", "ikl", "mno" });
        // l.setCustom(ColorTemplate.VORDIPLOM_COLORS, new String[] { "abc",
        // "def", "ghj", "ikl", "mno" });

//        XYMarkerView mv = new XYMarkerView(this, xAxisFormatter);
//        mv.setChartView(mChart); // For bounds control
//        mChart.setMarker(mv); // Set the marker to the chart
//
//        setData(12, 50);
    }

    protected RectF mOnValueSelectedRectF = new RectF();
    @Override
    public void onValueSelected(Entry e, Highlight h) {
        if (e == null)
            return;

        RectF bounds = mOnValueSelectedRectF;
        mChartSummary.getBarBounds((BarEntry) e, bounds);
        MPPointF position = mChartSummary.getPosition(e, YAxis.AxisDependency.LEFT);



        MPPointF.recycleInstance(position);
    }

    @Override
    public void onNothingSelected() {

    }
    private void ShowDialogTransDate(String Titile, final int i) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_datetimepicker);
        TextView title = (TextView) dialog.findViewById(R.id.dialog_datetime_title);
        title.setText("" + Titile);
        Button Ok = (Button) dialog.findViewById(R.id.setdatetime_ok);
        final DatePicker dp = (DatePicker) dialog.findViewById(R.id.date_picker);
        Button cancel = (Button) dialog.findViewById(R.id.setdatetime_cancel);
        dp.setMaxDate((System.currentTimeMillis() - 1000));
        Ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String month = String.valueOf(dp.getMonth() + 1);
                String day = String.valueOf(dp.getDayOfMonth());
                String strDateTime = ( dp.getYear()+ "-" + (((month.length() == 1 ? "0" + month : month))) + "-" + (day.length() == 1 ? "0" + day : day));

                switch (i) {
                    case 1:
                        from_date.setText("" + strDateTime);
                        break;
                    case 2:
                        to_date.setText("" + strDateTime);
                        break;
                    default:
                }
                dialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }
    public void showAlertWithCancel(String BuilderText) {
        android.support.v7.app.AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
        builder.setTitle("My Accounts");
        builder.setMessage(BuilderText);
        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
}
