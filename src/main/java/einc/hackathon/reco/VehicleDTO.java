package einc.hackathon.reco;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class VehicleDTO {

    private String id;
    private Integer year;
    private String make;
    private String model;
    private String variant;
    private String trim;
    private String bodyType;
    private String fuelType;
    private String transmission;
    private Double displacement;
    private Integer soldPrice;
    private String buyer;
    private Date soldDate;
    private String status;
    private String seller;
    private Integer askPrice;
    private float score;
}
