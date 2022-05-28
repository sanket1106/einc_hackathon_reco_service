package einc.hackathon.reco;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class RecoController {

    private final VehicleRepository vehicleRepository;
    private final VehicleDocumentRepository vehicleDocumentRepository;
    private final VehicleSearchService vehicleSearchService;

    @ResponseBody
    @GetMapping("/recentlyBoughtVehicles/{buyerId}")
    public List<VehicleDTO> getRecentlyBoughtVehicles(@PathVariable String buyerId) {
        return vehicleRepository.findTop5ByBuyerOrderBySoldDateDesc(buyerId)
            .stream()
            .map(vehicle ->
                VehicleDTO.builder()
                    .askPrice(vehicle.getAskPrice())
                    .bodyType(vehicle.getBodyType())
                    .buyer(vehicle.getBuyer())
                    .displacement(vehicle.getDisplacement())
                    .id(vehicle.getId())
                    .fuelType(vehicle.getFuelType())
                    .make(vehicle.getMake())
                    .model(vehicle.getModel())
                    .seller(vehicle.getSeller())
                    .soldDate(vehicle.getSoldDate())
                    .soldPrice(vehicle.getSoldPrice())
                    .status(vehicle.getStatus())
                    .transmission(vehicle.getTransmission())
                    .trim(vehicle.getTrim())
                    .variant(vehicle.getVariant())
                    .year(vehicle.getYear())
                    .build())
            .collect(Collectors.toList());
        //return "Done";
    }

    @ResponseBody
    @GetMapping("/recommendedVehicles/{buyerId}")
    public List<VehicleDTO> getRecommendedVehicles(@PathVariable String buyerId) {
        return vehicleSearchService.findSimilarUnsoldVehicles(buyerId);
        //return "Done";
    }

    @ResponseBody
    @PutMapping ("/indexVehicles")
    public String indexVehicles() {
        vehicleDocumentRepository.deleteAll();
        final var vehicles = vehicleRepository.findAll();
        final var vehicleDocs = vehicles.stream().map(this::buildVehicleDocument)
        .collect(Collectors.toList());
        vehicleDocumentRepository.saveAll(vehicleDocs);
        return "Done";
    }

    private VehicleDocument buildVehicleDocument(Vehicle v) {
        return VehicleDocument.builder()
            .id(v.getId())
            .askPrice(v.getAskPrice())
            .fuelType(v.getFuelType())
            .make(v.getMake())
            .model(v.getModel())
            .status(v.getStatus())
            .bodyType(v.getBodyType())
            .transmission(v.getTransmission())
            .variant(v.getVariant())
            .year(v.getYear())
            .buyer(v.getBuyer())
            .displacement(v.getDisplacement())
            .solddate(v.getSoldDate() != null ? v.getSoldDate().getTime() : null)
            .soldPrice(v.getSoldPrice())
            .trim(v.getTrim())
            .build();
    }

}
