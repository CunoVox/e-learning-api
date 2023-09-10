package com.elearning.models;


import java.util.Date;

import com.elearning.utils.enumAttribute.EnumLogActionType;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
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

    public UpdateLog() {
    }

    public static UpdateLog createUpdateAttributeLog(String name, Object newValue, String updateBy, String id) {
        return createLog(EnumLogActionType.UPDATE.name(), name, newValue, updateBy, id);
    }

    public static UpdateLog createCreateLog(Object newValue, String updateBy) {
        return createLog(EnumLogActionType.CREATE.name(), "Create Object", newValue, updateBy, (String)null);
    }

    public static UpdateLog createDeleteLog(String updateBy, String id) {
        return createLog(EnumLogActionType.DELETE.name(), "Delete", (Object)null, updateBy, id);
    }

    protected static UpdateLog createLog(String actionType, String name, Object newValue, String updateBy, String forId) {
        UpdateLog updateLog = new UpdateLog();
        updateLog.setActionType(actionType);
        updateLog.setName(name);
        updateLog.setNewValue(newValue);
        updateLog.setUpdateBy(updateBy);
        updateLog.setForId(forId);
        updateLog.setUpdateTime(new Date());
        return updateLog;
    }
}

