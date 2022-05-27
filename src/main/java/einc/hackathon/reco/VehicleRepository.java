package einc.hackathon.reco;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, String> {

    List<Vehicle> findAllByBuyer(String buyerId);
}
