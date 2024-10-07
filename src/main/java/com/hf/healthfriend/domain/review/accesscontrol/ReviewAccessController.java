package com.hf.healthfriend.domain.review.accesscontrol;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hf.healthfriend.auth.accesscontrol.AccessControlTrigger;
import com.hf.healthfriend.auth.accesscontrol.AccessController;
import com.hf.healthfriend.domain.matching.repository.MatchingRepository;
import com.hf.healthfriend.domain.member.repository.MemberRepository;
import com.hf.healthfriend.domain.review.dto.request.ReviewCreationRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@AccessController
@RequiredArgsConstructor
public class ReviewAccessController {
    private final MemberRepository memberRepository;
    private final MatchingRepository matchingRepository;
    private final ObjectMapper objectMapper;

    @AccessControlTrigger(path = "/hf/reviews", method = "POST")
    public boolean controlAccessToAddReview(BearerTokenAuthentication authentication, HttpServletRequest request)
            throws IOException {
        long accessingMemberId = Long.parseLong(authentication.getName());

        ReviewCreationRequestDto requestBody;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()))) {
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            requestBody = this.objectMapper.readValue(sb.toString(), ReviewCreationRequestDto.class);
        }

        return accessingMemberId == requestBody.getReviewerId();
    }
}
