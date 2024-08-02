package com.xiongdwm.fiberGDB.entities;

import com.xiongdwm.fiberGDB.entities.relationship.Fiber;
import jakarta.validation.constraints.NotNull;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.exceptions.NoSuchRecordException;
import org.neo4j.driver.summary.ResultSummary;
import org.springframework.data.neo4j.core.schema.*;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

@Node
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
    private String exist;
    @Relationship(type = "fiber", direction = Relationship.Direction.OUTGOING)
    private Set<Fiber> cables;

    public enum RoutePointType{
        STATION("机房"),
        ODN("光交箱"),
        BOX("分线盒"),
        TERMINAL("终端盒"),
        SWITCH_BOX("开关柜"),
        RING_MAIN_UNIT("环网柜"),
        TYPE_T("T节点"),
        TRANSFORMER_STATION("变电站"),
        TERMINAL_POLE("终端杆"),
        PYLON("铁塔"),
        PI_NODE("Π节点"),
        TYPE_UNDEFINED("未知节点类型");
        private final String info;
        RoutePointType(String info){
            this.info=info;
        }
        public String getInfo(){
            return info;
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

    public String getExist() {
        return exist;
    }

    public void setExist(String exist) {
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
