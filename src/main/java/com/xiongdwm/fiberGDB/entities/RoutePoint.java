package com.xiongdwm.fiberGDB.entities;

import com.xiongdwm.fiberGDB.entities.relationship.Fiber;
import com.xiongdwm.fiberGDB.entities.relationship.FiberConclusion;
import com.xiongdwm.fiberGDB.support.FacilityStage;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.neo4j.core.schema.*;

import java.util.Set;

@Node("RoutePoint")
public class RoutePoint {
    @Id
    private @NotNull Long id;
    private String name;
    private String no;
    private String area;
    private Double lng;
    private Double lat;
    private RoutePointType type;
    private String level;
    private FacilityStage exist;
    @Relationship(type = "FIBER_CONCLUSION", direction = Relationship.Direction.OUTGOING)
    private Set<Fiber> cables;
    @Relationship(type="FIBER_CONCLUSION",direction = Relationship.Direction.OUTGOING)
    private Set<FiberConclusion> conclusions;

    public enum RoutePointType {
        STATION("机房"),
        ODN("光交箱"),
        BOX("分光器"),
        TERMINAL("终端盒"),
        SWITCH_BOX("开关柜"),
        Ring_Main_Unit("环网柜"),
        TYPE_T("T节点"),
        TRANSFORMER_STATION("变电站"),
        TERMINAL_POLE("终端杆"),
        PYLON("铁塔"),
        FIBER_BOX("分纤箱"),//DP
        RESOURCE_DOT("资源点"),//基站
        TYPE_UNDEFINED("未知节点类型");

        RoutePointType(String name) {
            this.name = name;
        }

        private String name;

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
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

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public RoutePointType getType() {
        return type;
    }

    public void setType(RoutePointType type) {
        this.type = type;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public FacilityStage getExist() {
        return exist;
    }

    public void setExist(FacilityStage exist) {
        this.exist = exist;
    }

    public Set<Fiber> getCables() {
        return cables;
    }

    public void setCables(Set<Fiber> cables) {
        this.cables = cables;
    }

    public void addCable(Fiber fiber){
        this.cables.add(fiber);
    }

    @Override
    public String toString() {
        return "RoutePoint{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
