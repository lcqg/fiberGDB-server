package com.xiongdwm.fiberGDB.support;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.xiongdwm.fiberGDB.support.exception.CoordinateSystemException;

public class GpsUtils {

    public static double pi = 3.1415926535897932384626;
    public static double x_pi = 3.14159265358979324 * 3000.0 / 180.0;
    public static double a = 6378245.0;
    public static double ee = 0.00669342162296594323;

    public static double transformLat(double x, double y) {
        double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y
                + 0.2 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(y * pi) + 40.0 * Math.sin(y / 3.0 * pi)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(y / 12.0 * pi) + 320 * Math.sin(y * pi / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    public static double transformLon(double x, double y)  {
        double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1
                * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(x * pi) + 40.0 * Math.sin(x / 3.0 * pi)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(x / 12.0 * pi) + 300.0 * Math.sin(x / 30.0
                * pi)) * 2.0 / 3.0;
        return ret;
    }

    public static double[] transform(double lat, double lon) {
        if (outOfChina(lat, lon)) {
            return new double[]{lat,lon};
        }
        double dLat = transformLat(lon - 105.0, lat - 35.0);
        double dLon = transformLon(lon - 105.0, lat - 35.0);
        double radLat = lat / 180.0 * pi;
        double magic = Math.sin(radLat);
        magic = 1 - ee * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * pi);
        dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * pi);
        double mgLat = lat + dLat;
        double mgLon = lon + dLon;
        return new double[]{mgLat,mgLon};
    }
    public static boolean outOfChina(double lat, double lon) {
        if (lon < 72.004 || lon > 137.8347)
            return true;
        if (lat < 0.8293 || lat > 55.8271)
            return true;
        return false;
    }
    /**
     * 84 to 火星坐标系 (GCJ-02) World Geodetic System ==> Mars Geodetic System
     *
     * @param lat
     * @param lon
     * @return
     */
    public static double[] gps84_To_Gcj02(double lat, double lon) {
        if (outOfChina(lat, lon)) {
            return new double[]{lat,lon};
        }
        double dLat = transformLat(lon - 105.0, lat - 35.0);
        double dLon = transformLon(lon - 105.0, lat - 35.0);
        double radLat = lat / 180.0 * pi;
        double magic = Math.sin(radLat);
        magic = 1 - ee * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * pi);
        dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * pi);
        double mgLat = lat + dLat;
        double mgLon = lon + dLon;
        return new double[]{mgLat, mgLon};
    }

    /**
     * * 火星坐标系 (GCJ-02) to 84 * * @param lon * @param lat * @return
     * */
    public static double[] gcj02_To_Gps84(double lat, double lon) {
        double[] gps = transform(lat, lon);
        double lontitude = lon * 2 - gps[1];
        double latitude = lat * 2 - gps[0];
        return new double[]{latitude, lontitude};
    }
    /**
     * 火星坐标系 (GCJ-02) 与百度坐标系 (BD-09) 的转换算法 将 GCJ-02 坐标转换成 BD-09 坐标
     *
     * @param lat
     * @param lon
     */
    public static double[] gcj02_To_Bd09(double lat, double lon) {
        double x = lon, y = lat;
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * x_pi);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * x_pi);
        double tempLon = z * Math.cos(theta) + 0.0065;
        double tempLat = z * Math.sin(theta) + 0.006;
        double[] gps = {tempLat,tempLon};
        return gps;
    }

    /**
     * * 火星坐标系 (GCJ-02) 与百度坐标系 (BD-09) 的转换算法 * * 将 BD-09 坐标转换成GCJ-02 坐标 * * @param
     * bd_lat * @param bd_lon * @return
     */
    public static double[] bd09_To_Gcj02(double lat, double lon) {
        double x = lon - 0.0065, y = lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_pi);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_pi);
        double tempLon = z * Math.cos(theta);
        double tempLat = z * Math.sin(theta);
        double[] gps = {tempLat,tempLon};
        return gps;
    }

    /**将gps84转为bd09
     * @param lat
     * @param lon
     * @return
     */
    public static double[] gps84_To_Bd09(double lat,double lon){
        double[] gcj02 = gps84_To_Gcj02(lat,lon);
        double[] bd09 = gcj02_To_Bd09(gcj02[0],gcj02[1]);
        return bd09;
    }
    public static double[] any_to_Bd09(double lat,double lng,String system){
        if(!CoordinateSystem.contains(system))throw new CoordinateSystemException("坐标系错误："+system);
        CoordinateSystem systemEnum=CoordinateSystem.valueOf(system);
        switch (systemEnum){
            case BD09:
                return new double[]{lat,lng};
            case GCJ09:
                return gcj02_To_Bd09(lat,lng);
            case GPS84:
                return gps84_To_Bd09(lat,lng);
        }
        throw new CoordinateSystemException("坐标系错误："+system);
    }
    public static double[] bd09_To_Gps84(double lat,double lon){
        double[] gcj02 = bd09_To_Gcj02(lat, lon);
        double[] gps84 = gcj02_To_Gps84(gcj02[0], gcj02[1]);
        //保留小数点后六位
        gps84[0] = retain6(gps84[0]);
        gps84[1] = retain6(gps84[1]);
        return gps84;
    }

    /**保留小数点后六位
     * @param num
     * @return
     */
    private static double retain6(double num){
        String result = String .format("%.6f", num);
        return Double.valueOf(result);
    }


    //google map
    private static final  double EARTH_RADIUS = 6378137;//赤道半径(单位m)

    /**
     * 转化为弧度(rad)
     * */
    private static double rad(double d)
    {
        return d * Math.PI / 180.0;
    }
    /**
     * 基于googleMap中的算法得到两经纬度之间的距离,计算精度与谷歌地图的距离精度差不多，相差范围在0.2米以下
     * @param lon1 第一点的精度
     * @param lat1 第一点的纬度
     * @param lon2 第二点的精度
     * @param lat2 第二点的纬度
     * @return 返回的距离，单位kmg
     * */
    public static double getDistance(double lon1,double lat1,double lon2, double lat2)
    {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lon1) - rad(lon2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2)+Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2),2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return s;
    }

    public static Boolean isPointInPolygon(List<double[]> polygon, double[] pt) {
        int ptNum = polygon.size();
        if (ptNum < 3) {
            return false;
        }

        int j = ptNum - 1;
        Boolean oddNodes = false;
        for (int k = 0; k < ptNum; k++) {
            double[] ptK = polygon.get(k);
            double[] ptJ = polygon.get(j);
            if (((ptK[0] > pt[0]) != (ptJ[0] > pt[0])) && (pt[1] < (ptJ[1] - ptK[1]) * (pt[0] - ptK[0]) / (ptJ[0] - ptK[0]) + ptK[1])) {
                oddNodes = !oddNodes;
            }
            j = k;
        }
        return oddNodes;
    }

    /**
     *
     * @param lat 纬度
     * @param lng 经度
     * @param distance 距离 单位：米 可以为负数
     * @return 根据一个经纬度和距离得到大致的经纬度
     */
    // public static double[] coordinateAdding(double lat,double lng,double distance){
    //     double differ=ArithmeticUtils.Companion.times(0.00000899322,distance);
    //     double lat1=ArithmeticUtils.Companion.add(lat,differ);
    //     double lng1=ArithmeticUtils.Companion.add(lng,differ/Math.cos(lat*Math.PI/180));
    //     return new double[]{lng1,lat1};
    // }

    public static String getCenterPoint(List<double[]> listCoordinates) {
        double[] center = new double[2];
        double x = 0, y = 0, z = 0;
        for (double[] c : listCoordinates) {
            double lat = c[0] * Math.PI / 180;
            double lon = c[1] * Math.PI / 180;
            x += Math.cos(lat) * Math.cos(lon);
            y += Math.cos(lat) * Math.sin(lon);
            z += Math.sin(lat);
        }
        x = x / listCoordinates.size();
        y = y / listCoordinates.size();
        z = z / listCoordinates.size();
        double lon = Math.atan2(y, x);
        double hyp = Math.sqrt(x * x + y * y);
        double lat = Math.atan2(z, hyp);
        center[0] = lat * 180 / Math.PI;
        center[1] = lon * 180 / Math.PI;
        return center[0] + "," + center[1];
    }

    public static double[][] findCircleIntersections(double x1, double y1, double r1, double x2, double y2, double r2) {
        double d = getDistance(x1, y1, x2, y2);

        if (d > r1 + r2 || d < Math.abs(r1 - r2) || (d == 0 && r1 == r2)) {
            return new double[0][0];
        }

        double a = (r1 * r1 - r2 * r2 + d * d) / (2 * d);
        double h = Math.sqrt(r1 * r1 - a * a);
        double x3 = x1 + a * (x2 - x1) / d;
        double y3 = y1 + a * (y2 - y1) / d;

        if (h == 0) {
            return new double[][]{{x3, y3}};
        } else {
            double x4 = x3 + h * (y2 - y1) / d;
            double y4 = y3 - h * (x2 - x1) / d;
            double x5 = x3 - h * (y2 - y1) / d;
            double y5 = y3 + h * (x2 - x1) / d;
            return new double[][]{{x4, y4}, {x5, y5}};
        }
    }

    public enum CoordinateSystem{
        BD09,
        GCJ09,
        GPS84;
        public static boolean contains(String s){
            List<String> v= Arrays.stream(CoordinateSystem.values()).map(CoordinateSystem::name).collect(Collectors.toList());
            return v.contains(s);
        }

    }
}