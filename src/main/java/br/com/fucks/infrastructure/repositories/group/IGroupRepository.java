package br.com.fucks.infrastructure.repositories.group;

import br.com.fucks.domain.model.group.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IGroupRepository extends JpaRepository<Group, Long> {

    @Override
    @Query(value = "FROM Group grp " +
            "WHERE grp.enabled = TRUE ")
    Page<Group> findAll(Pageable page);

    @Query(value = "FROM Group grp " +
            "WHERE grp.name LIKE :filter " +
            "AND ( :showEnabled = true " +
            "   AND (grp.enabled = false OR grp.enabled = true)" +
            "   OR (grp.enabled = true ) " +
            ") ")
    Page<Group> findAllByFilters(@Param("filter") String filter, @Param("showEnabled") Boolean showEnabled, Pageable page);

}
