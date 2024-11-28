package com.hf.healthfriend.domain.member.service;

import com.hf.healthfriend.domain.member.constant.FitnessLevel;
import com.hf.healthfriend.domain.member.domain.Tier;
import com.hf.healthfriend.domain.member.dto.MemberDto;
import com.hf.healthfriend.domain.member.dto.request.MemberCreationRequestDto;
import com.hf.healthfriend.domain.member.dto.request.MemberUpdateRequestDto;
import com.hf.healthfriend.domain.member.dto.request.MembersRecommendRequest;
import com.hf.healthfriend.domain.member.dto.response.*;
import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.member.exception.DuplicateMemberCreationException;
import com.hf.healthfriend.domain.member.exception.FitnessLevelUpdateException;
import com.hf.healthfriend.domain.member.exception.MemberNotFoundException;
import com.hf.healthfriend.domain.member.repository.MemberRepository;
import com.hf.healthfriend.domain.member.repository.dto.MemberUpdateDto;
import com.hf.healthfriend.domain.member.repository.dto.ProfileQueryResultDto;
import com.hf.healthfriend.domain.review.dto.response.RevieweeResponseDto;
import com.hf.healthfriend.domain.review.dto.response.SimpleReviewResponseDto;
import com.hf.healthfriend.domain.review.service.ReviewService;
import com.hf.healthfriend.domain.spec.service.SpecService;
import com.hf.healthfriend.global.file.FileUrlResolver;
import com.hf.healthfriend.global.util.mapping.BeanMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;
    private final SpecService specService;
    private final FileUrlResolver fileUrlResolver;
    private final BeanMapper beanMapper;
    private final ReviewService reviewService;

    /**
     * MemberCreationRequestDto에 있는 데이터를 가지고 새로운 Member를 생성한다.
     *
     * @param dto 생성할 Member의 정보가 담겨 있는 DTO 객체. id는 email과 같으며 password는 필요없다. (OAuth 2.0을 사용하는 도메인
     *            정책 때문)
     * @return 생성된 회원의 정보가 담긴 DTO 객체
     * @throws DuplicateMemberCreationException 이미 생성된 회원을 다시 생성하려고 할 경우 발생
     */
    public MemberCreationResponseDto createMember(MemberCreationRequestDto dto)
            throws DuplicateMemberCreationException {
        if (this.memberRepository.existsByLoginId(dto.getId())) {
            throw new DuplicateMemberCreationException(dto.getId());
        }

        Member newMember = new Member(dto.getId());
        BeanUtils.copyProperties(dto, newMember);
        if (log.isDebugEnabled()) {
            log.debug("newMember={}", newMember);
        }

        String profileImagePath = null;
        if (dto.getProfileImageFileExtension() != null) {
            profileImagePath = this.fileUrlResolver.generateFilePathWithUuid(dto.getProfileImageFileExtension(), "image");
            newMember.setProfileImageUrl(profileImagePath);
            log.info("id={}, profileImagePath={}", newMember.getId(), profileImagePath);
        } else {
            log.info("id={}, File not created", newMember.getId());
        }

        Member saved = this.memberRepository.save(newMember);
        List<Long> generatedSpecIds = this.specService.addSpec(saved.getId(), dto.getSpecs());
        return MemberCreationResponseDto.of(saved, this.fileUrlResolver.generateUploadUrl(profileImagePath), generatedSpecIds);
    }

    public boolean isMemberExists(Long memberId) {
        return this.memberRepository.existsById(memberId);
    }

    public boolean isMemberOfEmailExists(String email) {
        return this.memberRepository.existsByEmail(email);
    }

    public MemberDto findMember(Long memberId) throws MemberNotFoundException {
        Member findMember = this.memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));
        return MemberDto.of(findMember);
    }

    public MemberDto findMemberByLoginId(String loginId) throws MemberNotFoundException {
        // TODO: 현재 정책에서 loginId == email 이지만 추후 정책 변경 혹은 확장에 대비해서 여기 코드 고쳐야 함
        return findMemberByEmail(loginId);
    }

    public MemberDto findMemberByEmail(String email) throws MemberNotFoundException {
        Member findMember = this.memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberNotFoundException(email));
        return MemberDto.of(findMember);
    }

    public MemberUpdateResponseDto updateMember(Long memberId, MemberUpdateRequestDto requestDto) throws MemberNotFoundException {
        validateUpdateRequest(memberId, requestDto);
        MemberUpdateDto updateDto = this.beanMapper.generateBean(requestDto, MemberUpdateDto.class);
        String profileImagePath = null;
        if (requestDto.getProfileImageFileExtension() != null) {
            profileImagePath = this.fileUrlResolver.generateFilePathWithUuid(requestDto.getProfileImageFileExtension(), "image");
            updateDto = updateDto.toBuilder()
                    .profileImageUrl(profileImagePath)
                    .build();
            log.info("id={}, profileImagePath={}", memberId, profileImagePath);
        }

        Member updatedMember = this.memberRepository.update(memberId, updateDto);
        this.specService.updateSpecsOfMember(memberId, requestDto.getSpecUpdate());
        return MemberUpdateResponseDto.builder()
                .profileImageUploadUrl(this.fileUrlResolver.generateUploadUrl(profileImagePath))
                .cd1(updatedMember.getCd1())
                .cd2(updatedMember.getCd2())
                .cd3(updatedMember.getCd3())
                .introduction(updatedMember.getIntroduction())
                .fitnessLevel(updatedMember.getFitnessLevel())
                .companionStyle(updatedMember.getCompanionStyle())
                .fitnessEagerness(updatedMember.getFitnessEagerness())
                .fitnessObjective(updatedMember.getFitnessObjective())
                .fitnessKind(updatedMember.getFitnessKind())
                .build();
    }

    private void validateUpdateRequest(Long memberId, MemberUpdateRequestDto requestDto) {
        if (requestDto.getFitnessLevel() == null) {
            return;
        }

        if (requestDto.getFitnessLevel() == FitnessLevel.ADVANCED) {
            Member member = this.memberRepository.findById(memberId)
                    .orElseThrow(() -> new MemberNotFoundException(memberId));
            Tier tier = member.getTier();
            if (tier.getFitnessLevel() != FitnessLevel.BEGINNER
                    || tier.getTier() == 5) {
                throw new FitnessLevelUpdateException("새싹에서 고수로 변경할 때 새싹 레벨 5여야 합니다.");
            }
        }
    }

    public List<MemberRecommendResponse> recommendMember(MembersRecommendRequest request, int pageNumber){
        Pageable pageable = PageRequest.of(pageNumber - 1, 6);
        return memberRepository.recommendMembers(request, pageable);
    }

    public List<MemberSearchResponse> searchMembers(String keyword, int pageNumber, int size) {
        Pageable pageable = PageRequest.of(pageNumber - 1, size);
        return memberRepository.searchMembers(keyword, pageable);
    }

    /**
     * 매칭 과정에서 다른 사람의 회원 정보를 조회할 때 필요한 데이터를 반환하는 메소드.
     * 회원 정보와 매칭 횟수 등을 조회할 수 있다.
     *
     * @param memberId 프로필을 조회할 회원의 ID
     * @return 프로필 정보가 담긴 DTO
     */
    public ProfileResponseDto getProfileOfMember(Long memberId) {
        ProfileQueryResultDto profileResult = this.memberRepository.findProfileByMemberId(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));
        RevieweeResponseDto reviewDto = this.reviewService.getRevieweeInfo(memberId);

        return ProfileResponseDto.builder()
                .memberId(profileResult.memberId())
                .introduction(profileResult.introduction())
                .specs(profileResult.specs())
                .reviews(reviewDto.reviewDetails()
                        .stream()
                        .map((r) -> new SimpleReviewResponseDto(r.evaluationType(), r.reviewDetailsPerEvaluationType()))
                        .toList())
                .averageReviewScore(profileResult.averageReviewScore())
                .build();
    }
}
