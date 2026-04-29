package ru.practicum.category.contract;

import ru.practicum.dto.CategoryShortInfo;

import java.util.Collection;
import java.util.Map;

public interface CategoryShortInfoProvider {
    CategoryShortInfo getShortInfo(long categoryId);

    Map<Long, CategoryShortInfo> getShortInfoByIds(Collection<Long> categoryIds);
}