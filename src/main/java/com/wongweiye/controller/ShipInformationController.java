package com.wongweiye.controller;

import com.wongweiye.dto.ShipInformationDTO;
import com.wongweiye.model.ShipInformation;
import com.wongweiye.service.ShipInformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping(path = "/v2", produces = "application/json")
@RestController
@SuppressWarnings("unchecked")
public class ShipInformationController {

    @Autowired
    ShipInformationService shipInformationService;


    @GetMapping("/userDetails/me/shipProperties")
    public ResponseEntity<ShipInformationDTO> getShipID(){


        List<ShipInformationDTO> shipInfo = shipInformationService.getShipInformation();

        if(shipInfo != null)
        {
            return ResponseEntity.ok().body(shipInfo.get(0));

        }
        else
        {
            return ResponseEntity.notFound().build();
        }

    }

    @GetMapping("/userDetails/me/searchShip")
    public ResponseEntity<List<ShipInformation>> searchShipInformation(@RequestParam("name") String name , @RequestParam("record") String record){


        Pageable page = PageRequest.of(0,10);

        Page<ShipInformation> shipInfo = shipInformationService.searchShipInformation(name, record, page);

        return ResponseEntity.ok().body(shipInfo.toList());
    }




}
