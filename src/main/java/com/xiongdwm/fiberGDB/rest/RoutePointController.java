package com.xiongdwm.fiberGDB.rest;

import com.xiongdwm.fiberGDB.bo.FiberDto;
import com.xiongdwm.fiberGDB.bo.PathResult;
import com.xiongdwm.fiberGDB.bo.requestEntity.SearchRouteParam;
import com.xiongdwm.fiberGDB.entities.RoutePoint;
import com.xiongdwm.fiberGDB.entities.relationship.Fiber;
import com.xiongdwm.fiberGDB.resources.RoutePointResources;
import com.xiongdwm.fiberGDB.support.RSAUtils;
import com.xiongdwm.fiberGDB.support.View;
import com.xiongdwm.fiberGDB.support.orm.helper.AbstractCypherHelper;
import com.xiongdwm.fiberGDB.support.serialize.JacksonUtil;


import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;

@RestController
public class RoutePointController {
    @Resource
    private RSAUtils rsaUtils;
    @Resource
    private RoutePointResources routePointResources;

    @RequestMapping("/web/init")
    public Object init() throws Exception {
        System.out.println("=================init=========================");
        Map<String, Object> map = new HashMap<>();
        map.put("code", 200);
        map.put("msg", "success");
        return JacksonUtil.mapToNode(map);
    }

    @RequestMapping("/web/getSignature")
    public Object getSignature(String info) throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("code", 200);
        map.put("msg", rsaUtils.decrypt(info) + "====================" + info);
        return JacksonUtil.mapToNode(map);
    }

    @RequestMapping("/test")
    public Object test(Long ak) throws Exception {
        System.out.println(ak);
        return View.SUCCESS;
    }


    @RequestMapping("/point/add")
    public Object save(RoutePoint p) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", 200);
        map.put("msg", routePointResources.save(p));
        return map;
    }

    @RequestMapping("/rel/add")
    public Object saveRel(FiberDto fiberDto) {
        // routePointResources.createFiber(fiberDto.getFromId(),fiberDto.getToId(),fiberDto.getFiber()).block();
        Fiber fiber = new Fiber();
        BeanUtils.copyProperties(fiberDto, fiber);
        routePointResources.createFiberNoneReactive(fiberDto.getFromId(), fiberDto.getToId(), fiber, AbstractCypherHelper.OperationType.CREATE);
        return View.SUCCESS;
    }

    @RequestMapping("/rel/searchRoute")
    public Object searchRoute(SearchRouteParam param) {
        return View.getSuccess(routePointResources.retrieve(param.startId(), param.endId(), param.weight(), param.routeCount(),param.maxDistance(),param.siteType()));
    }

    @RequestMapping("/rel/searchRouteByStationName")
    public List<PathResult> streamingSearchRoute(@RequestBody SearchRouteParam param) {
        Long startId = param.startId();
        Long endId = param.endId();
        if(startId == null || endId == null) {
            RoutePoint start = routePointResources.findRoutePointByName(param.fromStation());
            System.out.println("start:"+start);
            RoutePoint end = routePointResources.findRoutePointByName(param.toStation());
            System.out.println("end:"+end);
            if (start == null || end == null) {
                return emptyList();
            }
            startId = start.getId();
            endId = end.getId();
        }
        System.out.println("路径搜索");
        return routePointResources.retrieve(startId, endId, param.weight(), param.routeCount(),param.maxDistance(), param.siteType());
    }
}
