package einc.hackathon.reco;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.Instant;
import java.util.Date;

@Data
@NoArgsConstructor
@Entity(name = "Vehicle")
public class Vehicle {

    @Id
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

}
