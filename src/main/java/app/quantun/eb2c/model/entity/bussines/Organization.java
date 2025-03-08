package app.quantun.eb2c.model.entity.bussines;

import app.quantun.eb2c.model.entity.AuditModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "organizations")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Organization extends AuditModel<String> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @Column(nullable = true)
    private String description;

    @Column(nullable = true)
    private String taxId;

    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties(value = {"organization", "hibernateLazyInitializer", "handler"}, allowSetters = true)
    @ToString.Exclude
    private Set<Branch> branches = new HashSet<>();

    // Helper method to maintain bidirectional relationship
    public void addBranch(Branch branch) {
        branches.add(branch);
        branch.setOrganization(this);
    }

    public void removeBranch(Branch branch) {
        branches.remove(branch);
        branch.setOrganization(null);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Organization organization = (Organization) o;
        return getId() != null && Objects.equals(getId(), organization.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}