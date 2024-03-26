package eu.europa.ec.etrustex.web.persistence.repository;

import eu.europa.ec.etrustex.web.common.template.TemplateType;
import eu.europa.ec.etrustex.web.persistence.entity.Template;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TemplateRepository extends JpaRepository<Template, Long> {
    @Query("select t from Group g join g.templates t where g.type = 'ROOT'")
    List<Template> getDefaultTemplates();

    @Query("select t from Group g join g.templates t where g.type = 'ROOT' and t.type = :type")
    Template getDefaultTemplates(@Param("type") TemplateType type);

    @Query("select t from Group g join g.templates t where g.id = :group_id")
    List<Template> getByGroupId(@Param("group_id") Long groupId);
}
