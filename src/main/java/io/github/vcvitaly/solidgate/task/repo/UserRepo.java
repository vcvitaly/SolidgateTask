package io.github.vcvitaly.solidgate.task.repo;

import io.github.vcvitaly.solidgate.task.model.User;
import java.util.List;
import java.util.Set;

public interface UserRepo {

    Set<Integer> getExistingUserIds(Set<Integer> userIds);

    List<User> selectUsers(Set<Integer> ids);
}
