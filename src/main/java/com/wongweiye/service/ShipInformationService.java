package com.wongweiye.service;


import com.wongweiye.dto.ShipInformationDTO;
import com.wongweiye.model.ShipInformation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ShipInformationService {
    public List<ShipInformationDTO> getShipInformation();
    Page<ShipInformation> searchShipInformation(String shipName, String shipRecord, Pageable pageable);

}
