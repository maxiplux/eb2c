package app.quantun.eb2c.model.entity.bussines;


import app.quantun.eb2c.model.entity.AuditModel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Entity
public class Category extends AuditModel<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Column(name = "name", unique = true)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subcategory_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))

    @ToString.Exclude
    private Category subCategory;


}
