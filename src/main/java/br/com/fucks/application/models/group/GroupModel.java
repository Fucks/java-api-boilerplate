package br.com.fucks.application.models.group;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupModel {

    private Long id;

    private String name;

    private String abbreviation;

    private Boolean enabled;

}
