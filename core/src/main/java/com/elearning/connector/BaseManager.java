package com.elearning.connector;

import com.elearning.handler.ServiceException;
import com.elearning.models.UpdateLog;
import com.elearning.utils.enumAttribute.EnumRelatedObjectsStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.util.*;

public class BaseManager<T> {
    @Autowired
    protected MongoTemplate mongoTemplate;
    protected String collectionName;
    protected Class<T> tClass;

    public List<String> getIdRelatedObjectsById(String fromId, String toCollection, String type) {
        List<String> rs = new ArrayList();
        List<Map> rsMap = this.getConnector(this.collectionName, fromId, toCollection, "", type);
        Iterator var6 = rsMap.iterator();

        while(var6.hasNext()) {
            Map map = (Map)var6.next();
            if (!ObjectUtils.isEmpty(map.get(toCollection + "_id"))) {
                rs.add(map.get(toCollection + "_id").toString());
            }
        }

        return rs;
    }

    public Map<String, List<String>> getIdRelatedObjectsById(List<String> fromIds, String toCollection, String type) {
        Map<String, List<String>> rs = new HashMap();
        List<Map> rsMap = this.getConnector(this.collectionName, fromIds, toCollection, "", type);
        Iterator var6 = rsMap.iterator();

        while(var6.hasNext()) {
            Map map = (Map)var6.next();
            if (!ObjectUtils.isEmpty(map.get(toCollection + "_id"))) {
                if (rs.containsKey(map.get(this.collectionName + "_id").toString())) {
                    ((List)rs.get(map.get(this.collectionName + "_id").toString())).add(map.get(toCollection + "_id").toString());
                } else {
                    rs.put(map.get(this.collectionName + "_id").toString(), new ArrayList(Arrays.asList(map.get(toCollection + "_id").toString())));
                }
            }
        }

        return rs;
    }

    public Map<String, List<String>> getIdRelatedObjectsById(String fromCollection, List<String> fromIds, String toCollection, String type) {
        Map<String, List<String>> rs = new HashMap();
        List<Map> rsMap = this.getConnector(fromCollection, fromIds, toCollection, "", type);
        Iterator var7 = rsMap.iterator();

        while(var7.hasNext()) {
            Map map = (Map)var7.next();
            if (!ObjectUtils.isEmpty(map.get(toCollection + "_id"))) {
                if (rs.containsKey(map.get(fromCollection + "_id"))) {
                    ((List)rs.get(map.get(fromCollection + "_id"))).add((String)map.get(toCollection + "_id"));
                } else {
                    rs.put((String)map.get(fromCollection + "_id"), new ArrayList(Arrays.asList((String)map.get(toCollection + "_id"))));
                }
            }
        }

        return rs;
    }

    public List<String> getIdRelatedObjectsById(String fromCollection, String fromId, String toCollection, String type) {
        List<Map> rsMap = this.getConnector(fromCollection, fromId, toCollection, "", type);
        if (!ObjectUtils.isEmpty(rsMap)) {
            List<String> rs = new ArrayList();
            Iterator var7 = rsMap.iterator();

            while(var7.hasNext()) {
                Map map = (Map)var7.next();
                if (!ObjectUtils.isEmpty(map.get(toCollection + "_id"))) {
                    rs.add(map.get(toCollection + "_id").toString());
                }
            }

            return rs;
        } else {
            return new ArrayList();
        }
    }

    public List<String> getIdRelatedObjectsById(String fromCollection, String fromId, String toCollection, String toId, String type) {
        List<Map> rsMap = this.getConnector(fromCollection, fromId, toCollection, toId, type);
        if (!ObjectUtils.isEmpty(rsMap)) {
            List<String> rs = new ArrayList();
            Iterator var8 = rsMap.iterator();

            while(var8.hasNext()) {
                Map map = (Map)var8.next();
                if (!ObjectUtils.isEmpty(map.get(toCollection + "_id"))) {
                    rs.add(map.get(toCollection + "_id").toString());
                }
            }

            return rs;
        } else {
            return new ArrayList();
        }
    }

    public List<Map> getRelatedObjectsById(String fromCollection, String fromId, String toCollection, String toId, String type) {
        new ArrayList();
        return this.getConnector(fromCollection, fromId, toCollection, toId, type);
    }

    public List<String> getIdsByRelatedObjectId(String toCollection, String toId, String type) {
        List<String> rs = new ArrayList();
        List<Map> rsMap = this.getConnector(this.collectionName, "", toCollection, toId, type);
        Iterator var6 = rsMap.iterator();

        while(var6.hasNext()) {
            Map map = (Map)var6.next();
            if (!ObjectUtils.isEmpty(map.get(this.collectionName + "_id"))) {
                rs.add(map.get(this.collectionName + "_id").toString());
            }
        }

        return rs;
    }

    public Map<String, Object> getConnectorById(String id) {
        return (Map)this.mongoTemplate.findById(id, Map.class, "connector");
    }

    private List<Map> getConnector(String fromCollection, String fromId, String toCollection, String toId, String type) {
        Query query = Query.query(this.buildQuery(fromCollection, (List)(ObjectUtils.isEmpty(fromId) ? new ArrayList() : Arrays.asList(fromId)), toCollection, toId, type, EnumRelatedObjectsStatus.ACTIVE.getValue()));
        return this.mongoTemplate.find(query, Map.class, "connector");
    }

    private List<Map> getConnector(String fromCollection, List<String> fromIds, String toCollection, String toId, String type) {
        Query query = Query.query(this.buildQuery(fromCollection, fromIds, toCollection, toId, type, EnumRelatedObjectsStatus.ACTIVE.getValue()));
        return this.mongoTemplate.find(query, Map.class, "connector");
    }

