package com.elearning.controller;

import com.elearning.entities.Course;
import com.elearning.entities.Enrollment;
import com.elearning.entities.Price;
import com.elearning.handler.ServiceException;
import com.elearning.models.dtos.CourseDTO;
import com.elearning.models.dtos.EnrollmentDTO;
import com.elearning.models.searchs.ParameterSearchCourse;
import com.elearning.models.searchs.ParameterSearchEnrollment;
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

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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
    @Autowired
    CourseController courseController;

    public EnrollmentDTO createEnrollment(EnrollmentDTO enrollmentDTO) {
        String userId = this.getUserIdFromContext();
        if (userId == null) {
            throw new ServiceException("Vui lòng đăng nhập!");
        }
        enrollmentDTO.setUserId(userId);
        Enrollment enrollment = buildEntity(enrollmentDTO);
        return saveEnrollment(enrollment);
    }

    public Boolean checkEnrollment(String courseId) {
        String userId = this.getUserIdFromContext();
        if (userId == null) {
            return false;
        }
        Enrollment entity = iEnrollmentRepository.findByCourseIdAndUserId(courseId, userId);
        return entity != null;
    }

    @Transactional(rollbackFor = {NullPointerException.class, ServiceException.class})
    public Enrollment buildEntity(EnrollmentDTO enrollmentDTO) {
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

    public ListWrapper<EnrollmentDTO> userEnrollments(ParameterSearchEnrollment parameterSearchEnrollment) {
        String userId = this.getUserIdFromContext();
        if (userId == null) {
            return ListWrapper.<EnrollmentDTO>builder()
                    .data(new ArrayList<>())
                    .build();
        }
        parameterSearchEnrollment.setUserIds(Collections.singletonList(userId));
        return searchEnrollments(parameterSearchEnrollment);
    }

    public ListWrapper<EnrollmentDTO> searchEnrollments(ParameterSearchEnrollment parameterSearchEnrollment) {
        ListWrapper<Enrollment> wrapper = iEnrollmentRepository.searchEnrollment(parameterSearchEnrollment);
        List<EnrollmentDTO> enrollmentDTOS = toDTOs(wrapper.getData(), parameterSearchEnrollment.getBuildCourseChild());
        return ListWrapper.<EnrollmentDTO>builder()
                .currentPage(wrapper.getCurrentPage())
                .totalPage(wrapper.getTotalPage())
                .total(wrapper.getTotal())
                .maxResult(wrapper.getMaxResult())
                .data(enrollmentDTOS)
                .build();
    }

    //public Boolean
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

            Enrollment e1 = iEnrollmentRepository.save(entity);
            return toDTO(e1, courseController.getCourseById(e1.getCourseId()));
        }
        // Kiểm tra giá tiền
        Price pricePromotion = iPriceRepository.findByParentIdAndType(course.getId(), EnumPriceType.PROMOTION.name());
        if (pricePromotion != null) {
            //Nếu có giá khuyến mãi
            throw new ServiceException("Chưa xử lý giá tiền");
//            return enrollPromotionPrice(enrollment);
        }
        // Nếu không có giá khuyến mãi tính bằng giá bán
        Price priceSell = iPriceRepository.findByParentIdAndType(course.getId(), EnumPriceType.SELL.name());
        //Nếu giá bán free
        if (priceSell != null && priceSell.getPrice() != null && priceSell.getPrice().isNullOrZero()) {
            Long sub = course.getSubscriptions();
            course.setSubscriptions((sub != null) ? sub + 1 : 1);
            iCourseRepository.save(course);

            enrollment.setId(iSequenceValueItemRepository.getSequence(Enrollment.class));
            enrollment.setCreatedBy(enrollment.getUserId());
            enrollment.setPricePurchase(priceSell.getPrice().toString());
            enrollment = iEnrollmentRepository.save(enrollment);


            return toDTO(enrollment, courseController.getCourseById(enrollment.getCourseId()));
        } else {
            throw new ServiceException("Chưa xử lý giá tiền");
//                return enrollSellPrice(enrollment);
        }
    }

    private EnrollmentDTO enrollPromotionPrice(Enrollment enrollment) {
        return null;
    }

    private EnrollmentDTO enrollSellPrice(Enrollment enrollment) {
        return null;
    }

    public EnrollmentDTO toDTO(Enrollment entity, CourseDTO courseDTO) {
        if (entity == null) return null;
        return EnrollmentDTO.builder()
                .id(entity.getId())
                .courseId(entity.getCourseId())
                .userId(entity.getUserId())
                .currentMillis(entity.getCurrentMillis())
                .percentComplete(entity.getPercentComplete())
                .courseDTO(courseDTO)
                .currentCourse(entity.getCurrentCourse())
                .createdBy(entity.getCreatedBy())
                .createAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt() != null ? entity.getUpdatedAt() : null)
                .isDeleted(entity.getIsDeleted() != null && entity.getIsDeleted())
                .build();
    }

    public List<EnrollmentDTO> toDTOs(List<Enrollment> enrollments, Boolean buildCourseChild) {
        if (enrollments == null) return Collections.emptyList();
        List<String> courseIds = enrollments.stream().map(Enrollment::getCourseId).collect(Collectors.toList());
        ListWrapper<CourseDTO> wrapper = courseController.searchCourseDTOS(ParameterSearchCourse.builder().buildChild(buildCourseChild).ids(courseIds).build());
        Map<String, CourseDTO> courseDTOMap = new HashMap<>();
        if (wrapper!=null && !wrapper.getData().isNullOrEmpty()) {
            for (Enrollment enrollment : enrollments) {
                Optional<CourseDTO> courseDTO = wrapper.getData().stream().filter(c -> c.getId().equals(enrollment.getCourseId())).findFirst();
                courseDTO.ifPresent(dto -> courseDTOMap.put(enrollment.getId(), dto));
            }
        }
        return enrollments.stream()
                .map(enrollment -> toDTO(enrollment, courseDTOMap.get(enrollment.getId())))
                .collect(Collectors.toList());
    }

}
