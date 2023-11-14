package com.elearning.controller;

import com.elearning.entities.Course;
import com.elearning.entities.Enrollment;
import com.elearning.entities.Price;
import com.elearning.handler.ServiceException;
import com.elearning.models.dtos.EnrollmentDTO;
import com.elearning.models.searchs.ParameterSearchCourse;
import com.elearning.models.wrapper.ListWrapper;
import com.elearning.reprositories.ICourseRepository;
import com.elearning.reprositories.IEnrollmentRepository;
import com.elearning.reprositories.IPriceRepository;
import com.elearning.reprositories.ISequenceValueItemRepository;
import com.elearning.utils.Extensions;
import com.elearning.utils.enumAttribute.EnumPriceType;
import lombok.experimental.ExtensionMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;

@Controller
@ExtensionMethod(Extensions.class)
public class EnrollmentController extends BaseController {
    @Autowired
    IEnrollmentRepository iEnrollmentRepository;
    @Autowired
    ISequenceValueItemRepository iSequenceValueItemRepository;
    @Autowired
    ICourseRepository iCourseRepository;
    @Autowired
    IPriceRepository iPriceRepository;

    public EnrollmentDTO createEnrollment(EnrollmentDTO enrollmentDTO) {
        String userId = this.getUserIdFromContext();
        if (userId == null) {
            throw new ServiceException("Vui lòng đăng nhập!");
        }
        enrollmentDTO.setUserId(userId);
        Enrollment enrollment = buildEntity(enrollmentDTO);
        return saveEnrollment(enrollment);
    }

    private Enrollment buildEntity(EnrollmentDTO enrollmentDTO) {
        Enrollment enrollment = Enrollment.builder()
                .courseId(enrollmentDTO.getCourseId())
                .userId(enrollmentDTO.getUserId())
                .currentCourse(enrollmentDTO.getCurrentCourse())
                .currentMillis(enrollmentDTO.getCurrentMillis())
                .createdBy(enrollmentDTO.getCreatedBy())
                .createdAt(new Date())
                .build();
        if (!enrollmentDTO.getId().isBlankOrNull()) {
            enrollment.setId(enrollmentDTO.getId());
            enrollment.setUpdatedAt(enrollmentDTO.getUpdatedAt() != null ? enrollmentDTO.getUpdatedAt() : null);
        }
        return enrollment;
    }

    @Transactional(rollbackFor = {NullPointerException.class, ServiceException.class})
    public EnrollmentDTO saveEnrollment(Enrollment enrollment) {
        ParameterSearchCourse parameterSearchCourse = new ParameterSearchCourse();
        parameterSearchCourse.setIds(Collections.singletonList(enrollment.getCourseId()));
        ListWrapper<Course> courseListWrapper = iCourseRepository.searchCourse(parameterSearchCourse);
        if (courseListWrapper.getData().isEmpty()) {
            throw new ServiceException("Không tin thấy khóa học!");
        }
        Course course = courseListWrapper.getData().get(0);
        if (course.getLevel() != 1) {
            throw new ServiceException("Lỗi khóa học không hợp lệ!");
        }
        // Kiểm tra user đã đăng kí khóa học này chưa
        Enrollment entity = iEnrollmentRepository.findByCourseIdAndUserId(course.getId(), enrollment.getUserId());
        // Nếu đã đăng kí cập nhật quá trình học.
        if (entity != null) {
            entity.setCurrentCourse(entity.getCurrentCourse());
            entity.setCurrentMillis(entity.getCurrentMillis());
            entity.setUpdatedAt(new Date());
            entity.setUpdatedBy(enrollment.getUserId());

            entity = iEnrollmentRepository.save(entity);
            return toDTO(entity);
        }
        // Kiểm tra giá tiền
        Price pricePromotion = iPriceRepository.findByParentIdAndType(course.getId(), EnumPriceType.PROMOTION.name());
        if (pricePromotion != null) {
            //Nếu có giá khuyến mãi
            return enrollPromotionPrice(enrollment);
        }
        // Nếu không có giá khuyến mãi tính bằng giá bán
        Price priceSell = iPriceRepository.findByParentIdAndType(course.getId(), EnumPriceType.SELL.name());
        if (priceSell != null && priceSell.getPrice() != null) {
            //Nếu giá bán free
            if (priceSell.getPrice().isNullOrZero()) {
                enrollment.setPricePurchase(priceSell.getPrice().toString());
                enrollment = iEnrollmentRepository.save(enrollment);
                return toDTO(enrollment);
            } else {
                return enrollSellPrice(enrollment);
            }
        }
        return null;
    }

    private EnrollmentDTO enrollPromotionPrice(Enrollment enrollment) {
        return null;
    }

    private EnrollmentDTO enrollSellPrice(Enrollment enrollment) {
        return null;
    }

    public EnrollmentDTO toDTO(Enrollment entity) {
        if (entity == null) return null;
        return EnrollmentDTO.builder()
                .id(entity.getId())
                .courseId(entity.getCourseId())
                .userId(entity.getUserId())
                .currentMillis(entity.getCurrentMillis())
                .percentComplete(entity.getPercentComplete())
                .currentCourse(entity.getCurrentCourse())
                .createdBy(entity.getCreatedBy())
                .createAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt() != null ? entity.getUpdatedAt() : null)
                .isDeleted(entity.getIsDeleted() != null && entity.getIsDeleted())
                .build();
    }
}