    private Criteria buildQuery(String fromCollection, List<String> fromIds, String toCollection, String toId, String type, Integer status) {
        Criteria criteria = new Criteria();
        List<Criteria> filterList = new ArrayList();
        if (!ObjectUtils.isEmpty(fromCollection)) {
            filterList.add(Criteria.where(fromCollection + "_id").exists(true));
        }

        if (!ObjectUtils.isEmpty(toCollection)) {
            filterList.add(Criteria.where(toCollection + "_id").exists(true));
        }

        QueryBuilderUtils.addMultipleValuesFilter(filterList, fromCollection + "_id", fromIds);
        QueryBuilderUtils.addSingleValueFilter(filterList, toCollection + "_id", toId);
        QueryBuilderUtils.addSingleValueFilter(filterList, "status", status);
        QueryBuilderUtils.addSingleValueFilter(filterList, "type", type);
        QueryBuilderUtils.andOperator(criteria, filterList);
        return criteria;
    }



    public Map<String, Object> addRelatedObjectById(String fromId, String toCollection, String toId, int weight, int status, String type, String updatedBy) throws ServiceException {
        return this.addRelatedObjectById(this.collectionName, fromId, toCollection, toId, weight, status, type, updatedBy);
    }

    public Map<String, Object> addRelatedObjectById(String fromId, String toCollection, String toId, int weight, int status, String type, Map<String, Object> attributes, String updatedBy) throws ServiceException {
        return this.addRelatedObjectById(this.collectionName, fromId, toCollection, toId, weight, status, type, attributes, updatedBy);
    }

    public Map<String, Object> addRelatedObjectById(String fromCollection, String fromId, String toCollection, String toId, int weight, int status, String type, String updatedBy) throws ServiceException {
        Map<String, Object> map = new HashMap();
        map.put(fromCollection + "_id", fromId);
        map.put(toCollection + "_id", toId);
        map.put("status", status);
        map.put("weight", weight);
        map.put("type", type);
        return this.addConnector(fromCollection, fromId, toCollection, toId, type, map, updatedBy);
    }

    public Map<String, Object> addRelatedObjectById(String fromCollection, String fromId, String toCollection, String toId, int weight, int status, String type, Map<String, Object> attributes, String updatedBy) throws ServiceException{
        Map<String, Object> map = new HashMap();
        map.put(fromCollection + "_id", fromId);
        map.put(toCollection + "_id", toId);
        map.put("status", status);
        map.put("weight", weight);
        map.put("type", type);
        if (!ObjectUtils.isEmpty(attributes)) {
            map.put("attributes", attributes);
        }

        return this.addConnector(fromCollection, fromId, toCollection, toId, type, map, updatedBy);
    }

    private Map<String, Object> addConnector(String fromCollection, String fromId, String toCollection, String toId, String type, Map<String, Object> map, String updatedBy) throws ServiceException {
        if (!ObjectUtils.isEmpty(fromCollection) && !ObjectUtils.isEmpty(fromId) && !ObjectUtils.isEmpty(toCollection) && !ObjectUtils.isEmpty(type) && !ObjectUtils.isEmpty(toId) && !ObjectUtils.isEmpty(toCollection)) {
            if (this.mongoTemplate.exists(Query.query(this.buildQuery(fromCollection, Arrays.asList(fromId), toCollection, toId, type, EnumRelatedObjectsStatus.ACTIVE.getValue())), Map.class, "connector")) {
                throw new ServiceException("Related Object existed");
            }

            if (this.mongoTemplate.exists(Query.query(this.buildQuery(fromCollection, Arrays.asList(fromId), toCollection, toId, type, EnumRelatedObjectsStatus.INACTIVE.getValue())), Map.class, "connector")) {
                this.updateStatusConnector(fromCollection, fromId, toCollection, toId, type, EnumRelatedObjectsStatus.ACTIVE.getValue(), updatedBy);
            }

            List<Map> rs = this.mongoTemplate.find(Query.query(this.buildQuery(fromCollection, Arrays.asList(fromId), toCollection, toId, type, EnumRelatedObjectsStatus.ACTIVE.getValue())), Map.class, "connector");
            if (!ObjectUtils.isEmpty(rs)) {
                return (Map)rs.get(0);
            }
        }

        return (Map)(!ObjectUtils.isEmpty(map) ? this.createObject(map, updatedBy, "connector") : new HashMap());
    }

    private void updateStatusConnector(String fromCollection, String fromId, String toCollection, String toId, String type, int status, String updateBy) {
        Update update = new Update();
        update.set("updatedStamp", new Date());
        update.set("updatedBy", updateBy);
        update.set("status", status);
        Query query = Query.query(this.buildQuery(fromCollection, Arrays.asList(fromId), toCollection, toId, type, (Integer)null));
        this.mongoTemplate.updateFirst(query, update, "connector");
    }

    private Map<String, Object> createObject(Map<String, Object> object, String updateBy, String collectionName) {
        Assert.notNull(object, "Object must not null");
        if (!object.containsKey("status")) {
            object.put("status", EnumRelatedObjectsStatus.ACTIVE.getValue());
        }

        object.put("updatedBy", updateBy);
        object.put("createdBy", updateBy);
        object.put("createdStamp", new Date());
        Map<String, Object> newObject = this.mongoTemplate.insert(object, collectionName);
        this.mongoTemplate.insert(UpdateLog.createCreateLog(newObject, updateBy, this.tClass.getName()));
        return newObject;
    }
}
