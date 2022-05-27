package einc.hackathon.reco;

import lombok.RequiredArgsConstructor;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.result.HandlerResultHandlerSupport;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VehicleSearchService {

    private final ElasticsearchOperations elasticsearchOperations;
    private final VehicleRepository vehicleRepository;

    public List<VehicleDTO> findSimilarUnsoldVehicles(String buyerId) {

        final var boughtVehicles = vehicleRepository.findAllByBuyer(buyerId);
        final var bodyTypesToSearch = determineBodyTypesToSearch(boughtVehicles);
        final var priceRange = determinePriceRange(boughtVehicles);
        final var makesToSearch = determineMakesToSearch(boughtVehicles);
        final var yearRange = determineYearRange(boughtVehicles);
        final var fuelTypesToSearch = determineFuelTypeToSearch(boughtVehicles);
        final var displacementsToSearch = determineDisplacementsToSearch(boughtVehicles);
        //final var modelsToSearch = determineModelsToSearch(boughtVehicles);

        Criteria bodyTypeCriteria = new Criteria("bodyType").in(bodyTypesToSearch).boost(15);
        Criteria priceRangeCriteria = new Criteria("askPrice").between(priceRange[0], priceRange[1]).boost(12);
        Criteria yearCriteria = new Criteria("year").between(yearRange[0], yearRange[1]).boost(10);
        Criteria displacementCriteria = new Criteria("displacement").in(displacementsToSearch).boost(7);
        Criteria fuelTypeCriteria = new Criteria("fuelType").in(fuelTypesToSearch).boost(5);
        Criteria makeCriteria = new Criteria("make").in(makesToSearch);
        //Criteria modelsCriteria = new Criteria("model").in(modelsToSearch);

        Criteria statusCriteria = new Criteria("status").is("listed");
        Criteria criteria = bodyTypeCriteria.or(priceRangeCriteria)
            .or(makeCriteria)
            .or(yearCriteria)
            .or(fuelTypeCriteria)
            .or(displacementCriteria);
            //.or(modelsCriteria);

        Criteria finalCriteria = criteria.and(statusCriteria);

        Query query = CriteriaQuery.builder(finalCriteria).build();

        final var vehicleDocs = elasticsearchOperations.search(query, VehicleDocument.class, IndexCoordinates.of("vehicles"));
        final var vehicleIdsMap = vehicleDocs.stream()
            .collect(Collectors.toMap(
                searchHit -> searchHit.getContent().getId(),
                SearchHit<VehicleDocument>::getScore));

        return vehicleRepository.findAllById(vehicleIdsMap.keySet())
            .stream()
            .map(vehicle ->
                VehicleDTO.builder()
                    .askPrice(vehicle.getAskPrice())
                    .bodyType(vehicle.getBodyType())
                    .displacement(vehicle.getDisplacement())
                    .id(vehicle.getId())
                    .fuelType(vehicle.getFuelType())
                    .make(vehicle.getMake())
                    .model(vehicle.getModel())
                    .seller(vehicle.getSeller())
                    .soldDate(vehicle.getSoldDate())
                    .status(vehicle.getStatus())
                    .transmission(vehicle.getTransmission())
                    .trim(vehicle.getTrim())
                    .variant(vehicle.getVariant())
                    .year(vehicle.getYear())
                    .score(vehicleIdsMap.get(vehicle.getId()))
                    .build())
            .sorted(Comparator.comparing(VehicleDTO::getScore))
            .collect(Collectors.toList());
    }


    private Set<String> determineFuelTypeToSearch(List<Vehicle> boughtVehicles) {
        return boughtVehicles.stream()
            .map(Vehicle::getMake)
            .collect(Collectors.toSet());
    }

    private Set<Double> determineDisplacementsToSearch(List<Vehicle> boughtVehicles) {
        return boughtVehicles.stream()
            .map(Vehicle::getDisplacement)
            .collect(Collectors.toSet());
    }

    private Set<String> determineMakesToSearch(List<Vehicle> boughtVehicles) {
        return boughtVehicles.stream()
            .map(Vehicle::getMake)
            .collect(Collectors.toSet());
    }

    private Set<String> determineModelsToSearch(List<Vehicle> boughtVehicles) {
        return boughtVehicles.stream()
            .map(Vehicle::getModel)
            .collect(Collectors.toSet());
    }

    private Integer[] determineYearRange(List<Vehicle> boughtVehicles) {
        Integer minYear = -1;
        Integer maxYear = -1;
        for (Vehicle boughtVehicle : boughtVehicles) {
            if (minYear == -1 || boughtVehicle.getYear() < minYear) {
                minYear = boughtVehicle.getYear();
            }

            if (maxYear == -1 || boughtVehicle.getYear() > maxYear) {
                maxYear = boughtVehicle.getYear();
            }
        }
        return new Integer[]{minYear, maxYear};
    }

    private Integer[] determinePriceRange(List<Vehicle> boughtVehicles) {
        Integer minPrice = -1;
        Integer maxPrice = -1;
        for (Vehicle boughtVehicle : boughtVehicles) {
            if (minPrice == -1 || boughtVehicle.getSoldPrice() < minPrice) {
                minPrice = boughtVehicle.getSoldPrice();
            }

            if (maxPrice == -1 || boughtVehicle.getSoldPrice() > maxPrice) {
                maxPrice = boughtVehicle.getSoldPrice();
            }
        }
        return new Integer[]{minPrice, maxPrice};
    }

    private Set<String> determineBodyTypesToSearch(List<Vehicle> boughtVehicles) {
        return boughtVehicles.stream()
            .map(Vehicle::getBodyType)
            .collect(Collectors.toSet());
        /*Integer highestBodyTypeIndex = -1;
        Integer lowestBodyTypeIndex = -1;
        //Map<String, Integer> map = new HashMap<>();
        for (Vehicle boughtVehicle : boughtVehicles) {
            final var bodyType = boughtVehicle.getBodyType();
            final var index = Constants.bodyTypes.indexOf(bodyType);
            if (lowestBodyTypeIndex == -1) {
                lowestBodyTypeIndex = index;
            }
            else if (index < lowestBodyTypeIndex) {
                lowestBodyTypeIndex = index;
            }

            if (highestBodyTypeIndex == -1) {
                highestBodyTypeIndex = index;
            }
            else if (index > highestBodyTypeIndex) {
                highestBodyTypeIndex = index;
            }
        }

        final var  bodyTypesToSearch = Constants.bodyTypes.subList(lowestBodyTypeIndex,
            highestBodyTypeIndex + 1 == Constants.bodyTypes.size() ? highestBodyTypeIndex : highestBodyTypeIndex + 1);
        return bodyTypesToSearch;*/
    }
}
