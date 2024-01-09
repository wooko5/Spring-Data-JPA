package study.datajpa.entity;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import java.time.LocalDateTime;

@Getter
@MappedSuperclass //해당 어노테이션이 있어야 JpaBaseEntity를 상속받은 다른 엔티티가 해당 칼럼(생성/수정일시)을 사용할 수 있음
public class JpaBaseEntity {

    @Column(updatable = false)
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdDate = now;
        this.updatedDate = now; //최소 생성시 수정일시는 생성일시와 동일, null로 두지않음
    }
}
