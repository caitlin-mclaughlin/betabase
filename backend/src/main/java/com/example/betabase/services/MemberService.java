package com.example.betabase.services;


import com.example.betabase.models.Member;
import com.example.betabase.repositories.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Optional;

@Service
public class MemberService {

    private final MemberRepository repo;

    public MemberService(MemberRepository repo) {
        this.repo = repo;
    }

    public Optional<Member> checkInMember(String memberId) {
        return repo.findByMemberId(memberId);
    }

    public Member save(Member member) {
        return repo.save(member);
    }

    public Member getMemberById(String memberId) throws IOException {
        try {
            URI uri = new URI("http", null, "localhost", 8080, "/api/members/checkin/" + memberId, null, null);
            HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() == 200) {
                InputStream input = conn.getInputStream();
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(input, Member.class);
            } else {
                return null; // or handle error
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Member> search(String query) {
        return repo.findByQuery(query.toLowerCase());
    }

    // You can later add methods to check-in history, logs, etc.
}
