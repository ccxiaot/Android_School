package com.hnucm18jr.roseapp.Shouye.Daka;

import androidx.appcompat.app.AppCompatActivity;
import android.hardware.SensorEvent;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.hnucm18jr.roseapp.R;

public class RunActivity extends AppCompatActivity   {



    private Button btnOk;
    private TextView tvTime,mTextView,mTextView2;

    private boolean bStart=false;
    private Handler mHander=new Handler();
    private int mCount=0;
    private int second1 = 0;
    private int second2 = 0;
    private int minute1 = 0;
    private int minute2 = 0;
    private int hour1 = 0;
    private int hour2 = 0;
    private MapView mMapView = null;
    BaiduMap mBaiduMap=null;
    LocationClient mLocationClient=null;

    float sb;
    int i=0,j=0,a=0;
    private Runnable mCounter=new Runnable() {
        @Override
        public void run() {
            mCount++;
            if (mCount < 10) {
                second1 = mCount;
            }
            //System.out.println("θΏε₯ζε");
            else if (mCount >= 10 && mCount < 60) {
                second2 = mCount / 10;
                second1 = mCount % 10;
            } else {
                minute1 = 1;
                second1 = 0;
                second2 = 0;
            }



            tvTime.setText(" "+ hour2 + hour1 + ":" + minute2 + minute1 + ":" + second2 + second1);
            mHander.postDelayed(this,1000);
        }
        //tvTime.setText(" "+ hour2 + hour1 + ":" + minute2 + minute1 + ":" + second2 + second1);
        //mHander.postDelayed(this,1000);

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);

        }
        setContentView(R.layout.activity_run);

        initView();
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.button4:
                        if (bStart==false){
                            btnOk.setText("ιΏζζε");
                            mHander.post(mCounter);
                        }else{
                            btnOk.setText("εΌε§θ·ζ­₯");
                            mHander.removeCallbacks(mCounter);
                        }
                        bStart=!bStart;
                        break;
                }
            }
        });
        btnOk.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                finish();
                return false;

            }
        });
        //θ·εε°εΎζ§δ»ΆεΌη¨
       mMapView = (MapView) findViewById(R.id.baiduMapView);
        //θ·εε°ε°εΎ
        mBaiduMap = mMapView.getMap();
        //θ?Ύη½?ε°εΎζΎε€§ηεζ°
        init();
        //θ?Ύη½?ε°εΎε?δ½ηδΈδΊεζ°οΌε¦ε?δ½εΎζ οΌη²ΎεΊ¦ει’θ²η­
        configure();
        //ε?δ½εε§ε
        init_location();

    }

    private void initView() {
        btnOk = (Button) findViewById(R.id.button4);
        tvTime = (TextView) findViewById(R.id.textView3);

        mTextView=findViewById(R.id.textView1);
        mTextView2=findViewById(R.id.time11);

    }


    @Override
    protected void onResume() {
        mMapView.onResume();
        //2.δΈΊη³»η»ηε ιεΊ¦δΌ ζε¨ζ³¨εηε¬δΊδ»Ά

        super.onResume();

    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();


    }

    @Override
    protected void onDestroy() {
        mLocationClient.stop();
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }



    /**
     * η»§ζΏζ½θ±‘η±»BDAbstractListenerεΉΆιεεΆonReceieveLocationζΉζ³ζ₯θ·εε?δ½ζ°ζ?οΌεΉΆε°εΆδΌ η»MapViewγ
     */
    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //mapView ιζ―εδΈε¨ε€ηζ°ζ₯ζΆηδ½η½?
            if (location == null || mMapView == null){
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // ζ­€ε€θ?Ύη½?εΌεθθ·εε°ηζΉεδΏ‘ζ―οΌι‘ΊζΆι0-360
                    .direction(location.getDirection()).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
        }
    }

    /**
     *    θ?Ύη½?ε°εΎζΎε€§ηεζ°
     */
    public  void init(){
        //θ?Ύη½?ε°εΎζΎε€§ηεζ°
        MapStatus.Builder  builder=new MapStatus.Builder();
        builder.zoom(18f);
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
    }

    /**
     * θͺε?δΉεε?Ή:
     * εζ°θ―΄ζ
     * (1)ε?δ½ζ¨‘εΌ ε°εΎSDKζ―ζδΈη§ε?δ½ζ¨‘εΌοΌNORMALοΌζ?ιζοΌ, FOLLOWINGοΌθ·ιζοΌ, COMPASSοΌη½ηζοΌ
     * οΌ2οΌζ―ε¦εΌε―ζΉε
     * οΌ3οΌθͺε?δΉε?δ½εΎζ  ζ―ζθͺε?δΉε?δ½εΎζ ζ ·εΌοΌ
     * οΌ4οΌθͺε?δΉη²ΎεΊ¦εε‘«ει’θ²
     * οΌ5οΌθͺε?δΉη²ΎεΊ¦εθΎΉζ‘ι’θ²
     */
    public void configure(){
        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode.FOLLOWING,true,
                BitmapDescriptorFactory.fromResource(R.mipmap.dingdian),
                0xAAFFFF88,0xAA00FF00));
    }

    /**
     * ε?δ½ηεε§ε
     */
    public void init_location(){
        mBaiduMap.setMyLocationEnabled(true);
        mLocationClient = new LocationClient(RunActivity.this);
//ιθΏLocationClientOptionθ?Ύη½?LocationClientηΈε³εζ°
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // ζεΌgps
        option.setCoorType("bd09ll"); // θ?Ύη½?εζ η±»ε
        option.setScanSpan(1000);
//θ?Ύη½?locationClientOption
        mLocationClient.setLocOption(option);
//ζ³¨εLocationListenerηε¬ε¨
        MyLocationListener myLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(myLocationListener);
//εΌε―ε°εΎε?δ½εΎε±
        mLocationClient.start();
    }
}