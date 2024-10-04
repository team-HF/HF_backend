package com.hf.healthfriend.domain.member.service;

import com.hf.healthfriend.domain.member.dto.MemberDto;
import com.hf.healthfriend.domain.member.dto.request.MemberCreationRequestDto;
import com.hf.healthfriend.domain.member.dto.request.MemberUpdateRequestDto;
import com.hf.healthfriend.domain.member.dto.response.MemberCreationResponseDto;
import com.hf.healthfriend.domain.member.dto.response.MemberUpdateResponseDto;
import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.member.exception.DuplicateMemberCreationException;
import com.hf.healthfriend.domain.member.exception.MemberNotFoundException;
import com.hf.healthfriend.domain.member.repository.MemberRepository;
import com.hf.healthfriend.domain.member.repository.dto.MemberUpdateDto;
import com.hf.healthfriend.domain.spec.dto.SpecDto;
import com.hf.healthfriend.domain.spec.dto.response.SpecUpdateResponseDto;
import com.hf.healthfriend.domain.spec.service.SpecService;
import com.hf.healthfriend.global.util.file.FileUrlResolver;
import com.hf.healthfriend.global.util.file.MultipartFileUploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;
    private final SpecService specService;
    private final FileUrlResolver fileUrlResolver;
    private final MultipartFileUploader multipartFileUploader;

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

        if (dto.getProfileImage() != null) {
            String filePath = storeProfileImage(dto.getProfileImage());
            if (filePath != null) {
                log.info("id={}, filePath={}", newMember.getId(), filePath);
                newMember.setProfileImageUrl(filePath);
            } else {
                log.info("id={}, File not created", newMember.getId());
            }
        }

        Member saved = this.memberRepository.save(newMember);
        return MemberCreationResponseDto.of(saved);
    }

    public boolean isMemberExists(Long memberId) {
        return this.memberRepository.existsById(memberId);
    }

    public boolean isMemberOfEmailExists(String email) {
        return this.memberRepository.existsByEmail(email);
    }

    public MemberDto findMember(Long memberId) throws MemberNotFoundException {
        Member findMember = this.memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));
        return bindToDto(findMember);
    }

    public MemberDto findMemberByLoginId(String loginId) throws MemberNotFoundException {
        // TODO: 현재 정책에서 loginId == email 이지만 추후 정책 변경 혹은 확장에 대비해서 여기 코드 고쳐야 함
        return findMemberByEmail(loginId);
    }

    public MemberDto findMemberByEmail(String email) throws MemberNotFoundException {
        Member findMember = this.memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberNotFoundException(email));
        return bindToDto(findMember);
    }

    public MemberUpdateResponseDto updateMember(Long memberId, MemberUpdateRequestDto requestDto) throws MemberNotFoundException {
        MemberUpdateDto updateDto = MemberUpdateDto.builder()
                .role(requestDto.getRole())
                .nickname(requestDto.getNickname())
                .birthDate(requestDto.getBirthDate())
                .gender(requestDto.getGender())
                .introduction(requestDto.getIntroduction())
                .fitnessLevel(requestDto.getFitnessLevel())
                .companionStyle(requestDto.getCompanionStyle())
                .fitnessEagerness(requestDto.getFitnessEagerness())
                .fitnessObjective(requestDto.getFitnessObjective())
                .fitnessKind(requestDto.getFitnessKind())
                .build();
        if (requestDto.getProfileImage() != null) {
            String filePath = storeProfileImage(requestDto.getProfileImage());
            if (filePath != null) {
                log.info("id={}, filePath={}", memberId, filePath);
                updateDto = updateDto.toBuilder()
                        .profileImageUrl(filePath)
                        .build();
            } else {
                log.info("id={}, File not updated", memberId);
            }
        }
        Member updatedMember = this.memberRepository.update(memberId, updateDto);
        return MemberUpdateResponseDto.builder()
                .memberId(updatedMember.getId())
                .loginId(updatedMember.getLoginId())
                .role(updatedMember.getRole())
                .email(updatedMember.getEmail())
                .creationTime(updatedMember.getCreationTime())
                .nickname(updatedMember.getNickname())
                .profileImageUrl(this.fileUrlResolver.resolveFileUrl(updatedMember.getProfileImageUrl()))
                .birthDate(updatedMember.getBirthDate())
                .gender(updatedMember.getGender())
                .introduction(updatedMember.getIntroduction())
                .fitnessLevel(updatedMember.getFitnessLevel())
                .companionStyle(updatedMember.getCompanionStyle())
                .fitnessEagerness(updatedMember.getFitnessEagerness())
                .fitnessObjective(updatedMember.getFitnessObjective())
                .fitnessKind(updatedMember.getFitnessKind())
                .build();
    }

    private String storeProfileImage(MultipartFile profileImage) {
        String originalFilename = profileImage.getOriginalFilename();
        String filePath = this.fileUrlResolver.generateFilePath(originalFilename, "image");

        try {
            this.multipartFileUploader.uploadFile(filePath, profileImage);
            return filePath;
        } catch (IOException e) {
            log.error("[FATAL] 파일 출력 중 Error 발생", e);
            return null;
        }
    }

    private MemberDto bindToDto(Member member) {
        return MemberDto.builder()
                .memberId(member.getId())
                .loginId(member.getLoginId())
                .role(member.getRole())
                .email(member.getEmail())
                .creationTime(member.getCreationTime())
                .nickname(member.getNickname())
                .profileImageUrl(this.fileUrlResolver.resolveFileUrl(member.getProfileImageUrl()))
                .birthDate(member.getBirthDate())
                .gender(member.getGender())
                .introduction(member.getIntroduction())
                .fitnessLevel(member.getFitnessLevel())
                .companionStyle(member.getCompanionStyle())
                .fitnessEagerness(member.getFitnessEagerness())
                .fitnessObjective(member.getFitnessObjective())
                .fitnessKind(member.getFitnessKind()) // TODO: Eager Fetch를 해야 하는 게 더 나을지 고민해 보기
                .build();
    }
}
