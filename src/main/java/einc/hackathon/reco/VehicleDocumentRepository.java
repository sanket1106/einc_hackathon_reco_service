package einc.hackathon.reco;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface VehicleDocumentRepository extends ElasticsearchRepository<VehicleDocument, String> {


}
