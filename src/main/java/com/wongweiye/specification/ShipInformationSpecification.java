package com.wongweiye.specification;

import com.wongweiye.model.ShipInformation;
import org.springframework.data.jpa.domain.Specification;

public class ShipInformationSpecification {

    public static Specification<ShipInformation> hasShipName(String shipName) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("shipReferenceID"), shipName);
    }


    public static Specification<ShipInformation> hasShipRecord(String shipRecord) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("shipRecordID"), shipRecord);
    }
}
