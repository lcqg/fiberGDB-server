package com.xiongdwm.fiberGDB.support.orm.entity;

import com.xiongdwm.fiberGDB.entities.RoutePoint;
import com.xiongdwm.fiberGDB.entities.relationship.Fiber;
import com.xiongdwm.fiberGDB.support.orm.entity.iInterfaces.DynamicPrinciple;
import com.xiongdwm.fiberGDB.support.orm.helper.AbstractCypherHelper;
import com.xiongdwm.fiberGDB.support.orm.helper.exception.Neo4jPrincipleException;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EdgePrinciple extends DynamicPrinciple {
    private final Class<?>clazz;
    private  Class<?> nodeClazz;
    private final String relationshipType;
    private String nodeLabel;
    private AbstractCypherHelper.RelationshipType direction= AbstractCypherHelper.RelationshipType.UNDIRECTED;
    public static boolean nodeEvolving=false;
    private String nodeStartPrinciple="";
    private String nodeEndPrinciple="";
    private String edgePrinciple="";

    private final StringBuilder principleBuilder=new StringBuilder();



    public EdgePrinciple(Class<?> clazz) {
        this.clazz = clazz;
        this.relationshipType = clazz.getSimpleName().toUpperCase();
    }
    public EdgePrinciple(Class<?> clazz, Class<?> nodeClazz) {
        this.clazz = clazz;
        this.nodeClazz = nodeClazz;
        if(nodeClazz.getAnnotation(Node.class)==null){
            throw new Neo4jPrincipleException("EDGE","second class param must be annotated by @Node!!");
        }
        this.relationshipType = clazz.getSimpleName().toUpperCase();
        //the `type` value of @Relationship in nodeClazz must equal to the value of relationshipType, otherwise throw exception
        Field[] fields = nodeClazz.getDeclaredFields();
        List<String> collect = Arrays.stream(fields).filter(field -> field.getAnnotation(Relationship.class) != null)
                .map(field -> field.getAnnotation(Relationship.class).type())
                .collect(Collectors.toList());
        if(!collect.contains(relationshipType)){
            throw new Neo4jPrincipleException("EDGE","Relationship type `"+relationshipType+"` not found in node `"+nodeClazz.getSimpleName().toUpperCase()+"`!!");
        }
        nodeEvolving=true;
        super.nodeEvolved=true;
        this.nodeLabel= nodeClazz.getSimpleName();
    }

    public EdgePrinciple matchNodeStartAt(String key, Object value){
        principleBuilder.setLength(0);
        if(!nodeEvolving)throw new Neo4jPrincipleException("EDGE","no nodes claim in this principle, you can't use this method!!");
        if(isPropertiesNotInEntity(nodeClazz, key))throw new Neo4jPrincipleException("EDGE","no property `"+key+"` found in class `"+nodeClazz.getSimpleName()+"`!!");
        if(null!=nodeStartPrinciple&&!nodeStartPrinciple.equals("")) {
            principleBuilder.append(nodeStartPrinciple);
            principleBuilder.append(",");
        }

        boolean isStringType=value instanceof String;

        if(isStringType){
            principleBuilder.append(key).append("='").append(value).append("'");
        } else {
            principleBuilder.append(key).append("=").append(value);
        }
        nodeStartPrinciple=principleBuilder.toString();
        return this;
    }

    public EdgePrinciple matchNodeEndAt(String key, Object value){
        principleBuilder.setLength(0);
        if(!nodeEvolving)throw new Neo4jPrincipleException("EDGE","no nodes claim in this principle, you can't use this method!!");
        if(isPropertiesNotInEntity(nodeClazz, key))throw new Neo4jPrincipleException("EDGE","no property `"+key+"` found in class `"+nodeClazz.getSimpleName()+"`!!");
        if(null!=nodeEndPrinciple&&!nodeEndPrinciple.equals("")) {
            principleBuilder.append(nodeEndPrinciple);
            principleBuilder.append(",");
        }
        boolean isStringType=value instanceof String;

        if(isStringType){
            principleBuilder.append(key).append("='").append(value).append("'");
        } else {
            principleBuilder.append(key).append("=").append(value);
        }
        nodeEndPrinciple=principleBuilder.toString();
        return this;
    }
    //厄里斯

    public EdgePrinciple matchEdge(String key, Object value){
        return this;
    }

    public EdgePrinciple clear(){
        nodeEvolving=false;
        this.nodeClazz=null;
        this.nodeLabel=null;
        this.nodeStartPrinciple=null;
        this.nodeEndPrinciple=null;
        this.edgePrinciple=null;
        this.principleBuilder.setLength(0);
        this.direction= AbstractCypherHelper.RelationshipType.UNDIRECTED;
        return this;
    }


    @Override
    public String getTableName() {
        return relationshipType;
    }


    @Override
    public String[] getTablesName() {
        return new String[]{relationshipType, nodeLabel};
    }

    @Override
    public String[] getPropertyNamesForEdge() {
        return Arrays.stream(clazz.getDeclaredFields()).map(Field::getName).toArray(String[]::new);
    }

    @Override
    public String[] getPrincipalArray() {
        return new String[]{nodeStartPrinciple,nodeEndPrinciple,edgePrinciple};
    }

    public String getNodeStartPrinciple() {
        return nodeStartPrinciple;
    }

    public String getNodeEndPrinciple() {
        return nodeEndPrinciple;
    }

    public String getEdgePrinciple() {
        return edgePrinciple;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public String getRelationshipType() {
        return relationshipType;
    }

    public AbstractCypherHelper.RelationshipType getDirection() {
        return direction;
    }

    public void setDirection(AbstractCypherHelper.RelationshipType direction) {
        this.direction = direction;
    }

    public Class<?> getNodeClazz() {
        return nodeClazz;
    }

    public void setNodeClazz(Class<?> nodeClazz) {
        this.nodeClazz = nodeClazz;
    }

    public String getNodeLabel() {
        return nodeLabel;
    }

    public void setNodeLabel(String nodeLabel) {
        this.nodeLabel = nodeLabel;
    }

    public static void main(String[] args) {
        EdgePrinciple edgePrinciple = new EdgePrinciple(Fiber.class, RoutePoint.class);
        edgePrinciple.matchNodeEndAt("name", "end").matchNodeStartAt("name", "start").matchNodeStartAt("lat",0.0).matchNodeEndAt("lat", 100.0);

        String[] s=edgePrinciple.getPrincipalArray();
        System.out.println(Arrays.toString(s));
        System.out.println(s[1]);
        System.out.println(s[0]);
        System.out.println();
    }
}
