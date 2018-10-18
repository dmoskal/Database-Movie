package com.company.Service;

import com.company.Entity.Friendship;

import java.util.List;

public interface FriendshipService {

    public void addFriend(final Long friendA_ID, final Long friendB_ID);
    public void deleteFriend(final Long friendA_ID, final Long friendB_ID);
    public List<Friendship> findFriendship(final Long id);
}
