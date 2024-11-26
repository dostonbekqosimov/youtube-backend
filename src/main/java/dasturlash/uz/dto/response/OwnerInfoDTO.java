package dasturlash.uz.dto.response;

import lombok.Data;

@Data
public class OwnerInfoDTO {
    private String id;
    private String name;
    private String surname;

    public OwnerInfoDTO(String id, String name, String surname) {
        this.id = id;
        this.name = name;
        this.surname = surname;
    }
}