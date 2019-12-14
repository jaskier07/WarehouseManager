//package pl.kania.warehousemanager.services.synchronization;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.env.Environment;
//import org.springframework.stereotype.Service;
//import pl.kania.warehousemanager.model.vector.ProductVectorClock;
//import pl.kania.warehousemanager.model.vector.ProductVectorClockNode;
//
//import javax.annotation.PostConstruct;
//import java.util.Arrays;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//@Service
//public class VectorClockService {
//
//    @Autowired
//    private Environment environment;
//
//    private String clientId;
//    private String client2Id;
//    private String serverId;
//
//    @PostConstruct
//    void initializeIds() {
//        serverId = environment.getProperty("server.id");
//        clientId = environment.getProperty("android.client.id");
//        client2Id = environment.getProperty("android.client2.id");
//    }
//
//    public ProductVectorClock createNewVector(int initialQuantity) {
//        ProductVectorClockNode server = new ProductVectorClockNode(initialQuantity, serverId);
//        ProductVectorClockNode u1 = new ProductVectorClockNode(clientId);
//        ProductVectorClockNode u2 = new ProductVectorClockNode(client2Id);
//
//        server.incrementVersion();
//
//        return new ProductVectorClock(Arrays.asList(server, u1, u2));
//    }
//
//    public Set<String> getDifferentNodeNames(ProductVectorClock p1, ProductVectorClock p2) {
//        final Set<String> differentNodeNames = new HashSet<>();
//        for (ProductVectorClockNode node1 : p1.getNodes()) {
//            for (ProductVectorClockNode node2 : p2.getNodes()) {
//                if (node1.getNodeName().equals(node2.getNodeName())) {
//                    if (node1.getVersion() != node2.getVersion()) {
//                        differentNodeNames.add(node1.getNodeName());
//                    }
//                }
//            }
//        }
//        return differentNodeNames;
//    }
//}
