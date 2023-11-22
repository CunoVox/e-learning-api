package com.elearning.controller;

import com.elearning.entities.Course;
import com.elearning.entities.Enrollment;
import com.elearning.entities.Rating;
import com.elearning.handler.ServiceException;
import com.elearning.models.dtos.CourseDTO;
import com.elearning.models.dtos.CourseRatingDTO;
import com.elearning.models.dtos.RatingDTO;
import com.elearning.reprositories.IEnrollmentRepository;
import com.elearning.reprositories.IRatingRepository;
import com.elearning.reprositories.ISequenceValueItemRepository;
import com.elearning.utils.Extensions;
import lombok.experimental.ExtensionMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@ExtensionMethod(Extensions.class)
public class RatingController extends BaseController {
    @Autowired
    IRatingRepository ratingRepository;
    @Autowired
    ISequenceValueItemRepository sequenceValueItemRepository;
    @Autowired
    IEnrollmentRepository enrollmentRepository;

    public RatingDTO createRating(RatingDTO dto) {
        String userId = this.getUserIdFromContext();
        if (userId == null) {
            throw new ServiceException("Vui lòng đăng nhập!");
        }
        dto.setUserId(userId);
        dto.setCreatedBy(userId);
        Rating rating = buildEntity(dto);
        Rating savedRating = saveRating(rating);
        return toDTO(savedRating);
    }

    public Rating buildEntity(RatingDTO dto) {
        Rating rating = Rating.builder()
                .courseId(dto.getCourseId())
                .userId(dto.getUserId())
                .rate(dto.getRate())
                .createdBy(dto.getUserId())
                .build();
        if (!dto.getId().isBlankOrNull()) {
            rating.setId(dto.getId());
            rating.setUpdatedAt(dto.getUpdatedAt() != null ? dto.getUpdatedAt() : null);
        }
        return rating;
    }

    public List<RatingDTO> courseRating(String courseId) {
        List<Rating> ratings = ratingRepository.findAllByCourseId(courseId);
        return toDTOs(ratings);
    }

    public RatingDTO userRating(String courseId, String userId) {
        Optional<Rating> rating = ratingRepository.findByCourseIdAndUserId(courseId, userId);
        if (rating.isPresent()) {
            return toDTO(rating.get());
        }
        return new RatingDTO();
    }

    public CourseRatingDTO calcRating(String courseId) {
        List<Rating> ratings = ratingRepository.findAllByCourseId(courseId);
        int rates = 0;
        int totalRatings = 0;
        Map<Integer, Integer> ratingCounts = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            ratingCounts.put(i, 0);
        }
        for (Rating rating : ratings) {
            rates += rating.getRate();
            ratingCounts.put(rating.getRate(), ratingCounts.get(rating.getRate()) + 1);
            totalRatings++;
        }
        return CourseRatingDTO.builder()
                .totalRatings(totalRatings)
                .averageRate((totalRatings > 0) ? ((double) rates / totalRatings) : 0.0)
                .ratingCounts(ratingCounts)
                .build();
    }

    @Transactional(rollbackFor = {NullPointerException.class, ServiceException.class})
    public Rating saveRating(Rating rating) {
        if (rating.getCourseId().isBlankOrNull()) {
            throw new ServiceException("Không thể thực hiện hành động này");
        }
        Enrollment enrollment = enrollmentRepository.findByCourseIdAndUserId(rating.getCourseId(), rating.getUserId());
        if (enrollment == null) {
            throw new ServiceException("Cần phải đăng kí khóa học để đánh giá");
        }
        Optional<Rating> saved = ratingRepository.findByCourseIdAndUserId(rating.getCourseId(), rating.getUserId());
        if (saved.isPresent()) {
            rating.setId(saved.get().getId());
            rating.setUpdatedAt(new Date());
            rating.setUpdatedBy(rating.getUserId());
        } else {
            rating.setId(sequenceValueItemRepository.getSequence(Rating.class));
        }
        rating.setUpdatedAt(new Date());
        if (rating.getRate() <= 0) {
            rating.setRate(1);
        }
        if (rating.getRate() > 5) {
            rating.setRate(5);
        }
        return ratingRepository.save(rating);
    }

    private RatingDTO toDTO(Rating entity) {
        if (entity == null) return null;
        return RatingDTO.builder()
                .id(entity.getId())
                .courseId(entity.getCourseId())
                .userId(entity.getUserId())
                .rate(entity.getRate())
                .createdBy(entity.getCreatedBy())
                .createAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt() != null ? entity.getUpdatedAt() : null)
                .isDeleted(entity.getIsDeleted() != null && entity.getIsDeleted())
                .build();
    }

    private List<RatingDTO> toDTOs(List<Rating> ratings) {
        if (ratings == null) {
            return Collections.emptyList();
        }
        return ratings.stream().map(this::toDTO).collect(Collectors.toList());
    }
}
