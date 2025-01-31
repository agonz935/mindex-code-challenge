package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.service.CompensationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Thought about including List of compensation in Employee data, but decided against it
 * to keep it simple since we are posting and updating.
 */
@Service
public class CompensationServiceImpl implements CompensationService {
    private static final Logger LOG = LoggerFactory.getLogger(CompensationServiceImpl.class);

    @Autowired
    private CompensationRepository compensationRepository;

    /**
     * POST API call for employee compensation.
     * If no date is provided, effective date will be set to current date.
     * @param compensation obj
     * @return compensation
     */
    @Override
    public Compensation create(Compensation compensation) {
        LOG.debug("Creating compensation [{}]", compensation);

        if (compensation.getEffectiveDate() == null) {
            compensation.setEffectiveDate(new Date());
        }

        compensationRepository.insert(compensation);

        return compensation;
    }

    /**
     * GET API call for employee compensation based on employee ID.
     * If compensation is null, runtime exception will be thrown.
     * @param employeeId used to find compensation.
     * @return compensation: will return most recent employee compensation if one exists
     */

    @Override
    public Compensation read(String employeeId){
        LOG.debug("Reading compensation for employee ID [{}]", employeeId);

        Compensation compensation = compensationRepository.findTopByEmployeeIdOrderByEffectiveDateDesc(employeeId);

        if(compensation == null){
            throw new RuntimeException("No compensation found for employee ID [" + employeeId + "]");
        }

        return compensation;
    }
}
