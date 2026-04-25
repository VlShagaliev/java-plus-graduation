package ru.practicum.user.contract;

import ru.practicum.event.api.dto.UserShortInfo;

import java.util.Collection;
import java.util.Map;

public interface UserShortInfoProvider {
    UserShortInfo getShortInfo(long userId);

    Map<Long, UserShortInfo> getShortInfoByIds(Collection<Long> userIds);
}