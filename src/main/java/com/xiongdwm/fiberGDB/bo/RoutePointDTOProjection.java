package com.xiongdwm.fiberGDB.bo;

import com.xiongdwm.fiberGDB.entities.RoutePoint;
import jakarta.validation.constraints.NotNull;

public class RoutePointDTOProjection {
    private @NotNull Long id;
    private String name;
    private String no;
    private String area;
    private Double lng;
    private Double lat;
    private RoutePoint.RoutePointType type;
    private String level;
    private String exist;

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

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public RoutePoint.RoutePointType getType() {
        return type;
    }

    public void setType(RoutePoint.RoutePointType type) {
        this.type = type;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getExist() {
        return exist;
    }

    public void setExist(String exist) {
        this.exist = exist;
    }

    @Override
    public String toString() {
        return "RoutePointDTOProjection{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", lng=" + lng +
                ", lat=" + lat +
                '}';
    }
}
