package app.quantun.eb2c.repository;

import app.quantun.eb2c.model.entity.bussines.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}