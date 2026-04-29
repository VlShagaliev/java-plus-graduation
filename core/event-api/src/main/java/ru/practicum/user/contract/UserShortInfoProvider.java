package ru.practicum.user.contract;

import ru.practicum.dto.UserShortInfo;

import java.util.Collection;
import java.util.Map;

public interface UserShortInfoProvider {
    UserShortInfo getShortInfo(long userId);

    Map<Long, UserShortInfo> getShortInfoByIds(Collection<Long> userIds);
}