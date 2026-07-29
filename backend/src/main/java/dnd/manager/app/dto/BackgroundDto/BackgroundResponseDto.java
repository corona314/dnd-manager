package dnd.manager.app.dto.BackgroundDto;

import java.util.List;

import dnd.manager.app.dto.FeatDto;
import dnd.manager.app.dto.SkillDto;
import dnd.manager.app.dto.ItemDto.ItemSummaryDto;

public record BackgroundResponseDto(
    String name,
    String description,
    List<String> abilities,
    List<FeatDto> feats,
    List<SkillDto> skills,
    List<ItemSummaryDto> tools,
    List<BackgroundItemDto> items,
    List<BackgroundStartingMoneyDto> startingMoney
) {}
