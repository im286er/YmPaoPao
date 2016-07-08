package com.yeming.paopao.aty;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.yeming.paopao.R;
import com.yeming.paopao.app.YmApplication;
import com.yeming.paopao.views.ToastView;


/**
 * author   YeMing(yeming_1001@163.com)
 * Date:    2015-01-28 21:27
 * version: V1.0
 * Description:    定位 地图 页面
 */
public class MapLocationActivity extends Activity{

    private static final String TAG = "MapLocationActivity";
    private Context context ;
    // 定位相关
    LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    private MyLocationConfiguration.LocationMode mCurrentMode;
    BitmapDescriptor mCurrentMarker;
    //Marker mCurrentMarker ;
    MapView mMapView;
    BaiduMap mBaiduMap;
    boolean isFirstLoc = true;// 是否首次定位
    boolean isRequest = false;// 是否手动触发请求定位
    private Marker mMarker;
    private InfoWindow mInfoWindow;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.map_location_layout);

        YmApplication.getInstance().addActivity(this);
        context = this ;

        setActionBar();

        mCurrentMode = MyLocationConfiguration.LocationMode.FOLLOWING;
        // 地图初始化
        mMapView = (MapView) findViewById(R.id.mapView);

        mMapView.removeViewAt(1); // 去掉百度logo
    //    mMapView.showScaleControl(false);// 隐藏比例尺控件
        //隐藏缩放按钮
        mMapView.showZoomControls(false);
        mBaiduMap = mMapView.getMap();

    //    mBaiduMap.setMyLocationEnabled(true);
        mBaiduMap.setMyLocationEnabled(false); //不显示我的位置，样覆盖物代替

        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() { //点击覆盖物事件

            @Override
            public boolean onMarkerClick(Marker arg0) {
                showLocationPop(arg0);
                return false;
            }
        });

        // 修改为自定义marker
    //    mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.location_mark);
     //   mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
     //                   mCurrentMode, true, mCurrentMarker));

        // 传入null则，恢复默认图标
    //    mCurrentMarker = null;
     //   mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
     //           mCurrentMode, true, null));

        // 定位初始化
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(5000);
        option.setNeedDeviceDirect(true);
        option.setAddrType("all");// 返回的定位结果包含地址信息
        option.setIsNeedAddress(true);//返回的定位结果包含地址信息
        mLocClient.setLocOption(option);
        mLocClient.start();
    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null)
                return;
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                            // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            if (isRequest || isFirstLoc) {
                isFirstLoc = false;
                isRequest = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());

                /*OverlayOptions overlayOptions = new MarkerOptions().position(ll).icon(mCurrentMarker) ;
               mBaiduMap.addOverlay(overlayOptions) ;*/

                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
                mBaiduMap.animateMapStatus(u);

                initOverlay(ll,location.getAddrStr());


            }
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }

    /**
     * 使用覆盖物，初始化
     * @param ll       坐标
     * @param address     位置信息
     */
    private void initOverlay(LatLng ll,String address) {
        //修改为自定义marker
        mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.sm_mark_icon);
        // 构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions().position(ll).icon(mCurrentMarker);
        // 在地图上添加Marker，并显示
        Marker marker = (Marker) mBaiduMap.addOverlay(option) ;
        marker.setTitle(address);
    }

    /**
     * 显示位置泡泡
     * @param marker
     */
    private void showLocationPop(final Marker marker){
        View view = LayoutInflater.from(this).inflate(R.layout.location_paopao_layout, null); //自定义气泡形状
        TextView textView = (TextView) view.findViewById(R.id.title);
        textView.setTypeface(YmApplication.chineseTypeface);

        LatLng pt = null;
        double latitude, longitude;
        latitude = marker.getPosition().latitude;
        longitude = marker.getPosition().longitude;
        pt = new LatLng(latitude + 0.0004, longitude + 0.00005);
        textView.setText(marker.getTitle());

        // 定义用于显示该InfoWindow的坐标点
        // 创建InfoWindow的点击事件监听者
        /*InfoWindow.OnInfoWindowClickListener listener = null ;
        listener = new InfoWindow.OnInfoWindowClickListener() {
            public void onInfoWindowClick() {
                mBaiduMap.hideInfoWindow();//影藏气泡

            }
        };*/
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBaiduMap.hideInfoWindow();//影藏气泡
            }
        });
        // 创建InfoWindow
        mInfoWindow = new InfoWindow(view,pt,-35) ;
        mBaiduMap.showInfoWindow(mInfoWindow); //显示气泡
    }




    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        // 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }

    /**
     * 手动触发一次定位请求
     */
    public void requestLocClick() {
        isRequest = true;
        mLocClient.requestLocation();
        ToastView.showToast(context,R.string.locationing, Toast.LENGTH_SHORT);
    }

    /**
     * actionbar style
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void setActionBar(){
        //this.getActionBar().setTitle("PaoPao");
        getActionBar().setBackgroundDrawable(
                this.getBaseContext().getResources()
                        .getDrawable(R.drawable.actionbar_bg));
        //getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);
        int titleId = Resources.getSystem().getIdentifier("action_bar_title",
                "id", "android");
        TextView textView = (TextView) findViewById(titleId);
        textView.setTypeface(YmApplication.chineseTypeface);
        textView.setTextColor(0xFFdfdfdf);
        textView.setTextSize(20);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map_location_action, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId() ;
        switch (id){
            case R.id.action_location:
                requestLocClick() ;
                break ;
            case android.R.id.home:
                onBackPressed();
                break ;
        }
        return super.onMenuItemSelected(featureId, item);
    }
}
