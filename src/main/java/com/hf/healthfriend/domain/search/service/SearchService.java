package com.hf.healthfriend.domain.search.service;

import com.hf.healthfriend.domain.member.dto.response.MemberSearchResponse;
import com.hf.healthfriend.domain.member.service.MemberService;
import com.hf.healthfriend.domain.post.dto.response.PostListObject;
import com.hf.healthfriend.domain.post.service.PostService;
import com.hf.healthfriend.domain.search.constant.SearchCategory;
import com.hf.healthfriend.domain.search.dto.SearchResponse;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final MemberService memberService;
    private final PostService postService;

    public SearchResponse search(int page, int size, SearchCategory searchCategory, String keyword) {
        List<PostListObject> postList = new ArrayList<>();
        List<MemberSearchResponse> profileList = new ArrayList<>();
        if(searchCategory == SearchCategory.POST) {
            postList = postService.getList(page, size, null, null, keyword);
        }
        else if (searchCategory == SearchCategory.PROFILE){
            profileList = memberService.searchMembers(keyword,page,size);
        }else{
            postList = postService.getList(page, size, null, null, keyword);
            profileList = memberService.searchMembers(keyword,page,size);
        }
        return SearchResponse.builder()
                .postList(postList)
                .profileList(profileList)
                .build();
    }
}
