package com.hf.healthfriend.domain.search.service;

import com.hf.healthfriend.domain.member.dto.response.MemberSearchResponse;
import com.hf.healthfriend.domain.member.service.MemberService;
import com.hf.healthfriend.domain.post.dto.response.PostListObject;
import com.hf.healthfriend.domain.post.service.PostService;
import com.hf.healthfriend.domain.search.constant.SearchCategory;
import com.hf.healthfriend.domain.search.dto.SearchResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RList;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final MemberService memberService;
    private final PostService postService;
    private final RedissonClient redissonClient;

    public SearchResponse search(int page, int size, SearchCategory searchCategory, String keyword, Long memberId) {
        List<PostListObject> postList = new ArrayList<>();
        List<MemberSearchResponse> profileList = new ArrayList<>();
        List<String> recentSearchList = new ArrayList<>();

        if(searchCategory == SearchCategory.POST) {
            postList = postService.getList(page, size, null, null, keyword);
        }
        else if (searchCategory == SearchCategory.PROFILE){
            profileList = memberService.searchMembers(keyword,page,size);
        }else{
            postList = postService.getList(page, size, null, null, keyword);
            profileList = memberService.searchMembers(keyword,page,size);
        }

        if (memberId != null) {
            recentSearchList = getRecentSearchKeywords(memberId);
            saveRecentSearchKeyword(memberId, keyword);
        }

        return SearchResponse.builder()
                .postList(postList)
                .profileList(profileList)
                .recentSearchList(recentSearchList)
                .build();
    }

    void saveRecentSearchKeyword(Long memberId, String keyword) {
        RList<String> recentSearchList = redissonClient.getList("recent_search_keywords:"+memberId);
        recentSearchList.remove(keyword); //중복 저장 방지
        recentSearchList.add(0,keyword);
        if (recentSearchList.size() > 6) {
            recentSearchList.remove(6);
        }
        recentSearchList.expire(Duration.ofDays(3));
    }

    public List<String> getRecentSearchKeywords(Long memberId) {
        RList<String> recentSearchKeywords = redissonClient.getList("recent_search_keywords:"+memberId);
        return new ArrayList<>(recentSearchKeywords);
    }

    public void deleteRecentSearch(Long memberId) {
        RList<String> myList = redissonClient.getList("recent_search_keywords:"+memberId);
        myList.clear();
    }
}
