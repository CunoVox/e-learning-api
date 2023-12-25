package com.elearning.apis;


import com.elearning.controller.RatingController;
import com.elearning.models.dtos.RatingDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rating")
@RequiredArgsConstructor
@Tag(name = "Rating", description = "Rating API")
public class RatingAPI {
    @Autowired
    RatingController ratingController;

    @PostMapping("/")
    public RatingDTO saveRating(@RequestBody RatingDTO dto){
        return ratingController.createRating(dto);
    }
}
