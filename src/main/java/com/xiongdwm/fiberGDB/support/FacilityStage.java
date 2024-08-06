package com.xiongdwm.fiberGDB.support;

import java.util.List;

public enum FacilityStage {
    ABANDON("冗余资源信息",-2),
    EXCLUDE("不计入",-2),
    OVERLOAD("满超载",-1),
    NONEXISTENT("现场无",-1),
    BE_MERGED("被合并",-1), //2
    UNAVAILABLE("不可用",-1),
    SKETCH("规划建设",-1),
    FIBER_NO_IDLE("纤芯不足",-1),
    UNDER_CALIBRATION("勘误进行",-1), //1 2
    REPAIR("维修进行",-1), //1 2
    DEMANDED("规划新建",0),
    PROPOSE("工程新建",0), //1 2
    FIBER_TAIL("尾纤直连", 1), //1 2
    FIBER_FUSION("直熔", 1), //1 2
    INNER_FIBER("内缆",1),
    METRO_FIBER("地铁光缆",1),
    DESIGN("设计完成",1),
    WAIT_FOR_CONSTRUCT("待建设",1),
    UNDER_CONSTRUCTION("建设中",1),
    WAIT_FOR_MAINTENANCE("待交维",1), //1 2
    OLD_NEW("利旧新建",1), //工程新建变待交维需要变成利旧新建
    NOT_RECORDED("资管无/资管需更新",1), //2
    UNDER_RUNNING("在网",1), //1 2

    FIBER_OPTIC_CORE("光交分芯", 1),
    CONNECTOR_CORE("接头分芯", 1),
    DISTRIBUTION_CORE("分纤分芯", 1),

    QRCODE_UPDATE("二维码更新",1), //1 2
    UNDEFINE("未知",1);
    private final String text;
    private final Integer code;


    FacilityStage(String text, Integer code) {
        this.text = text;
        this.code = code;
    }

    public String getText() {
        return text;
    }

    public Integer getCode() {
        return code;
    }

    public static List<FacilityStage> getHalf(){
        return List.of(
                FIBER_TAIL,
                FIBER_FUSION,
                INNER_FIBER,
                METRO_FIBER
        );
    }

}
