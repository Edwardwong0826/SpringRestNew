package com.wongweiye.repository;

import com.wongweiye.model.ShipInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ShipInformationRepository extends JpaRepository<ShipInformation,Long>, JpaSpecificationExecutor<ShipInformation> {

}
