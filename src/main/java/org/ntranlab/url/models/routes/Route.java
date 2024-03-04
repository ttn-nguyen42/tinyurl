package org.ntranlab.url.models.routes;

import java.io.Serializable;
import java.sql.Date;

import org.springframework.lang.NonNull;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Route implements Serializable{
    @Id
    private String id;

    @NonNull
    private String alias;

    @NonNull
    private String destination;

    private boolean disabled;
    private Date timestamp;
}
