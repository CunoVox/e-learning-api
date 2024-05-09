package com.elearning.reprositories.impls;

import com.elearning.connector.Connector;
import com.elearning.reprositories.ICategoryRepository;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.Assert;

import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.Map;

public abstract class BaseRepositoryCustom {
    @Autowired
    protected MongoTemplate mongoTemplate;

    @Autowired
    protected ICategoryRepository categoryRepository;


//    @Autowired
//    protected ICourseRepository courseRepository;
//
//    @Autowired
//    protected IFileRelationshipRepository fileRelationshipRepository;
//
//    @Autowired
//    protected IPriceRepository priceRepository;
//
//    @Autowired
//    protected IUserRepository userRepository;

    @Autowired
    protected Connector connector;

    public void updateAttribute(@NotEmpty String id, @NotEmpty Map<String, Object> values, String updateBy, Class clazz) {
        Update update = new Update();
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            update.set(entry.getKey(), entry.getValue());
        }
        update.set("updatedAt", new Date());
        update.set("updatedBy", updateBy);
        UpdateResult updateResult = mongoTemplate.updateFirst(
                Query.query(Criteria.where("_id").is(id)), update, clazz);
        Assert.isTrue(updateResult.getModifiedCount() != 0, "Record id " + id + " for collection + " + clazz + " is not exits!");
    }
}
