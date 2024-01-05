package com.wongweiye.repository;

import com.wongweiye.dto.ConfigurationLocationDTO;
import com.wongweiye.model.ConfigurationLocation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfigurationLocationRepository  extends JpaRepository<ConfigurationLocation, Long> {

    //@Query(value="SELECT location FROM ConfigurationLocation location WHERE (?1 is null or location.locationStatus = ?1) ")
    //public Page<ConfigurationLocationDTO> getLocation(Boolean status, Pageable pageable);

    public Page<ConfigurationLocationDTO> getLocation(String status, Pageable pageable);

}
