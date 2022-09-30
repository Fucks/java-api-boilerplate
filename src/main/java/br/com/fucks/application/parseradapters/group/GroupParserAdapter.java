package br.com.fucks.application.parseradapters.group;

import br.com.fucks.application.models.group.GroupModel;
import br.com.fucks.domain.model.group.Group;
import br.com.fucks.application.parseradapters.IParserAdapter;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class GroupParserAdapter implements IParserAdapter<GroupModel, Group> {

    @Override
    public GroupModel toModel(Group entity) {
        return GroupModel
                .builder()
                .id(entity.getId())
                .name(entity.getName())
                .abbreviation(entity.getAbbreviation())
                .enabled(entity.getEnabled())
                .build();
    }

    @Override
    public Group toEntity(GroupModel model) {
        return Group
                .builder()
                .id(model.getId())
                .name(model.getName())
                .abbreviation(model.getAbbreviation())
                .build();
    }
}
