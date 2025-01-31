package com.mindex.challenge.dao;

import com.mindex.challenge.data.Compensation;
import org.springframework.stereotype.Repository;
import org.springframework.data.mongodb.repository.MongoRepository;

@Repository
public interface CompensationRepository extends MongoRepository<Compensation, String> {
    // Returns most recent update
    Compensation findTopByEmployeeIdOrderByEffectiveDateDesc(String employeeId);
}
