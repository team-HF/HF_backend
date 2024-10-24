package com.hf.healthfriend.domain.spec.service;

import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.member.exception.MemberNotFoundException;
import com.hf.healthfriend.domain.member.repository.MemberRepository;
import com.hf.healthfriend.domain.spec.constants.SpecUpdateType;
import com.hf.healthfriend.domain.spec.dto.SpecDto;
import com.hf.healthfriend.domain.spec.dto.request.SpecUpdateRequestDto;
import com.hf.healthfriend.domain.spec.dto.response.SpecUpdateResponseDto;
import com.hf.healthfriend.domain.spec.entity.Spec;
import com.hf.healthfriend.domain.spec.repository.SpecRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SpecService {
    private final SpecRepository specRepository;
    private final MemberRepository memberRepository;

    /**
     * 경력 및 수상이력을 추가한다.
     *
     * @param dtos 경력 및 수상이력 정보가 담긴 DTO 리스트
     * @return Autogenerated IDs
     */
    public List<Long> addSpec(Long memberId, List<SpecDto> dtos) {
        // addSpec 메소드는 MemberService에서 사용될 수 있다. (회원 등록 / 회원 정보 수정)
        // 그러므로 영속성 컨텍스트에 있는 Member 엔티티를 그대로 들고 온다.
        Member member = this.memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId, "회원이 존재하지 않음"));
        if (dtos == null) {
            return List.of();
        }

        List<Spec> specs = dtos.stream()
                .map((dto) ->
                        new Spec(
                                member,
                                dto.getStartDate(),
                                dto.getEndDate(),
                                dto.isCurrent(),
                                dto.getTitle(),
                                dto.getDescription()
                        ))
                .toList();

        List<Spec> saved = this.specRepository.saveAll(specs);
        return saved.stream()
                .map(Spec::getSpecId)
                .toList();
    }

    public List<SpecDto> getSpecsOfMember(Long memberId) {
        validateMemberWhetherMemberExists(memberId);
        return this.specRepository.findByMemberId(memberId)
                .stream()
                .map(SpecDto::of)
                .toList();
    }

    public SpecUpdateResponseDto updateSpecsOfMember(Long memberId, List<SpecUpdateRequestDto> specUpdateRequestDtos) {
        validateMemberWhetherMemberExists(memberId);
        if (specUpdateRequestDtos == null) {
            return new SpecUpdateResponseDto(List.of(), List.of(), List.of());
        }
        List<Long> insertedSpecIds = addSpec(memberId, specUpdateRequestDtos.stream()
                .filter((dto) -> dto.getSpecUpdateType() == SpecUpdateType.INSERT)
                .map(SpecUpdateRequestDto::getSpec)
                .toList());

        Map<Long, SpecUpdateRequestDto> specsBySpecId = specUpdateRequestDtos.stream()
                .filter((dto) -> dto.getSpecUpdateType() != SpecUpdateType.INSERT)
                .collect(Collectors.toMap(
                        SpecUpdateRequestDto::getSpecId,
                        (s) -> s,
                        (s1, s2) -> s1
                ));
        List<Spec> specsToUpdate = this.specRepository.findBySpecIdsIn(specsBySpecId.keySet());
        
        List<Long> updatedSpecIds = new ArrayList<>();
        List<Long> deletedSpecIds = new ArrayList<>();

        for (Spec specToUpdate : specsToUpdate) {
            SpecUpdateRequestDto updateDto = specsBySpecId.get(specToUpdate.getSpecId());
            switch (updateDto.getSpecUpdateType()) {
                case UPDATE -> {
                    specToUpdate.update(updateDto.getSpec());
                    updatedSpecIds.add(specToUpdate.getSpecId());
                }
                case DELETE -> {
                    specToUpdate.delete();
                    deletedSpecIds.add(specToUpdate.getSpecId());
                }
            }
        }
        return new SpecUpdateResponseDto(insertedSpecIds, updatedSpecIds, deletedSpecIds);
    }

    private void validateMemberWhetherMemberExists(Long memberId) {
        if (!this.memberRepository.existsById(memberId)) {
            throw new MemberNotFoundException(memberId, "회원이 존재하지 않음");
        }
    }
}
