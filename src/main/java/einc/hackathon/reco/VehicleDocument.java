package einc.hackathon.reco;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.Id;
import java.time.Instant;
import java.util.Date;

@Data
@Builder
@Document(indexName = "vehicles")
public class VehicleDocument {

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
    @Field(type = FieldType.Date)
    private Long solddate;
    private String status;
    private Integer askPrice;
}
