package org.iris.patient.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Data
@Entity
@Table(name = "Patient", schema = "HSFHIR_X0001_S")
public class Patient extends PanacheEntityBase {

    @Id
    @Column(name = "Key")
    public String key;

    @Column(name = "name")
    public String name;

    @Column(name = "address")
    public String address;

    @Column(name = "birthdate")
    public LocalDate birthDate;

    @Column(name = "gender")
    public String gender;

}
