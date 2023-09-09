package com.elearning.manager;

import com.elearning.connector.BaseManager;
import com.elearning.entities.Course;
import com.elearning.entities.User;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class CourseManager extends BaseManager<User> {

}
