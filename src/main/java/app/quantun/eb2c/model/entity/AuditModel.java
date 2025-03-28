package app.quantun.eb2c.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;

@MappedSuperclass

@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(
        value = {"createdAt", "updatedAt"},
        allowGetters = true
)
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public abstract class AuditModel<T> implements Serializable {
    @CreatedBy
    @Column(name = "created_by")
    protected T createdBy;
    @LastModifiedBy
    @Column(name = "modified_by")
    protected T modifiedBy;

    // Getters and Setters (Omitted for brevity)
    @Column(name = "created_at", nullable = true, updatable = true)
    @CreatedDate
    private LocalDateTime createdAt;
    @Column(name = "updated_at", nullable = true)
    @LastModifiedDate
    private LocalDateTime updatedAt;

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();

        }

    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
        if (updatedAt == null) {
            this.updatedAt = LocalDateTime.now();

        }

    }


}