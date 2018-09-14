package terry.com.ncstbuildingdatasetgatherer.cameraContents;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import java.text.SimpleDateFormat;

public class MyLocationListener implements LocationListener
{//位置变化监听器
    public double latitude;//获得 经度
    public double longitude;//获得 纬度
    @Override	//当位置变化时触发
    public void onLocationChanged(Location location)
    {
        if(location!=null)
        {
            try
            {
                latitude = location.getLatitude();//获得 经度
                longitude = location.getLongitude();//获得 纬度
                double altitude = location.getAltitude();  //获得 海拔
                double speed = location.getSpeed();//获得速度
                double accuracy = location.getAccuracy();//获得精度 精确到m
                double degrees =location.getBearing();//获得方位角   东偏北n度
                double time=location.getTime();//获得卫星上的时间
                //卫星上的时间转换为当前时间
                SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String timeNow=format.format(time);
                StringBuilder sb=new StringBuilder("当前GPS信息为：\n");
                sb.append("经度 ："+latitude+"\n");
                sb.append("纬度 ："+longitude+"\n");
                sb.append("海拔 ："+altitude+"\n");
                sb.append("速度 ："+speed+"\n");
                sb.append("精度m ："+accuracy+"\n");
                sb.append("方位角："+degrees+"\n");
                sb.append("时间 ："+timeNow+"\n");
                Log.e("LocationListener",sb.toString());
            }catch(Exception e)
            {
                e.printStackTrace();
            }}}
    @Override//Location Provider被禁用时更新
    public void onProviderDisabled(String provider){}
    @Override//Location Provider被启用时更新
    public void onProviderEnabled(String provider){}
    @Override//当Provider硬件状态变化时更新
    public void onStatusChanged(String provider, int status,Bundle extras){}
}
