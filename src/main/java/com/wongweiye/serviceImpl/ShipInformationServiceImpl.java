package com.wongweiye.serviceImpl;

import com.wongweiye.dto.ShipInformationDTO;
import com.wongweiye.model.ShipInformation;
import com.wongweiye.repository.ShipInformationRepository;
import com.wongweiye.service.ShipInformationService;
import com.wongweiye.specification.ShipInformationSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ShipInformationServiceImpl implements ShipInformationService {

    @Autowired
    private ShipInformationRepository shipInformationRepository;

    public List<ShipInformationDTO> getShipInformation() {

        List<ShipInformation> shipInfo = getShipInformationRepository().findAll();

        if(!shipInfo.isEmpty())
        {
            return shipInfo.stream().limit(1).map( s -> new ShipInformationDTO(s.getShipID(),s.getShipReferenceID(),
                    s.getShipRecordID(),s.isShipEnabled(),s.getShipCode(),s.getShipName(),s.getShipType()))
                    .collect(Collectors.toList());

        }
        else
        {
            return null;
        }
    }

    public Page<ShipInformation> searchShipInformation(String shipName, String shipRecord, Pageable pageable){
        Specification<ShipInformation> spec = Specification.where(null);

        if (Objects.nonNull(shipName)) {
            spec = spec.and(ShipInformationSpecification.hasShipName(shipName));
        }

        if(Objects.nonNull(shipRecord)){
            spec = spec.and(ShipInformationSpecification.hasShipRecord(shipRecord));
        }

        return shipInformationRepository.findAll(spec, pageable);

    }

    public ShipInformationRepository getShipInformationRepository() {
        return shipInformationRepository;
    }

    public void setShipInformationRepository(ShipInformationRepository shipInformationRepository) {
        this.shipInformationRepository = shipInformationRepository;
    }
}
