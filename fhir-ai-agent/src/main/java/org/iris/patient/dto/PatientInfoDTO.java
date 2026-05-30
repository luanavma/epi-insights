package org.iris.patient.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PatientInfoDTO {

    private String key;
    private String name;
    private String address;
    private String birthDate;
    private String gender;

    private List<String> medications;
    private List<String> conditions;
    private List<String> allergies;


}
