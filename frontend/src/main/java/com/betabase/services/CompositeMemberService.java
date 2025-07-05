package com.betabase.services;

import com.betabase.models.CompositeMember;
import com.betabase.models.Membership;
import com.betabase.models.User;
import com.betabase.services.MembershipApiService.MembershipNotFoundException;
import javafx.application.Platform;

import java.util.*;
import java.util.function.Consumer;

public class CompositeMemberService {
    private final UserApiService userService;
    private final MembershipApiService membershipService;

    private final Map<Long, CompositeMember> cache = new HashMap<>();

    public CompositeMemberService(UserApiService userService, MembershipApiService membershipService) {
        this.userService = userService;
        this.membershipService = membershipService;
    }

    public void searchCompositeMembers(String query, Long gymId,
                                       Consumer<List<CompositeMember>> onSuccess,
                                       Consumer<Exception> onError) {
        new Thread(() -> {
            try {
                List<User> users = userService.searchUsers(query);
                List<CompositeMember> result = new ArrayList<>();

                for (User user : users) {
                    try {
                        Membership membership = membershipService.getForUserAndGym(user.getId(), gymId);
                        CompositeMember cm = new CompositeMember(user, membership);
                        result.add(cm);
                        cache.put(user.getId(), cm);
                    } catch (MembershipNotFoundException ignored) {
                        // Skip users with no membership in this gym
                    }
                }

                Platform.runLater(() -> onSuccess.accept(result));
            } catch (Exception e) {
                Platform.runLater(() -> onError.accept(e));
            }
        }).start();
    }

    public void getCompositeMemberById(Long userId,
                                       Long gymId,
                                       Consumer<CompositeMember> onSuccess,
                                       Consumer<Exception> onError) {
        CompositeMember cached = cache.get(userId);
        if (cached != null && cached.getMembership().getGymId().equals(gymId)) {
            Platform.runLater(() -> onSuccess.accept(cached));
            return;
        }

        new Thread(() -> {
            try {
                User user = userService.getMemberById(userId);
                if (user == null) {
                    Platform.runLater(() -> onError.accept(new RuntimeException("User not found")));
                    return;
                }

                Membership membership = membershipService.getForUserAndGym(user.getId(), gymId);
                CompositeMember member = new CompositeMember(user, membership);
                cache.put(userId, member);

                Platform.runLater(() -> onSuccess.accept(member));
            } catch (Exception e) {
                Platform.runLater(() -> onError.accept(e));
            }
        }).start();
    }

    public CompositeMember getCompositeMemberById(Long userId) {
        return cache.get(userId);
    }

    public CompositeMember updateCompositeMember(CompositeMember member) {
        try {
            User updatedUser = userService.updateUser(member.getUser());
            Membership updatedMembership = membershipService.updateMembership(member.getMembership());
            CompositeMember updated = new CompositeMember(updatedUser, updatedMembership);
            cache.put(updated.getId(), updated);
            return updated;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void refreshCompositeMember(Long userId, Long gymId,
                                       Consumer<CompositeMember> onSuccess,
                                       Consumer<Exception> onError) {
        new Thread(() -> {
            try {
                User user = userService.getMemberById(userId);
                if (user == null) {
                    Platform.runLater(() -> onError.accept(new RuntimeException("User not found")));
                    return;
                }

                Membership membership = membershipService.getForUserAndGym(user.getId(), gymId);
                CompositeMember refreshed = new CompositeMember(user, membership);
                cache.put(userId, refreshed);

                Platform.runLater(() -> onSuccess.accept(refreshed));
            } catch (Exception e) {
                Platform.runLater(() -> onError.accept(e));
            }
        }).start();
    }

    public void clearCache() {
        cache.clear();
    }

    public void removeFromCache(Long userId) {
        cache.remove(userId);
    }

    public boolean isCached(Long userId, Long gymId) {
        CompositeMember cached = cache.get(userId);
        return cached != null && cached.getMembership().getGymId().equals(gymId);
    }
}
