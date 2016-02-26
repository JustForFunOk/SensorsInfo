package com.example.test_sensor;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class RtChartsActivity extends Activity {
	
	private Timer timer = new Timer();
    private GraphicalView chart;
    private Button goBackButton;
    private TimerTask task;
    private int ADD = 10;//ÿ�θ���ͼ���������ݸ���,�����첽�߳��еĹ�����.���뱣֤û�½�һ���߳�ʱ֮ǰ�Ǹ��Ѿ����
    private long X;
    private int Y;
    private int add_j=0;
    private int [] addY = new int[ADD];
	private long [] addX = new long[ADD];
	/**��������*/
    private static final int SERIES_NR=1;
    private static final String TAG = "message";
    private TimeSeries series1;
    private XYMultipleSeriesDataset dataset1;
    private Handler handler;
    private Random random=new Random();
    final private int LENGTH=500;//ͼ�����ܹ���ʾ�����ݸ���
    
    /**ʱ������*/
    Date[] xcache = new Date[LENGTH];
	/**����*/
    int[] ycache = new int[LENGTH];
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rt_charts);
		
		//�õ�mainActivity��ѡ���sensor
		Intent intent = this.getIntent();
		int sensorName = intent.getIntExtra("sensorName", Sensor.TYPE_ACCELEROMETER);
		SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		Sensor sensor = sensorManager.getDefaultSensor(sensorName);
		
		
		LinearLayout layout = (LinearLayout)findViewById(R.id.linearlayout);
        //����ͼ��
		chart = ChartFactory.getTimeChartView(this, getDateDemoDataset(), getDemoRenderer(), "hh:mm:ss");
		layout.addView(chart, new LayoutParams(LayoutParams.WRAP_CONTENT,380));
		//ΪTextView����¼�
		goBackButton = (Button) findViewById(R.id.goBackButton);
		goBackButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(RtChartsActivity.this, "ceshiview", 1).show();
				Intent intent = new Intent();
				intent.setClass(RtChartsActivity.this, MainActivity.class);
				startActivity(intent);
			}
		});
		
		sensorManager.registerListener(new SensorEventListener() {
			
			@Override
			public void onSensorChanged(final SensorEvent event) {
				//�½�һ���̣߳�ȥִ��update�ķ�UI����	
				new Thread(new Runnable() {
					@Override
					public void run() {
						addX[add_j]=new Date().getTime();
						addY[add_j]=(int) (event.values[2]*5-50);
						add_j++;
						if (add_j>=ADD) {
							add_j=0;
							Message message = new Message();
	//		        	    message.what = 200;
			        	    handler.sendMessage(message);
						}
					}
				}).start();
			}
			
			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
				// TODO Auto-generated method stub
				
			}
		}, sensor, SensorManager.SENSOR_DELAY_FASTEST);
		
//�ݹ۲죺 NORMALԼ3Hz  UIԼ9Hz  	GAMEԼ25Hz  FASTESTԼ50Hz
		handler = new Handler() {
        	@Override
        	public void handleMessage(Message msg) {
        		//ˢ��ͼ�� 		
        		updateChart();
        		super.handleMessage(msg);
        	}
        };
//        task = new TimerTask() {
//        	@Override
//        	public void run() {
//        		Message message = new Message();
////        	    message.what = 200;
//        	    handler.sendMessage(message);
//        	}
//        };
////        timer.schedule(task, 2*1000,1000);//����֮��ʼִ�У�ÿ��ִ��һ��
//        timer.schedule(task, 1000, 200);
        
//        //�ټ�һ���첽����
//        new Thread(new Runnable() {
//			@Override
//			public void run() {
//				long date_temp = 0;
//				while (true) {
//					if (date_temp!=addX[0]) {
//						date_temp=addX[0];
//						
//
//					}
//				}
//			}
//		}).start();
        
        
        
	}
	
		private void updateChart() {
		    int length = series1.getItemCount();
		    if(length>=LENGTH) length = LENGTH;
		    //��ǰ��ĵ���뻺��
			for (int i = 0; i < length; i++) {
				xcache[i] =  new Date((long)series1.getX(i));
				ycache[i] = (int) series1.getY(i);
			}
		    
			series1.clear();
			for (int j = ADD-1; j >=0; j--) {
				series1.add(new Date(addX[j]), addY[j]);
			}
			for (int k = 0; k < length; k++) {
	    		series1.add(xcache[k], ycache[k]);
	    	}
			//�����ݼ�������µĵ㼯
			dataset1.removeSeries(series1);
			dataset1.addSeries(series1);
			
			
			//���߸���
			chart.invalidate();
		}
	   
	/**
	 * �趨�����ʽ
	 * @return
	 */
	   private XYMultipleSeriesRenderer getDemoRenderer() {
		    XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		    renderer.setChartTitle("ʵʱ����");//����
		    renderer.setChartTitleTextSize(20);
		    renderer.setXTitle("ʱ��");    //x��˵��
		    renderer.setAxisTitleTextSize(16);
		    renderer.setAxesColor(Color.BLACK);
		    renderer.setLabelsTextSize(15);    //����̶������С
		    renderer.setLabelsColor(Color.BLACK);
		    renderer.setLegendTextSize(15);    //����˵��
		    renderer.setXLabelsColor(Color.BLACK);
		    renderer.setYLabelsColor(0,Color.BLACK);
		    renderer.setShowLegend(false);
		    renderer.setMargins(new int[] {20, 30, 100, 0});//ͼ��λ�� �� �� �� ��
		    XYSeriesRenderer r = new XYSeriesRenderer();
		    r.setColor(Color.BLUE);
		    r.setChartValuesTextSize(15);
		    r.setChartValuesSpacing(3);
		    r.setPointStyle(PointStyle.POINT);
		    r.setFillBelowLine(true);
		    r.setFillBelowLineColor(Color.WHITE);
		    r.setFillPoints(true);
		    renderer.addSeriesRenderer(r);
		    renderer.setMarginsColor(Color.WHITE);
		    renderer.setPanEnabled(false,false);
		    renderer.setShowGrid(true);
		    renderer.setYAxisMax(50);
		    renderer.setYAxisMin(-30);
		    renderer.setInScroll(true);  //������С
		    return renderer;
		  }
	   /**
	    * ���ݶ���
	    * @return
	    */
	   private XYMultipleSeriesDataset getDateDemoDataset() {
		    dataset1 = new XYMultipleSeriesDataset();
		    final int nr = LENGTH+1;
		    long value = new Date().getTime();
		    Random r = new Random();
//����SERIES_NRΪ���ߵ��������������á�		    
		    for (int i = 0; i < SERIES_NR; i++) {
		      series1 = new TimeSeries("Demo series " + (i + 1));
		      for (int k = 0; k < nr; k++) {
//�������ó�ʼ�����ߣ�nr�ǳ�ʼ���ݵĸ���������������Ϊ0�ˡ�
		        series1.add(new Date(value-k*100), 0);//ԭ���Ĵ���������д��value+k*1000�ˣ��ֵֹ�
		      }
		      dataset1.addSeries(series1);
		    }
		    Log.i(TAG, dataset1.toString());
		    return dataset1;
		  }
	    @Override
	    public void onDestroy() {
	    	//����������ʱ�ص�Timer
	    	timer.cancel();
	    	super.onDestroy();
	    };
}
