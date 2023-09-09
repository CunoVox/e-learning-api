package com.elearning.models;


import java.util.Date;

import com.elearning.utils.enumAttribute.EnumLogActionType;
import org.springframework.data.annotation.Id;

public class UpdateLog {
    @Id
    private String id;
    private String type;
    private String actionType;
    private String forId;
    private String name;
    private Object newValue;
    private String updateBy;
    private Date updateTime;
    private String forCollection;

    public UpdateLog() {
    }

    public static UpdateLog createUpdateAttributeLog(String name, Object newValue, String updateBy, String forCollection, String id) {
        return createLog(EnumLogActionType.UPDATE.name(), name, newValue, updateBy, forCollection, id);
    }

    public static UpdateLog createCreateLog(Object newValue, String updateBy, String forCollection) {
        return createLog(EnumLogActionType.CREATE.name(), "Create Object", newValue, updateBy, forCollection, (String)null);
    }

    public static UpdateLog createDeleteLog(String updateBy, String forCollection, String id) {
        return createLog(EnumLogActionType.DELETE.name(), "Delete", (Object)null, updateBy, forCollection, id);
    }

    protected static UpdateLog createLog(String actionType, String name, Object newValue, String updateBy, String forCollection, String forId) {
        UpdateLog updateLog = new UpdateLog();
        updateLog.setActionType(actionType);
        updateLog.setName(name);
        updateLog.setNewValue(newValue);
        updateLog.setUpdateBy(updateBy);
        updateLog.setForCollection(forCollection);
        updateLog.setForId(forId);
        updateLog.setUpdateTime(new Date());
        return updateLog;
    }

    public String getId() {
        return this.id;
    }

    public String getType() {
        return this.type;
    }

    public String getActionType() {
        return this.actionType;
    }

    public String getForId() {
        return this.forId;
    }

    public String getName() {
        return this.name;
    }

    public Object getNewValue() {
        return this.newValue;
    }

    public String getUpdateBy() {
        return this.updateBy;
    }

    public Date getUpdateTime() {
        return this.updateTime;
    }

    public String getForCollection() {
        return this.forCollection;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public void setActionType(final String actionType) {
        this.actionType = actionType;
    }

    public void setForId(final String forId) {
        this.forId = forId;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setNewValue(final Object newValue) {
        this.newValue = newValue;
    }

    public void setUpdateBy(final String updateBy) {
        this.updateBy = updateBy;
    }

    public void setUpdateTime(final Date updateTime) {
        this.updateTime = updateTime;
    }

    public void setForCollection(final String forCollection) {
        this.forCollection = forCollection;
    }
}

