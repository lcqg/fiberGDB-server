package com.xiongdwm.fiberGDB.bo.fiberRDB;

import com.xiongdwm.fiberGDB.entities.RoutePoint;
import com.xiongdwm.fiberGDB.support.FacilityStage;

import java.util.Date;

public class RoutePointEntityRDB {
    private Long id;
    private String name;
    private Double lat;
    private Double lng;
    private String address;
    private RoutePoint.RoutePointType type;
    private String gps84;
    private String icon;
    private String street;
    private String area;
    private String pics; //照片 逗号分隔
    private String level;
    private Date upTime; //设施启用时间
    private Long fromClient; //根据用户
    private String qrCode; //二维码
    private FacilityStage exist;
    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public RoutePoint.RoutePointType getType() {
        return type;
    }

    public void setType(RoutePoint.RoutePointType type) {
        this.type = type;
    }

    public String getGps84() {
        return gps84;
    }

    public void setGps84(String gps84) {
        this.gps84 = gps84;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getPics() {
        return pics;
    }

    public void setPics(String pics) {
        this.pics = pics;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Date getUpTime() {
        return upTime;
    }

    public void setUpTime(Date upTime) {
        this.upTime = upTime;
    }

    public Long getFromClient() {
        return fromClient;
    }

    public void setFromClient(Long fromClient) {
        this.fromClient = fromClient;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public FacilityStage getExist() {
        return exist;
    }

    public void setExist(FacilityStage exist) {
        this.exist = exist;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
