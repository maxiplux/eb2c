package app.quantun.b2b.model.entity.bussines;


import app.quantun.b2b.model.entity.AuditModel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Entity
public class Category extends AuditModel<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name="name", unique=true)
    private String name;


    private Category subCategory;


}
