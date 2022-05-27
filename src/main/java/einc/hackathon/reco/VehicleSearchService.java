package einc.hackathon.reco;

import lombok.RequiredArgsConstructor;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VehicleSearchService {

    private final ElasticsearchOperations elasticsearchOperations;
    private final VehicleRepository vehicleRepository;

    public List<Vehicle> findSimilarUnsoldVehicles(String buyerId) {

        final var boughtVehicles = vehicleRepository.findAllByBuyer(buyerId);
        Integer highestBodyTypeIndex = -1;
        Integer lowestBodyTypeIndex = -1;
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

        QueryBuilder termQueryBuilder = QueryBuilders.termsQuery("bodyType", bodyTypesToSearch).boost(1);
        Query searchQuery = new NativeSearchQueryBuilder()
            .withQuery(termQueryBuilder)
            .build();

        Criteria bodyTypeCriteria = new Criteria("bodyType").in(bodyTypesToSearch);
        Criteria statusCriteria = new Criteria("status").is("listed");
        Criteria criteria = bodyTypeCriteria.and(statusCriteria);
        Query query = CriteriaQuery.builder(criteria).build();

        final var vehicles = elasticsearchOperations.search(query, VehicleDocument.class, IndexCoordinates.of("vehicles"));
        return vehicleRepository.findAllById(
            vehicles.get()
                .map(vehicle -> {
                    final var v = vehicle.getContent();
                    System.out.println(v.getMake() + " " + v.getModel() + " " + v.getId());
                    return v.getId();
                }).collect(Collectors.toList()));
    }
}
